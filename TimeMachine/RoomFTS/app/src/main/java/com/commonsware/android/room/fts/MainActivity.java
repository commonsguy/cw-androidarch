package com.commonsware.android.room.fts;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

public class MainActivity extends FragmentActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getSupportFragmentManager().findFragmentById(android.R.id.content)==null) {
      getSupportFragmentManager().beginTransaction()
        .add(android.R.id.content, new BookFragment())
        .commit();
    }
  }

  public void search(String expr) {
    if (!TextUtils.isEmpty(expr)) {
      getSupportFragmentManager().beginTransaction()
        .replace(android.R.id.content, SearchFragment.newInstance(expr))
        .addToBackStack(null)
        .commit();
    }
  }
}
