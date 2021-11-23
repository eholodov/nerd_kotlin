package com.dunice.nerd_kotlin.common.db

import com.google.api.client.util.DateTime
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface RemaindersRepository : MongoRepository<RemainderDocument, String>{
}