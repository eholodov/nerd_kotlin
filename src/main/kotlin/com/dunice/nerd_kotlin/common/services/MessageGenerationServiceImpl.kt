package com.dunice.nerd_kotlin.common.services

import com.dunice.nerd_kotlin.common.db.MembersRepository
import com.dunice.nerd_kotlin.common.db.RemainderDocument
import com.dunice.nerd_kotlin.common.errors.CustomException
import com.dunice.nerd_kotlin.common.types.ExamDTO
import com.dunice.nerd_kotlin.common.utils.getCyrillicDayOfWeek
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Service
@Profile("prod")
class MessageGenerationServiceImpl(val slackService: SlackService, val membersRepository: MembersRepository) : MessageGenerationService {

    override fun generateStudentMessage(info: ExamDTO) {
        val names = slackService.getNamesByEmail(info.studentEmail, info.interviewerEmail, info.assistantEmail ?: "")
        val messagePart = "${names[info.interviewerEmail]?.fullName} ${names[info.assistantEmail]?.fullName ?: ""}"
        val message = "Привет, ${
            names[info.studentEmail]?.fullName?.split(" ")?.get(0)
        }! ${String(Character.toChars(0x1F44B))}\n" +
                "Твое расписание матрицы на эту неделю:\n" +
                "${getCyrillicDayOfWeek(info.datetime.dayOfWeek)} (${info.datetime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}): \n" +
                ">\uD83D\uDCDA ${info.subject} ${
                    info.datetime.atZoneSameInstant(ZoneId.of("Europe/Moscow"))
                        .format(DateTimeFormatter.ofPattern("HH:mm"))
                } " +
                "$messagePart ${info.room}"
        slackService.sendMessage(info.studentEmail, message)
    }

    override fun generateInterviewerOrAssistantMessage(examDataDTO: List<ExamDTO>) {
        val cardsGroupedByInterviewerAndAssistant = examDataDTO.groupBy(ExamDTO::interviewerEmail)
            .mapValues { it.value.toMutableList() }.toMutableMap()
        examDataDTO.forEach {
             dto ->
                if (dto.assistantEmail != null && dto.assistantEmail != "") {
                    if (cardsGroupedByInterviewerAndAssistant.containsKey(dto.assistantEmail)) {
                        cardsGroupedByInterviewerAndAssistant[dto.assistantEmail]!!.add(dto)
                    }
                    else {
                        cardsGroupedByInterviewerAndAssistant[dto.assistantEmail] = listOf(dto).toMutableList()
                    }
                }
        }
        cardsGroupedByInterviewerAndAssistant.forEach { groupedCard ->
            val messageText = buildString {
                append("Привет, ")
                append(membersRepository.findOneByEmail(groupedCard.key).orElseThrow{ CustomException("Interviewer with email \"${groupedCard.key}\" is not found") }.fullName.split(" ")[0])
                append("! ${String(Character.toChars(0x1F44B))} \n ")
                append("Твое расписание матрицы на эту неделю: \n ")
                groupedCard.value.sortBy { it.datetime }
                val groupedByWeekDay = groupedCard.value.groupBy { it.datetime.dayOfWeek }
                groupedByWeekDay.forEach {
                    append("*${getCyrillicDayOfWeek(it.key)} ${ZonedDateTime.ofInstant(it.value[0].datetime.toInstant(), ZoneId.of("Europe/Moscow"))
                        .format(DateTimeFormatter.ofPattern("(dd.MM.yyyy)"))}*\n")
                    it.value.forEach { interview ->
                        val names = slackService.getNamesByEmail(interview.studentEmail, interview.assistantEmail ?: "")
                        append(
                            ">📚 ${interview.subject} ${ZonedDateTime.ofInstant(interview.datetime.toInstant(),
                                ZoneId.of("Europe/Moscow")).format(DateTimeFormatter.ofPattern("HH:mm"))}" +
                                    " ${names[interview.studentEmail]?.fullName} "
                        )
                        append(if (interview.assistantEmail != null && interview.assistantEmail != groupedCard.key) "ассистент ${names[interview.assistantEmail]?.fullName} " else "")
                        append("${ interview.room } \n")
                    }
                }

            }
            slackService.sendMessage(groupedCard.key ,messageText)
        }
    }

    override fun generateRemainderMessage(remainderDocument: RemainderDocument) {
        val names = slackService.getNamesByEmail(remainderDocument.studentEmail, remainderDocument.interviewerEmail, remainderDocument.assistantEmail?: "")
        names.forEach{
            val messageText = buildString {
                append("Привет, ")
                append(it.value.fullName.split(" ")[0])
                append("! ${String(Character.toChars(0x1F44B))}\n ")
                append("Через 10 минут у тебя матрица: \n")
                append(
                    ">📚 ${remainderDocument.subject} " +
                            ZonedDateTime.ofInstant(remainderDocument.dateTime.plus(10L, ChronoUnit.MINUTES), ZoneId.of("Europe/Moscow"))
                                .format(DateTimeFormatter.ofPattern("HH:mm")) +
                            " ${if (it.key.equals(remainderDocument.studentEmail))
                                "${names[remainderDocument.interviewerEmail]?.fullName} ${names[remainderDocument.assistantEmail]?.fullName?: ""}"
                            else "${names[remainderDocument.studentEmail]?.fullName}"} ${remainderDocument.room}"
                )
            }
            slackService.sendMessage(it.key, messageText)
        }
    }

    override fun generateRemainderDescription(remainderDocument: RemainderDocument) : String {
        val names = slackService.getNamesByEmail(remainderDocument.studentEmail, remainderDocument.interviewerEmail, remainderDocument.assistantEmail?: "")
        return "Матрица для ${names[remainderDocument.studentEmail]?.fullName} по предмету ${remainderDocument.subject} запланирована на " +
                "${ZonedDateTime.ofInstant(remainderDocument.dateTime.plus(10L, ChronoUnit.MINUTES),
            ZoneId.of("Europe/Moscow")).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))} в комнате ${remainderDocument.subject}\n" +
                "Принимающие: ${names[remainderDocument.interviewerEmail]?.fullName} ${names[remainderDocument.assistantEmail]?.fullName?: ""}"
    }
}