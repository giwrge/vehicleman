package com.vehicleman.domain.model

import java.util.UUID

data class Driver(
    val driverId: String = UUID.randomUUID().toString(),
    val name: String
)
