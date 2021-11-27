package com.dunice.nerd_kotlin.common.db

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface AcademyReminderRepository : MongoRepository<AcademyReminderDocument, String> {
    fun deleteAllByIsSentAndDepartment(isSent: Boolean, department: String) : Long


    fun findAllByIsSentAndDepartmentAndDateToSendGreaterThan(isSent: Boolean, department: String, now: Instant) : List<AcademyReminderDocument>
}
