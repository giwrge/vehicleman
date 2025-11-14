package com.vehicleman.domain.use_case

import javax.inject.Inject

data class RecordUseCases @Inject constructor(
    val getRecords: GetRecords,
    val getRecord: GetRecord,
    val saveRecord: SaveRecord,
    val deleteRecord: DeleteRecord,
    val getLatestOdometer: GetLatestOdometer,
    val populateDatabaseWithFakeDataUseCase: PopulateDatabaseWithFakeDataUseCase
)
