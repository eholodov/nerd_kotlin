package com.dunice.nerd_kotlin.common.db

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WeeklyIsSendRepository : MongoRepository<WeeklySentDocument, String> {

    fun findOneByWeekNumberAndDepartment(weekNumber: String, department: String) : Optional<WeeklySentDocument>

    fun findAllByDepartment(department: String) : List<WeeklySentDocument>

}