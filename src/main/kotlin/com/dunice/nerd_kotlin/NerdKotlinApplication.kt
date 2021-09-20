package com.dunice.nerd_kotlin


import com.dunice.nerd_kotlin.common.services.GoogleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NerdKotlinApplication() {}



fun main(args: Array<String>) {
	runApplication<NerdKotlinApplication>(*args)
}
