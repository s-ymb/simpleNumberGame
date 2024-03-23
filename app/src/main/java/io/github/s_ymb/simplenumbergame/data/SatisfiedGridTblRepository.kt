package io.github.s_ymb.simplenumbergame.data

import kotlinx.coroutines.flow.Flow

interface SatisfiedGridTblRepository {
    fun getAllGrids(): Flow<List<SatisfiedGridTbl>>

    // Flow 以外で戻したいため
    fun getAll(): List<SatisfiedGridTbl>

    fun getGrid(data: String): Flow<SatisfiedGridTbl?>

    fun get(data: String): SatisfiedGridTbl?

    fun getCnt(data: String): Int

    suspend fun insert(satisfiedGridTbl: SatisfiedGridTbl)

    fun delete(data: String)
}