package com.dunice.nerd_kotlin.common.utils

import com.dunice.nerd_kotlin.common.types.SpreadSheetCardInfo
import org.springframework.stereotype.Component

@Component
class GroupingUtils {

    fun groupCardsByPerson(cards: List<SpreadSheetCardInfo>): MutableMap<String, List<String>> {
        val groupedList = emptyMap<String, List<String>>().toMutableMap()
        for (card in cards) {
            val info = arrayListOf<String>(card.department, card.subject, card.interviewer, card.date, card.time, card.room, card.assistant!!)
            if (groupedList.containsKey(card.nameStudent)) {
                groupedList[card.nameStudent] = info
            }
            else {
                groupedList[card.nameStudent] = info
            }
        }
        return groupedList
    }
}