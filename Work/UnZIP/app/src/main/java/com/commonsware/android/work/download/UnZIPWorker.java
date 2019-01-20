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
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UnZIPWorker extends Worker {
  public static final String KEY_ZIPFILE="zipFile";
  public static final String KEY_RESULTDIR="resultDir";

  public UnZIPWorker(@NonNull Context context,
                     @NonNull WorkerParameters workerParams) {
    super(context, workerParams);
  }

  @NonNull
  @Override
  public Result doWork() {
    File downloadedFile=new File(getInputData().getString(KEY_ZIPFILE));
    File dir=getApplicationContext().getCacheDir();
    String resultDirData=getInputData().getString(KEY_RESULTDIR);
    File resultDir=new File(dir, resultDirData==null ? "results" : resultDirData);

    try {
      ZipUtils.unzip(downloadedFile, resultDir, 2048, 1024*1024*16);
      downloadedFile.delete();
    }
    catch (Exception e) {
      Log.e(getClass().getSimpleName(), "Exception unZIPing file", e);

      return ListenableWorker.Result.failure();
    }

    return ListenableWorker.Result.success();
  }
}
