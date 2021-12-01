package com.dunice.nerd_kotlin.academyReminder.types

import com.dunice.nerd_kotlin.academyReminder.AcademySchedulerServiceImpl
import com.dunice.nerd_kotlin.common.db.AcademyReminderDocument
import com.dunice.nerd_kotlin.common.db.AcademyReminderRepository
import com.dunice.nerd_kotlin.services.slack.SlackServiceImpl
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class AcademyReminderTask(
    private val academyReminderDocument: AcademyReminderDocument,
    private val academyReminderRepository: AcademyReminderRepository,
    private val departmentScheduler: MutableMap<String, MutableList<AcademyReminderTask>>,
    private val slackServiceImpl: SlackServiceImpl,
    private val department: String,
    private val lock: ReentrantLock,
) : TimerTask() {
    override fun run() {


        try {
            slackServiceImpl.postMessage(
                academyReminderDocument.slackId,
                academyReminderDocument.message
            )
            academyReminderDocument.isSent = true
            academyReminderRepository.save(academyReminderDocument)
            lock.lock()

            val elemToRemove = departmentScheduler[department]!!.find {
                it.academyReminderDocument.id == academyReminderDocument.id
            }

            departmentScheduler[department]!!.remove(elemToRemove)

        } finally {
            lock.unlock()
        }
    }

}
