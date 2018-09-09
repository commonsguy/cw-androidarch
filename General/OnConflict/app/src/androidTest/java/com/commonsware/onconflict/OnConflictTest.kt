package com.commonsware.onconflict

import android.database.sqlite.SQLiteConstraintException
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnConflictTest {
  private lateinit var db: SimpleDatabase

  @Before
  fun setUp() {
    db = SimpleDatabase.create(InstrumentationRegistry.getTargetContext(), true)
  }

  @Test(expected = SQLiteConstraintException::class)
  fun insertWithAbortSequential() {
    val entity = SimpleEntity(1, "foo")

    db.store().apply {
      insertWithAbort(entity)
      insertWithAbort(entity)
    }
  }

  @Test(expected = SQLiteConstraintException::class)
  fun insertWithAbortAtomic() {
    val entity = SimpleEntity(1, "foo")
    val entity2 = SimpleEntity(2, "foo")

    db.store().insertWithAbort(entity, entity2)
  }

  @Test(expected = SQLiteConstraintException::class)
  fun insertAndUpdateWithAbort() {
    val entity = SimpleEntity(1, "foo")
    val entity2 = SimpleEntity(2, "bar")
    val entity3 = SimpleEntity(1, "bar")

    db.store().apply {
      insertWithAbort(entity, entity2)
      updateWithAbort(entity3)
    }
  }

  @Test(expected = SQLiteConstraintException::class)
  fun insertWithFailSequential() {
    val entity = SimpleEntity(1, "foo")

    db.store().apply {
      insertWithFail(entity)
      insertWithFail(entity)
    }
  }

  @Test(expected = SQLiteConstraintException::class)
  fun insertWithFailAtomic() {
    val entity = SimpleEntity(1, "foo")
    val entity2 = SimpleEntity(2, "foo")

    db.store().insertWithFail(entity, entity2)
  }

  @Test(expected = SQLiteConstraintException::class)
  fun insertAndUpdateWithFail() {
    val entity = SimpleEntity(1, "foo")
    val entity2 = SimpleEntity(2, "bar")
    val entity3 = SimpleEntity(1, "bar")

    db.store().apply {
      insertWithFail(entity, entity2)
      updateWithFail(entity3)
    }
  }

  @Test
  fun insertWithIgnoreSequential() {
    val entity = SimpleEntity(1, "foo")

    db.store().apply {
      insertWithIgnore(entity)
      insertWithIgnore(entity)
    }
  }

  @Test
  fun insertWithIgnoreAtomic() {
    val entity = SimpleEntity(1, "foo")
    val entity2 = SimpleEntity(2, "foo")

    db.store().insertWithIgnore(entity, entity2)
  }

  @Test
  fun insertAndUpdateWithIgnore() {
    val entity = SimpleEntity(1, "foo")
    val entity2 = SimpleEntity(2, "bar")
    val entity3 = SimpleEntity(1, "bar")

    db.store().apply {
      insertWithIgnore(entity, entity2)
      updateWithIgnore(entity3)
    }
  }

  @Test
  fun insertWithReplaceSequential() {
    val entity = SimpleEntity(1, "foo")

    db.store().apply {
      insertWithReplace(entity)
      insertWithReplace(entity)
    }
  }

  @Test
  fun insertWithReplaceAtomic() {
    val entity = SimpleEntity(1, "foo")
    val entity2 = SimpleEntity(2, "foo")

    db.store().insertWithReplace(entity, entity2)
  }

  @Test
  fun insertAndUpdateWithReplace() {
    val entity = SimpleEntity(1, "foo")
    val entity2 = SimpleEntity(2, "bar")
    val entity3 = SimpleEntity(1, "bar")

    db.store().apply {
      insertWithReplace(entity, entity2)
      updateWithReplace(entity3)
    }
  }

  @Test(expected = SQLiteConstraintException::class)
  fun insertWithRollbackSequential() {
    val entity = SimpleEntity(1, "foo")

    db.store().apply {
      insertWithRollback(entity)
      insertWithRollback(entity)
    }
  }

  @Test(expected = SQLiteConstraintException::class)
  fun insertWithRollbackAtomic() {
    val entity = SimpleEntity(1, "foo")
    val entity2 = SimpleEntity(2, "foo")

    db.store().insertWithRollback(entity, entity2)
  }

  @Test(expected = SQLiteConstraintException::class)
  fun insertAndUpdateWithRollback() {
    val entity = SimpleEntity(1, "foo")
    val entity2 = SimpleEntity(2, "bar")
    val entity3 = SimpleEntity(1, "bar")

    db.store().apply {
      insertWithRollback(entity, entity2)
      updateWithRollback(entity3)
    }
  }

  @Test
  fun updateSeveralWithAbort() {
    val entities = arrayOf(
      SimpleEntity(1, "foo"),
      SimpleEntity(2, "bar"),
      SimpleEntity(3, "goo"),
      SimpleEntity(4, "baz"),
      SimpleEntity(5, "who")
    )

    val all = db.store().let {
      it.insertWithAbort(*entities)

      try {
        it.updateSeveralWithAbort("other")
      } catch (ex: SQLiteConstraintException) {
        // expected
      }

      it.selectAll()
    }

    for (i in 0..4) {
      assertEquals(all[i], entities[i])
    }
  }

  @Test
  fun updateSeveralWithFail() {
    val entities = arrayOf(
      SimpleEntity(1, "foo"),
      SimpleEntity(2, "bar"),
      SimpleEntity(3, "goo"),
      SimpleEntity(4, "baz"),
      SimpleEntity(5, "who")
    )

    val all = db.store().let {
      it.insertWithFail(*entities)

      try {
        it.updateSeveralWithFail("other")
      } catch (ex: SQLiteConstraintException) {
        // expected
      }

      it.selectAll()
    }

    val count = all.map { it.message }.count { it == "other" }

    assertEquals(1, count)
  }

  @Test
  fun insertThreeWithAbort() {
    val entity = SimpleEntity(1, "foo")
    val entity2 = SimpleEntity(2, "foo")
    val entity3 = SimpleEntity(3, "bar")

    try {
      db.store().insertThreeWithAbort(entity, entity2, entity3)
    }
    catch (ex: SQLiteConstraintException) {
      // expected
    }

    assertEquals(2, db.store().selectAll().size)
  }

  @Test
  fun insertThreeWithFail() {
    val entity = SimpleEntity(1, "foo")
    val entity2 = SimpleEntity(2, "foo")
    val entity3 = SimpleEntity(3, "bar")

    try {
      db.store().insertThreeWithFail(entity, entity2, entity3)
    }
    catch (ex: SQLiteConstraintException) {
      // expected
    }

    assertEquals(2, db.store().selectAll().size)
  }
}
