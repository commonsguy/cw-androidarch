/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.citypop;

import android.arch.paging.KeyedDataSource;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;

class CitiesDataSource extends KeyedDataSource<Integer, City> {
  private final City.Store store;

  CitiesDataSource(City.Store store) {
    this.store=store;
  }

  @NonNull
  @Override
  public Integer getKey(@NonNull City item) {
    return(item.population);
  }

  @Nullable
  @Override
  public List<City> loadInitial(int pageSize) {
    return(store.initialByPopulation(pageSize));
  }

  @Nullable
  @Override
  public List<City> loadAfter(@NonNull Integer currentEndKey, int pageSize) {
    return(store.afterByPopulation(currentEndKey, pageSize));
  }

  @Nullable
  @Override
  public List<City> loadBefore(@NonNull Integer currentBeginKey, int pageSize) {
    return(store.beforeByPopulation(currentBeginKey, pageSize));
  }
}
