package com.beomsic.common.application.event

interface EventHandler<in EVENT> {
    fun eventType(): Class<*>
    fun handle(event: EVENT)
}