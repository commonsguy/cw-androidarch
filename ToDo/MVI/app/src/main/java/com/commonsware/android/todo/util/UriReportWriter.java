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

package com.commonsware.android.todo.util;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import com.commonsware.android.todo.ReportWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import io.reactivex.Single;

public class UriReportWriter implements ReportWriter<Uri> {
  final ContentResolver cr;
  final Uri uri;

  public UriReportWriter(Context ctxt, Uri uri) {
    cr=ctxt.getContentResolver();
    this.uri=uri;
  }

  @Override
  public Single<Uri> write(final String report) {
    return(Single.create(e -> {
      OutputStream os=cr.openOutputStream(uri);
      OutputStreamWriter osw=new OutputStreamWriter(os);

      osw.write(report);
      osw.flush();
      osw.close();

      e.onSuccess(uri);
    }));
  }
}
