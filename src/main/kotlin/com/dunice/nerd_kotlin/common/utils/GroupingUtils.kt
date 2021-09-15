package com.dunice.nerd_kotlin.common.utils

import com.dunice.nerd_kotlin.common.types.SpreadSheetCardInfo
import org.springframework.stereotype.Component

@Component
class GroupingUtils {

    fun groupCardsByPerson(cards: List<SpreadSheetCardInfo>): Map<String, List<SpreadSheetCardInfo>>
        = cards.groupBy(SpreadSheetCardInfo::nameStudent)

    fun groupCardsByInterviewerAndAssistant(cards: List<SpreadSheetCardInfo>)  {
        val cardsGroupedByInterviewer = cards.groupBy(SpreadSheetCardInfo::interviewer)
        println()

    }
}