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

package com.commonsware.android.livedata;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends FragmentActivity {
  private EventLogAdapter adapter;
  private State state;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    RecyclerView rv=findViewById(R.id.transcript);

    state=(State)getLastCustomNonConfigurationInstance();

    if (state==null) {
      state=new State();
      state.sensorLiveData=
        new SensorLiveData(this, Sensor.TYPE_LIGHT,
          SensorManager.SENSOR_DELAY_UI);
    }

    adapter=new EventLogAdapter();
    rv.setAdapter(adapter);

    state.sensorLiveData.observe(this, event -> adapter.add(event));
  }

  @Override
  public Object onRetainCustomNonConfigurationInstance() {
    return(state);
  }

  private static class State {
    final ArrayList<SensorLiveData.Event> events=new ArrayList<>();
    SensorLiveData sensorLiveData;
  }

  private class EventLogAdapter extends RecyclerView.Adapter<RowHolder> {
    @Override
    public RowHolder onCreateViewHolder(ViewGroup parent,
                                        int viewType) {
      View row=
        getLayoutInflater()
          .inflate(android.R.layout.simple_list_item_1, parent, false);

      return(new RowHolder(row));
    }

    @Override
    public void onBindViewHolder(RowHolder holder,
                                 int position) {
      holder.bind(state.events.get(position));
    }

    @Override
    public int getItemCount() {
      return(state.events.size());
    }

    void add(SensorLiveData.Event what) {
      if (!state.events.contains(what)) {
        state.events.add(what);
        notifyItemInserted(getItemCount()-1);
      }
    }
  }

  private static class RowHolder extends RecyclerView.ViewHolder {
    private static final DateFormat fmt=
      new SimpleDateFormat("HH:mm:ss", Locale.US);
    private final TextView tv;

    RowHolder(View itemView) {
      super(itemView);

      tv=itemView.findViewById(android.R.id.text1);
    }

    void bind(SensorLiveData.Event event) {
      tv.setText(String.format("%s = %f", fmt.format(event.date),
        event.values[0]));
    }
  }
}