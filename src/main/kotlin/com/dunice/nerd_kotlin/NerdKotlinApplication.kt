package com.dunice.nerd_kotlin

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File
import java.io.FileReader
import java.nio.file.Path
import javax.annotation.PostConstruct
import kotlin.io.path.Path

@SpringBootApplication
class NerdKotlinApplication {

	@Value("\${service.account.path}")
	val filePath: String = ""

	@PostConstruct
	fun printTest() {
	}

}

fun main(args: Array<String>) {
	runApplication<NerdKotlinApplication>(*args)
}
