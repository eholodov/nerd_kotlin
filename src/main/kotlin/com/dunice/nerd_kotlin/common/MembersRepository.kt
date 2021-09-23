package com.dunice.nerd_kotlin.common

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MembersRepository : MongoRepository<Member, String>{

    //fun saveAllIfNotExist(entities: Iterable<Member>)

    fun findByEmail(email: String) : Optional<Member?>

    //fun ups
}