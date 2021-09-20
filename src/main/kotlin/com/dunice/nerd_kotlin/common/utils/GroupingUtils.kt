package com.dunice.nerd_kotlin.common.utils

import com.dunice.nerd_kotlin.common.types.InterviewerCardInfo
import com.dunice.nerd_kotlin.common.types.SpreadSheetCardInfo


fun groupCardsByPerson(cards: List<SpreadSheetCardInfo>): Map<String, List<SpreadSheetCardInfo>>
        = cards.groupBy(SpreadSheetCardInfo::nameStudent)

fun groupCardsByInterviewerAndAssistant(cards: List<SpreadSheetCardInfo>): MutableMap<String, MutableList<InterviewerCardInfo>> {
        val cardsGroupedByInterviewerAndAssistant = cards.groupBy(SpreadSheetCardInfo::interviewer)
            .mapValues { it.value.map {it -> it.toInterviewCardInfo() }.toMutableList()}.toMutableMap()
        cards.forEach {
            if (!it.assistant.isNullOrBlank()) {
                val assistantCard = it.toInterviewCardInfo()
                assistantCard.isAssistant = true
                assistantCard.mainInterviewer = it.interviewer
                cardsGroupedByInterviewerAndAssistant[it.assistant]?.add(assistantCard)
            }
        }
        return cardsGroupedByInterviewerAndAssistant
    }