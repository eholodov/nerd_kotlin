package com.dunice.nerd_kotlin.academyReminder

import com.dunice.nerd_kotlin.academyReminder.types.AcademyReminderTask
import com.dunice.nerd_kotlin.common.db.AcademyReminderDocument
import com.dunice.nerd_kotlin.common.db.AcademyReminderRepository
import com.dunice.nerd_kotlin.services.slack.SlackServiceImpl
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import java.util.concurrent.locks.ReentrantLock


@Service
class AcademySchedulerServiceImpl(
    val academyReminderRepository: AcademyReminderRepository,
    val mongoTemplate: MongoTemplate,
    val slackServiceImpl: SlackServiceImpl,
    val env: Environment
) {
    @Volatile var departmentScheduler = mutableMapOf<String, MutableList<AcademyReminderTask>>()
    private val lock = ReentrantLock()
    private var logger = LoggerFactory.getLogger(this.javaClass);

    fun schedule(reminders: List<AcademyReminderDocument>, department: String) {

        logger.info("-> method schedule in class {}, \n reminders {}, \n department", this.javaClass.simpleName, reminders, department)

        try {
            lock.lock()

            departmentScheduler.putIfAbsent(department, mutableListOf())
            val scheduledTasks = departmentScheduler[department]

            reminders.forEach {

                val task = AcademyReminderTask(
                    it,
                    academyReminderRepository,
                    departmentScheduler,
                    slackServiceImpl,
                    department,
                    lock
                )
                scheduledTasks!!.add(task)

                val delay = it.dateToSend.toEpochMilli() - Instant.now().toEpochMilli()
                Timer(false).schedule(task, if (delay > 0) delay else 0)
            }

        } finally {
            lock.unlock()
        }
        logger.info("<! method schedule in class {}", this.javaClass.simpleName)
    }


    fun cancelScheduledTasksByDepartment(department: String) {

        logger.info("-> method cancelScheduledTasksByDepartment in class {} \n department {}", this.javaClass.simpleName, department)

        try {
            val scheduledTasks = departmentScheduler[department]
            lock.lock()
            if (scheduledTasks !=null && scheduledTasks.isNotEmpty()) {
                scheduledTasks.forEach {
                    it.cancel()
                }
                scheduledTasks.clear()
            }
        } finally {
            lock.unlock()
        }
        logger.info("<! method cancelScheduledTasksByDepartment in class {}", this.javaClass.simpleName)
    }

    fun refreshAllReminders() {

        logger.info("-> method refreshAllReminders in class {}", this.javaClass.simpleName)

        try {
            lock.lock()
            departmentScheduler.toList().forEach {
                it.second.forEach{ it.cancel() }
                it.second.clear()
            }
            handleTasksFromDb()
        } finally {
            lock.unlock()
        }
        logger.info("<! method refreshAllReminders in class {}", this.javaClass.simpleName)
    }

    fun startCrons(department: String) {

        logger.info("-> method startCrons in class {} \n department {}", this.javaClass.simpleName, department)

        val academyReminderDocuments = academyReminderRepository.findAllByIsSentAndDepartmentAndDateToSendGreaterThan(false, department, Instant.now())

        this.schedule(academyReminderDocuments, department)

        logger.info("<! method startCrons in class {}", this.javaClass.simpleName)
    }

    @Profile("prod")
    @EventListener(classes =  [ContextRefreshedEvent::class] )
    fun handleTasksFromDb() {

        logger.info("-> method handleTasksFromDb in class {}", this.javaClass.simpleName)

        if (env.activeProfiles.contains("dev")) {
            return
        }

        val data = mongoTemplate.findDistinct(
            Query(Criteria.where("isSent").`is`(false)),
            "department",
            AcademyReminderDocument::class.java,
            String::class.java)

        data.forEach { startCrons(it) }

        logger.info("<! method handleTasksFromDb in class {}", this.javaClass.simpleName)
    }

    fun getActiveReminders(): MutableMap<String, List<Instant>> {

        logger.info("-> method getActiveReminders in class {}", this.javaClass.simpleName)

        return departmentScheduler.toList().fold(mutableMapOf()) {acc, item ->

            acc[item.first] = item.second.map { Instant.ofEpochMilli(it.scheduledExecutionTime()) }

            acc
        }
    }
}
