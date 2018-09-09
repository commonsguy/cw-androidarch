/***
 Copyright (c) 2017-2018 CommonsWare, LLC
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

package com.commonsware.android.livedata;

import android.arch.lifecycle.LiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ServiceLiveData extends LiveData<Float>
  implements ServiceConnection {
  private final Context app;
  private ILightReporter reporter;

  ServiceLiveData(Context ctxt) {
    app=ctxt.getApplicationContext();
  }

  @Override
  protected void onActive() {
    super.onActive();

    app.bindService(new Intent(app, LightSensorService.class), this, Context.BIND_AUTO_CREATE);
  }

  @Override
  protected void onInactive() {
    goAway();
    app.unbindService(this);

    super.onInactive();
  }

  @Override
  public void onServiceConnected(ComponentName name, IBinder service) {
    reporter=ILightReporter.Stub.asInterface(service);

    try {
      reporter.registerCallback(cb);
    }
    catch (RemoteException e) {
      Log.e(getClass().getSimpleName(), "Exception registering callback", e);
    }
  }

  @Override
  public void onServiceDisconnected(ComponentName name) {
    reporter=null;
  }

  private void goAway() {
    try {
      reporter.unregisterCallback(cb);
    }
    catch (RemoteException e) {
      Log.e(getClass().getSimpleName(), "Exception unregistering callback", e);
    }
    finally {
      reporter=null;
    }
  }

  private final ILightCallback cb=new ILightCallback.Stub() {
    @Override
    public void onLightEvent(float value) {
      postValue(value);
    }
  };
}
