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

package com.commonsware.android.citypop;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.List;

public class CitiesFragment extends RecyclerViewFragment {
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setLayoutManager(new LinearLayoutManager(getActivity()));
    getRecyclerView()
      .addItemDecoration(new DividerItemDecoration(getActivity(),
        LinearLayoutManager.VERTICAL));

    CitiesViewModel vm=ViewModelProviders.of(this).get(CitiesViewModel.class);
    final CityAdapter adapter=new CityAdapter(getActivity().getLayoutInflater());

    vm.cities.observe(this, cities -> adapter.setList(cities));

    setAdapter(adapter);
  }

  private static class CityAdapter extends RecyclerView.Adapter<RowHolder> {
    private final LayoutInflater inflater;
    private List<City> cities;

    CityAdapter(LayoutInflater inflater) {
      this.inflater=inflater;
    }

    @Override
    public RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return(new RowHolder(inflater.inflate(R.layout.row, parent, false)));
    }

    @Override
    public void onBindViewHolder(RowHolder holder, int position) {
      City city=(cities==null ? null : cities.get(position));

      if (city==null) {
        holder.clear();
      }
      else {
        holder.bind(city);
      }
    }

    @Override
    public int getItemCount() {
      return(cities==null ? 0 : cities.size());
    }

    void setList(List<City> cities) {
      this.cities=cities;
    }
  }

  private static class RowHolder extends RecyclerView.ViewHolder {
    private final TextView cityLabel;
    private final TextView country;
    private final TextView population;

    RowHolder(View itemView) {
      super(itemView);

      cityLabel=itemView.findViewById(R.id.city);
      country=itemView.findViewById(R.id.country);
      population=itemView.findViewById(R.id.population);
    }

    void bind(City city) {
      cityLabel.setText(city.city);
      country.setText(city.country);
      population.setText(NumberFormat.getInstance().format(city.population));
    }

    void clear() {
      cityLabel.setText(null);
      country.setText(null);
      population.setText(null);
    }
  }
}
