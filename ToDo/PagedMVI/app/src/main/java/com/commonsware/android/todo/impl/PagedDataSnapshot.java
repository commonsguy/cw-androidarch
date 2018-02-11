/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _Android's Architecture Components_
 https://commonsware.com/AndroidArch
 */

package com.commonsware.android.todo.impl;

import android.arch.paging.PagedList;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PagedDataSnapshot<T, PK> {
  private final SnapshotDataSource<T, PK> dataSource;
  private final PagedList<T> pagedList;

  public PagedDataSnapshot(SnapshotDataSource<T, PK> dataSource, int count) {
    this.dataSource=dataSource;

    PagedList.Builder<Integer, T> builder=
      new PagedList.Builder<>(dataSource, count);

    pagedList=builder
      .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
      .setMainThreadExecutor(new MainThreadExecutor())
      .build();
  }

  public SnapshotDataSource<T, PK> dataSource() {
    return dataSource;
  }

  public PagedList<T> pagedList() {
    return pagedList;
  }

  private static class MainThreadExecutor implements Executor {
    private Handler mainHandler=new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable command) {
      mainHandler.post(command);
    }
  }
}
