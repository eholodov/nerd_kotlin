package com.dunice.nerd_kotlin.common

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MembersRepository : MongoRepository<Member, String>{

    //fun saveAllIfNotExist(entities: Iterable<Member>)

    fun getByEmail(email: String) : Member?

    //fun ups
}