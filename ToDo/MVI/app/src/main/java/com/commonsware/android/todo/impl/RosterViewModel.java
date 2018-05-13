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

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import java.util.List;
import io.reactivex.BackpressureStrategy;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class RosterViewModel extends AndroidViewModel {
  private LiveData<ViewState> states;
  private ViewState lastState=ViewState.empty().build();
  private final PublishSubject<Action> actionSubject=
    PublishSubject.create();

  public RosterViewModel(Application ctxt) {
    super(ctxt);

    ObservableTransformer<Result, ViewState> toView=
      results -> (results.map(result -> {
        lastState=foldResultIntoState(lastState, result);

        return(lastState);
      }));

    Controller controller=new Controller(ctxt);

    states=LiveDataReactiveStreams
      .fromPublisher(controller.resultStream()
        .subscribeOn(Schedulers.single())
        .compose(toView)
        .cache()
        .toFlowable(BackpressureStrategy.LATEST)
        .share());
    controller.subscribeToActions(actionSubject);
    process(Action.load());
  }

  public LiveData<ViewState> stateStream() {
    return(states);
  }

  public ViewState currentState() {
    return(stateStream().getValue());
  }

  public void process(Action action) {
    actionSubject.onNext(action);
  }

  private ViewState foldResultIntoState(@NonNull ViewState state,
    @NonNull Result result) throws Exception {
    if (result instanceof Result.Added) {
      return(state.add(((Result.Added)result).model()));
    }
    else if (result instanceof Result.Modified) {
      return(state.modify(((Result.Modified)result).model()));
    }
    else if (result instanceof Result.Deleted) {
      return(state.delete(((Result.Deleted)result).models()));
    }
    else if (result instanceof Result.Loaded) {
      List<ToDoModel> models=((Result.Loaded)result).models();

      return(ViewState.builder()
        .isLoaded(true)
        .items(models)
        .filterMode(((Result.Loaded)result).filterMode())
        .current(models.size()==0 ? null : models.get(0))
        .build());
    }
    else if (result instanceof Result.Selected) {
      return(state.selected(((Result.Selected)result).position()));
    }
    else if (result instanceof Result.Unselected) {
      return(state.unselected(((Result.Unselected)result).position()));
    }
    else if (result instanceof Result.UnselectedAll) {
      return(state.unselectedAll());
    }
    else if (result instanceof Result.Showed) {
      return(state.show(((Result.Showed)result).current()));
    }
    else if (result instanceof Result.Filter) {
      return(state.filtered(((Result.Filter)result).filterMode()));
    }
    else {
      throw new IllegalStateException("Unexpected result type: "+result.toString());
    }
  }
}
