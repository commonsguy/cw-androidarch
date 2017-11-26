/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.citypop;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.LivePagedListProvider;
import android.arch.paging.PagedList;
import android.arch.paging.TiledDataSource;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class PagingTests {
  private CityDatabase db;
  private City.Store store;
  private CountDownLatch responseLatch;
  private PagedList<City> pagedCities;
  private int loadedPosition;
  private int loadedCount;

  @Before
  public void setUp() {
    db=CityDatabase.get(InstrumentationRegistry.getTargetContext());
    store=db.cityStore();
  }

  @After
  public void tearDown() {
    db.close();
    assertTrue(InstrumentationRegistry
      .getTargetContext()
      .getDatabasePath(CityDatabase.DB_NAME)
      .delete());
  }

  @Test
  public void basic() {
    assertEquals(1063, store.allByPopulation().size());
  }

/*
  @Test
  public void tiledSequential() {
    TiledDataSource<City> cities=store.tiledByPopulation();
    List<City> all=store.allByPopulation();
    int count=cities.countItems();

    assertEquals(1063, count);

    for (int i=0;i<count;i+=20) {
      List<City> slice=cities.loadRange(i, 20);

      assertTrue(slice.size()==20 || slice.size()==count-i);
      assertThat(slice, is(all.subList(i, i+slice.size())));
    }
  }
*/

  @Test
  public void pagedSequential() throws InterruptedException {
    LivePagedListProvider<?, City> provider=store.pagedByPopulation();

    testPaging(provider.create(null, 50));
  }

/*
  @Test
  public void pagedSansPlaceholders() throws InterruptedException {
    LivePagedListProvider<?, City> provider=store.pagedByPopulation();
    PagedList.Config config=new PagedList.Config.Builder()
      .setPageSize(50)
      .setEnablePlaceholders(false)
      .build();

    testPaging(provider.create(null, config));
  }

  @Test
  public void pagedWithPrefetch() throws InterruptedException {
    LivePagedListProvider<?, City> provider=store.pagedByPopulation();
    PagedList.Config config=new PagedList.Config.Builder()
      .setPageSize(50)
      .setPrefetchDistance(100)
      .build();

    testPaging(provider.create(null, config));
  }
*/

  private void testPaging(final LiveData<PagedList<City>> livePages)
    throws InterruptedException {
    List<City> all=store.allByPopulation();

    responseLatch=new CountDownLatch(1);
    livePages.observeForever(CITY_OBSERVER);

    assertTrue(responseLatch.await(1, TimeUnit.SECONDS));
    assertNotNull(pagedCities);

    for (int i=0;i<50;i++) {
      assertEquals(all.get(i), pagedCities.get(i));
    }

    for (int i=51;i<all.size();i++) {
      assertEquals(null, pagedCities.get(i));
    }

    responseLatch=new CountDownLatch(1);
    pagedCities.addWeakCallback(null, new PagedList.Callback() {
      @Override
      public void onChanged(int position, int count) {
        loadedPosition=position;
        loadedCount=count;
        responseLatch.countDown();
      }

      @Override
      public void onInserted(int position, int count) {
        fail("How did we get here?");
      }

      @Override
      public void onRemoved(int position, int count) {
        fail("Ditto!");
      }
    });

    pagedCities.loadAround(175);
    assertTrue(responseLatch.await(1, TimeUnit.SECONDS));

    for (int i=0;i<loadedCount;i++) {
      assertEquals(all.get(loadedPosition+i), pagedCities.get(loadedPosition+i));
    }

    InstrumentationRegistry.getInstrumentation().runOnMainSync(
      () -> livePages.removeObserver(CITY_OBSERVER));
    pagedCities.detach();
  }

  private final Observer<PagedList<City>> CITY_OBSERVER=
    new Observer<PagedList<City>>() {
    @Override
    public void onChanged(@Nullable PagedList<City> cities) {
      pagedCities=cities;
      responseLatch.countDown();
    }
  };
}
