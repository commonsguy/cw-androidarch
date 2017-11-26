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

package com.commonsware.android.lifecycle;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends FragmentActivity {
  private EventLogAdapter adapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    setTitle(getString(R.string.title, hashCode()));

    RecyclerView rv=findViewById(R.id.transcript);

    adapter=new EventLogAdapter(getLastCustomNonConfigurationInstance());
    rv.setAdapter(adapter);

    getLifecycle().addObserver(new LObserver(adapter));
  }

  @Override
  public Object onRetainCustomNonConfigurationInstance() {
    return(adapter.getModel());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.launch) {
      startActivity(new Intent(this, getClass()));
      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  static class LObserver implements DefaultLifecycleObserver {
    private final EventLogAdapter adapter;

    LObserver(EventLogAdapter adapter) {
      this.adapter=adapter;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
      adapter.add("ON_CREATE");
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
      adapter.add("ON_START");
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
      adapter.add("ON_RESUME");
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
      adapter.add("ON_PAUSE");
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
      adapter.add("ON_STOP");
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
      adapter.add("ON_DESTROY");
    }
  }

  private static class Event {
    final Date when;
    final String what;

    Event(String what) {
      when=new Date();
      this.what=what;
    }
  }

  private class EventLogAdapter extends RecyclerView.Adapter<RowHolder> {
    private final ArrayList<Event> events=new ArrayList<>();

    EventLogAdapter(Object state) {
      super();

      if (state!=null) {
        events.addAll((ArrayList<Event>)state);
      }
    }

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
      holder.bind(events.get(position));
    }

    @Override
    public int getItemCount() {
      return(events.size());
    }

    void add(String what) {
      events.add(new Event(what));
      notifyItemInserted(getItemCount());
    }

    Object getModel() {
      return(events);
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

    void bind(Event event) {
      tv.setText(String.format("%s = %s", fmt.format(event.when),
        event.what));
    }
  }
}