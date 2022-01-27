package com.dunice.nerd_kotlin.services.slack

import com.dunice.nerd_kotlin.academyReminder.types.Event
import com.dunice.nerd_kotlin.common.db.MemberDocument
import com.dunice.nerd_kotlin.common.db.MembersRepository
import com.dunice.nerd_kotlin.common.errors.CustomException
import com.dunice.nerd_kotlin.common.errors.PERSON_NOT_FOUND
import com.dunice.nerd_kotlin.common.errors.SlackEmailNotFoundException
import com.slack.api.Slack
import com.slack.api.methods.kotlin_extension.request.chat.blocks
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.users.UsersListRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service

@Service
class SlackServiceImpl(val mongoTemplate: MongoTemplate,
                       val membersRepository: MembersRepository)
    : SlackService {

    private val className = this.javaClass.simpleName

    private val slack : Slack = Slack.getInstance()
    private val log = LoggerFactory.getLogger(this.javaClass.simpleName)

    @Value("\${slack.web.api.token}")
    lateinit var token : String

    @Value("\${slack.web.api.teamId}")
    lateinit var teamId : String

    @Value("#{\${slackGoogleDocAliases}}")
    lateinit var slackGoogleDocAliases : Map<String, String>

    @Value("\${slack.web.api.logChannel}")
    lateinit var slackLogChannel : String

//    @PostConstruct
//    private fun init() {
//        this.getUsersFromSlack()
//    }

    override fun sendMessage(email: String, message: String) {
        this.postMessage(membersRepository.findOneByEmail(email).get().slackId, message)
    }

    override fun checkEmail(email: String): Boolean {
        return membersRepository.existsByEmail(email)
    }

    override fun sendLogMessage(message: String) {
        this.postMessage(channel = slackLogChannel, messageText = message)
    }

    override fun getNamesByEmail(vararg emails: String) : Map<String, MemberDocument> {
        return emails.filter { it != "" }.map {
            membersRepository.findOneByEmail(it).orElseGet {
                this.getUsersFromSlack()
                return@orElseGet membersRepository.findOneByEmail(it).orElseThrow { SlackEmailNotFoundException(it) }
            }
        }.associateBy { it.email!! }
    }

    fun getUsersFromSlackV2() {

        log.info("->")

        val users = slack.methods().usersList(UsersListRequest.builder().token(token).teamId(teamId).build()).members?:
        throw CustomException(PERSON_NOT_FOUND)
        users.filter{!it.isDeleted}.forEach {
            val name = if (slackGoogleDocAliases.containsKey(it.realName)) slackGoogleDocAliases[it.realName]!! else it.realName
            val member = MemberDocument(it.profile.email, it.id, name)
            mongoTemplate.upsert(Query().addCriteria(Criteria.where("email").`is`(member.email)),
                Update().set("slackId", member.slackId).set("fullName", member.fullName), "slackIds")
        }
        log.info("<!")
    }

    @Deprecated("use getUsersFromSlack v2")
    private fun getUsersFromSlack() {
        val documents: MutableList<MemberDocument> = emptyList<MemberDocument>().toMutableList()
        val users = slack.methods().usersList(UsersListRequest.builder().token(token).teamId(teamId).build()).members?:
            throw CustomException(PERSON_NOT_FOUND)
        users.filter{!it.isDeleted}.map {
            val name = if (slackGoogleDocAliases.containsKey(it.realName)) slackGoogleDocAliases[it.realName]!! else it.realName
            val member = MemberDocument(it.profile.email, it.id, name)
            documents.add(member)
            mongoTemplate.upsert(Query().addCriteria(Criteria.where("email").`is`(member.email)),
                Update().set("slackId", member.slackId).set("fullName", member.fullName), "slackIds")
        }
    }

    fun getSlackIds(data: List<Event>): MutableMap<String, String> {

        log.info("-> \n data {}", data)

        val recipients = data.fold(mutableSetOf<String>()) { acc, item ->

            item.recipients.forEach {
                acc.add(it)
            }
            acc
        }

        var slackIdsFullName = membersRepository.findByFullNameIn(recipients.toList())

        if (recipients.size > slackIdsFullName.size) {
            getUsersFromSlackV2()
            slackIdsFullName = membersRepository.findByFullNameIn(recipients.toList())
            if (recipients.size > slackIdsFullName.size) {
                val diff = recipients.filter { recipient ->
                    val element = slackIdsFullName.find { it.getFullName() == recipient  }

                    element == null
                }

                throw RuntimeException("Не были найдены slack id для пользователе ${diff.joinToString(" ")}")
            }
        }
        log.info("<!")

        return slackIdsFullName.fold(mutableMapOf()) { acc, item ->

            acc[item.getFullName()] = item.getSlackId()
            acc
        }
    }

    fun postMessage(channel: String, messageText: String) = slack.methods(token)
        .chatPostMessage(ChatPostMessageRequest.builder()
            .channel(channel)
            .blocks {
                section {
                    markdownText(messageText)
                }
            }
            .text(messageText)
            .build()
        )
    }