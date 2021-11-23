package com.dunice.nerd_kotlin.reminder

interface RemaindersService {
    fun startCrons()
    fun refreshCrons()
    fun getCurrentCrons()
}