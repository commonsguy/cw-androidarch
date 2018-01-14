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

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.commonsware.android.todo.impl.ToDoModel;
import com.commonsware.android.todo.impl.ViewState;

public class DisplayFragment extends AbstractRosterFragment {
  private final SnapHelper snapperCarr=new PagerSnapHelper();
  private PageAdapter adapter;
  private LinearLayoutManager layoutManager;
  private DisplayViewModel displayViewModel;
  private boolean ignoreNextScroll=false;

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

    DisplayViewModelFactory factory=new DisplayViewModelFactory(state);

    displayViewModel=ViewModelProviders.of(this, factory).get(DisplayViewModel.class);
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
            displayViewModel.setCurrentModel(adapter.getItem(position));
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

    startObserving();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    displayViewModel.onSaveInstanceState(outState);
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
  void render(ViewState state) {
    adapter.setState(state);

    if (!displayViewModel.hasCurrentModelId()) {
      String initialModelId=getInitialModelId();

      if (initialModelId!=null) {
        displayViewModel.setCurrentModelId(initialModelId);
      }
    }

    updatePager(false);
  }

  void updatePager(boolean smooth) {
    if (adapter.getItemCount()>0 && displayViewModel.getCurrentModelId()!=null) {
      int position=adapter.getPosition(displayViewModel.getCurrentModelId());

      if (position>=0) {
        if (smooth) {
          getRecyclerView().smoothScrollToPosition(position);
        }
        else {
          getRecyclerView().scrollToPosition(position);
        }

        adapter.notifyItemChanged(position);
      }
    }
  }

  void showModel(ToDoModel model) {
    ignoreNextScroll=true;
    displayViewModel.setCurrentModelId(model.id());
    updatePager(true);
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

  private static class DisplayViewModel extends ViewModel {
    private static final String STATE_CURRENT="currentModelId";
    private String currentModelId;

    DisplayViewModel(Bundle state) {
      if (state!=null) {
        currentModelId=state.getString(STATE_CURRENT);
      }
    }

    String getCurrentModelId() {
      return(currentModelId);
    }

    void setCurrentModel(ToDoModel model) {
      setCurrentModelId(model.id());
    }

    void setCurrentModelId(String currentModelId) {
      this.currentModelId=currentModelId;
    }

    boolean hasCurrentModelId() {
      return(getCurrentModelId()!=null);
    }

    void onSaveInstanceState(Bundle state) {
      state.putString(STATE_CURRENT, currentModelId);
    }
  }

  private static class DisplayViewModelFactory implements
    ViewModelProvider.Factory {
    private final Bundle state;

    private DisplayViewModelFactory(Bundle state) {
      this.state=state;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
      return((T)new DisplayViewModel(state));
    }
  }
}