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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import com.commonsware.android.todo.R;
import com.commonsware.android.todo.impl.FilterMode;
import com.commonsware.android.todo.impl.PagedDataSnapshot;
import com.commonsware.android.todo.impl.ToDoModel;
import com.commonsware.android.todo.impl.ToDoRepository;
import com.commonsware.android.todo.impl.RosterReport;

public class GistUploadService extends JobIntentService {
  private static final int UNIQUE_JOB_ID=34321;
  private static final String EXTRA_FILTER_MODE="filterMode";
  private static final String CHANNEL_WHATEVER="channel_whatever";
  private static int NOTIFY_ID=1337;
  private NotificationManager mgr;

  public static void upload(Context ctxt, FilterMode filterMode) {
    Intent i=new Intent(ctxt, GistUploadService.class)
      .putExtra(EXTRA_FILTER_MODE, filterMode);

    enqueueWork(ctxt, GistUploadService.class, UNIQUE_JOB_ID, i);
  }

  @Override
  public void onCreate() {
    super.onCreate();

    mgr=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
      mgr.getNotificationChannel(CHANNEL_WHATEVER)==null) {
      mgr.createNotificationChannel(new NotificationChannel(CHANNEL_WHATEVER,
        "Whatever", NotificationManager.IMPORTANCE_DEFAULT));
    }
  }

  @Override
  protected void onHandleWork(@NonNull Intent i) {
    final FilterMode filterMode=
      (FilterMode)i.getSerializableExtra(EXTRA_FILTER_MODE);
    ToDoRepository repo=ToDoRepository.get(this);
    PagedDataSnapshot<ToDoModel, String> snapshot=repo.allForFilter(filterMode);
    String url=new RosterReport(GistUploadService.this)
      .generate(snapshot, new GistReportWriter())
      .blockingGet();
    NotificationCompat.Builder b=
      new NotificationCompat.Builder(this, CHANNEL_WHATEVER);

    b.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL);
      b.setContentTitle(getString(R.string.notif_title))
        .setContentText(getString(R.string.notif_text))
        .setSmallIcon(R.drawable.ic_stat_upload);

    Intent outbound=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    PendingIntent pi=PendingIntent.getActivity(this, 0,
      outbound, PendingIntent.FLAG_UPDATE_CURRENT);

    b.setContentIntent(pi);
    mgr.notify(NOTIFY_ID, b.build());
  }
}
