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

import android.arch.paging.PagedList;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import com.commonsware.android.todo.R;
import com.commonsware.android.todo.databinding.TodoRowBinding;
import com.commonsware.android.todo.impl.Action;
import com.commonsware.android.todo.impl.ToDoModel;

class RosterListAdapter extends AbstractRosterFragment.BaseRosterAdapter<RosterRowHolder>
  implements ActionMode.Callback {
  private RosterListFragment host;
  private ActionMode activeMode=null;

  public RosterListAdapter(RosterListFragment host) {
    this.host=host;
  }

  @Override
  public RosterRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    TodoRowBinding binding=
      TodoRowBinding.inflate(host.getLayoutInflater(), parent, false);

    return(new RosterRowHolder(binding, this));
  }

  @Override
  public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    host.getActivity().getMenuInflater().inflate(R.menu.actions_roster_mode, menu);
    activeMode=mode;
    updateActionMode();

    return(true);
  }

  @Override
  public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
    return(false);
  }

  @Override
  public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    if (item.getItemId()==R.id.delete) {
      host.requestDelete(host.currentState().getSelectionCount());
      return(true);
    }

    return(false);
  }

  @Override
  public void onDestroyActionMode(ActionMode mode) {
    if (activeMode!=null) {
      activeMode=null;
      host.process(Action.unselectAll());
    }
  }

  void exitMultiSelectMode() {
    activeMode.finish();
  }

  void updateActionMode() {
    int count=host.currentState().getSelectionCount();

    if (count==0 && activeMode!=null) {
      exitMultiSelectMode();
    }
    else if (count>0 && activeMode==null) {
      host.enterMultiSelectMode();
    }
  }

  @Override
  public void setList(PagedList<ToDoModel> pagedList) {
    super.setList(pagedList);

    updateActionMode();
  }

  boolean isInMultiSelectMode() {
    return (activeMode!=null);
  }

  void modify(ToDoModel model, boolean isChecked) {
    host.edit(model, isChecked);
  }

  boolean isSelected(int position) {
    return (host.currentState().isSelected(position));
  }

  boolean isCurrent(ToDoModel model) {
    return(shouldShowCurrent() &&
      host.currentState().current()!=null &&
      host.currentState().current().id().equals(model.id()));
  }

  private boolean shouldShowCurrent() {
    return host.shouldShowCurrent();
  }

  void toggleSelected(int position) {
    if (isSelected(position)) {
      host.process(Action.unselect(position));
    }
    else   {
      host.process(Action.select(position));
    }
  }

  void showModel(ToDoModel model) {
    ((RosterListFragment.Contract)host.getActivity()).showModel(model);
    host.process(Action.show(model));
  }
}
