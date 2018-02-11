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
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class Controller {
  private final BehaviorSubject<Result> resultSubject=
    BehaviorSubject.create();
  private final ToDoRepository toDoRepo;
  private final FilterModeRepository filterModeRepo;
  private final Context ctxt;
  private FilterMode currentFilterMode=null;

  public Controller(Context ctxt) {
    this.ctxt=ctxt.getApplicationContext();
    toDoRepo=ToDoRepository.get(ctxt);
    filterModeRepo=FilterModeRepository.get();
  }

  public void subscribeToActions(Observable<Action> actionStream) {
    actionStream
      .observeOn(Schedulers.io())
      .subscribe(this::processImpl);
  }

  public Observable<Result> resultStream() {
    return (resultSubject);
  }

  private void processImpl(Action action) {
    if (action instanceof Action.Add) {
      add(((Action.Add)action).model());
    }
    else if (action instanceof Action.Edit) {
      modify(((Action.Edit)action).model());
    }
    else if (action instanceof Action.Delete) {
      delete(((Action.Delete)action).ids());
    }
    else if (action instanceof Action.Load) {
      load(((Action.Load)action).currentId());
    }
    else if (action instanceof Action.Select) {
      select(((Action.Select)action).position());
    }
    else if (action instanceof Action.Unselect) {
      unselect(((Action.Unselect)action).position());
    }
    else if (action instanceof Action.UnselectAll) {
      unselectAll();
    }
    else if (action instanceof Action.Show) {
      show(((Action.Show)action).current());
    }
    else if (action instanceof Action.Filter) {
      filter(((Action.Filter)action).filterMode());
    }
    else {
      throw new IllegalStateException("Unexpected action: "+action.toString());
    }
  }

  private void add(ToDoModel model) {
    toDoRepo.add(model);
    resultSubject.onNext(Result.changed(buildSnapshot(), model));
  }

  private void modify(ToDoModel model) {
    toDoRepo.replace(model);
    resultSubject.onNext(Result.changed(buildSnapshot(), model));
  }

  private void delete(List<String> toDelete) {
    toDoRepo.delete(toDelete);

    PagedDataSnapshot<ToDoModel, String> snapshot=buildSnapshot();

    resultSubject.onNext(Result.changed(snapshot, toDoRepo.forId(null, snapshot)));
  }

  private void load(String currentId) {
    filterModeRepo.load(ctxt)
      .subscribeOn(Schedulers.io())
      .subscribe(filterMode -> {
          currentFilterMode=filterMode;

          PagedDataSnapshot<ToDoModel, String> snapshot=buildSnapshot();

          resultSubject.onNext(Result.loaded(snapshot, filterMode,
            toDoRepo.forId(currentId, snapshot)));
        }
      );
  }

  private void select(int position) {
    resultSubject.onNext(Result.selected(position));
  }

  private void unselect(int position) {
    resultSubject.onNext(Result.unselected(position));
  }

  private void unselectAll() {
    resultSubject.onNext(Result.unselectedAll());
  }

  private void show(ToDoModel current) {
    resultSubject.onNext(Result.showed(current));
  }

  private void filter(FilterMode mode) {
    currentFilterMode=mode;
    filterModeRepo.save(mode);
    resultSubject.onNext(Result.filter(mode, buildSnapshot()));
  }

  private PagedDataSnapshot<ToDoModel, String> buildSnapshot() {
    return toDoRepo.allForFilter(currentFilterMode);
  }
}
