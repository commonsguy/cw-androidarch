/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.recyclerview.videolist;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.util.Log;

public class LifecycleApplication extends Application
  implements LifecycleObserver {
  @Override
  public void onCreate() {
    super.onCreate();

    ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
  public void created() {
    Log.d(getClass().getSimpleName(), "ON_CREATE");
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  public void started() {
    Log.d(getClass().getSimpleName(), "ON_START");
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  public void resumed() {
    Log.d(getClass().getSimpleName(), "ON_RESUME");
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  public void paused() {
    Log.d(getClass().getSimpleName(), "ON_PAUSE");
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
  public void stopped() {
    Log.d(getClass().getSimpleName(), "ON_STOP");
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  public void destroyed() {
    Log.d(getClass().getSimpleName(), "ON_DESTROY");
  }
}
