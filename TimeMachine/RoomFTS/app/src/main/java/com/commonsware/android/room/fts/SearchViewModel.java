/***
 Copyright (c) 2018 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed search an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _Android's Architecture Components_
 https://commonsware.com/AndroidArch
 */

package com.commonsware.android.room.fts;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.Transformations;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class SearchViewModel extends AndroidViewModel {
  LiveData<PagedList<BookSearchResult>> results;

  public SearchViewModel(Application app) {
    super(app);
  }

  LiveData<PagedList<BookSearchResult>> search(String expr) {
    Single<DataSource.Factory<Integer, BookSearchResult>> liveSearch=
      BookRepository.get(getApplication()).search(expr).subscribeOn(Schedulers.single());

    results=Transformations
      .switchMap(LiveDataReactiveStreams.fromPublisher(liveSearch.toFlowable().cache()),
        factory -> new LivePagedListBuilder<>(factory, 50).build());

    return results;
  }
}
