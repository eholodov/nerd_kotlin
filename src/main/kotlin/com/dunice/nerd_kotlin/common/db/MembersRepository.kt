package com.dunice.nerd_kotlin.common.db

import com.dunice.nerd_kotlin.common.aop.RepositoryProxy
import com.dunice.nerd_kotlin.common.db.projections.SlackIdFullNameProjection
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MembersRepository : MongoRepository<MemberDocument, String>{

    fun findOneByEmail(email: String) : Optional<MemberDocument>

    fun existsByEmail(email: String) : Boolean

    @RepositoryProxy
    fun findByFullNameIn(names: List<String>): List<SlackIdFullNameProjection>
}