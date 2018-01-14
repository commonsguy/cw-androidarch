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

package com.commonsware.android.todo;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.commonsware.android.todo.impl.Action;
import com.commonsware.android.todo.impl.Result;
import com.commonsware.android.todo.impl.RosterViewModel;
import com.commonsware.android.todo.impl.ToDoDatabase;
import com.commonsware.android.todo.impl.ToDoModel;
import com.commonsware.android.todo.impl.ViewState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import io.reactivex.subjects.PublishSubject;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class ViewModelTests {
  private LinkedBlockingQueue<ViewState> receivedStates=new LinkedBlockingQueue<>();
  private final PublishSubject<Result> results=PublishSubject.create();

  @Before
  public void setUp() {
    ToDoDatabase db=
      ToDoDatabase.get(InstrumentationRegistry.getTargetContext(), true);
    
    db.todoStore().deleteAll();
  }

  @Test
  public void withController() throws InterruptedException {
    Application app=
      (Application)InstrumentationRegistry.getTargetContext().getApplicationContext();
    final RosterViewModel viewModel=new RosterViewModel(app);

    final Observer<ViewState> observer=
      viewState -> receivedStates.offer(viewState);

    final LiveData<ViewState> liveStates=viewModel.stateStream();

    liveStates.observeForever(observer);

    ViewState state=receivedStates.poll(10, TimeUnit.SECONDS);

    assertNotNull(state);
    assertState(state);

    if (!state.isLoaded()) {
      state=receivedStates.poll(10, TimeUnit.SECONDS);
      assertNotNull(state);
      assertState(state);
    }

    final ToDoModel fooModel=ToDoModel.creator().description("foo").build();

    testActionToState(viewModel, Action.add(fooModel), fooModel);

    final ToDoModel barModel=ToDoModel.creator().description("bar").build();

    testActionToState(viewModel, Action.add(barModel), fooModel, barModel);

    final ToDoModel gooModel=ToDoModel.creator()
      .description("goo")
      .isCompleted(true)
      .build();

    testActionToState(viewModel, Action.add(gooModel), fooModel, barModel,
      gooModel);

    final ToDoModel mutatedFoo=fooModel.toBuilder().isCompleted(true).build();

    testActionToState(viewModel, Action.edit(mutatedFoo), mutatedFoo, barModel,
      gooModel);

    final ToDoModel mutatedBar=barModel.toBuilder().description("bar!").build();

    testActionToState(viewModel, Action.edit(mutatedBar), mutatedFoo,
      mutatedBar, gooModel);

    final ToDoModel mutatedGoo=gooModel.toBuilder()
      .description("goo!")
      .isCompleted(false)
      .build();

    testActionToState(viewModel, Action.edit(mutatedGoo), mutatedFoo,
      mutatedBar, mutatedGoo);

    testActionToState(viewModel, Action.delete(mutatedFoo), mutatedBar,
      mutatedGoo);
    testActionToState(viewModel, Action.delete(mutatedGoo), mutatedBar);
    testActionToState(viewModel, Action.delete(mutatedBar));

    // confirm no lingering unexpected stuff

    assertNull(receivedStates.poll(1, TimeUnit.SECONDS));
  }

  private ViewState testResultToState(Result result,
                                      ToDoModel... models)
    throws InterruptedException {
    results.onNext(result);

    ViewState state=receivedStates.poll(2, TimeUnit.SECONDS);

    assertState(state, models);

    return(state);
  }

  private void testActionToState(RosterViewModel viewModel,
                                 Action action,
                                 ToDoModel... models) throws InterruptedException {
    viewModel.process(action);

    ViewState state=receivedStates.poll(1, TimeUnit.SECONDS);

    assertState(state, models);
  }

  private void assertState(ViewState state, ToDoModel... models) {
    assertModels(state.items(), models);
    assertNull(state.cause());
  }

  private void assertModels(List<ToDoModel> items, ToDoModel... models) {
    assertThat(items, both(everyItem(isIn(models))).and(containsInAnyOrder(models)));
  }
}