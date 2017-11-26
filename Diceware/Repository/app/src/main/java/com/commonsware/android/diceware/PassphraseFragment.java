/***
  Copyright (c) 2014 CommonsWare, LLC
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

package com.commonsware.android.diceware;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PassphraseFragment extends Fragment {
  private static final int REQUEST_OPEN=1337;
  private static final int[] WORD_COUNT_MENU_IDS={
    R.id.word_count_4,
    R.id.word_count_5,
    R.id.word_count_6,
    R.id.word_count_7,
    R.id.word_count_8,
    R.id.word_count_9,
    R.id.word_count_10
  };
  private TextView passphrase;
  private PassphraseViewModel viewModel;
  private Menu menu;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setHasOptionsMenu(true);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return(inflater.inflate(R.layout.activity_main, container, false));
  }

  @Override
  public void onViewCreated(View view, Bundle state) {
    super.onViewCreated(view, state);

    passphrase=view.findViewById(R.id.passphrase);
    viewModel=ViewModelProviders
      .of(this, new PassphraseViewModel.Factory(getActivity(), state))
      .get(PassphraseViewModel.class);
    updateMenu();
    viewModel.words().observe(this,
      words -> passphrase.setText(TextUtils.join(" ", words)));
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    viewModel.onSaveInstanceState(outState);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);
    this.menu=menu;
    menu.findItem(R.id.open).setEnabled(true);
    updateMenu();

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.open:
        open();
        return(true);

      case R.id.refresh:
        viewModel.refresh();
        return(true);

      case R.id.word_count_4:
      case R.id.word_count_5:
      case R.id.word_count_6:
      case R.id.word_count_7:
      case R.id.word_count_8:
      case R.id.word_count_9:
      case R.id.word_count_10:
        item.setChecked(!item.isChecked());
        viewModel.setCount(Integer.parseInt(item.getTitle().toString()));

        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode,
                               Intent resultData) {
    if (resultCode==Activity.RESULT_OK) {
      viewModel.setSource(resultData.getData());
    }
  }

  private void open() {
    Intent i=
      new Intent()
        .setType("text/plain")
        .setAction(Intent.ACTION_OPEN_DOCUMENT)
        .addCategory(Intent.CATEGORY_OPENABLE);

    startActivityForResult(i, REQUEST_OPEN);
  }

  private void updateMenu() {
    if (menu!=null && viewModel!=null) {
      MenuItem checkable=menu.findItem(WORD_COUNT_MENU_IDS[viewModel.getCount()-4]);

      if (checkable!=null) {
        checkable.setChecked(true);
      }
    }
  }
}
