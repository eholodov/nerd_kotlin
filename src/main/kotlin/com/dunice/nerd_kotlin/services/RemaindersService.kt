package com.dunice.nerd_kotlin.services

interface RemaindersService {
    fun startCrons()
    fun refreshCrons()
    fun getCurrentCrons()
}