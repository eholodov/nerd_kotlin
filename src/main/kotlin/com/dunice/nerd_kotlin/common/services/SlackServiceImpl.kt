package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.db.MemberDocument
import com.dunice.nerd_kotlin.common.db.MembersRepository
import com.dunice.nerd_kotlin.common.db.RemainderDocument
import com.dunice.nerd_kotlin.common.db.RemaindersRepository
import com.dunice.nerd_kotlin.common.errors.CustomException
import com.dunice.nerd_kotlin.common.errors.PERSON_NOT_FOUND
import com.dunice.nerd_kotlin.common.errors.SlackEmailNotFoundException
import com.dunice.nerd_kotlin.common.types.ExamDTO
import com.dunice.nerd_kotlin.common.utils.getCyrillicDayOfWeek
import com.slack.api.Slack
import com.slack.api.methods.kotlin_extension.request.chat.blocks
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.users.UsersListRequest
import com.slack.api.model.block.Blocks
import com.slack.api.model.block.Blocks.asBlocks
import com.slack.api.model.block.Blocks.section
import com.slack.api.model.block.composition.BlockCompositions.markdownText
import com.slack.api.model.block.composition.MarkdownTextObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.Executors
import javax.annotation.PostConstruct
import kotlin.concurrent.schedule
import kotlin.concurrent.thread
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask

@Service
class SlackServiceImpl(val mongoTemplate: MongoTemplate,
                       val membersRepository: MembersRepository,
                       val remaindersRepository: RemaindersRepository)
    : SlackService {

    private val slack : Slack = Slack.getInstance()

    @Value("\${slack.web.api.token}")
    lateinit var token : String

    @Value("\${slack.web.api.teamId}")
    lateinit var teamId : String

    @Value("#{\${slackGoogleDocAliases}}")
    lateinit var slackGoogleDocAliases : Map<String, String>

    @PostConstruct
    private fun init() {
        this.getUsersFromSlack()

    }

    override fun processRequest(examDataDTO: List<ExamDTO>) {
        val entities = examDataDTO.map { RemainderDocument(dateTime = it.datetime.minusMinutes(10L).toInstant(),
        assistantEmail = it.assistantEmail, interviewerEmail = it.interviewerEmail, studentEmail = it.studentEmail, subject = it.subject, room = it.room) }
        remaindersRepository.saveAll(entities)
        val cardsGroupedByInterviewerAndAssistant = examDataDTO.groupBy(ExamDTO::interviewerEmail)
            .mapValues { it.value.toMutableList() }.toMutableMap()
        cardsGroupedByInterviewerAndAssistant.values.forEach {
                it.forEach { dto ->
                if (!dto.assistantEmail.isNullOrBlank()) {
                    cardsGroupedByInterviewerAndAssistant[dto.assistantEmail]?.add(dto)
                }
            }
        }
        examDataDTO.forEach{sendStudentMessage(it)}
        scheduleRemainders()
    }

    private fun sendStudentMessage(examDTO: ExamDTO) {
        this.generateStudentMessage(
            examDTO,
            this.getNamesByEmail(examDTO.studentEmail, examDTO.interviewerEmail, examDTO.assistantEmail.orEmpty())
        )
    }

    private fun sendInterviewerOrAssistantMessage() {}

    private fun sendRemainderMessage(remainderDocument: RemainderDocument) {
        //println(Calendar.getInstance().getTime(remainderDocument.dateTime))
        Timer(false).schedule(time = Date.from(remainderDocument.dateTime)) {
            println("🤢this is schedule task🤢")
            println()
        }
    }

    private fun getNamesByEmail(vararg emails: String) : Map<String, MemberDocument> {
        return emails.filter { it != "" }.map {
            membersRepository.findOneByEmail(it).orElseGet {
                this.getUsersFromSlack()
                return@orElseGet membersRepository.findOneByEmail(it).orElseThrow { SlackEmailNotFoundException(it) }
            }
        }.associateBy { it.email!! }
    }

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

    private fun postMessage(channel: String, messageText: String) = slack.methods(token)
        .chatPostMessage(ChatPostMessageRequest.builder()
            .channel(channel)
            .blocks {
                section {
                    markdownText(messageText)
                }
            }
            .build()
        )

    private fun generateStudentMessage(info : ExamDTO, names: Map<String, MemberDocument>) {
            val messagePart = "${names[info.interviewerEmail]?.fullName} ${names[info.assistantEmail]?.fullName?: ""}"
            val studentMessage = "Привет, ${names[info.studentEmail]?.fullName?.split(" ")?.get(0)}! ${String(Character.toChars(0x1F44B))}\n" +
                "Твое расписание матрицы на эту неделю:\n" +
                "${getCyrillicDayOfWeek(info.datetime.dayOfWeek)} (${info.datetime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}): " +
                "${info.subject} ${info.datetime.format(DateTimeFormatter.ofPattern("HH:mm"))} " +
                "$messagePart ${info.room}"

                //postMessage(it.value.slackId, message)
            println(studentMessage)
        }

    private fun generateInterviewerOrAssistantMessage(cards : MutableMap<String, MutableList<ExamDTO>>) {
        cards.forEach {
            val messageText = buildString {
                append("Привет, ")
                append(membersRepository.findOneByEmail(it.key).get().fullName.split(" ")[0])
                append("! :wave::skin-tone-2: \n ")
                append("Твое расписание матрицы на эту неделю: \n ")
                val groupedByWeekDay = it.value.groupBy { it.datetime.dayOfWeek }
                groupedByWeekDay.forEach {
                    append("*${getCyrillicDayOfWeek(it.key)} ")
                    it.value.forEach { interview ->
                        val names = getNamesByEmail(interview.studentEmail, interview.assistantEmail ?: "")
                        append(
                            ">📚 ${interview.subject} ${interview.datetime.format(DateTimeFormatter.ofPattern("HH:mm"))}" +
                                    " \"${names[interview.studentEmail]?.fullName} "
                        )
                        append(if (interview.assistantEmail != null) " ассистент ${names[interview.assistantEmail]?.fullName}" else "")
                        append(interview.room)
                    }
                }

            }
            sendInterviewerOrAssistantMessage()
        }
    }

    private fun scheduleRemainders() {
        val todayExams = mongoTemplate.find(
            Query().addCriteria(
                Criteria.where("dateTime").gt(Instant.now()).lte(Instant.now().plus(24L, ChronoUnit.HOURS)))
            , RemainderDocument::class.java,"remainders")
        todayExams.forEach {
            sendRemainderMessage(it)
        }
    }
}