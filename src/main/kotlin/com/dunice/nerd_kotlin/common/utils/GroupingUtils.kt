package com.dunice.nerd_kotlin.common.utils

import com.dunice.nerd_kotlin.common.types.InterviewerCardInfo
import com.dunice.nerd_kotlin.common.types.SpreadSheetCardInfo
import com.dunice.nerd_kotlin.common.types.convertFromSheetCard
import org.springframework.stereotype.Component

@Component
class GroupingUtils {

    fun groupCardsByPerson(cards: List<SpreadSheetCardInfo>): Map<String, List<SpreadSheetCardInfo>>
        = cards.groupBy(SpreadSheetCardInfo::nameStudent)

    fun groupCardsByInterviewerAndAssistant(cards: List<SpreadSheetCardInfo>)  {
        val cardsGroupedByInterviewerAndAssistant = cards.groupBy(SpreadSheetCardInfo::interviewer)
            .mapValues { it.value.map { convertFromSheetCard(it) }.toMutableList()}.toMutableMap()
        cards.forEach {
            if (!it.assistant.isNullOrBlank()) {
                val assistantCard = convertFromSheetCard(it)
                assistantCard.isAssistant = true
                assistantCard.mainInterviewer = it.interviewer
                cardsGroupedByInterviewerAndAssistant[it.assistant]?.add(assistantCard)
            }
        }
    }
}