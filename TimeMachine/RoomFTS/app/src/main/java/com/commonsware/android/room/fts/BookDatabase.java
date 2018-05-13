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

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Database(entities = {ChapterEntity.class, ParagraphEntity.class}, version=1)
abstract public class BookDatabase extends RoomDatabase {
  static final String DB_NAME="time-machine.db";

  abstract public BookStore store();

  private static volatile BookDatabase INSTANCE=null;

  synchronized static BookDatabase get(Context ctxt) {
    if (INSTANCE==null) {
      INSTANCE=create(ctxt);
    }

    return INSTANCE;
  }

  private static BookDatabase create(Context ctxt) {
    RoomDatabase.Builder<BookDatabase> b=
      Room.databaseBuilder(ctxt.getApplicationContext(), BookDatabase.class,
        DB_NAME);

    b.addCallback(new Callback() {
      @Override
      public void onCreate(@NonNull SupportSQLiteDatabase db) {
        super.onCreate(db);

        db.execSQL("CREATE VIRTUAL TABLE booksearch USING fts4(sequence, prose)");
      }
    });

    BookDatabase books=b.build();

    populate(ctxt, books);

    return books;
  }

  private static void populate(Context ctxt, BookDatabase books) {
    if (books.store().chapterCount()==0) {
      try {
        AssetManager assets=ctxt.getAssets();
        int sequenceNumber=0;

        for (String path : assets.list("")) {
          List<String> paragraphs=paragraphs(assets.open(path));
          String title=title(path);
          ChapterEntity chapter=new ChapterEntity(title);
          List<ParagraphEntity> paragraphEntities=new ArrayList<>();

          for (String paragraph : paragraphs) {
            paragraphEntities.add(new ParagraphEntity(paragraph));
          }

          sequenceNumber=
            books.store().insert(chapter, paragraphEntities, sequenceNumber);
        }
      }
      catch (IOException e) {
        Log.e("BookDatabase", "Exception reading in assets", e);
      }
    }
  }

  // inspired by https://stackoverflow.com/a/10065920/115145

  private static List<String> paragraphs(InputStream is) throws IOException {
    BufferedReader in=new BufferedReader(new InputStreamReader(is));
    List<String> result=new ArrayList<>();

    try {
      StringBuilder paragraph=new StringBuilder();

      while (true) {
        String line=in.readLine();

        if (line==null) {
          break;
        }
        else if (TextUtils.isEmpty(line)) {
          if (!TextUtils.isEmpty(paragraph)) {
            result.add(paragraph.toString().trim());
            paragraph=new StringBuilder();
          }
        }
        else {
          paragraph.append(line);
          paragraph.append(' ');
        }
      }

      if (!TextUtils.isEmpty(paragraph)) {
        result.add(paragraph.toString().trim());
      }
    }
    finally {
      is.close();
    }

    return result;
  }

  private static String title(String path) {
    String[] pieces=path.substring(0, path.length()-4).split("-");
    StringBuilder buf=new StringBuilder();

    for (int i=1;i<pieces.length;i++) {
      buf.append(pieces[i]);
      buf.append(' ');
    }

    return buf.toString().trim();
  }
}
