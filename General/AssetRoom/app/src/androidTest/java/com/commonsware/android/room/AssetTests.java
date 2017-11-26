/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _Android's Architecture Components_
 https://commonsware.com/AndroidArch
 */

package com.commonsware.android.room;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AssetTests {
  private ConstantsDatabase db;
  private Constant.Store store;

  @Before
  public void setUp() {
    db=ConstantsDatabase.get(InstrumentationRegistry.getTargetContext());
    store=db.constantsStore();
  }

  @After
  public void tearDown() {
    db.close();
    assertTrue(InstrumentationRegistry
      .getTargetContext()
      .getDatabasePath(ConstantsDatabase.DB_NAME)
      .delete());
  }

  @Test
  public void assets() {
    assertEquals(13, store.all().size());
    store.insert(new Constant("Pi", 3.1415926));
    assertEquals(14, store.all().size());
  }
}
