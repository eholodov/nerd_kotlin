package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.Member
import com.dunice.nerd_kotlin.common.MembersRepository
import com.dunice.nerd_kotlin.common.errors.SlackEmailNotFoundException
import com.dunice.nerd_kotlin.common.types.ExamDTO
import com.dunice.nerd_kotlin.common.utils.getCyrillicDayOfWeek
import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.users.UsersListRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct

@Service
class SlackServiceImpl(val membersRepository: MembersRepository) : SlackService {

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

    override fun sendMessage(examDTO: ExamDTO) {
        this.generateMessage(
            examDTO,
            this.getNamesByEmail(examDTO.studentEmail, examDTO.interviewerEmail, examDTO.assistantEmail.orEmpty())
        )


    }

    private fun getNamesByEmail(vararg emails: String) : Map<String, Member> {
        return emails.map {
            membersRepository.findById(it).orElseGet {
                this.getUsersFromSlack()
                return@orElseGet membersRepository.findByEmail(it).orElseThrow { SlackEmailNotFoundException(it) }
            }
        }.associateBy { it.email!! }
    }

    private fun getUsersFromSlack() {
        val documents: MutableList<Member> = emptyList<Member>().toMutableList()

        slack.methods().usersList(UsersListRequest.builder().token(token).teamId(teamId).build()).members.map {
            val member = Member(it.profile.email, it.id,
                ((if (slackGoogleDocAliases.containsKey(it.realName)) slackGoogleDocAliases[it.realName] else it.realName)!!))
            documents.add(member)
        }
        membersRepository.saveAll(documents)
    }

    private fun postMessage(channel: String, messageText: String) = slack.methods(token)
        .chatPostMessage(ChatPostMessageRequest.builder()
            .channel(channel)
            .text(messageText)
            .build()
        )

    private fun generateMessage(info : ExamDTO, names: Map<String, Member>) {
        names.forEach{
            val messagePart = if (it.key == info.studentEmail) "${names[info.interviewerEmail]?.fullName} ${names[info.assistantEmail]?.fullName?: ""}"
            else "${names[info.studentEmail]?.fullName}"
            val message = "Привет, ${it.value.fullName.split(" ")[0]}! ${String(Character.toChars(0x1F44B))}\n" +
                "Твое расписание матрицы на эту неделю:\n" +
                "${getCyrillicDayOfWeek(info.datetime.dayOfWeek)} (${info.datetime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}):" +
                "${info.subject} ${info.datetime.format(DateTimeFormatter.ofPattern("HH:mm"))} " +
                "$messagePart ${info.room}"

                postMessage(it.value.slackId, message)
            }
        }
//
}