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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.commonsware.android.todo.impl.ToDoDatabase;
import com.commonsware.android.todo.impl.ToDoEntity;
import com.commonsware.android.todo.impl.ToDoModel;
import com.commonsware.android.todo.impl.ToDoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RepoTests {
  private ToDoDatabase db;

  @Before
  public void setUp() {
    db=ToDoDatabase.get(InstrumentationRegistry.getTargetContext(), true);
    db.todoStore().deleteAll();
  }

  @Test
  public void db() {
    assertEntities(db.todoStore().all().blockingGet());

    final ToDoModel firstModel=ToDoModel.creator().description("foo").build();
    db.todoStore().insert(ToDoEntity.fromModel(firstModel));
    assertEntities(db.todoStore().all().blockingGet(), firstModel);

    final ToDoModel secondModel=ToDoModel.creator().description("bar").build();
    db.todoStore().insert(ToDoEntity.fromModel(secondModel));
    assertEntities(db.todoStore().all().blockingGet(), secondModel, firstModel);

    final ToDoModel mutatedFirst=firstModel.toBuilder().isCompleted(true).build();
    db.todoStore().update(ToDoEntity.fromModel(mutatedFirst));
    assertEntities(db.todoStore().all().blockingGet(), secondModel, mutatedFirst);

    final ToDoModel mutatedSecond=secondModel.toBuilder().description("bar!").build();
    db.todoStore().update(ToDoEntity.fromModel(mutatedSecond));
    assertEntities(db.todoStore().all().blockingGet(), mutatedSecond, mutatedFirst);

    db.todoStore().delete(ToDoEntity.fromModel(mutatedFirst));
    assertEntities(db.todoStore().all().blockingGet(), mutatedSecond);
    db.todoStore().deleteAll();
    assertEntities(db.todoStore().all().blockingGet());
  }

  @Test
  public void repository() {
    final ToDoRepository repo=ToDoRepository.get(InstrumentationRegistry.getTargetContext());

    assertModels(repo.all().blockingGet());

    final ToDoModel firstModel=ToDoModel.creator().description("foo").notes("hello, world!").build();
    repo.add(firstModel);
    assertModels(repo.all().blockingGet(), firstModel);

    final ToDoModel secondModel=ToDoModel.creator().description("bar").build();
    repo.add(secondModel);
    assertModels(repo.all().blockingGet(), secondModel, firstModel);

    final ToDoModel mutatedFirst=firstModel.toBuilder().isCompleted(true).build();
    repo.replace(mutatedFirst);
    assertModels(repo.all().blockingGet(), secondModel, mutatedFirst);

    final ToDoModel mutatedSecond=secondModel.toBuilder().description("bar!").build();
    repo.replace(mutatedSecond);
    assertModels(repo.all().blockingGet(), mutatedSecond, mutatedFirst);

    repo.delete(Collections.singletonList(mutatedFirst));
    assertModels(repo.all().blockingGet(), mutatedSecond);
    repo.delete(Collections.singletonList(mutatedSecond));
    assertModels(repo.all().blockingGet());
  }

  private void assertModels(List<ToDoModel> items, ToDoModel... models) {
    assertThat(items, both(everyItem(isIn(models))).and(containsInAnyOrder(models)));
  }

  private void assertEntities(List<ToDoEntity> entities, ToDoModel... models) {
    assertEquals(models.length, entities.size());

    for (int i=0;i<models.length;i++) {
      assertEquals(models[i], entities.get(i).toModel());
    }
  }
}