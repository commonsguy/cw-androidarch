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

import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;
import java.util.Collections;
import java.util.List;

abstract class BaseDataSource<T> extends PositionalDataSource<T> {
  abstract protected int countItems();
  abstract protected List<T> loadRangeAtPosition(int position, int size);

  @Override
  public void loadInitial(@NonNull LoadInitialParams params,
    @NonNull LoadInitialCallback<T> callback) {
    int total=countItems();

    if (total==0) {
      callback.onResult(Collections.emptyList(), 0, 0);
    }
    else {
      final int position=computeInitialLoadPosition(params, total);
      final int size=computeInitialLoadSize(params, position, total);
      List<T> list=loadRangeAtPosition(position, size);

      if (list!=null && list.size()==size) {
        callback.onResult(list, position, total);
      }
      else {
        invalidate();
      }
    }
  }

  @Override
  public void loadRange(@NonNull LoadRangeParams params,
                        @NonNull LoadRangeCallback<T> callback) {
    List<T> list=loadRangeAtPosition(params.startPosition, params.loadSize);

    if (list!=null) {
      callback.onResult(list);
    }
    else {
      invalidate();
    }
  }
}
