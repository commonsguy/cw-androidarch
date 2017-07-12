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

package com.commonsware.android.livedata;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.util.Date;

class SensorLiveData extends LiveData<SensorLiveData.Event> {
  final private SensorManager sensorManager;
  private final Sensor sensor;
  private final int delay;

  SensorLiveData(Context ctxt, int sensorType, int delay) {
    sensorManager=
      (SensorManager)ctxt.getApplicationContext()
        .getSystemService(Context.SENSOR_SERVICE);
    this.sensor=sensorManager.getDefaultSensor(sensorType);
    this.delay=delay;

    if (this.sensor==null) {
      throw new IllegalStateException("Cannot obtain the requested sensor");
    }
  }

  @Override
  protected void onActive() {
    super.onActive();

    sensorManager.registerListener(listener, sensor, delay);
  }

  @Override
  protected void onInactive() {
    sensorManager.unregisterListener(listener);

    super.onInactive();
  }

  final private SensorEventListener listener=new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      setValue(new Event(event));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
      // unused
    }
  };

  class Event {
    final Date date=new Date();
    final float[] values;

    Event(SensorEvent event) {
      values=new float[event.values.length];

      System.arraycopy(event.values, 0, values, 0, event.values.length);
    }
  }
}
