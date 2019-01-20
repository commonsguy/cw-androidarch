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
    Constraints constraints=new Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .setRequiresBatteryNotLow(true)
      .build();
    OneTimeWorkRequest downloadWork=
      new OneTimeWorkRequest.Builder(DownloadWorker.class)
        .setConstraints(constraints)
        .setInputData(new Data.Builder()
          .putString(DownloadWorker.KEY_URL,
            "https://commonsware.com/Android/Android-1_0-CC.pdf")
          .putString(DownloadWorker.KEY_FILENAME, "oldbook.pdf")
          .build())
        .addTag("download")
        .build();

    WorkManager.getInstance().enqueue(downloadWork);

    final LiveData<WorkInfo> liveOpStatus=
      WorkManager.getInstance().getWorkInfoByIdLiveData(downloadWork.getId());

    liveWorkStatus.addSource(liveOpStatus, workStatus -> {
      liveWorkStatus.setValue(workStatus);

      if (workStatus.getState().isFinished()) {
        liveWorkStatus.removeSource(liveOpStatus);
      }
    });
  }
}
