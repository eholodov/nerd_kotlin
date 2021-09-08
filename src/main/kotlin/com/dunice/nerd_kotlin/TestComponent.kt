package com.dunice.nerd_kotlin

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
open class TestComponent {

    @Value("\${spring.data.mongodb.uri}")
    lateinit var mongoUri: String

    @EventListener()
    fun contextRefreshed(event: ContextRefreshedEvent) {
        println("test $mongoUri")
    }
}