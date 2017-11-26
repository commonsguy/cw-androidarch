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

import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.commonsware.android.livedata.databinding.RowBinding;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends FragmentActivity {
  private EventLogAdapter adapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    RecyclerView rv=findViewById(R.id.transcript);

    SensorViewModel viewModel=ViewModelProviders.of(this).get(SensorViewModel.class);

    adapter=new EventLogAdapter();
    rv.setAdapter(adapter);

    Transformations
      .map(viewModel.getSensorLiveData(), RowModel::new)
      .observe(this, rowModel -> adapter.add(rowModel));
  }

  private class EventLogAdapter extends RecyclerView.Adapter<RowHolder> {
    private final ArrayList<RowModel> models=new ArrayList<>();

    @Override
    public RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return(new RowHolder(RowBinding.inflate(getLayoutInflater(), parent, false)));
    }

    @Override
    public void onBindViewHolder(RowHolder holder, int position) {
      holder.setModel(models.get(position));
    }

    @Override
    public int getItemCount() {
      return(models.size());
    }

    void add(RowModel model) {
      models.add(model);
      notifyItemInserted(getItemCount());
    }
  }

  static class RowHolder extends RecyclerView.ViewHolder {
    final RowBinding binding;

    RowHolder(RowBinding binding) {
      super(binding.getRoot());

      this.binding=binding;
    }

    void setModel(RowModel model) {
      binding.setModel(model);
    }
  }

  public static class RowModel {
    private static final DateFormat fmt=
      new SimpleDateFormat("HH:mm:ss", Locale.US);
    public final String date;
    public final float value;

    RowModel(SensorLiveData.Event event) {
      date=fmt.format(event.date);
      value=event.values[0];
    }
  }
}