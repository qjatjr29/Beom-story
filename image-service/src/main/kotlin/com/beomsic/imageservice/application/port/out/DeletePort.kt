package com.beomsic.imageservice.application.port.out

interface DeletePort {
    suspend fun deleteImage(imageUrl: String)
}