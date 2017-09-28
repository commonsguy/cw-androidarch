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

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import java.util.UUID;

@Entity(indices={@Index(value="postalCode", unique=true)})
class Customer {
  @PrimaryKey
  @NonNull
  public final String id;

  public final String postalCode;
  public final String displayName;

  @Ignore
  Customer(String postalCode, String displayName) {
    this(UUID.randomUUID().toString(), postalCode, displayName);
  }

  Customer(String id, String postalCode, String displayName) {
    this.id=id;
    this.postalCode=postalCode;
    this.displayName=displayName;
  }
}