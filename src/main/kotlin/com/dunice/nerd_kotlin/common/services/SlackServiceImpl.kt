package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.Member
import com.dunice.nerd_kotlin.common.MembersRepository
import com.dunice.nerd_kotlin.common.errors.CustomException
import com.dunice.nerd_kotlin.common.errors.PERSON_NOT_FOUND
import com.dunice.nerd_kotlin.common.types.ExamDataDTO
import com.dunice.nerd_kotlin.common.utils.getCyrillicDayOfWeek
import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.users.UsersListRequest
import com.slack.api.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct

@Service
class SlackServiceImpl(val mongoTemplate: MongoTemplate, val membersRepository: MembersRepository) : SlackService {

    private val slack : Slack = Slack.getInstance()

    @Value("\${slack.web.api.token}")
    private var token : String = ""

    @Value("\${slack.web.api.teamId}")
    private var teamId : String = ""

    @Value("#{\${slackGoogleDocAliases}}")
    lateinit var slackGoogleDocAliases : Map<String, String>

    @PostConstruct
    private fun init() {
        this.getUsersFromSlack()
    }

    override fun sendMessage(examDataDTO: ExamDataDTO) {
        this.postMessage(getUserId(examDataDTO.email), generateMessage(examDataDTO))
    }

    private fun getUsersFromSlack() {
        val documents: MutableList<Member> = emptyList<Member>().toMutableList()
        val users = slack.methods().usersList(UsersListRequest.builder().token(token).teamId(teamId).build()).members?:
            throw CustomException(PERSON_NOT_FOUND)
        users.map {
            val member = Member(it.profile.email, it.id)
            documents.add(member)
            mongoTemplate.upsert(Query().addCriteria(Criteria.where("email").`is`(member.email)),
                Update().set("slackId", member.slackId), "slackIds")
        }


    }

    private fun getUserId(email: String) : String =
        membersRepository.getByEmail(email)?.slackId?: throw CustomException(PERSON_NOT_FOUND)

    private fun postMessage(channel: String, messageText: String) = slack.methods(token)
        .chatPostMessage(ChatPostMessageRequest.builder()
            .channel(channel)
            .text(messageText)
            .build()
        )

    private fun generateMessage(info : ExamDataDTO) : String =
        "Привет, ${info.nameStudent.split(" ")[0]}! ${String(Character.toChars(0x1F44B))}\n" +
                "Твое расписание матрицы на эту неделю:\n" +
                "${getCyrillicDayOfWeek(info.datetime.dayOfWeek)} (${info.datetime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}):" +
                "${info.subject} ${info.datetime.format(DateTimeFormatter.ofPattern("HH:mm"))} " +
                "${info.interviewer} ${info.room}"
}