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

package com.commonsware.android.room.dao;

import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.HashSet;
import java.util.List;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class DaoTests {
  StuffDatabase db;
  StuffStore store;

  @Before
  public void setUp() {
    db=StuffDatabase.create(InstrumentationRegistry.getTargetContext(), true);
    store=db.stuffStore();
  }

  @After
  public void tearDown() {
    db.close();
  }

  @Test
  public void versionedThingy() {
    final VersionedThingy firstThingy=new VersionedThingy();

    store.insert(firstThingy);

    final VersionedThingy retrievedThingy=
      store.findById(firstThingy.id, firstThingy.versionCode);

    assertEquals(firstThingy.id, retrievedThingy.id);
    assertEquals(firstThingy.versionCode, retrievedThingy.versionCode);
  }

  @Test
  public void customer() {
    final HashSet<String> tags=new HashSet<>();

    tags.add("scuplture");
    tags.add("bronze");
    tags.add("slow-pay");

    final Location loc=new Location("");

    loc.setLatitude(40.7047282);
    loc.setLongitude(-74.0148544);

    final Customer firstCustomer=new Customer("10001", "Fearless Girl", loc, tags);

    assertEquals(loc.getLatitude(), firstCustomer.officeLocation.getLatitude(),
      .000001);
    assertEquals(loc.getLatitude(), firstCustomer.officeLocation.getLatitude(),
      .000001);
    assertEquals(tags, firstCustomer.tags);

    store.insert(firstCustomer);

    final List<Customer> result=store.findByPostalCodes(10, firstCustomer.postalCode);

    assertEquals(1, result.size());

    final Customer retrievedCustomer=result.get(0);

    assertEquals(firstCustomer.id, retrievedCustomer.id);
    assertEquals(firstCustomer.displayName, retrievedCustomer.displayName);
    assertEquals(firstCustomer.postalCode, retrievedCustomer.postalCode);
    assertEquals(loc.getLatitude(), retrievedCustomer.officeLocation.getLatitude(),
      .000001);
    assertEquals(loc.getLatitude(), retrievedCustomer.officeLocation.getLatitude(),
      .000001);
    assertEquals(tags, retrievedCustomer.tags);
    assertNotNull(retrievedCustomer.creationDate);

    final List<CustomerDisplayTuple> displayTuples=
      store.loadDisplayTuplesByPostalCodes(10, firstCustomer.postalCode);

    assertEquals(1, displayTuples.size());

    final CustomerDisplayTuple tuple=displayTuples.get(0);

    assertEquals(firstCustomer.id, tuple.id);
    assertEquals(firstCustomer.displayName, tuple.displayName);

    assertEquals(1, store.getCustomerCount());

    final CustomerStats stats=store.getCustomerStats();

    assertEquals(stats.count, 1);
    assertEquals(stats.max, firstCustomer.postalCode);

    final int deleted=store.nukeCertainCustomersFromOrbit(firstCustomer.id);

    assertEquals(1, deleted);
    assertEquals(0, store.getCustomerCount());
  }
}
