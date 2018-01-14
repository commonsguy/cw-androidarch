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
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

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

  public Single<List<ToDoModel>> all() {
    return(db.todoStore().all().map(entities -> {
      ArrayList<ToDoModel> result=new ArrayList<>(entities.size());

      for (ToDoEntity entity : entities) {
        result.add(entity.toModel());
      }

      return(result);
    }));
  }

  public void add(ToDoModel model) {
    db.todoStore().insert(ToDoEntity.fromModel(model));
  }

  public void replace(ToDoModel model) {
    db.todoStore().update(ToDoEntity.fromModel(model));
  }

  public void delete(List<ToDoModel> models) {
    List<ToDoEntity> entities=new ArrayList<>();

    for (ToDoModel model : models) {
      entities.add(ToDoEntity.fromModel(model));
    }

    db.todoStore().delete(entities);
  }
}
