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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

public class TripUnitTests {
  private TripStore store;

  @Before
  public void setUp() {
    store=Mockito.mock(TripStore.class);

    final HashMap<String, Trip> trips=new HashMap<>();

    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        ArrayList<Trip> result=new ArrayList<>(trips.values());

        Collections.sort(result, new Comparator<Trip>() {
          @Override
          public int compare(Trip left, Trip right) {
            return(left.title.compareTo(right.title));
          }
        });

        return(result);
      }
    }).when(store).selectAll();

    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        String id=(String)invocation.getArguments()[0];

        return(trips.get(id));
      }
    }).when(store).findById(any(String.class));

    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        for (Object rawTrip : invocation.getArguments()) {
          Trip trip=(Trip)rawTrip;

          trips.put(trip.id, trip);
        }

        return(null);
      }
    }).when(store).insert(Matchers.<Trip>anyVararg());

    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        for (Object rawTrip : invocation.getArguments()) {
          Trip trip=(Trip)rawTrip;

          trips.put(trip.id, trip);
        }

        return(null);
      }
    }).when(store).update(Matchers.<Trip>anyVararg());

    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        for (Object rawTrip : invocation.getArguments()) {
          Trip trip=(Trip)rawTrip;

          trips.remove(trip.id);
        }

        return(null);
      }
    }).when(store).delete(Matchers.<Trip>anyVararg());
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
