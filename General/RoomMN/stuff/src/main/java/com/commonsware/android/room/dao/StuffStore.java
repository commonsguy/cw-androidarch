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

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import java.util.List;

@Dao
interface StuffStore {
  @Insert
  void insert(Customer... customers);

  @Query("SELECT * FROM Customer WHERE postalCode IN (:postalCodes) LIMIT :max")
  List<Customer> findByPostalCodes(int max, String... postalCodes);

  @Query("SELECT COUNT(*) FROM Customer")
  int getCustomerCount();

  @Query("DELETE FROM Customer WHERE id IN (:ids)")
  int nukeCertainCustomersFromOrbit(String... ids);

  @Query("SELECT * FROM Customer WHERE ABS(latitude-:lat)<.000001 AND ABS(longitude-:lon)<.000001")
  List<Customer> findCustomersAt(double lat, double lon);

  @Query("SELECT * FROM categories")
  List<Category> selectAllCategories();

  @Query("SELECT * FROM categories WHERE parentId IS NULL")
  Category findRootCategory();

  @Query("SELECT * FROM categories WHERE parentId=:parentId")
  List<Category> findChildCategories(String parentId);

  @Insert
  void insert(Category... categories);

  @Delete
  void delete(Category... categories);

  @Insert
  void insert(Customer.CategoryJoin... joins);

  @Delete
  void delete(Customer.CategoryJoin... joins);

  @Query("SELECT categories.* FROM categories\n"+
    "INNER JOIN customer_category_join ON categories.id=customer_category_join.categoryId\n"+
    "WHERE customer_category_join.customerId=:customerId")
  List<Category> categoriesForCustomer(String customerId);

  @Query("SELECT Customer.* FROM Customer\n"+
    "INNER JOIN customer_category_join ON Customer.id=customer_category_join.customerId\n"+
    "WHERE customer_category_join.categoryId=:categoryId")
  List<Customer> customersForCategory(String categoryId);

  @Query("SELECT * FROM Customer")
  LiveData<List<Customer>> allCustomers();

/*
  @Query("SELECT categories.* FROM categories\n"+
    "INNER JOIN customer_category_join ON categories.id=customer_category_join.categoryId\n"+
    "WHERE customer_category_join.customerId IN (:customerIds)")
  LiveData<List<Category>> categoriesForCustomers(List<String> customerIds);
*/

  @Query("SELECT COUNT(*) FROM customer_category_join")
  int getJoinCount();
}
