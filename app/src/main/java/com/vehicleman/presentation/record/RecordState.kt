package com.vehicleman.presentation.record

import com.vehicleman.domain.model.Record

data class RecordState(
    val records: List<Record> = emptyList()
)
