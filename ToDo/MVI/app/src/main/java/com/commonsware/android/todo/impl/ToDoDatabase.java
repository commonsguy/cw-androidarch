/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _Android's Architecture Components_
 https://commonsware.com/AndroidArch
 */

package com.commonsware.android.todo.impl;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities={ToDoEntity.class}, version=1)
@TypeConverters({TypeTransmogrifier.class})
public abstract class ToDoDatabase extends RoomDatabase {
  public abstract ToDoEntity.Store todoStore();

  private static final String DB_NAME="stuff.db";
  private static volatile ToDoDatabase INSTANCE=null;

  synchronized static ToDoDatabase get(Context ctxt) {
    return(get(ctxt, false));
  }

  public synchronized static ToDoDatabase get(Context ctxt, boolean memoryOnly) {
    if (INSTANCE==null) {
      INSTANCE=create(ctxt, memoryOnly);
    }

    return(INSTANCE);
  }

  private static ToDoDatabase create(Context ctxt, boolean memoryOnly) {
    RoomDatabase.Builder<ToDoDatabase> b;

    if (memoryOnly) {
      b=Room.inMemoryDatabaseBuilder(ctxt.getApplicationContext(),
        ToDoDatabase.class);
    }
    else {
      b=Room.databaseBuilder(ctxt.getApplicationContext(), ToDoDatabase.class,
        DB_NAME);
    }

    return(b.build());
  }
}
