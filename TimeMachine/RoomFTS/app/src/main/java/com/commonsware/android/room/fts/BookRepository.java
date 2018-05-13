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

import android.arch.paging.DataSource;
import android.content.Context;
import io.reactivex.Single;

public class BookRepository {
  private static volatile BookRepository INSTANCE=null;
  private final Context ctxt;

  synchronized static BookRepository get(Context ctxt) {
    if (INSTANCE==null) {
      INSTANCE=new BookRepository(ctxt);
    }

    return INSTANCE;
  }

  private BookRepository(Context ctxt) {
    this.ctxt=ctxt.getApplicationContext();
  }

  Single<DataSource.Factory<Integer, ParagraphEntity>> paragraphs() {
    return Single.create(emitter ->
      emitter.onSuccess(BookDatabase.get(ctxt).store().paragraphs()));
  }

  Single<DataSource.Factory<Integer, BookSearchResult>> search(String expr) {
    return Single.create(emitter ->
      emitter.onSuccess(BookDatabase.get(ctxt).store().search(expr)));
  }
}
