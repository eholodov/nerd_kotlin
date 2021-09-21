package com.dunice.nerd_kotlin.common.configs

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration

@Configuration
class MongoConfig : AbstractMongoClientConfiguration() {

    @Value("\${spring.data.mongodb.database}")
    private lateinit var databaseName: String

    override fun getDatabaseName(): String = databaseName

    @Bean
    fun getDatabase() : MongoDatabase {
        val connectionString = ConnectionString("mongodb://localhost:27017/${databaseName}")
        val mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build()
        return MongoClients.create(mongoClientSettings).getDatabase(databaseName)
    }
}