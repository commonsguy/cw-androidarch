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
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Transaction;
import java.util.List;

@Dao
abstract public class BookStore {
  @Insert
  abstract long insert(ChapterEntity chapter);

  @Insert
  abstract void insert(List<ParagraphEntity> paragraphs);

  @Query("SELECT COUNT(*) FROM chapters")
  abstract int chapterCount();

  @Query("SELECT * FROM chapters")
  abstract List<ChapterEntity> chapters();

  @Query("SELECT COUNT(*) FROM paragraphs WHERE chapterId=:chapterId")
  abstract int paragraphCount(long chapterId);

  @Query("SELECT * FROM paragraphs ORDER BY sequence ASC")
  abstract DataSource.Factory<Integer, ParagraphEntity> paragraphs();

  @Transaction
  int insert(ChapterEntity chapter, List<ParagraphEntity> paragraphs,
             int startingSequenceNo) {
    long chapterId=insert(chapter);

    for (ParagraphEntity paragraph : paragraphs) {
      paragraph.chapterId=chapterId;
      paragraph.sequence=startingSequenceNo++;
      insertFTS(paragraph);
    }

    insert(paragraphs);

    return startingSequenceNo;
  }

  @RawQuery(observedEntities = ParagraphEntity.class)
  protected abstract DataSource.Factory<Integer, BookSearchResult> _search(SupportSQLiteQuery query);

  @RawQuery
  protected abstract List<BookSearchResult> _searchSynchronous(SupportSQLiteQuery query);

  @RawQuery
  protected abstract long _insert(SupportSQLiteQuery queryish);

  DataSource.Factory<Integer, BookSearchResult> search(String expr) {
    return _search(buildSearchQuery(expr));
  }

  List<BookSearchResult> searchSynchronous(String expr) {
    return _searchSynchronous(buildSearchQuery(expr));
  }

  void insertFTS(ParagraphEntity entity) {
    _insert(new SimpleSQLiteQuery("INSERT INTO booksearch (sequence, prose) VALUES (?, ?)",
      new Object[] {entity.sequence, entity.prose}));
  }

  private SimpleSQLiteQuery buildSearchQuery(String expr) {
    return new SimpleSQLiteQuery("SELECT sequence, snippet(booksearch) AS snippet FROM booksearch WHERE prose MATCH ? ORDER BY sequence ASC",
      new Object[] {expr});
  }
}
