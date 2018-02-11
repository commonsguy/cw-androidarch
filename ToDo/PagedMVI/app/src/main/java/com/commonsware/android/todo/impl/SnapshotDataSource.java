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

import java.util.Collections;
import java.util.List;

public abstract class SnapshotDataSource<T, PK> extends BaseDataSource<T> {
  protected abstract List<PK> loadKeys();
  protected abstract List<T> loadForIds(List<PK> pks);

  private volatile List<PK> keys=null;

  public int findPositionForKey(PK key) {
    if (keys==null) {
      throw new IllegalStateException("Attempted to find position for key without having keys loaded");
    }

    return keys.indexOf(key);
  }

  public PK findKeyForPosition(int position) {
    if (keys==null) {
      throw new IllegalStateException("Attempted to find position for key without having keys loaded");
    }

    return keys.get(position);
  }

  @Override
  protected List<T> loadRangeAtPosition(int position, int size) {
    initKeys();

    return loadForIds(keys.subList(position, position+size));
  }

  @Override
  protected int countItems() {
    initKeys();

    return keys.size();
  }

  synchronized private void initKeys() {
    if (keys==null) {
      keys=Collections.unmodifiableList(loadKeys());
    }
  }
}
