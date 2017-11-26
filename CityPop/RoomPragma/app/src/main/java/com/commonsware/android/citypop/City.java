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

package com.commonsware.android.citypop;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;
import java.util.List;

@Entity(tableName = "cities")
class City {
  @PrimaryKey
  @NonNull
  final String id;
  final String country;
  final String city;
  final int population;

  City(@NonNull String id, String country, String city, int population) {
    this.id=id;
    this.country=country;
    this.city=city;
    this.population=population;
  }

  @Override
  public String toString() {
    return(city);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof City) {
      City other=(City)obj;

      return(id.equals(other.id));
    }

    return(false);
  }

  @Override
  public int hashCode() {
    return(id.hashCode());
  }

  @Dao
  interface Store {
    @Query("SELECT * FROM cities ORDER BY population DESC")
    LiveData<List<City>> allByPopulation();

    @Insert
    void insert(City city);

    @Insert
    void insert(List<City> city);
  }
}
