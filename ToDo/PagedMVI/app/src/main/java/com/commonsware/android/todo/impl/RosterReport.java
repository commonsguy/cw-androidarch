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

import android.content.Context;
import android.text.format.DateUtils;
import com.commonsware.android.todo.Report;
import com.commonsware.android.todo.ReportWriter;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import java.util.Calendar;
import java.util.List;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Function;

public class RosterReport implements Report<PagedDataSnapshot<ToDoModel, String>> {
  private static final String TEMPLATE="<h1>To-Do Items</h1>\n"+
    "{{#this}}\n"+
    "<h2>{{description}}</h2>\n"+
    "<p>{{#isCompleted}}<b>COMPLETED</b> &mdash; {{/isCompleted}}Created on: {{createdOn}}</p>\n"+
    "<p>{{notes}}</p>\n"+
    "{{/this}}";
  private final Template template;

  public RosterReport(final Context ctxt) {
    template=Mustache.compiler()
      .nullValue("")
      .withFormatter(value -> {
        if (value instanceof Calendar) {
          Calendar date=(Calendar)value;

          return(DateUtils
            .getRelativeDateTimeString(ctxt, date.getTimeInMillis(),
              DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0)
            .toString());
        }
        else {
          return(String.valueOf(value));
        }
      }).compile(TEMPLATE);
  }

  @Override
  public <D> Single<D> generate(PagedDataSnapshot<ToDoModel, String> snapshot,
                                ReportWriter<D> writer) {
    return Single.just(snapshot.dataSource())
      .map(dataSource -> {
        int size=dataSource.countItems();

        return dataSource.loadRangeAtPosition(0, size);
      })
      .flatMap(models -> writer.write(template.execute(models)));
  }
}
