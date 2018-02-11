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
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import io.reactivex.BackpressureStrategy;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class RosterViewModel extends AndroidViewModel {
  private static final String STATE_CURRENT_ID="currentId";
  private LiveData<ViewState> states;
  private ViewState lastState=ViewState.empty().build();
  private final PublishSubject<Action> actionSubject=
    PublishSubject.create();

  public RosterViewModel(Application ctxt, Bundle state) {
    super(ctxt);

    ObservableTransformer<Result, ViewState> toView=
      results -> (results.map(result -> {
        lastState=foldResultIntoState(lastState, result);

        return(lastState);
      }));

    Controller controller=new Controller(ctxt);

    states=LiveDataReactiveStreams
      .fromPublisher(controller.resultStream()
        .subscribeOn(Schedulers.io())
        .compose(toView)
        .cache()
        .toFlowable(BackpressureStrategy.LATEST)
        .share());
    controller.subscribeToActions(actionSubject);

    String currentId=(state==null ? null : state.getString(STATE_CURRENT_ID));

    process(Action.load(currentId));
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
    if (result instanceof Result.Changed) {
      return(state.changed(((Result.Changed)result).snapshot(),
        ((Result.Changed)result).current()));
    }
    else if (result instanceof Result.Loaded) {
      Result.Loaded loadResult=(Result.Loaded)result;

      return(ViewState.builder()
        .isLoaded(true)
        .snapshot(loadResult.snapshot())
        .filterMode(loadResult.filterMode())
        .current(loadResult.current())
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
      return(state.filtered(((Result.Filter)result).filterMode(), ((Result.Filter)result).snapshot()));
    }
    else {
      throw new IllegalStateException("Unexpected result type: "+result.toString());
    }
  }

  public void onSaveInstanceState(Bundle state) {
    if (lastState!=null && lastState.current()!=null) {
      state.putString(STATE_CURRENT_ID, lastState.current().id());
    }
  }

  public static class Factory implements ViewModelProvider.Factory {
    private final Application app;
    private final Bundle state;

    public Factory(@android.support.annotation.NonNull Application app,
                   Bundle state) {
      this.app=app;
      this.state=state;
    }

    @android.support.annotation.NonNull
    @Override
    public <T extends ViewModel> T create(
      @android.support.annotation.NonNull Class<T> modelClass) {
      return (T)new RosterViewModel(app, state);
    }
  }
}
