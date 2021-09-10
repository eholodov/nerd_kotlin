package com.dunice.nerd_kotlin

import com.dunice.nerd_kotlin.common.services.GoogleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File
import java.io.FileReader
import java.nio.file.Path
import javax.annotation.PostConstruct
import kotlin.io.path.Path

@SpringBootApplication

class NerdKotlinApplication(@Autowired val googleService: GoogleService) {

	@PostConstruct
	fun init() {
		this.printData()
	}
	fun printData() {
		googleService.createCredentials()
		val dataSheet = googleService.readData()
		for (i in 0 until dataSheet!![0].size) {
			for (j in 0 until dataSheet.size) {
				println(dataSheet[j][i])
			}
			println("--------------------------")
		}
	}

}

fun main(args: Array<String>) {
	runApplication<NerdKotlinApplication>(*args)
}
