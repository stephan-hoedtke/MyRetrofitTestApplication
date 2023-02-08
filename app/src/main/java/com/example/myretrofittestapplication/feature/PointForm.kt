package com.example.myretrofittestapplication.feature

import java.time.ZonedDateTime

data class PointForm(
    val id: Long,
    val name: String,
    val description: String,
    val creationDate: ZonedDateTime,
    val creatorName: String,
)

