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

import com.google.auto.value.AutoValue;
import java.util.Collections;
import java.util.List;

public abstract class Result {
  public static Result added(ToDoModel model) {
    return(new AutoValue_Result_Added(model));
  }

  public static Result modified(ToDoModel model) {
    return(new AutoValue_Result_Modified(model));
  }

  static Result deleted(List<ToDoModel> models) {
    return(new AutoValue_Result_Deleted(Collections.unmodifiableList(models)));
  }

  static Result loaded(List<ToDoModel> models, FilterMode filterMode) {
    return(new AutoValue_Result_Loaded(Collections.unmodifiableList(models), filterMode));
  }

  static Result selected(int position) {
    return(new AutoValue_Result_Selected(position));
  }

  static Result unselected(int position) {
    return(new AutoValue_Result_Unselected(position));
  }

  static Result unselectedAll() {
    return(new AutoValue_Result_UnselectedAll());
  }

  static Result showed(ToDoModel current) {
    return(new AutoValue_Result_Showed(current));
  }

  static Result filter(FilterMode mode) {
    return(new AutoValue_Result_Filter(mode));
  }

  @AutoValue
  public static abstract class Added extends Result {
    public abstract ToDoModel model();
  }

  @AutoValue
  public static abstract class Modified extends Result {
    public abstract ToDoModel model();
  }

  @AutoValue
  public static abstract class Deleted extends Result {
    public abstract List<ToDoModel> models();
  }

  @AutoValue
  static abstract class Selected extends Result {
    public abstract int position();
  }

  @AutoValue
  static abstract class Unselected extends Result {
    public abstract int position();
  }

  @AutoValue
  static abstract class UnselectedAll extends Result {
  }

  @AutoValue
  static abstract class Showed extends Result {
    public abstract ToDoModel current();
  }

  @AutoValue
  static abstract class Filter extends Result {
    public abstract FilterMode filterMode();
  }

  @AutoValue
  public static abstract class Loaded extends Result {
    public abstract List<ToDoModel> models();
    public abstract FilterMode filterMode();
  }
}
