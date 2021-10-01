package com.dunice.nerd_kotlin.common.jwt

import com.dunice.nerd_kotlin.common.errors.CustomException
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils.hasText
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtFilter() : OncePerRequestFilter() {

    private val AUTHORIZATION : String = "Authorization"

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token: String = getTokenFromRequest(request)
        if (token == null) {
            filterChain.doFilter(request, response)
        } else if (JavaJwtProvider().validateToken(token)) {
            filterChain.doFilter(request, response)
        }
        else {
            throw CustomException("Token is not valid")
            }
        }

    private fun getTokenFromRequest(request: HttpServletRequest): String {
        val bearer = request.getHeader(AUTHORIZATION)
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7)
        } else throw CustomException("Token is empty or doesn't have Bearer")
    }
}