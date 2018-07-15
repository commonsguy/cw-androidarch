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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.test.WorkManagerTestInitHelper;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DownloadWorkerTest {
  private File expected;

  @Before
  public void setUp() {
    WorkManagerTestInitHelper
      .initializeTestWorkManager(InstrumentationRegistry.getTargetContext());

    expected=
      new File(InstrumentationRegistry.getTargetContext().getCacheDir(),
        "oldbook.pdf");

    if (expected.exists()) {
      expected.delete();
    }
  }

  @Test
  public void download() {
    assertFalse(expected.exists());

    WorkManager.getInstance().enqueue(buildWorkRequest(null));

    assertTrue(expected.exists());
  }

  @Test
  public void downloadWithConstraints() {
    Constraints constraints=new Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .setRequiresBatteryNotLow(true)
      .build();
    WorkRequest work=buildWorkRequest(constraints);

    assertFalse(expected.exists());

    WorkManager.getInstance().enqueue(work);
    WorkManagerTestInitHelper.getTestDriver().setAllConstraintsMet(work.getId());

    assertTrue(expected.exists());
  }

  private WorkRequest buildWorkRequest(Constraints constraints) {
    OneTimeWorkRequest.Builder builder=
      new OneTimeWorkRequest.Builder(DownloadWorker.class)
        .setInputData(new Data.Builder()
          .putString(DownloadWorker.KEY_URL,
            "https://commonsware.com/Android/Android-1_0-CC.pdf")
          .putString(DownloadWorker.KEY_FILENAME, "oldbook.pdf")
          .build())
        .addTag("download");

    if (constraints!=null) {
      builder.setConstraints(constraints);
    }

    return builder.build();
  }
}
