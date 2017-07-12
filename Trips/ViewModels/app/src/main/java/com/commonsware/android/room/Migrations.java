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

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;

public class Migrations {
  static final Migration FROM_1_TO_2=new Migration(1,2) {
    @Override
    public void migrate(SupportSQLiteDatabase db) {
      db.execSQL("CREATE TABLE IF NOT EXISTS `lodgings` (`id` TEXT, `title` TEXT, `duration` INTEGER, `priority` INTEGER, `startTime` INTEGER, `creationTime` INTEGER, `updateTime` INTEGER, `address` TEXT, `tripId` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
      db.execSQL("CREATE  INDEX `index_lodgings_tripId` ON `lodgings` (`tripId`)");
      db.execSQL("CREATE TABLE IF NOT EXISTS `flights` (`id` TEXT, `title` TEXT, `duration` INTEGER, `priority` INTEGER, `startTime` INTEGER, `creationTime` INTEGER, `updateTime` INTEGER, `departingAirport` TEXT, `arrivingAirport` TEXT, `airlineCode` TEXT, `flightNumber` TEXT, `seatNumber` TEXT, `tripId` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
      db.execSQL("CREATE  INDEX `index_flights_tripId` ON `flights` (`tripId`)");
    }
  };

  static final Migration BROKEN_1_TO_2=new Migration(1,2) {
    @Override
    public void migrate(SupportSQLiteDatabase db) {
      db.execSQL("CREATE TABLE IF NOT EXISTS `lodgings` (`id` TEXT, `title` TEXT, `duration` INTEGER, `priority` INTEGER, `startTime` INTEGER, `creationTime` INTEGER, `updateTime` INTEGER, `address` TEXT, `tripId` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
      db.execSQL("CREATE TABLE IF NOT EXISTS `flights` (`id` TEXT, `title` TEXT, `duration` INTEGER, `priority` INTEGER, `startTime` INTEGER, `creationTime` INTEGER, `updateTime` INTEGER, `departingAirport` TEXT, `arrivingAirport` TEXT, `airlineCode` TEXT, `flightNumber` TEXT, `seatNumber` TEXT, `tripId` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
    }
  };
}
