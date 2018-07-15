package com.commonsware.android.livedata;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableField;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

public class SensorViewModel extends AndroidViewModel {
  public final SensorLiveData sensorLiveData;
  public final ObservableField<String> sensorReading=new ObservableField<>();

  public SensorViewModel(@NonNull Application app) {
    super(app);

    sensorLiveData=new SensorLiveData(app, Sensor.TYPE_LIGHT,
      SensorManager.SENSOR_DELAY_UI);
  }
}
