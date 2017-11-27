/***
  Copyright (c) 2008-2017 CommonsWare, LLC
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

package com.commonsware.android.room;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Date;
import java.util.List;

public class TripsFragment extends RecyclerViewFragment {
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setLayoutManager(new LinearLayoutManager(getActivity()));
    getRecyclerView()
      .addItemDecoration(new DividerItemDecoration(getActivity(),
        LinearLayoutManager.VERTICAL));

    TripRosterViewModel vm=((MainActivity)getActivity()).getViewModel();

    vm.allTrips.observe(this, trips -> {
      setAdapter(new TripsAdapter(trips, getActivity().getLayoutInflater()));

      if (trips==null || trips.size()==0) {
        final TripStore store=TripDatabase.get(getActivity()).tripStore();

        new Thread() {
          @Override
          public void run() {
            store.insert(new Trip("Vacation!", 10080, Priority.MEDIUM, new Date()),
              new Trip("Business Trip", 4320, Priority.OMG, new Date()));
          }
        }.start();
      }
    });
  }

  private static class TripsAdapter extends RecyclerView.Adapter<RowHolder> {
    private final List<Trip> trips;
    private final LayoutInflater inflater;

    private TripsAdapter(List<Trip> trips, LayoutInflater inflater) {
      this.trips=trips;
      this.inflater=inflater;
    }

    @Override
    public RowHolder onCreateViewHolder(ViewGroup parent,
                                        int viewType) {
      return(new RowHolder(inflater.inflate(android.R.layout.simple_list_item_1,
        parent, false)));
    }

    @Override
    public void onBindViewHolder(RowHolder holder, int position) {
      holder.bind(trips.get(position));
    }

    @Override
    public int getItemCount() {
      return(trips==null ? 0 : trips.size());
    }
  }

  private static class RowHolder extends RecyclerView.ViewHolder {
    private final TextView title;

    RowHolder(View itemView) {
      super(itemView);

      title=itemView.findViewById(android.R.id.text1);
    }

    void bind(Trip trip) {
      title.setText(trip.title);
    }
  }
}
