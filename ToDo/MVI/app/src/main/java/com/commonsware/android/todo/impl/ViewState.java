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
import android.support.v7.util.DiffUtil;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AutoValue
public abstract class ViewState {
  public abstract boolean isLoaded();
  public abstract List<ToDoModel> items();
  abstract Set<Integer> selections();
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
    return(builder().items(new ArrayList<>()));
  }

  ViewState add(ToDoModel model) {
    List<ToDoModel> models=new ArrayList<>(items());

    models.add(model);
    sort(models);

    return(toBuilder()
      .items(Collections.unmodifiableList(models))
      .current(model)
      .build());
  }

  ViewState modify(ToDoModel model) {
    List<ToDoModel> models=new ArrayList<>(items());
    ToDoModel original=find(models, model.id());

    if (original!=null) {
      int index=models.indexOf(original);
      models.set(index, model);
    }

    sort(models);

    return(toBuilder()
      .items(Collections.unmodifiableList(models))
      .build());
  }

  ViewState delete(List<ToDoModel> toDelete) {
    List<ToDoModel> models=new ArrayList<>(items());

    for (ToDoModel model : toDelete) {
      ToDoModel original=find(models, model.id());

      if (original==null) {
        throw new IllegalArgumentException("Cannot find model to delete: "+model.toString());
      }
      else {
        models.remove(original);
      }
    }

    sort(models);

    return(toBuilder()
      .items(Collections.unmodifiableList(models))
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

  ViewState filtered(FilterMode mode) {
    return(toBuilder()
      .filterMode(mode)
      .build());
  }

  @Memoized
  public List<ToDoModel> filteredItems() {
    return(ToDoModel.filter(items(), filterMode()));
  }

  public ToDoModel find(String id) {
    return(find(items(), id));
  }

  public int getFilteredPosition(String id) {
    for (int i=0;i<filteredItems().size();i++) {
      ToDoModel model=filteredItems().get(i);

      if (id.equals(model.id())) {
        return(i);
      }
    }

    return(-1);
  }

  public List<ToDoModel> getSelectedModels() {
    List<ToDoModel> result=new ArrayList<>();

    for (int i=0;i<items().size();i++) {
      if (isSelected(i)) {
        result.add(items().get(i));
      }
    }

    return(result);
  }

  private ToDoModel find(List<ToDoModel> models, String id) {
    int position=findPosition(models, id);

    return(position>=0 ? models.get(position) : null);
  }

  private int findPosition(List<ToDoModel> models, String id) {
    for (int i=0;i<models.size();i++) {
      ToDoModel candidate=models.get(i);

      if (id.equals(candidate.id())) {
        return(i);
      }
    }

    return(-1);
  }

  public boolean isSelected(int position) {
    return(selections().contains(position));
  }

  public int getSelectionCount() {
    return(selections().size());
  }

  private void sort(List<ToDoModel> models) {
    Collections.sort(models, ToDoModel.SORT_BY_NOTES);
  }

  private Builder toBuilder() {
    return(builder()
      .isLoaded(isLoaded())
      .cause(cause())
      .items(items())
      .selections(selections())
      .current(current())
      .filterMode(filterMode()));
  }

  @AutoValue.Builder
  abstract static class Builder {
    abstract Builder isLoaded(boolean isLoaded);
    abstract Builder items(List<ToDoModel> items);
    abstract Builder selections(Set<Integer> positions);
    abstract Builder cause(Throwable cause);
    abstract Builder filterMode(FilterMode mode);
    abstract Builder current(ToDoModel current);
    abstract ViewState build();
  }

  public static class Differ extends DiffUtil.Callback {
    private final ViewState oldState;
    private final ViewState newState;

    public Differ(ViewState oldState, ViewState newState) {
      this.oldState=oldState;
      this.newState=newState;
    }

    @Override
    public int getOldListSize() {
      return(oldState==null ? 0 : oldState.filteredItems().size());
    }

    @Override
    public int getNewListSize() {
      return(newState==null ? 0 : newState.filteredItems().size());
    }

    @Override
    public boolean areItemsTheSame(int oldPos, int newPos) {
      return(oldState.filteredItems().get(oldPos).id()
        .equals(newState.filteredItems().get(newPos).id()));
    }

    @Override
    public boolean areContentsTheSame(int oldPos, int newPos) {
      boolean oldSelected=oldState.isSelected(oldPos);
      boolean newSelected=newState.isSelected(newPos);

      if (oldSelected!=newSelected) {
        return(false);
      }

      ToDoModel older=oldState.filteredItems().get(oldPos);
      ToDoModel newer=newState.filteredItems().get(newPos);

      return(older.isCompleted()==newer.isCompleted() &&
            older.description().equals(newer.description()));
    }
  }
}
