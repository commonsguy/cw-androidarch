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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.JsonReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class BulkInsertTests {
  private CityDatabase db;
  private City.Store store;

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
    if (db!=null) {
      db.close();
      InstrumentationRegistry
        .getTargetContext()
        .getDatabasePath(CityDatabase.DB_NAME)
        .delete();
    }
  }

  @Test
  public void doNothing() throws IOException {
    parseJson(city -> { });
  }

  @Test
  public void insertIndividually() throws IOException {
    db=CityDatabase.create(InstrumentationRegistry.getTargetContext(), false);
    store=db.cityStore();

    parseJson(city -> store.insert(city));
  }

  @Test
  public void insertBatched() throws IOException {
    db=CityDatabase.create(InstrumentationRegistry.getTargetContext(), false);
    store=db.cityStore();

    final ArrayList<City> cities=new ArrayList<>();

    parseJson(cities::add);

    store.insert(cities);
  }

  @Test
  public void insertPragmaIndividually() throws IOException {
    db=CityDatabase.create(InstrumentationRegistry.getTargetContext(), true);
    store=db.cityStore();

    parseJson(city -> store.insert(city));
  }

  @Test
  public void insertPragmaBatched() throws IOException {
    db=CityDatabase.create(InstrumentationRegistry.getTargetContext(), true);
    store=db.cityStore();

    final ArrayList<City> cities=new ArrayList<>();

    parseJson(cities::add);

    store.insert(cities);
  }

  private void parseJson(CityHandler handler) throws IOException {
    InputStream is=InstrumentationRegistry.getContext().getAssets().open("cities.json");
    JsonReader json=new JsonReader(new InputStreamReader(is));

    json.beginArray();

    while (json.hasNext()) {
      String name=null, country=null, id=null;
      int population=0;

      json.beginObject();

      while (json.hasNext()) {
        String prop=json.nextName();

        if (prop.equals("city")) {
          name=json.nextString();
        }
        else if (prop.equals("country")) {
          country=json.nextString();
        }
        else if (prop.equals("id")) {
          id=json.nextString();
        }
        else if (prop.equals("population")) {
          population=json.nextInt();
        }
        else {
          fail("Unexpected JSON property: "+prop);
        }
      }

      handler.handle(new City(id, country, name, population));

      json.endObject();
    }

    json.endArray();
  }

  private interface CityHandler {
    void handle(City city);
  }
}
