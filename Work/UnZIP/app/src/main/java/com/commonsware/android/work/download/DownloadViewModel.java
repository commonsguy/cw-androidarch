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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class DownloadViewModel extends ViewModel {
  public final MediatorLiveData<WorkInfo> liveWorkStatus=new MediatorLiveData<>();

  public void doTheDownload() {
    OneTimeWorkRequest downloadWork=
      new OneTimeWorkRequest.Builder(DownloadWorker.class)
        .setConstraints(new Constraints.Builder()
          .setRequiredNetworkType(NetworkType.CONNECTED)
          .setRequiresBatteryNotLow(true)
          .build())
        .setInputData(new Data.Builder()
          .putString(DownloadWorker.KEY_URL,
            "https://commonsware.com/Android/source_1_0.zip")
          .build())
        .addTag("download")
        .build();
    OneTimeWorkRequest unZIPWork=
      new OneTimeWorkRequest.Builder(UnZIPWorker.class)
        .setConstraints(new Constraints.Builder()
          .setRequiresStorageNotLow(true)
          .setRequiresBatteryNotLow(true)
          .build())
        .setInputData(new Data.Builder()
          .putString(DownloadWorker.KEY_RESULTDIR, "unzipped")
          .build())
        .addTag("unZIP")
        .build();

    WorkManager.getInstance()
      .beginWith(downloadWork)
      .then(unZIPWork)
      .enqueue();

    final LiveData<WorkInfo> liveOpStatus=
      WorkManager.getInstance().getWorkInfoByIdLiveData(unZIPWork.getId());

    liveWorkStatus.addSource(liveOpStatus, workStatus -> {
      liveWorkStatus.setValue(workStatus);

      if (workStatus.getState().isFinished()) {
        liveWorkStatus.removeSource(liveOpStatus);
      }
    });
  }
}
