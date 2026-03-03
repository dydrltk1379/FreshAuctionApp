package com.example.freshauctionapp.model

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/* 데이터베이스 액세스를 위한 Dao 인터페이스 */
@Dao
interface FreshDao {
    //Fresh 테이블에 경락가격정보 저장
    @Insert
    fun insertFresh(freshData: List<FreshData>)

    // SaveItem 테이블에 경락가격 저장
    @Insert
    fun insertSave(saveItem: SaveItem): Long

    //DataSource 반환(SaveItem)
    @Query("SELECT * FROM SaveItem")
    fun loadSaveItems(): DataSource.Factory<Int, SaveItem>

    //DataSource 반환(Fresh(FreshData))
    @Query("SELECT * FROM Fresh WHERE saveId = :saveId")
    fun loadFreshData(saveId: Long): DataSource.Factory<Int, FreshData>

    //DELETE Query(SaveItem)
    @Query("DELETE FROM SaveItem WHERE id = :saveId")
    fun deleteSaveData(saveId: Long)
}
