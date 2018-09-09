package com.commonsware.onconflict

import android.arch.persistence.room.*

@Entity(indices = [(Index(value = ["message"], unique = true))])
data class SimpleEntity(@PrimaryKey(autoGenerate = false) val id: Int, val message: String)

@Dao
interface SimpleEntityStore {
  @Insert(onConflict = OnConflictStrategy.ABORT)
  fun insertWithAbort(vararg entities: SimpleEntity)

  @Insert(onConflict = OnConflictStrategy.FAIL)
  fun insertWithFail(vararg entities: SimpleEntity)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun insertWithIgnore(vararg entities: SimpleEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertWithReplace(vararg entities: SimpleEntity)

  @Insert(onConflict = OnConflictStrategy.ROLLBACK)
  fun insertWithRollback(vararg entities: SimpleEntity)

  @Update(onConflict = OnConflictStrategy.ABORT)
  fun updateWithAbort(vararg entities: SimpleEntity)

  @Update(onConflict = OnConflictStrategy.FAIL)
  fun updateWithFail(vararg entities: SimpleEntity)

  @Update(onConflict = OnConflictStrategy.IGNORE)
  fun updateWithIgnore(vararg entities: SimpleEntity)

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun updateWithReplace(vararg entities: SimpleEntity)

  @Update(onConflict = OnConflictStrategy.ROLLBACK)
  fun updateWithRollback(vararg entities: SimpleEntity)

  @Query("UPDATE OR ABORT SimpleEntity SET message = :value")
  fun updateSeveralWithAbort(value: String)

  @Query("UPDATE OR FAIL SimpleEntity SET message = :value")
  fun updateSeveralWithFail(value: String)

  @Query("SELECT * FROM SimpleEntity ORDER BY id")
  fun selectAll(): List<SimpleEntity>

  @Transaction
  fun insertThreeWithAbort(entity1: SimpleEntity, entity2: SimpleEntity, entity3: SimpleEntity) {
    insertWithAbort(entity1)
    insertWithAbort(entity2)
    insertWithAbort(entity3)
  }

  @Transaction
  fun insertThreeWithFail(entity1: SimpleEntity, entity2: SimpleEntity, entity3: SimpleEntity) {
    insertWithFail(entity1)
    insertWithFail(entity2)
    insertWithFail(entity3)
  }
}