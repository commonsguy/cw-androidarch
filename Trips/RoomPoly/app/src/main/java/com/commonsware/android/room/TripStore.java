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

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;
import java.util.ArrayList;
import java.util.List;

@Dao
abstract class TripStore implements TypedDao<Link> {
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
    Comment
   */

  @Query("SELECT * FROM comments WHERE tripId=:tripId")
  abstract List<Comment> findCommentsForTrip(String tripId);

  @Insert
  abstract void insert(Comment... comments);

  @Update
  abstract void update(Comment... comments);

  @Delete
  abstract void delete(Comment... comments);

  /*
    Link
   */

  @Query("SELECT * FROM links WHERE tripId=:tripId")
  abstract List<Link> findLinksForTrip(String tripId);

  @Override
  @Query("SELECT * FROM links WHERE tripId=:tripId")
  public abstract DataSource.Factory<Integer, Link> pagedStuffForTrip(String tripId);

  @Insert
  abstract void insert(Link... comments);

  @Update
  abstract void update(Link... comments);

  @Delete
  abstract void delete(Link... comments);

  /*
    Note
   */

  @Transaction
  List<Note> findNotesForTrip(String tripId) {
    ArrayList<Note> result=new ArrayList<>();

    result.addAll(findCommentsForTrip(tripId));
    result.addAll(findLinksForTrip(tripId));

    return result;
  }

  @Transaction
  void insert(Note... notes) {
    for (Note note : notes) {
      if (note instanceof Comment) {
        insert((Comment)note);
      }
      else if (note instanceof Link) {
        insert((Link)note);
      }
      else {
        throw new IllegalArgumentException("Um, wut dis? "+note.getClass().getCanonicalName());
      }
    }
  }

  @Transaction
  void update(Note... notes) {
    for (Note note : notes) {
      if (note instanceof Comment) {
        update((Comment)note);
      }
      else if (note instanceof Link) {
        update((Link)note);
      }
      else {
        throw new IllegalArgumentException("Um, wut dis? "+note.getClass().getCanonicalName());
      }
    }
  }

  @Transaction
  void delete(Note... notes) {
    for (Note note : notes) {
      if (note instanceof Comment) {
        delete((Comment)note);
      }
      else if (note instanceof Link) {
        delete((Link)note);
      }
      else {
        throw new IllegalArgumentException("Um, wut dis? "+note.getClass().getCanonicalName());
      }
    }
  }
}
