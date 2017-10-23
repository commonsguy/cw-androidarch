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

package com.commonsware.databindingstate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.commonsware.databindingstate.databinding.MainBinding;

public class FormFragment extends Fragment {
  private static final String STATE_WORKAROUND="workaround";
  private MenuItem workaround;
  private boolean savedWorkaround=false;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setHasOptionsMenu(true);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    if (savedInstanceState!=null) {
      savedWorkaround=savedInstanceState.getBoolean(STATE_WORKAROUND);

      if (workaround!=null) {
        workaround.setChecked(savedWorkaround);
      }
    }

    MainBinding binding=MainBinding.inflate(inflater, container, false);

    if (savedWorkaround) {
      binding.executePendingBindings();
    }

    return(binding.getRoot());
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);
    workaround=menu.findItem(R.id.workaround);
    workaround.setChecked(savedWorkaround);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.workaround) {
      item.setChecked(!item.isChecked());
      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putBoolean(STATE_WORKAROUND, workaround.isChecked());
  }
}
