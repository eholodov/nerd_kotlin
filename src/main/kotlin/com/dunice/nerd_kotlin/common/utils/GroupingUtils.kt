package com.dunice.nerd_kotlin.common.utils

import com.dunice.nerd_kotlin.common.types.SpreadSheetCardInfo
import org.springframework.stereotype.Component

@Component
class GroupingUtils {

    fun groupCardsByPerson(cards: List<SpreadSheetCardInfo>): MutableMap<String, SpreadSheetCardInfo> {
        val groupedList = emptyMap<String, SpreadSheetCardInfo>().toMutableMap()
        for (card in cards) {
            groupedList[card.nameStudent] = card
        }
        return groupedList
    }
}