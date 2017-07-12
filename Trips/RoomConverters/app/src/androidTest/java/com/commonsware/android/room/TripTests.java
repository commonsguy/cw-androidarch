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
import java.util.Date;
import java.util.List;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class TripTests {
  TripDatabase db;
  TripStore store;

  @Before
  public void setUp() {
    db=TripDatabase.create(InstrumentationRegistry.getTargetContext(), true);
    store=db.tripStore();
  }

  @After
  public void tearDown() {
    db.close();
  }

  @Test
  public void basics() {
    assertEquals(0, store.selectAll().size());

    final Trip first=new Trip("Foo", 2880, Priority.LOW, new Date());

    assertNotNull(first.id);
    assertNotEquals(0, first.id.length());
    store.insert(first);

    assertTrip(store, first);

    final Trip updated=
      new Trip(first.id, "Foo!!!", 1440, Priority.MEDIUM, first.startTime,
        first.creationTime, first.updateTime);

    store.update(updated);
    assertTrip(store, updated);

    store.delete(updated);
    assertEquals(0, store.selectAll().size());
  }

  private void assertTrip(TripStore store, Trip trip) {
    List<Trip> results=store.selectAll();

    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(areIdentical(trip, results.get(0)));

    Trip result=store.findById(trip.id);

    assertNotNull(result);
    assertTrue(areIdentical(trip, result));
  }

  private boolean areIdentical(Trip one, Trip two) {
    return(one.id.equals(two.id) &&
      one.title.equals(two.title) &&
      one.duration==two.duration &&
      one.priority==two.priority &&
      areIdentical(one.startTime, two.startTime) &&
      areIdentical(one.creationTime, two.creationTime) &&
      areIdentical(one.updateTime, two.updateTime));
  }

  private boolean areIdentical(Date one, Date two) {
    return((one==null && two==null) ||
      one!=null && one.equals(two));
  }
}
