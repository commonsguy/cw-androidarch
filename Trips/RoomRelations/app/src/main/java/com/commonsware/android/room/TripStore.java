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

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

@Dao
interface TripStore {
  /*
    Trip
   */

  @Query("SELECT * FROM trips ORDER BY title")
  List<Trip> selectAllTrips();

  @Query("SELECT * FROM trips WHERE id=:id")
  Trip findTripById(String id);

  @Insert
  void insert(Trip... trips);

  @Update
  void update(Trip... trips);

  @Delete
  void delete(Trip... trips);

  /*
    Lodging
   */

  @Query("SELECT * FROM lodgings WHERE tripId=:tripId")
  List<Lodging> findLodgingsForTrip(String tripId);

  @Insert
  void insert(Lodging... lodgings);

  @Update
  void update(Lodging... lodgings);

  @Delete
  void delete(Lodging... lodgings);

  /*
    Flight
   */

  @Query("SELECT * FROM flights WHERE tripId=:tripId")
  List<Flight> findFlightsForTrip(String tripId);

  @Insert
  void insert(Flight... flights);

  @Update
  void update(Flight... flights);

  @Delete
  void delete(Flight... flights);
}
