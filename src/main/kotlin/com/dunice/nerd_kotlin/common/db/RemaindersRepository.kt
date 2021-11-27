package com.dunice.nerd_kotlin.common.db

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface RemaindersRepository : MongoRepository<RemainderDocument, String>{
}