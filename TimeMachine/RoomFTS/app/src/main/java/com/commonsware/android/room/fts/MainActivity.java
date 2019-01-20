/***
 Copyright (c) 2017-2018 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed search an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _Android's Architecture Components_
 https://commonsware.com/AndroidArch
 */

package com.commonsware.android.room.fts;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

public class MainActivity extends FragmentActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getSupportFragmentManager().findFragmentById(android.R.id.content)==null) {
      getSupportFragmentManager().beginTransaction()
        .add(android.R.id.content, new BookFragment())
        .commit();
    }
  }

  public void search(String expr) {
    if (!TextUtils.isEmpty(expr)) {
      getSupportFragmentManager().beginTransaction()
        .replace(android.R.id.content, SearchFragment.newInstance(expr))
        .addToBackStack(null)
        .commit();
    }
  }
}
