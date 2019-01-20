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

package com.commonsware.android.work.download;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import com.commonsware.cwac.security.ZipUtils;
import java.io.File;
import java.io.IOException;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class DownloadWorker extends Worker {
  public static final String KEY_URL="url";
  public static final String KEY_RESULTDIR="resultDir";

  public DownloadWorker(@NonNull Context context,
                        @NonNull WorkerParameters workerParams) {
    super(context, workerParams);
  }

  @NonNull
  @Override
  public Result doWork() {
    OkHttpClient client=new OkHttpClient();
    Request request=new Request.Builder()
      .url(getInputData().getString(KEY_URL))
      .build();

    File dir=getApplicationContext().getCacheDir();
    File downloadedFile=new File(dir, "temp.zip");

    if (downloadedFile.exists()) {
      downloadedFile.delete();
    }

    try (Response response=client.newCall(request).execute()) {
      BufferedSink sink=Okio.buffer(Okio.sink(downloadedFile));

      sink.writeAll(response.body().source());
      sink.close();
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(), "Exception downloading file", e);

      return ListenableWorker.Result.failure();
    }

    return ListenableWorker.Result.success(new Data.Builder()
      .putString(UnZIPWorker.KEY_ZIPFILE, downloadedFile.getAbsolutePath())
      .build());
  }
}
