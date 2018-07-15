package com.commonsware.android.livedata;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

public class SensorViewModel extends AndroidViewModel {
  public final MediatorLiveData<SensorLiveData.Event> sensorLiveData=
    new MediatorLiveData<>();

  public SensorViewModel(
    @NonNull Application app) {
    super(app);

    SensorLiveData opLiveData=
      new SensorLiveData(app, Sensor.TYPE_LIGHT, SensorManager.SENSOR_DELAY_UI);

    sensorLiveData.addSource(opLiveData, sensorLiveData::setValue);
  }
}
