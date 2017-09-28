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

import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import java.util.Date;
import java.util.UUID;

abstract class Plan {
  @PrimaryKey
  @NonNull
  public final String id;

  public final String title;
  public final int duration;

  @TypeConverters({Priority.class})
  public final Priority priority;

  public final Date startTime;
  public final Date creationTime;
  public final Date updateTime;

  @Ignore
  Plan(String title, int duration, Priority priority, Date startTime) {
    this(UUID.randomUUID().toString(), title, duration, priority, startTime,
      null, null);
  }

  @Ignore
  Plan(String id, String title, int duration, Priority priority, Date startTime) {
    this(id, title, duration, priority, startTime, null, null);
  }

  Plan(String id, String title, int duration, Priority priority,
       Date startTime, Date creationTime, Date updateTime) {
    this.id=id;
    this.title=title;
    this.duration=duration;
    this.priority=priority;
    this.startTime=startTime;
    this.creationTime=creationTime;
    this.updateTime=updateTime;
  }

  @Override
  public String toString() {
    return(title);
  }
}
