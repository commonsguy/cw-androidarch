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

public enum FilterMode {
  ALL(0),
  COMPLETED(1),
  OUTSTANDING(2);

  private final int value;

  static FilterMode forValue(int value) {
    FilterMode result=ALL;

    if (value==1) {
      result=COMPLETED;
    }
    else if (value==2) {
      result=OUTSTANDING;
    }

    return(result);
  }

  FilterMode(int value) {
    this.value=value;
  }

  int getValue() {
    return(value);
  }
}
