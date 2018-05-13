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

package com.commonsware.android.room;

import android.arch.persistence.room.TypeConverter;
import java.util.Date;

public class TypeTransmogrifier {
  @TypeConverter
  public static Long fromDate(Date date) {
    if (date==null) {
      return(null);
    }

    return(date.getTime());
  }

  @TypeConverter
  public static Date toDate(Long millisSinceEpoch) {
    if (millisSinceEpoch==null) {
      return(null);
    }

    return(new Date(millisSinceEpoch));
  }

  @TypeConverter
  public static Integer fromType(Note.Type type) {
    return type.value();
  }

  @TypeConverter
  public static Note.Type toType(Integer value) {
    return value==0 ? Note.Type.COMMENT : Note.Type.LINK;
  }
}
