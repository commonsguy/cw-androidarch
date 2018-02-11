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

import android.app.Activity;
import android.arch.paging.PagedList;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;
import com.commonsware.android.todo.BuildConfig;
import com.commonsware.android.todo.R;
import com.commonsware.android.todo.impl.Action;
import com.commonsware.android.todo.impl.FilterMode;
import com.commonsware.android.todo.impl.RosterReport;
import com.commonsware.android.todo.impl.ToDoModel;
import com.commonsware.android.todo.impl.ViewState;
import com.commonsware.android.todo.util.GistUploadService;
import com.commonsware.android.todo.util.UriReportWriter;
import com.commonsware.cwac.crossport.design.widget.Snackbar;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RosterListFragment extends AbstractRosterFragment {
  interface Contract {
    void addModel();
    void showModel(ToDoModel model);
    boolean shouldShowCurrent();
  }

  private static final int REQUEST_CREATE=1337;
  private static final String AUTHORITY=BuildConfig.APPLICATION_ID+".provider";
  private RosterListAdapter adapter;
  private Snackbar snackbar;
  private MenuItem filter, filterAll, filterCompleted, filterOutstanding;
  private ViewState lastViewState=null;

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity()));

    DividerItemDecoration decoration=new DividerItemDecoration(getActivity(),
      LinearLayoutManager.VERTICAL);

    getRecyclerView().addItemDecoration(decoration);

    adapter=new RosterListAdapter(this);
    getRecyclerView().setAdapter(adapter);

    Toolbar tb=view.findViewById(R.id.toolbar);

    tb.inflateMenu(R.menu.actions_roster);
    tb.setTitle(R.string.app_name);
    tb.setOnMenuItemClickListener(item -> (onOptionsItemSelected(item)));

    Menu menu=tb.getMenu();

    filter=menu.findItem(R.id.filter);
    filterAll=menu.findItem(R.id.all);
    filterCompleted=menu.findItem(R.id.completed);
    filterOutstanding=menu.findItem(R.id.outstanding);

    startObserving();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.add:
        ((Contract)getActivity()).addModel();
        return(true);

      case R.id.all:
        filterAll.setChecked(true);
        process(Action.filter(FilterMode.ALL));
        return(true);

      case R.id.completed:
        filterCompleted.setChecked(true);
        process(Action.filter(FilterMode.COMPLETED));
        return(true);

      case R.id.outstanding:
        filterOutstanding.setChecked(true);
        process(Action.filter(FilterMode.OUTSTANDING));
        return(true);

      case R.id.export:
        export();
        return(true);

      case R.id.share:
        share();
        return(true);

      case R.id.upload:
        GistUploadService.upload(getActivity(), currentState().filterMode());
        Toast.makeText(getActivity(), R.string.msg_upload, Toast.LENGTH_SHORT).show();
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode==REQUEST_CREATE) {
      if (resultCode==Activity.RESULT_OK) {
        completeExport(data.getData());
      }
    }
  }

  @Override
  protected void render(ViewState state) {
    super.render(state);

    if (adapter!=null) {
      if (state.cause()==null) {
        if (state.getSelectionCount()==0 && snackbar!=null &&
          snackbar.isShown()) {
          snackbar.dismiss();
        }

        if (lastViewState!=null) {
          Set<Integer> scrap=new HashSet<>(lastViewState.selections());

          scrap.removeAll(state.selections());

          for (int position : scrap) {
            adapter.notifyItemChanged(position);
          }

          scrap=new HashSet<>(state.selections());

          scrap.removeAll(lastViewState.selections());

          for (int position : scrap) {
            adapter.notifyItemChanged(position);
          }

          if (lastViewState.current()!=null) {
            adapter.notifyItemChanged(
              lastViewState.snapshot().dataSource()
                .findPositionForKey(lastViewState.current().id()));
          }
        }

        lastViewState=state;

        if (state.current()!=null) {
          adapter.notifyItemChanged(state.snapshot().dataSource()
            .findPositionForKey(state.current().id()));
        }
      }
      else {
        Snackbar
          .make(getView(), R.string.msg_crash, Snackbar.LENGTH_LONG)
          .show();
        Log.e(getClass().getSimpleName(), "Exception in obtaining view state",
          state.cause());
      }

      updateFilter(state);
    }
  }

  @Override
  protected void render(PagedList<ToDoModel> items) {
    adapter.setList(items);

    if (items.size()==0) {
      getEmptyView().setVisibility(View.VISIBLE);

      if (items.size()>0) {
        getEmptyView().setText(R.string.msg_empty_filter);
      }
      else {
        getEmptyView().setText(R.string.msg_empty);
      }
    }
    else {
      getEmptyView().setVisibility(View.GONE);
    }
  }

  public void edit(ToDoModel model, boolean isChecked) {
    process(Action.edit(model.toBuilder().isCompleted(isChecked).build()));
  }

  public void enterMultiSelectMode() {
    getActivity().startActionMode(adapter);
  }

  public void requestDelete(int selectionCount) {
    Resources res=getResources();
    String msg=res.getQuantityString(R.plurals.snackbar_delete,
      selectionCount, selectionCount);

    snackbar=Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG);

    snackbar
      .setAction(android.R.string.ok, view -> {
        process(Action.delete(currentState().getSelectedIds()));
        adapter.exitMultiSelectMode();
      })
      .show();
  }

  private void export() {
    Intent intent=
      new Intent(Intent.ACTION_CREATE_DOCUMENT)
        .addCategory(Intent.CATEGORY_OPENABLE)
        .setType("text/html");

    startActivityForResult(intent, REQUEST_CREATE);
  }

  private void completeExport(Uri uri) {
    exportReport(uri)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(this::viewReport);
  }

  private Single<Uri> exportReport(Uri uri) {
    return(new RosterReport(getActivity()).generate(currentState().snapshot(),
      new UriReportWriter(getActivity(), uri)));
  }

  private void viewReport(Uri uri) {
    Intent i=new Intent(Intent.ACTION_VIEW, uri)
      .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

    try {
      startActivity(i);
    }
    catch (ActivityNotFoundException e) {
      Toast.makeText(getActivity(), R.string.msg_export, Toast.LENGTH_LONG).show();
    }
  }

  private void share() {
    final Context app=getActivity().getApplication();

    SingleOnSubscribe<Uri> uriBuilder=e -> {
      final File shared=new File(app.getCacheDir(), "shared");
      final File report=new File(shared, "report.html");

      shared.mkdirs();

      e.onSuccess(FileProvider.getUriForFile(app, AUTHORITY, report));
    };

    Single.create(uriBuilder)
      .flatMap(uri -> (exportReport(uri)))
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(this::shareReport);
  }

  private void shareReport(Uri uri) {
    Intent i=new Intent(Intent.ACTION_SEND)
      .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      .setType("text/html")
      .putExtra(Intent.EXTRA_STREAM, uri);

    try {
      startActivity(i);
    }
    catch (ActivityNotFoundException e) {
      Toast.makeText(getActivity(), R.string.msg_export, Toast.LENGTH_LONG).show();
    }
  }

  void updateFilter(ViewState state) {
    if (filterAll!=null && state!=null) {
      switch (state.filterMode()) {
        case ALL:
          filter.setTitle("Filter");
          filterAll.setChecked(true);
          break;

        case COMPLETED:
          filter.setTitle("Completed");
          filterCompleted.setChecked(true);
          break;

        case OUTSTANDING:
          filter.setTitle("Outstanding");
          filterOutstanding.setChecked(true);
          break;
      }
    }
  }

  public boolean shouldShowCurrent() {
    return(((Contract)getActivity()).shouldShowCurrent());
  }
}
