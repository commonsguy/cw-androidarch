package com.commonsware.android.room;

import android.arch.paging.DataSource;

interface TypedDao<T extends Note> {
  DataSource.Factory<Integer, T> pagedStuffForTrip(String tripId);
}

