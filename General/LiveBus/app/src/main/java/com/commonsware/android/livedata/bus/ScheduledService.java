/***
  Copyright (c) 2013-2017 CommonsWare, LLC
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

package com.commonsware.android.livedata.bus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import java.util.Calendar;
import java.util.Random;

public class ScheduledService extends JobIntentService {
  static final MutableLiveData<Intent> BUS=new MutableLiveData<>();
  private static final int UNIQUE_JOB_ID=1337;
  private static int NOTIFY_ID=1337;
  private Random rng=new Random();

  static void enqueueWork(Context ctxt, Intent i) {
    enqueueWork(ctxt, ScheduledService.class, UNIQUE_JOB_ID, i);
  }

  @Override
  protected void onHandleWork(Intent intent) {
    Intent event=new Intent(EventLogFragment.ACTION_EVENT);
    long now=Calendar.getInstance().getTimeInMillis();
    int random=rng.nextInt();

    event.putExtra(EventLogFragment.EXTRA_RANDOM, random);
    event.putExtra(EventLogFragment.EXTRA_TIME, now);

    if (BUS.hasActiveObservers()) {
      BUS.postValue(event);
    }
    else {
      NotificationCompat.Builder b=new NotificationCompat.Builder(this);
      Intent ui=new Intent(this, EventDemoActivity.class);

      b.setAutoCancel(true).setDefaults(Notification.DEFAULT_SOUND)
       .setContentTitle(getString(R.string.notif_title))
       .setContentText(Integer.toHexString(random))
       .setSmallIcon(android.R.drawable.stat_notify_more)
       .setTicker(getString(R.string.notif_title))
       .setContentIntent(PendingIntent.getActivity(this, 0, ui, 0));
      
      NotificationManager mgr=
          (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

      mgr.notify(NOTIFY_ID, b.build());
    }
  }
}
