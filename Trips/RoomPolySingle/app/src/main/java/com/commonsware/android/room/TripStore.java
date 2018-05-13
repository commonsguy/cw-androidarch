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
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;
import java.util.ArrayList;
import java.util.List;

@Dao
abstract class TripStore {
  /*
    Trip
   */

  @Query("SELECT * FROM trips ORDER BY title")
  abstract List<Trip> selectAllTrips();

  @Query("SELECT * FROM trips WHERE id=:id")
  abstract Trip findTripById(String id);

  @Insert
  abstract void insert(Trip... trips);

  @Update
  abstract void update(Trip... trips);

  @Delete
  abstract void delete(Trip... trips);

  /*
    Lodging
   */

  @Query("SELECT * FROM lodgings WHERE tripId=:tripId")
  abstract List<Lodging> findLodgingsForTrip(String tripId);

  @Insert
  abstract void insert(Lodging... lodgings);

  @Update
  abstract void update(Lodging... lodgings);

  @Delete
  abstract void delete(Lodging... lodgings);

  /*
    Flight
   */

  @Query("SELECT * FROM flights WHERE tripId=:tripId")
  abstract List<Flight> findFlightsForTrip(String tripId);

  @Insert
  abstract void insert(Flight... flights);

  @Update
  abstract void update(Flight... flights);

  @Delete
  abstract void delete(Flight... flights);

  /*
    Note
   */

  @Query("SELECT * FROM notes WHERE tripId=:tripId")
  abstract List<Note> findNotesForTrip(String tripId);

  @Insert
  abstract void insert(Note... comments);

  @Update
  abstract void update(Note... comments);

  @Delete
  abstract void delete(Note... comments);

  /*
    Comment
   */

  @Query("SELECT * FROM notes WHERE tripId=:tripId AND type=0")
  abstract List<Comment> findCommentsForTrip(String tripId);

  /*
    Link
   */

  @Query("SELECT * FROM notes WHERE tripId=:tripId AND type=1")
  abstract List<Link> findLinksForTrip(String tripId);
}
