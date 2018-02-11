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
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;
import com.commonsware.android.todo.R;
import com.commonsware.android.todo.databinding.TodoEditBinding;
import com.commonsware.android.todo.impl.Action;
import com.commonsware.android.todo.impl.RosterViewModel;
import com.commonsware.android.todo.impl.ToDoModel;
import com.commonsware.android.todo.impl.ViewState;
import com.commonsware.cwac.crossport.design.widget.Snackbar;

public class EditFragment extends Fragment {
  private MenuItem deleteItem;

  interface Contract {
    void finishEdit(ToDoModel model, boolean deleted);
    boolean shouldShowTitle();
  }

  private static final String ARG_ID="id";
  private TodoEditBinding binding;
  private RosterViewModel viewModel;
  private boolean hadSavedState=false;
  private ToDoModel model=null;

  static EditFragment newInstance(ToDoModel model) {
    EditFragment result=new EditFragment();
    Bundle args=new Bundle();

    if (model!=null) {
      args.putString(ARG_ID, model.id());
    }

    result.setArguments(args);

    return(result);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    viewModel=((MainActivity)getActivity()).getViewModel();
    binding=TodoEditBinding.inflate(inflater, container, false);
    binding.executePendingBindings();

    hadSavedState=(savedInstanceState!=null);

    return(binding.getRoot());
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    Toolbar tb=view.findViewById(R.id.toolbar);

    tb.inflateMenu(R.menu.actions_edit);
    tb.setOnMenuItemClickListener(item -> (onOptionsItemSelected(item)));

    if (((Contract)getActivity()).shouldShowTitle()) {
      tb.setTitle(R.string.app_name);
    }

    deleteItem=tb.getMenu().findItem(R.id.delete);
    viewModel.stateStream().observe(this, this::render);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.save) {
      save();
      return(true);
    }
    else if (item.getItemId()==R.id.delete) {
      delete();
      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  private void render(ViewState viewState) {
    if (binding!=null) {
      if (getModelId()==null) {
        model=null;
      }
      else {
        model=viewState.current();

        if (!hadSavedState) {
          binding.setModel(model);
        }

        deleteItem.setVisible(true);
      }
    }
  }

  private void save() {
    ToDoModel.Builder builder;
    boolean existing=false;

    if (TextUtils.isEmpty(binding.description.getText())) {
      binding.description.setError(getString(R.string.error_description));
    }
    else {
      if (model==null) {
        builder=ToDoModel.creator();
      }
      else {
        builder=model.toBuilder();
        existing=true;
      }

      ToDoModel newModel=builder
        .description(binding.description.getText().toString())
        .notes(binding.notes.getText().toString())
        .isCompleted(binding.checkbox.isChecked())
        .build();

      if (existing) {
        viewModel.process(Action.edit(newModel));
      }
      else {
        viewModel.process(Action.add(newModel));
      }

      ((Contract)getActivity()).finishEdit(newModel, false);
    }
  }

  public void delete() {
    Resources res=getResources();
    String msg=res.getQuantityString(R.plurals.snackbar_delete, 1, 1);

    Snackbar snackbar=Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG);

    snackbar
      .setAction(android.R.string.ok, view -> {
        ToDoModel model=binding.getModel();

        viewModel.process(Action.delete(model));
        ((Contract)getActivity()).finishEdit(model, true);
      })
      .show();
  }

  private String getModelId() {
    return(getArguments().getString(ARG_ID));
  }
}
