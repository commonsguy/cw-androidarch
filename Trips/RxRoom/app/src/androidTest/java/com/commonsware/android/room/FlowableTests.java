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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import io.reactivex.functions.Consumer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class FlowableTests {
  private static final String TEST_ID="testid";
  TripDatabase db;
  TripStore store;
  CountDownLatch latch;
  Trip foundTrip;
  List<Trip> foundTrips;

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
  public void basics() throws InterruptedException {
    assertEquals(0, store.flowAllTrips().blockingFirst().size());

    latch=new CountDownLatch(1);
    store.flowTripById(TEST_ID).subscribe(trip -> {
      foundTrip=trip;
      latch.countDown();
    });

    assertFalse("Should have timed out!", latch.await(100, TimeUnit.MILLISECONDS));

    final Trip first=new Trip(TEST_ID, "Foo", 2880, Priority.LOW, new Date());

    assertNotNull(first.id);
    assertNotEquals(0, first.id.length());
    store.insert(first);

    assertTrue("Should not have timed out!", latch.await(1, TimeUnit.SECONDS));
    assertNotNull(foundTrip);
    assertTrue(areIdentical(first, foundTrip));
    assertEquals(1, store.flowAllTrips().blockingFirst().size());

    latch=new CountDownLatch(1);
    foundTrip=null;

    final Trip updated=
      new Trip(first.id, "Foo!!!", 1440, Priority.MEDIUM, first.startTime,
        first.creationTime, first.updateTime);

    store.update(updated);
    assertTrue("Should not have timed out!", latch.await(1, TimeUnit.SECONDS));
    assertNotNull(foundTrip);
    assertTrue(areIdentical(updated, foundTrip));
    assertEquals(1, store.flowAllTrips().blockingFirst().size());

    latch=new CountDownLatch(1);
    foundTrip=null;
    store.delete(updated);
    assertFalse("Should have timed out!", latch.await(100, TimeUnit.MILLISECONDS));
    assertEquals(0, store.flowAllTrips().blockingFirst().size());
  }

  @Test
  public void liveList() throws InterruptedException {
    latch=new CountDownLatch(1);
    store.flowAllTrips().subscribe(trips -> {
      foundTrips=trips;
      latch.countDown();
    });

    assertTrue("Should not have timed out!", latch.await(1, TimeUnit.SECONDS));
    assertEquals(0, foundTrips.size());
    foundTrips=null;

    final Trip first=new Trip(TEST_ID, "Foo", 2880, Priority.LOW, new Date());

    assertNotNull(first.id);
    assertNotEquals(0, first.id.length());

    latch=new CountDownLatch(1);
    store.insert(first);
    assertTrue("Should not have timed out!", latch.await(1, TimeUnit.SECONDS));
    assertEquals(1, foundTrips.size());
    assertTrue(areIdentical(first, foundTrips.get(0)));
    foundTrips=null;

    final Trip updated=
      new Trip(first.id, "Foo!!!", 1440, Priority.MEDIUM, first.startTime,
        first.creationTime, first.updateTime);

    latch=new CountDownLatch(1);
    store.update(updated);
    assertTrue("Should not have timed out!", latch.await(1, TimeUnit.SECONDS));
    assertEquals(1, foundTrips.size());
    assertTrue(areIdentical(updated, foundTrips.get(0)));
    foundTrips=null;

    latch=new CountDownLatch(1);
    store.delete(updated);
    assertTrue("Should not have timed out!", latch.await(1, TimeUnit.SECONDS));
    assertEquals(0, foundTrips.size());
  }

  private boolean areIdentical(Plan one, Plan two) {
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
