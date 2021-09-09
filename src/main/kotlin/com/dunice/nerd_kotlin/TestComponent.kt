package com.dunice.nerd_kotlin

import com.dunice.nerd_kotlin.common.services.GoogleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class TestComponent {

    @Value("\${spring.data.mongodb.uri}")
    lateinit var mongoUri: String


    @Autowired
    lateinit var googleService: GoogleService

    @EventListener()
    fun contextRefreshed(event: ContextRefreshedEvent) {
        googleService.getSpreadSheetId()
        println("test")
    }
}