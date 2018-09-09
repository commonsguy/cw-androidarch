package com.commonsware.onconflict

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [SimpleEntity::class], version = 1)
abstract class SimpleDatabase : RoomDatabase() {
  abstract fun store(): SimpleEntityStore

  companion object {
    fun create(context: Context, memoryOnly: Boolean): SimpleDatabase {
      return if (memoryOnly) {
        Room.inMemoryDatabaseBuilder(context, SimpleDatabase::class.java)
      }
      else {
        Room.databaseBuilder(context, SimpleDatabase::class.java, "simple.db")
      }.build()
    }
  }
}