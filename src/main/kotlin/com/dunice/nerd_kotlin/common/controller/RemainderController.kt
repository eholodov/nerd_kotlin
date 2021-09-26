package com.dunice.nerd_kotlin.common.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping
interface RemainderController {

    @PostMapping
    fun refreshCrons()

    @PostMapping
    fun startCrons()

    @GetMapping
    fun getCurrentCrons()

}