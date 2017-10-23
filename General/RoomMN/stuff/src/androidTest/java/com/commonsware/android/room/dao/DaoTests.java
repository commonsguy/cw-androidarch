/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _Android's Architecture Components_
 https://commonsware.com/AndroidArch
 */

package com.commonsware.android.room.dao;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DaoTests {
  private StuffDatabase db;
  private StuffStore store;
  private CountDownLatch responseLatch;

  @Before
  public void setUp() {
    db=StuffDatabase.create(InstrumentationRegistry.getTargetContext(), true);
    store=db.stuffStore();
    responseLatch=new CountDownLatch(1);
  }

  @After
  public void tearDown() {
    db.close();
  }

  @Test
  public void customer() {
    final HashSet<String> tags=new HashSet<>();

    tags.add("scuplture");
    tags.add("bronze");
    tags.add("slow-pay");

    final LocationColumns loc=new LocationColumns(40.7047282, -74.0148544);

    final Customer firstCustomer=new Customer("10001", "Fearless Girl", loc, tags);

    assertEquals(loc.latitude, firstCustomer.officeLocation.latitude,
      .000001);
    assertEquals(loc.longitude, firstCustomer.officeLocation.longitude,
      .000001);
    assertEquals(tags, firstCustomer.tags);

    store.insert(firstCustomer);

    final List<Customer> result=store.findByPostalCodes(10, firstCustomer.postalCode);

    assertEquals(1, result.size());

    final Customer retrievedCustomer=result.get(0);

    assertEquals(firstCustomer.id, retrievedCustomer.id);
    assertEquals(firstCustomer.displayName, retrievedCustomer.displayName);
    assertEquals(firstCustomer.postalCode, retrievedCustomer.postalCode);
    assertEquals(loc.latitude, retrievedCustomer.officeLocation.latitude,
      .000001);
    assertEquals(loc.longitude, retrievedCustomer.officeLocation.longitude,
      .000001);
    assertEquals(tags, retrievedCustomer.tags);
    assertEquals(firstCustomer.creationDate, retrievedCustomer.creationDate);

    final List<Customer> near=store.findCustomersAt(loc.latitude, loc.longitude);

    assertEquals(1, near.size());

    final Customer nearCustomer=near.get(0);

    assertEquals(firstCustomer.id, nearCustomer.id);
    assertEquals(firstCustomer.displayName, nearCustomer.displayName);
    assertEquals(firstCustomer.postalCode, nearCustomer.postalCode);
    assertEquals(loc.latitude, nearCustomer.officeLocation.latitude,
      .000001);
    assertEquals(loc.longitude, nearCustomer.officeLocation.longitude,
      .000001);
    assertEquals(tags, nearCustomer.tags);
    assertEquals(firstCustomer.creationDate, nearCustomer.creationDate);

    final int deleted=store.nukeCertainCustomersFromOrbit(firstCustomer.id);

    assertEquals(1, deleted);
    assertEquals(0, store.getCustomerCount());
  }

  @Test
  public void categories() {
    final Category root=new Category("Root!");

    store.insert(root);

    List<Category> results=store.selectAllCategories();

    assertEquals(1, results.size());
    assertIdentical(root, results.get(0));
    assertIdentical(root, store.findRootCategory());

    final Category child=new Category("Child!", root.id);

    store.insert(child);

    results=store.findChildCategories(root.id);

    assertEquals(1, results.size());
    assertIdentical(child, results.get(0));
    assertEquals(2, store.selectAllCategories().size());

    store.delete(root);
    results=store.selectAllCategories();
    assertEquals(0, results.size());
  }

  @Test
  public void simpleJoin() throws InterruptedException {
    final HashSet<String> tags=new HashSet<>();

    tags.add("sculpture");
    tags.add("bronze");
    tags.add("slow-pay");

    final LocationColumns loc=new LocationColumns(40.7047282, -74.0148544);

    final Customer firstCustomer=new Customer("10001", "Fearless Girl", loc, tags);

    tags.remove("slow-pay");
    tags.add("large");

    final Customer secondCustomer=new Customer("10002", "Charging Bull", loc, tags);

    store.insert(firstCustomer, secondCustomer);

    final Category root=new Category("Root!");
    final Category child=new Category("Child!", root.id);

    store.insert(root, child);

    final Customer.CategoryJoin join=
      new Customer.CategoryJoin(root.id, secondCustomer.id);

    store.insert(join);

    final List<Customer> customersForCategory=
      store.customersForCategory(root.id);

    assertEquals(1, customersForCategory.size());
    assertIdentical(secondCustomer, customersForCategory.get(0));
    assertEquals(0, store.customersForCategory(child.id).size());

    final List<Category> categoriesForCustomer=
      store.categoriesForCustomer(secondCustomer.id);

    assertEquals(1, categoriesForCustomer.size());
    assertIdentical(root, categoriesForCustomer.get(0));
    assertEquals(0, store.categoriesForCustomer(firstCustomer.id).size());

    store.delete(join);
    assertEquals(0, store.customersForCategory(root.id).size());
    assertEquals(0, store.customersForCategory(child.id).size());
    assertEquals(0, store.categoriesForCustomer(firstCustomer.id).size());
    assertEquals(0, store.categoriesForCustomer(secondCustomer.id).size());

    final Customer.CategoryJoin join1=
      new Customer.CategoryJoin(root.id, firstCustomer.id);
    final Customer.CategoryJoin join2=
      new Customer.CategoryJoin(child.id, firstCustomer.id);

    store.insert(join1, join2);
    assertEquals(1, store.customersForCategory(root.id).size());
    assertEquals(1, store.customersForCategory(child.id).size());
    assertEquals(2, store.categoriesForCustomer(firstCustomer.id).size());
    assertEquals(0, store.categoriesForCustomer(secondCustomer.id).size());

    final Customer.CategoryJoin join3=
      new Customer.CategoryJoin(child.id, secondCustomer.id);

    store.insert(join3);
    store.delete(join1);
    assertEquals(0, store.customersForCategory(root.id).size());
    assertEquals(2, store.customersForCategory(child.id).size());
    assertEquals(1, store.categoriesForCustomer(firstCustomer.id).size());
    assertEquals(1, store.categoriesForCustomer(secondCustomer.id).size());

/*
    final LiveData<List<Customer>> liveCustomers=store.allCustomers();
    final LiveData<List<String>> liveCustomerIds=Transformations.map(
      liveCustomers, new Function<List<Customer>, List<String>>() {
        @Override
        public List<String> apply(List<Customer> customers) {
          ArrayList<String> result=new ArrayList<>();

          for (Customer customer : customers) {
            result.add(customer.id);
          }

          return(result);
        }
      });
    final LiveData<List<Category>> liveCategories=Transformations.switchMap(
      liveCustomerIds, new Function<List<String>, LiveData<List<Category>>>() {
        @Override
        public LiveData<List<Category>> apply(List<String> customerIds) {
          return(store.categoriesForCustomers(customerIds));
        }
      });

    responseLatch=new CountDownLatch(2);
    liveCategories.observeForever(new Observer<List<Category>>() {
      @Override
      public void onChanged(@Nullable List<Category> category) {
        responseLatch.countDown();
      }
    });

    responseLatch.await(2, TimeUnit.SECONDS);
    assertEquals(0, responseLatch.getCount());
*/

    assertEquals(2, store.getJoinCount());
    store.nukeCertainCustomersFromOrbit(firstCustomer.id);
    assertEquals(1, store.getJoinCount());
    store.delete(root);
    assertEquals(0, store.getJoinCount());
  }

  private void assertIdentical(Category one, Category two) {
    assertEquals(one.id, two.id);
    assertEquals(one.title, two.title);
    assertEquals(one.parentId, two.parentId);
  }

  private void assertIdentical(Customer one, Customer two) {
    assertEquals(one.id, two.id);
    assertEquals(one.displayName, two.displayName);
    assertEquals(one.postalCode, two.postalCode);
    assertEquals(one.creationDate, two.creationDate);
  }
}
