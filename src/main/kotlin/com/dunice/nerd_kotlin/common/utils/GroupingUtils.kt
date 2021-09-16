package com.dunice.nerd_kotlin.common.utils

import com.dunice.nerd_kotlin.common.types.InterviewerCardInfo
import com.dunice.nerd_kotlin.common.types.SpreadSheetCardInfo
import org.springframework.stereotype.Component

@Component
class GroupingUtils {

    fun groupCardsByPerson(cards: List<SpreadSheetCardInfo>): Map<String, List<SpreadSheetCardInfo>>
        = cards.groupBy(SpreadSheetCardInfo::nameStudent)

    fun groupCardsByInterviewerAndAssistant(cards: List<SpreadSheetCardInfo>)  {
        val cardsGroupedByInterviewer = cards.groupBy(SpreadSheetCardInfo::interviewer)
        val cardsGroupedByInterviewerAndAssistant = emptyMap<String, MutableList<InterviewerCardInfo>>()
        for (card in cards) {
            val interviewerCardInfo = InterviewerCardInfo(card)
            interviewerCardInfo.isAssistant = false
            if (!card.assistant.isNullOrBlank()) {
                if (cardsGroupedByInterviewerAndAssistant.containsKey(card.assistant)) {
                    //cardsGroupedByInterviewerAndAssistant[card.assistant]?.add()
                }
            }
        }
        println()

    }
}