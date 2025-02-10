package com.beomsic.imageservice.adapter.out.external.event

data class UploadedImageEvent (
    val placeId: Long,
    val filename: String,
    val fileUrl: String,
)