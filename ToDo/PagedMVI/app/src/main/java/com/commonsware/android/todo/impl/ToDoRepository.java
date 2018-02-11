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

import android.content.Context;
import java.util.List;

public class ToDoRepository {
  private static volatile ToDoRepository INSTANCE=null;
  private final ToDoDatabase db;

  public synchronized static ToDoRepository get(Context ctxt) {
    if (INSTANCE==null) {
      INSTANCE=new ToDoRepository(ctxt.getApplicationContext());
    }

    return(INSTANCE);
  }

  private ToDoRepository(Context ctxt) {
    db=ToDoDatabase.get(ctxt);
  }

  public PagedDataSnapshot<ToDoModel, String> allForFilter(FilterMode filterMode) {
    return new PagedDataSnapshot(new ToDoModelDataSource(db, filterMode), 50);
  }

  ToDoModel forId(String id, PagedDataSnapshot<ToDoModel, String> snapshot) {
    if (id==null && snapshot.dataSource().countItems()>0) {
      id=snapshot.dataSource().findKeyForPosition(0);
    }

    if (id!=null) {
      ToDoEntity entity=db.todoStore().forId(id);

      if (entity!=null) {
        return entity.toModel();
      }
    }

    return null;
  }

  public void add(ToDoModel model) {
    db.todoStore().insert(ToDoEntity.fromModel(model));
  }

  public void replace(ToDoModel model) {
    db.todoStore().update(ToDoEntity.fromModel(model));
  }

  public void delete(List<String> ids) {
    db.todoStore().delete(ids);
  }
}
