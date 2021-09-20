package com.dunice.nerd_kotlin.common.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import java.util.*


@Configuration
class MongoConfiguration : AbstractMongoClientConfiguration() {

    private val databaseName = "nerdTest"
    override fun getDatabaseName(): String = databaseName

    override fun mongoClient(): MongoClient {
        val connectionString = ConnectionString("mongodb://localhost:27017/${databaseName}")
        val mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build()
        return MongoClients.create(mongoClientSettings)
    }

    public override fun getMappingBasePackages(): Set<String> = Collections.singleton("com.dunice")
}
