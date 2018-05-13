/***
 Copyright (c) 2018 CommonsWare, LLC
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

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName="paragraphs",
  foreignKeys=@ForeignKey(entity=ChapterEntity.class, parentColumns="id",
    childColumns="chapterId", onDelete=ForeignKey.CASCADE),
  indices={@Index(value="chapterId")})
public class ParagraphEntity {
  @PrimaryKey
  long sequence;
  String prose;
  long chapterId;

  ParagraphEntity(String prose) {
    this.prose=prose;
  }
}
