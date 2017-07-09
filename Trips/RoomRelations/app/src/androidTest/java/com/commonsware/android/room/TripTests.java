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
    assertEquals(0, store.selectAllTrips().size());

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
    assertEquals(0, store.selectAllTrips().size());
  }

  @Test
  public void lodging() {
    assertEquals(0, store.selectAllTrips().size());

    final Trip trip=new Trip("Foo", 2880, Priority.LOW, new Date());

    assertNotNull(trip.id);
    assertNotEquals(0, trip.id.length());
    store.insert(trip);
    assertTrip(store, trip);

    final Lodging first=
      new Lodging("Hotel Von", 2880, Priority.MEDIUM, new Date(),
        "1313 Mockingbird Lane", trip.id);

    assertNotNull(first.id);
    assertNotEquals(0, first.id.length());
    store.insert(first);
    assertLodging(store, first);

    store.delete(trip);
    assertEquals(0, store.findLodgingsForTrip(trip.id).size());
  }

  @Test
  public void flights() {
    assertEquals(0, store.selectAllTrips().size());

    final Trip trip=new Trip("Foo", 2880, Priority.LOW, new Date());

    assertNotNull(trip.id);
    assertNotEquals(0, trip.id.length());
    store.insert(trip);
    assertTrip(store, trip);

    final Flight first=
      new Flight("Northeast Airlines", 185, Priority.HIGH, new Date(),
        "PHL", "MCO", "NEA", "1734", "26B", trip.id);

    assertNotNull(first.id);
    assertNotEquals(0, first.id.length());
    store.insert(first);
    assertFlight(store, first);

    store.delete(trip);
    assertEquals(0, store.findFlightsForTrip(trip.id).size());
  }

  private void assertTrip(TripStore store, Trip trip) {
    List<Trip> results=store.selectAllTrips();

    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(areIdentical(trip, results.get(0)));

    Trip result=store.findTripById(trip.id);

    assertNotNull(result);
    assertTrue(areIdentical(trip, result));
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

  private void assertLodging(TripStore store, Lodging lodging) {
    List<Lodging> results=store.findLodgingsForTrip(lodging.tripId);

    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(areLodgingsIdentical(lodging, results.get(0)));
  }

  private boolean areLodgingsIdentical(Lodging one, Lodging two) {
    return(one.tripId.equals(two.tripId) &&
      one.address.equals(two.address) &&
      areIdentical(one, two));
  }

  private void assertFlight(TripStore store, Flight flight) {
    List<Flight> results=store.findFlightsForTrip(flight.tripId);

    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(areFlightsIdentical(flight, results.get(0)));
  }

  private boolean areFlightsIdentical(Flight one, Flight two) {
    return(one.tripId.equals(two.tripId) &&
      one.departingAirport.equals(two.departingAirport) &&
      one.arrivingAirport.equals(two.arrivingAirport) &&
      one.airlineCode.equals(two.airlineCode) &&
      one.flightNumber.equals(two.flightNumber) &&
      one.seatNumber.equals(two.seatNumber) &&
      areIdentical(one, two));
  }
}
