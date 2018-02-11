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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;
import com.commonsware.android.todo.R;
import com.commonsware.android.todo.databinding.TodoDisplayBinding;
import com.commonsware.android.todo.impl.Action;
import com.commonsware.android.todo.impl.ToDoModel;
import com.commonsware.android.todo.impl.ViewState;

public class DisplayFragment extends AbstractRosterFragment {
  private final SnapHelper snapperCarr=new PagerSnapHelper();
  private PageAdapter adapter;
  private LinearLayoutManager layoutManager;
  private boolean ignoreNextScroll=false;
  private MenuItem editMenu;
  private String currentModelId;

  interface Contract {
    void editModel(ToDoModel model);
    boolean shouldShowTitle();
  }

  private static final String ARG_ID="id";

  static DisplayFragment newInstance(ToDoModel model) {
    DisplayFragment result=new DisplayFragment();

    if (model!=null) {
      Bundle args=new Bundle();

      args.putString(ARG_ID, model.id());
      result.setArguments(args);
    }

    return(result);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle state) {
    super.onViewCreated(view, state);

    layoutManager=new LinearLayoutManager(getActivity(),
      LinearLayoutManager.HORIZONTAL, false);
    getRecyclerView().setLayoutManager(layoutManager);
    snapperCarr.attachToRecyclerView(getRecyclerView());
    adapter=new PageAdapter();
    getRecyclerView().setAdapter(adapter);
    getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (ignoreNextScroll) {
          ignoreNextScroll=false;
        }
        else {
          int position=getCurrentPosition();

          if (position>=0) {
            ToDoModel current=adapter.getItem(position);

            currentModelId=current.id();
            process(Action.show(current));
          }
        }
      }
    });

    Toolbar tb=view.findViewById(R.id.toolbar);

    tb.inflateMenu(R.menu.actions_display);
    tb.setOnMenuItemClickListener(item -> (onOptionsItemSelected(item)));

    if (((Contract)getActivity()).shouldShowTitle()) {
      tb.setTitle(R.string.app_name);
    }

    editMenu=tb.getMenu().findItem(R.id.edit);
    editMenu.setVisible(false);

    getRecyclerView().post(() -> render(currentState())); // needed in case returned here via BACK

    startObserving();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.edit) {
      ((Contract)getActivity()).editModel(adapter.getItem(getCurrentPosition()));
      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  protected void render(ViewState state) {
    super.render(state);

    if (adapter!=null && state!=null) {
      if (state.current()==null) {
        String initialModelId=getInitialModelId();

        if (initialModelId!=null) {
          currentModelId=initialModelId;
        }
      }
      else {
        currentModelId=state.current().id();
      }
    }
  }

  @Override
  protected void render(PagedList<ToDoModel> items) {
    adapter.setList(items);
    editMenu.setVisible(items.size()>0);
    updatePager();
  }

  void updatePager() {
    if (adapter.getItemCount()>0
      && currentModelId!=null
      && currentState().snapshot()!=null) {
      int position=currentState().snapshot().dataSource().findPositionForKey(currentModelId);

      if (position>=0) {
        ignoreNextScroll=true;
        getRecyclerView().scrollToPosition(position);
        adapter.notifyItemChanged(position);
      }
    }
  }

  void showModel(ToDoModel model) {
    currentModelId=model.id();
    updatePager();
  }

  private int getCurrentPosition() {
    return(layoutManager.findFirstCompletelyVisibleItemPosition());
  }

  private String getInitialModelId() {
    Bundle args=getArguments();

    return(args==null ? null : args.getString(ARG_ID));
  }

  private class PageAdapter extends BaseRosterAdapter<PageHolder> {
    @Override
    public PageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      TodoDisplayBinding binding=
        TodoDisplayBinding.inflate(getLayoutInflater(), parent, false);

      return(new PageHolder(binding));
    }
  }

  private class PageHolder extends ToDoModelViewHolder {
    private final TodoDisplayBinding binding;

    PageHolder(TodoDisplayBinding binding) {
      super(binding.getRoot());

      this.binding=binding;
    }

    @Override
    public void bind(ToDoModel model) {
      binding.setModel(model);
      binding.setCreatedOn(DateUtils.getRelativeDateTimeString(getActivity(),
        model.createdOn().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS,
        DateUtils.WEEK_IN_MILLIS, 0));
      binding.executePendingBindings();
    }
  }
}