package com.beomsic.placeservice.application.port.out

interface ImageRollbackOutboxPort {
    suspend fun saveImageRollbackMessage(placeId: Long, imageUrl: String)
}