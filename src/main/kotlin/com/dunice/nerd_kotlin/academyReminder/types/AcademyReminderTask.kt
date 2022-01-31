package com.dunice.nerd_kotlin.academyReminder.types

import com.dunice.nerd_kotlin.common.db.AcademyReminderDocument
import com.dunice.nerd_kotlin.common.db.AcademyReminderRepository
import com.dunice.nerd_kotlin.services.slack.SlackServiceImpl
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class AcademyReminderTask(
    private val academyReminderDocument: AcademyReminderDocument,
    private val academyReminderRepository: AcademyReminderRepository,
    private val departmentScheduler: MutableMap<String, MutableList<AcademyReminderTask>>,
    private val slackServiceImpl: SlackServiceImpl,
    private val department: String,
    private val requestId: String,
    private val lock: ReentrantLock,
) : TimerTask() {

    private val log = LoggerFactory.getLogger(this.javaClass.simpleName)

    override fun run() {

        MDC.put("requestId", requestId.ifEmpty { "IsEmpty" })
        log.info("->")

        try {
            log.info("-> postMessage")
            slackServiceImpl.postMessage(
                academyReminderDocument.slackId,
                academyReminderDocument.message
            )
            log.info(">! postMessage")
            academyReminderDocument.isSent = true
            log.info("-> saveInRepository")
            academyReminderRepository.save(academyReminderDocument)
            log.info(">! saveInRepository")
            lock.lock()

            val elemToRemove = departmentScheduler[department]!!.find {
                it.academyReminderDocument.id == academyReminderDocument.id
            }

            departmentScheduler[department]!!.remove(elemToRemove)

        } finally {
            lock.unlock()
        }
        log.info("<!")
    }

}
