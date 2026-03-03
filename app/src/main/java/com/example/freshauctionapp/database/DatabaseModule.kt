package com.example.freshauctionapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.freshauctionapp.model.FreshDao
import com.example.freshauctionapp.model.FreshData
import com.example.freshauctionapp.model.SaveItem

/* 데이터베이스 클래스 선언 */
@Database(entities = [FreshData::class, SaveItem::class], version = 1)
abstract class DatabaseModule : RoomDatabase() {

    /* DAO 클래스의 인스턴스를 반환하는 추상 메서드를 정의. */
    abstract fun freshDao(): FreshDao

    /* 데이터베이스 객체를 생성(Singleton)하여 반환하는 getDatabase() 함수 및
       변수들을 전역으로 선언 */
    companion object {
        // database 변수 선언
        private var database: DatabaseModule? = null

        //database 이름 상수 선언
        private const val ROOM_DB = "room.db"

        /* 데이터베이스 객체를 싱글톤으로 생성하여 반환하는 함수 */
        fun getDatabase(context: Context): DatabaseModule {
            if (database == null) {
                database = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseModule::class.java, ROOM_DB
                    /* allowMainThreadQueries(): MainThread에서 Room 사용 허용(원칙적으로 안됨)
                     fallbackToDestructiveMigration(): 기존 DB 삭제(실제 서비스에서는 사용금지) */
                ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
            }
            /* 안전한 강제 캐스팅 */
            return database!!
        }
    }
}
