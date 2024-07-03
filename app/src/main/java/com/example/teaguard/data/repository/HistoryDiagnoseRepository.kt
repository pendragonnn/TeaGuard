package com.example.teaguard.data.repository

import com.example.teaguard.data.local.entity.HistoryDiagnose
import com.example.teaguard.data.local.room.HistoryDiagnoseDao
import kotlinx.coroutines.flow.Flow

class HistoryDiagnoseRepository private constructor(
    private val historyDiagnoseDao: HistoryDiagnoseDao
) {

    fun getAllHistory(): Flow<List<HistoryDiagnose>> = historyDiagnoseDao.getAllHistory()

    suspend fun insert(historyDiagnose: HistoryDiagnose) {
        historyDiagnoseDao.insert(historyDiagnose)
    }

    suspend fun update(historyDiagnose: HistoryDiagnose) {
        historyDiagnoseDao.update(historyDiagnose)
    }

    suspend fun delete(historyDiagnose: HistoryDiagnose) {
        historyDiagnoseDao.delete(historyDiagnose)
    }


    companion object {
        @Volatile
        private var instance: HistoryDiagnoseRepository? = null

        fun getInstance(historyDiagnoseDao: HistoryDiagnoseDao): HistoryDiagnoseRepository =
            instance ?: synchronized(this) {
                instance ?: HistoryDiagnoseRepository(historyDiagnoseDao).also { instance = it }
            }
    }
}
