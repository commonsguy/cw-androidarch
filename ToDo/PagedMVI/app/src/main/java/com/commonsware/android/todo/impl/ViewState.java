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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AutoValue
public abstract class ViewState {
  public abstract boolean isLoaded();
  @Nullable public abstract PagedDataSnapshot<ToDoModel, String> snapshot();
  public abstract Set<Integer> selections();
  @Nullable public abstract Throwable cause();
  public abstract FilterMode filterMode();
  @Nullable public abstract ToDoModel current();

  static Builder builder() {
    return(new AutoValue_ViewState.Builder()
      .isLoaded(false)
      .selections(new HashSet<>())
      .filterMode(FilterMode.ALL));
  }

  static Builder empty() {
    return(builder().snapshot(null));
  }

  ViewState changed(PagedDataSnapshot<ToDoModel, String> snapshot, ToDoModel current) {
    return(toBuilder()
      .snapshot(snapshot)
      .current(current)
      .selections(new HashSet<>())
      .build());
  }

  ViewState selected(int position) {
    HashSet<Integer> selections=new HashSet<>(selections());

    selections.add(position);

    return(toBuilder()
      .selections(Collections.unmodifiableSet(selections))
      .build());
  }

  ViewState unselected(int position) {
    HashSet<Integer> selections=new HashSet<>(selections());

    selections.remove(position);

    return(toBuilder()
      .selections(Collections.unmodifiableSet(selections))
      .build());
  }

  ViewState unselectedAll() {
    return(toBuilder()
      .selections(Collections.unmodifiableSet(new HashSet<>()))
      .build());
  }

  ViewState show(ToDoModel current) {
    return(toBuilder()
      .current(current)
      .build());
  }

  ViewState filtered(FilterMode mode, PagedDataSnapshot<ToDoModel, String> snapshot) {
    return(toBuilder()
      .filterMode(mode)
      .snapshot(snapshot)
      .selections(new HashSet<>())
      .build());
  }

  public List<String> getSelectedIds() {
    List<String> result=new ArrayList<>();
    SnapshotDataSource<ToDoModel, String> dataSource=snapshot().dataSource();

    for (int selection : selections()) {
      result.add(dataSource.findKeyForPosition(selection));
    }

    return(result);
  }

  public boolean isSelected(int position) {
    return(selections().contains(position));
  }

  public int getSelectionCount() {
    return(selections().size());
  }

  private Builder toBuilder() {
    return(builder()
      .isLoaded(isLoaded())
      .cause(cause())
      .snapshot(snapshot())
      .selections(selections())
      .current(current())
      .filterMode(filterMode()));
  }

  @AutoValue.Builder
  abstract static class Builder {
    abstract Builder isLoaded(boolean isLoaded);
    abstract Builder snapshot(PagedDataSnapshot<ToDoModel, String> snapshot);
    abstract Builder selections(Set<Integer> positions);
    abstract Builder cause(Throwable cause);
    abstract Builder filterMode(FilterMode mode);
    abstract Builder current(ToDoModel current);
    abstract ViewState build();
  }
}
