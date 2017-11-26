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

package com.commonsware.android.recyclerview.videolist;

import android.app.Application;
import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.support.annotation.NonNull;
import android.util.Log;

public class LifecycleApplication extends Application
  implements DefaultLifecycleObserver {
  @Override
  public void onCreate() {
    super.onCreate();

    ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
  }

  @Override
  public void onCreate(@NonNull LifecycleOwner owner) {
    Log.d(getClass().getSimpleName(), "ON_CREATE");
  }

  @Override
  public void onStart(@NonNull LifecycleOwner owner) {
    Log.d(getClass().getSimpleName(), "ON_START");
  }

  @Override
  public void onResume(@NonNull LifecycleOwner owner) {
    Log.d(getClass().getSimpleName(), "ON_RESUME");
  }

  @Override
  public void onPause(@NonNull LifecycleOwner owner) {
    Log.d(getClass().getSimpleName(), "ON_PAUSE");
  }

  @Override
  public void onStop(@NonNull LifecycleOwner owner) {
    Log.d(getClass().getSimpleName(), "ON_STOP");
  }

  @Override
  public void onDestroy(@NonNull LifecycleOwner owner) {
    Log.d(getClass().getSimpleName(), "ON_DESTROY");
  }
}
