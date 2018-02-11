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

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import com.commonsware.android.todo.R;
import com.commonsware.android.todo.databinding.TodoRowBinding;
import com.commonsware.android.todo.impl.Action;
import com.commonsware.android.todo.impl.ToDoModel;
import com.commonsware.android.todo.impl.ViewState;
import java.util.List;

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
      host.requestDelete(getState().getSelectionCount());
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

  List<ToDoModel> getSelectedModels() {
    return (getState().getSelectedModels());
  }

  void updateActionMode() {
    int count=getState().getSelectionCount();

    if (count==0 && activeMode!=null) {
      exitMultiSelectMode();
    }
    else if (count>0 && activeMode==null) {
      host.enterMultiSelectMode();
    }
  }

  void updateFilter() {
    host.updateFilter(getState());
  }

  @Override
  void setState(ViewState state) {
    ToDoModel oldCurrent=null;

    if (getState()!=null) {
      oldCurrent=getState().current();
    }

    super.setState(state);

    updateFilter();
    updateActionMode();

    if (oldCurrent!=state.current()) {
      if (oldCurrent!=null) {
        notifyItemChanged(state.getFilteredPosition(oldCurrent.id()));
      }

      notifyItemChanged(state.getFilteredPosition(state.current().id()));
    }
  }

  boolean isInMultiSelectMode() {
    return (activeMode!=null);
  }

  void modify(ToDoModel model, boolean isChecked) {
    host.edit(model, isChecked);
  }

  boolean isSelected(int position) {
    return (getState().isSelected(position));
  }

  boolean isCurrent(ToDoModel model) {
    return(shouldShowCurrent() && getState().current()==model);
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

  List<ToDoModel> getModels() {
    return (getState().filteredItems());
  }
}
