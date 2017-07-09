/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MigrationTests {
  private static final String DB_NAME="MigrationTests.db";

  @Rule
  public MigrationTestHelper helper;

  @Before
  public void setUp() {
    helper=new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
      TripDatabase.class.getCanonicalName(),
      new FrameworkSQLiteOpenHelperFactory());
  }

  @Test
  public void test1To2() throws IOException {
    SupportSQLiteDatabase db=helper.createDatabase(DB_NAME, 1);

    db.execSQL("INSERT INTO trips (title) VALUES (NULL)");

    final Cursor firstResults=db.query("SELECT COUNT(*) FROM trips");

    assertEquals(1, firstResults.getCount());
    firstResults.moveToFirst();
    assertEquals(1, firstResults.getInt(0));

    firstResults.close();
    db.close();

    db=helper.runMigrationsAndValidate(DB_NAME, 2, true,
      Migrations.BROKEN_1_TO_2);

    final Cursor secondResults=db.query("SELECT COUNT(*) FROM trips");

    assertEquals(1, secondResults.getCount());
    secondResults.moveToFirst();
    assertEquals(1, secondResults.getInt(0));

    final Cursor lodgingResults=db.query("SELECT COUNT(*) FROM lodgings");

    assertEquals(1, lodgingResults.getCount());
    lodgingResults.moveToFirst();
    assertEquals(0, lodgingResults.getInt(0));

    final Cursor flightResults=db.query("SELECT COUNT(*) FROM flights");

    assertEquals(1, flightResults.getCount());
    flightResults.moveToFirst();
    assertEquals(0, flightResults.getInt(0));
  }
}
