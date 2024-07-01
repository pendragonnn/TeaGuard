package com.example.teaguard.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.teaguard.data.local.entity.HistoryDiagnose
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDiagnoseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(historyDiagnose: HistoryDiagnose)

    @Update
    suspend fun update(historyDiagnose: HistoryDiagnose)

    @Delete
    suspend fun delete(historyDiagnose: HistoryDiagnose)

    @Query("SELECT * FROM historydiagnose ORDER BY id DESC")
    fun getAllHistory(): Flow<List<HistoryDiagnose>>
}
