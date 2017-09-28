/*
 * Copyright (C) 2016 The Android Open Source Project
 * Modifications (c) 2017 CommonsWare, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.arch.persistence.db.framework;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

class AssetSQLiteOpenHelper implements SupportSQLiteOpenHelper {
  private final AssetHelper delegate;

  AssetSQLiteOpenHelper(Context context, String name, int version,
                        Callback callback) {
    delegate=createDelegate(context, name, version, callback);
  }

  private AssetHelper createDelegate(Context context, String name,
                                     int version, final Callback callback) {
    return new AssetHelper(context, name, version) {
      @Override
      public final void onCreate(SQLiteDatabase db) {
        wrappedDb=new FrameworkSQLiteDatabase(db);
        callback.onCreate(wrappedDb);
      }

      @Override
      public final void onUpgrade(SQLiteDatabase db, int oldVersion,
                                  int newVersion) {
        callback.onUpgrade(getWrappedDb(db), oldVersion,
          newVersion);
      }

      @Override
      public void onConfigure(SQLiteDatabase db) {
        callback.onConfigure(getWrappedDb(db));
      }

      @Override
      public final void onDowngrade(SQLiteDatabase db, int oldVersion,
                                    int newVersion) {
        callback.onDowngrade(getWrappedDb(db), oldVersion, newVersion);
      }

      @Override
      public void onOpen(SQLiteDatabase db) {
        callback.onOpen(getWrappedDb(db));
      }
    };
  }

  @Override
  public String getDatabaseName() {
    return delegate.getDatabaseName();
  }

  @Override
  @RequiresApi(api=Build.VERSION_CODES.JELLY_BEAN)
  public void setWriteAheadLoggingEnabled(boolean enabled) {
    delegate.setWriteAheadLoggingEnabled(enabled);
  }

  @Override
  public SupportSQLiteDatabase getWritableDatabase() {
    return delegate.getWritableSupportDatabase();
  }

  @Override
  public SupportSQLiteDatabase getReadableDatabase() {
    return delegate.getReadableSupportDatabase();
  }

  @Override
  public void close() {
    delegate.close();
  }

  abstract static class AssetHelper extends SQLiteAssetHelper {
    FrameworkSQLiteDatabase wrappedDb;

    AssetHelper(Context context, String name, int version) {
      super(context, name, null, null, version, null);
    }

    SupportSQLiteDatabase getWritableSupportDatabase() {
      SQLiteDatabase db=super.getWritableDatabase();
      return getWrappedDb(db);
    }

    SupportSQLiteDatabase getReadableSupportDatabase() {
      SQLiteDatabase db=super.getReadableDatabase();
      return getWrappedDb(db);
    }

    FrameworkSQLiteDatabase getWrappedDb(SQLiteDatabase sqLiteDatabase) {
      if (wrappedDb==null) {
        wrappedDb=new FrameworkSQLiteDatabase(sqLiteDatabase);
      }
      return wrappedDb;
    }

    @Override
    public synchronized void close() {
      super.close();
      wrappedDb=null;
    }
  }
}
