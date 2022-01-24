package com.dunice.nerd_kotlin.academyReminder

import com.dunice.nerd_kotlin.academyReminder.types.AcademyReminderTask
import com.dunice.nerd_kotlin.common.Logger
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
import javax.servlet.http.HttpServletRequest


@Service
class AcademySchedulerServiceImpl(
    val academyReminderRepository: AcademyReminderRepository,
    val mongoTemplate: MongoTemplate,
    val slackServiceImpl: SlackServiceImpl,
    val env: Environment,
//    val simpleLogger: Logger
//    val httpServletRequest: HttpServletRequest
) {
    @Volatile var departmentScheduler = mutableMapOf<String, MutableList<AcademyReminderTask>>()
    private val lock = ReentrantLock()
//    private var logger = LoggerFactory.getLogger(this.javaClass);

    fun schedule(reminders: List<AcademyReminderDocument>, department: String) {

//        simpleLogger.logStart("-> method schedule with header {} in class {} \n data {}",
//            this.javaClass.simpleName, reminders, department)
//        logger.info("-> method schedule with header {} \n reminders {}, \n department",
//        httpServletRequest.getHeader("requestId"), reminders, department)

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
//        simpleLogger.logFinish("-> method schedule with header {} in class {}",
//            this.javaClass.simpleName)
//        logger.info("<! method schedule in class")
    }


    fun cancelScheduledTasksByDepartment(department: String) {

//        simpleLogger.logStart("-> method cancelScheduledTasksByDepartment with header {} in class {} \n data {}",
//            this.javaClass.simpleName, department)
//        logger.info("-> method cancelScheduledTasksByDepartment with header {} \n department {}",
//            httpServletRequest.getHeader("requestId"), department)

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
//        simpleLogger.logFinish("-> method cancelScheduledTasksByDepartment with header {} in class {}",
//            this.javaClass.simpleName)
//        logger.info("<! method cancelScheduledTasksByDepartment")
    }

    fun refreshAllReminders() {

//        simpleLogger.logFinish("-> method refreshAllReminders with header {} in class {}",
//            this.javaClass.simpleName)
//        logger.info("-> method refreshAllReminders with header {}", httpServletRequest.getHeader("requestId"))

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
//        simpleLogger.logFinish("-> method refreshAllReminders with header {} in class {}",
//            this.javaClass.simpleName)
//        logger.info("<! method refreshAllReminders")
    }

    fun startCrons(department: String) {

//        simpleLogger.logStart("-> method startCrons with header {} in class {} \n data {}",
//            this.javaClass.simpleName, department)
//        logger.info("-> method startCrons with header {} \n department {}", httpServletRequest.getHeader("requestId"), department)

        val academyReminderDocuments = academyReminderRepository.findAllByIsSentAndDepartmentAndDateToSendGreaterThan(false, department, Instant.now())

        this.schedule(academyReminderDocuments, department)

//        simpleLogger.logFinish("-> method startCrons with header {} in class {}",
//            this.javaClass.simpleName)
//        logger.info("<! method startCrons")
    }

    @Profile("prod")
    @EventListener(classes =  [ContextRefreshedEvent::class] )
    fun handleTasksFromDb() {

//        simpleLogger.logFinish("-> method handleTasksFromDb with header {} in class {}",
//            this.javaClass.simpleName)
//        logger.info("-> method handleTasksFromDb with header {}", httpServletRequest.getHeader("requestId"))

        if (env.activeProfiles.contains("dev")) {
            return
        }

        val data = mongoTemplate.findDistinct(
            Query(Criteria.where("isSent").`is`(false)),
            "department",
            AcademyReminderDocument::class.java,
            String::class.java)

        data.forEach { startCrons(it) }

//        simpleLogger.logFinish("-> method handleTasksFromDb with header {} in class {}",
//            this.javaClass.simpleName)
    }

    fun getActiveReminders(): MutableMap<String, List<Instant>> {

//        simpleLogger.logFinish("->!< method getActiveReminders with header {} in class {}",
//            this.javaClass.simpleName)
//        logger.info("-> method getActiveReminders with header {} <!", httpServletRequest.getHeader("requestId"))

        return departmentScheduler.toList().fold(mutableMapOf()) {acc, item ->

            acc[item.first] = item.second.map { Instant.ofEpochMilli(it.scheduledExecutionTime()) }

            acc
        }
    }
}
