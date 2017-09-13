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

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(
  entities={Trip.class, Lodging.class, Flight.class},
  version=2
)
abstract class TripDatabase extends RoomDatabase {
  abstract TripStore tripStore();

  private static final String DB_NAME="trips.db";
  private static volatile TripDatabase INSTANCE=null;

  synchronized static TripDatabase get(Context ctxt) {
    if (INSTANCE==null) {
      INSTANCE=create(ctxt, false);
    }

    return(INSTANCE);
  }

  static TripDatabase create(Context ctxt, boolean memoryOnly) {
    RoomDatabase.Builder<TripDatabase> b;

    if (memoryOnly) {
      b=Room
        .inMemoryDatabaseBuilder(ctxt.getApplicationContext(), TripDatabase.class);
    }
    else {
      b=Room.databaseBuilder(ctxt.getApplicationContext(), TripDatabase.class,
        DB_NAME);
    }

    return(b.build());
  }
}
