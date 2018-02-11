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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.DiffCallback;
import com.google.auto.value.AutoValue;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@AutoValue
public abstract class ToDoModel {
  public abstract String id();
  public abstract boolean isCompleted();
  public abstract String description();
  @Nullable public abstract String notes();
  public abstract Calendar createdOn();

  static Builder builder() {
    return(new AutoValue_ToDoModel.Builder());
  }

  public static Builder creator() {
    return(builder()
      .isCompleted(false)
      .id(UUID.randomUUID().toString())
      .createdOn(Calendar.getInstance()));
  }

  public Builder toBuilder() {
    return(builder()
      .id(id())
      .isCompleted(isCompleted())
      .description(description())
      .notes(notes())
      .createdOn(createdOn()));
  }

  @Override
  public String toString() {
    return(description());
  }

  static final Comparator<ToDoModel> SORT_BY_NOTES=
    (one, two) -> (one.toString().compareTo(two.toString()));

  public static List<ToDoModel> filter(List<ToDoModel> models,
                                       FilterMode filterMode) {
    List<ToDoModel> result;

    if (filterMode==FilterMode.COMPLETED) {
      result=new ArrayList<>();

      for (ToDoModel model : models) {
        if (model.isCompleted()) {
          result.add(model);
        }
      }
    }
    else if (filterMode==FilterMode.OUTSTANDING) {
      result=new ArrayList<>();

      for (ToDoModel model : models) {
        if (!model.isCompleted()) {
          result.add(model);
        }
      }
    }
    else {
      result=new ArrayList<>(models);
    }

    return(result);
  }

  @AutoValue.Builder
  public abstract static class Builder {
    abstract Builder id(String id);
    public abstract Builder isCompleted(boolean isCompleted);
    public abstract Builder description(String desc);
    public abstract Builder notes(String notes);
    abstract Builder createdOn(Calendar date);
    public abstract ToDoModel build();
  }
}
