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
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(
  tableName="notes",
  foreignKeys=@ForeignKey(
    entity=Trip.class,
    parentColumns="id",
    childColumns="tripId",
    onDelete=CASCADE),
  indices=@Index("tripId"))
public class Note {
  public enum Type {
    COMMENT(0),
    LINK(1);

    private final int value;

    Type(int value) {
      this.value=value;
    }

    public int value() {
      return value;
    }
  }

  @PrimaryKey
  @NonNull
  public final String id;

  public final String title;
  public final String url;
  @NonNull public final String tripId;
  public final Type type;

  public Note(@NonNull String id, String title, @NonNull String url,
              @NonNull String tripId, Type type) {
    this.id=id;
    this.title=title;
    this.url=url;
    this.tripId=tripId;
    this.type=type;
  }
}
