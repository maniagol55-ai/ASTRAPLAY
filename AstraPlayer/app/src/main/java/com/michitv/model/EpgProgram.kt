package com.michitv.model

data class EpgProgram(
    val channelName: String,
    val title: String,
    val startTime: String,
    val endTime: String,
    val description: String = ""
)
