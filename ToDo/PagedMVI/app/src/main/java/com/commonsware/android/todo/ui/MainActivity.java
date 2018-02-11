/***
 Copyright (c) 2017 CommonsWare, LLC
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

package com.commonsware.android.todo.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.inputmethod.InputMethodManager;
import com.commonsware.android.todo.R;
import com.commonsware.android.todo.impl.RosterViewModel;
import com.commonsware.android.todo.impl.ToDoModel;

public class MainActivity extends FragmentActivity
  implements RosterListFragment.Contract, DisplayFragment.Contract,
  EditFragment.Contract {
  private static final String BACK_STACK_SHOW="showModel";
  private boolean isDualPane=false;
  private DisplayFragment display;
  private RosterViewModel viewModel;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    setContentView(R.layout.activity_main);

    isDualPane=(findViewById(R.id.detail)!=null);

    viewModel=ViewModelProviders
      .of(this, new RosterViewModel.Factory(getApplication(), state))
      .get(RosterViewModel.class);

    if (getSupportFragmentManager().findFragmentById(R.id.master)==null) {
      getSupportFragmentManager().beginTransaction()
        .add(R.id.master, new RosterListFragment())
        .commit();

      if (isDualPane) {
        display=DisplayFragment.newInstance(null);

        getSupportFragmentManager().beginTransaction()
          .replace(getDetailContainer(), display)
          .commit();
      }
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    viewModel.onSaveInstanceState(outState);
  }

  @Override
  public void showModel(ToDoModel model) {
    if (display==null) {
      getSupportFragmentManager().beginTransaction()
        .replace(getDetailContainer(), DisplayFragment.newInstance(model))
        .addToBackStack(BACK_STACK_SHOW)
        .commit();
    }
  }

  @Override
  public void addModel() {
    getSupportFragmentManager().beginTransaction()
      .replace(getDetailContainer(), EditFragment.newInstance(null))
      .addToBackStack(null)
      .commit();
  }

  @Override
  public void editModel(ToDoModel model) {
    getSupportFragmentManager().beginTransaction()
      .replace(getDetailContainer(), EditFragment.newInstance(model))
      .addToBackStack(null)
      .commit();
  }

  @Override
  public void finishEdit(ToDoModel model, boolean deleted) {
    hideSoftInput();

    if (deleted) {
      if (isDualPane) {
        getSupportFragmentManager().popBackStack();
      }
      else {
        getSupportFragmentManager().popBackStack(BACK_STACK_SHOW,
          FragmentManager.POP_BACK_STACK_INCLUSIVE);
      }
    }
    else {
      getSupportFragmentManager().popBackStack();

      if (display!=null) {
        display.showModel(model);
      }
    }
  }

  @Override
  public boolean shouldShowTitle() {
    return(!isDualPane);
  }

  @Override
  public boolean shouldShowCurrent() {
    return(isDualPane);
  }

  public RosterViewModel getViewModel() {
    return viewModel;
  }

  private int getDetailContainer() {
    return(isDualPane ? R.id.detail : R.id.master);
  }

  // based on https://stackoverflow.com/a/21574135/115145

  private void hideSoftInput() {
    if (getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null) {
      ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
  }
}
