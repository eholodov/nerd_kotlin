package com.dunice.nerd_kotlin.common.utils

import com.dunice.nerd_kotlin.common.types.SpreadSheetCardInfo
import org.springframework.stereotype.Component

@Component
class GroupingUtils {

    fun groupCardsByPerson(cards: List<SpreadSheetCardInfo>): MutableMap<String, MutableList<SpreadSheetCardInfo>> {
        val groupedList = emptyMap<String, MutableList<SpreadSheetCardInfo>>().toMutableMap()
        for (card in cards) {
            if (groupedList.containsKey(card.nameStudent)){
                groupedList[card.nameStudent]?.add(card)
            }
            else {
                groupedList[card.nameStudent] = mutableListOf(card)
            }
        }
        return groupedList
    }
}