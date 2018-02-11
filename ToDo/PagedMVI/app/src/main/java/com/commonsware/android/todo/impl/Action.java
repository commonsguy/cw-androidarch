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

import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import java.util.Collections;
import java.util.List;

public abstract class Action {
  public static Action add(ToDoModel model) {
    return(new AutoValue_Action_Add(model));
  }

  public static Action edit(ToDoModel model) {
    return(new AutoValue_Action_Edit(model));
  }

  public static Action delete(List<String> ids) {
    return(new AutoValue_Action_Delete(Collections.unmodifiableList(ids)));
  }

  public static Action delete(ToDoModel model) {
    return(delete(Collections.singletonList(model.id())));
  }

  public static Action select(int position) {
    return(new AutoValue_Action_Select(position));
  }

  public static Action unselect(int position) {
    return(new AutoValue_Action_Unselect(position));
  }

  public static Action unselectAll() {
    return(new UnselectAll());
  }

  public static Action show(ToDoModel model) {
    return(new AutoValue_Action_Show(model));
  }

  public static Action filter(FilterMode mode) {
    return(new AutoValue_Action_Filter(mode));
  }

  public static Action load(String currentId) {
    return(new AutoValue_Action_Load(currentId));
  }

  @AutoValue
  public static abstract class Add extends Action {
    public abstract ToDoModel model();
  }

  @AutoValue
  public static abstract class Edit extends Action {
    public abstract ToDoModel model();
  }

  @AutoValue
  public static abstract class Delete extends Action {
    public abstract List<String> ids();
  }

  @AutoValue
  static abstract class Select extends Action {
    public abstract int position();
  }

  @AutoValue
  static abstract class Unselect extends Action {
    public abstract int position();
  }

  static class UnselectAll extends Action {

  }

  @AutoValue
  static abstract class Show extends Action {
    public abstract ToDoModel current();
  }

  @AutoValue
  static abstract class Filter extends Action {
    public abstract FilterMode filterMode();
  }

  @AutoValue
  public static abstract class Load extends Action {
    @Nullable public abstract String currentId();
  }
}
