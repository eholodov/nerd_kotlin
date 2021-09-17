package com.dunice.nerd_kotlin.common.utils

import com.dunice.nerd_kotlin.common.types.InterviewerCardInfo
import com.dunice.nerd_kotlin.common.types.SpreadSheetCardInfo
import org.springframework.stereotype.Component

@Component
class GroupingUtils {

    fun groupCardsByPerson(cards: List<SpreadSheetCardInfo>): Map<String, List<SpreadSheetCardInfo>>
        = cards.groupBy(SpreadSheetCardInfo::nameStudent)

    fun groupCardsByInterviewerAndAssistant(cards: List<SpreadSheetCardInfo>)  {
        val cardsGroupedByInterviewerAndAssistant = cards.groupBy(SpreadSheetCardInfo::interviewer)
            .mapValues { it -> it.value.map { InterviewerCardInfo().convertFromSheetCard(it) } }
        for (card in cards) {
            if (!card.assistant.isNullOrBlank()) {
                if (cardsGroupedByInterviewerAndAssistant.containsKey(card.assistant)) {

                }
            }
        }
        println()

    }
}