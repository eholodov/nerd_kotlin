package com.dunice.nerd_kotlin.common.controller

import com.dunice.nerd_kotlin.common.services.RemaindersService
import com.dunice.nerd_kotlin.common.services.remainder_service.RemaindersService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class RemainderControllerImpl(val remaindersService: RemaindersService) : RemainderController {

    override fun refreshCrons() {
        remaindersService.refreshCrons()
    }

    override fun startCrons() {
        remaindersService.startCrons()
    }

    override fun getCurrentCrons() {
        remaindersService.getCurrentCrons()
    }
}