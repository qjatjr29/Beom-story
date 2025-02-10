package com.beomsic.imageservice.application.port.`in`.usecase

interface DeleteUseCase {
    suspend fun deleteImage(imageUrl: String)
}