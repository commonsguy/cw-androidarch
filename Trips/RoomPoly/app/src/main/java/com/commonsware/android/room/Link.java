/***
 Copyright (c) 2018 CommonsWare, LLC
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

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import java.util.UUID;
import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(
  tableName="links",
  foreignKeys=@ForeignKey(
    entity=Trip.class,
    parentColumns="id",
    childColumns="tripId",
    onDelete=CASCADE),
  indices=@Index("tripId"))
public class Link implements Note {
  @PrimaryKey
  @NonNull
  public final String id;

  public final String title;

  @NonNull public final String url;
  @NonNull public final String tripId;

  public Link(@NonNull String id, String title, @NonNull String url, @NonNull
    String tripId) {
    this.id=id;
    this.title=title;
    this.url=url;
    this.tripId=tripId;
  }

  @Ignore
  public Link(String title, @NonNull String url, @NonNull Trip trip) {
    this(UUID.randomUUID().toString(), title, url, trip.id);
  }

  @Override
  public String tripId() {
    return tripId;
  }
}
