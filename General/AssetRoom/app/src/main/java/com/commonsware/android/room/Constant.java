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
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import java.util.List;

@Entity(tableName = "constants")
class Constant {
  @PrimaryKey(autoGenerate = true)
  final Integer id;
  final String title;
  final double value;

  Constant(Integer id, String title, double value) {
    this.id=id;
    this.title=title;
    this.value=value;
  }

  @Ignore
  Constant(String title, double value) {
    this(null, title, value);
  }

  @Override
  public String toString() {
    return(title);
  }

  @Dao
  interface Store {
    @Insert
    void insert(Constant constant);

    @Query("SELECT * FROM constants ORDER BY title")
    List<Constant> all();
  }
}
