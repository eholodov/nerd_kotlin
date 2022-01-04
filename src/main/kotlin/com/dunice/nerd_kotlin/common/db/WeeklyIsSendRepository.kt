package com.dunice.nerd_kotlin.common.db

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WeeklyIsSendRepository : MongoRepository<WeeklyIsSendDocument, String> {

    fun findOneByWeekNumber(weekNumber: String) : Optional<WeeklyIsSendDocument>

}