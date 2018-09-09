/***
 Copyright (c) 2018 CommonsWare, LLC
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

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

public class LightSensorService extends Service {
  private SensorManager sensorManager;
  private Reporter reporter=new Reporter();

  @Override
  public void onCreate() {
    super.onCreate();

    sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return reporter;
  }

  private class Reporter extends ILightReporter.Stub {
    private RemoteCallbackList<ILightCallback> callbacks=new RemoteCallbackList<>();

    @Override
    public void registerCallback(ILightCallback cb) {
      callbacks.register(cb);

      if (callbacks.getRegisteredCallbackCount()==1) {
        sensorManager.registerListener(listener,
          sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
          SensorManager.SENSOR_DELAY_UI);
      }
    }

    @Override
    public void unregisterCallback(ILightCallback cb) {
      callbacks.unregister(cb);

      if (callbacks.getRegisteredCallbackCount()==0) {
        sensorManager.unregisterListener(listener);
      }
    }

    final private SensorEventListener listener=new SensorEventListener() {
      @Override
      public void onSensorChanged(SensorEvent event) {
        callbacks.beginBroadcast();

        for (int i=0;i<callbacks.getRegisteredCallbackCount();i++) {
          ILightCallback cb=callbacks.getBroadcastItem(i);

          try {
            cb.onLightEvent(event.values[0]);
          }
          catch (RemoteException e) {
            // we tried!
          }
        }

        callbacks.finishBroadcast();
      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // unused
      }
    };
  }
}
