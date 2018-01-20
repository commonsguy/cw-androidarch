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

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;
import com.commonsware.android.todo.R;
import com.commonsware.android.todo.databinding.TodoRowBinding;
import com.commonsware.android.todo.impl.ToDoModel;

public class RosterRowHolder
  extends AbstractRosterFragment.ToDoModelViewHolder {
  private final TodoRowBinding binding;
  private final RosterListAdapter adapter;

  RosterRowHolder(TodoRowBinding binding, RosterListAdapter adapter) {
    super(binding.getRoot());

    this.binding=binding;
    this.adapter=adapter;

    expandCheckbox(binding.checkbox,
      binding.checkbox.getContext().getResources().getDimensionPixelSize(
        R.dimen.expanded_cb_size));
  }

  @Override
  public void bind(ToDoModel model) {
    binding.setModel(model);
    binding.setHolder(this);
    binding.executePendingBindings();
  }

  public boolean isActivated() {
    return (adapter.isSelected(getAdapterPosition()));
  }

  public boolean isCurrent() {
    return adapter.isCurrent(binding.getModel());
  }

  public void completeChanged(ToDoModel model, boolean isChecked) {
    if (model.isCompleted()!=isChecked) {
      adapter.modify(model, isChecked);
    }
  }

  public void onClick() {
    if (adapter.isInMultiSelectMode()) {
      adapter.toggleSelected(getAdapterPosition());
      adapter.updateActionMode();
    }
    else {
      adapter.showModel(binding.getModel());
    }
  }

  public boolean onLongClick() {
    if (adapter.isInMultiSelectMode()) {
      onClick();
    }
    else {
      adapter.toggleSelected(getAdapterPosition());
    }

    return (true);
  }

  private void expandCheckbox(final View cb, final int size) {
    cb.post(() -> {
      if (View.class.isInstance(cb.getParent())) {
        Rect delegateArea=new Rect();

        cb.getHitRect(delegateArea);

        delegateArea.left-=size;
        delegateArea.right+=size;

        ((View)cb.getParent())
          .setTouchDelegate(new TouchDelegate(delegateArea, cb));
      }
    });
  }
}
