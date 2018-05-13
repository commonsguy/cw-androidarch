/***
 Copyright (c) 2017-2018 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed search an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _Android's Architecture Components_
 https://commonsware.com/AndroidArch
 */

package com.commonsware.android.room.fts;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedListAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

public class BookFragment extends RecyclerViewFragment implements
  SearchView.OnQueryTextListener, SearchView.OnCloseListener {
  private SearchView sv=null;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setHasOptionsMenu(true);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setLayoutManager(new LinearLayoutManager(getActivity()));

    BookViewModel vm=ViewModelProviders.of(this).get(BookViewModel.class);
    final ParagraphAdapter adapter=new ParagraphAdapter(getActivity().getLayoutInflater());

    vm.paragraphs.observe(this, adapter::submitList);

    setAdapter(adapter);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);

    configureSearchView(menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    return false;
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    search(query);

    return true;
  }

  @Override
  public boolean onClose() {
    return true;
  }

  private void configureSearchView(Menu menu) {
    MenuItem search=menu.findItem(R.id.search);

    sv=(SearchView)search.getActionView();
    sv.setOnQueryTextListener(this);
    sv.setOnCloseListener(this);
    sv.setSubmitButtonEnabled(true);
    sv.setIconifiedByDefault(true);
  }

  private void search(String expr) {
    ((MainActivity)getActivity()).search(expr);
  }

  private static class ParagraphAdapter extends PagedListAdapter<ParagraphEntity, RowHolder> {
    private final LayoutInflater inflater;

    ParagraphAdapter(LayoutInflater inflater) {
      super(PARA_DIFF);
      this.inflater=inflater;
    }

    @Override
    public RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return(new RowHolder(inflater.inflate(R.layout.row, parent, false)));
    }

    @Override
    public void onBindViewHolder(RowHolder holder, int position) {
      ParagraphEntity paragraph=getItem(position);

      if (paragraph==null) {
        holder.clear();
      }
      else {
        holder.bind(paragraph);
      }
    }
  }

  private static class RowHolder extends RecyclerView.ViewHolder {
    private final TextView prose;

    RowHolder(View itemView) {
      super(itemView);

      prose=itemView.findViewById(R.id.prose);
    }

    void bind(ParagraphEntity paragraph) {
      prose.setText(paragraph.prose);
    }

    void clear() {
      prose.setText(null);
    }
  }

  static final DiffUtil.ItemCallback<ParagraphEntity> PARA_DIFF=
    new DiffUtil.ItemCallback<ParagraphEntity>() {
      @Override
      public boolean areItemsTheSame(ParagraphEntity oldItem,
                                     ParagraphEntity newItem) {
        return oldItem.sequence==newItem.sequence;
      }

      @Override
      public boolean areContentsTheSame(ParagraphEntity oldItem,
                                        ParagraphEntity newItem) {
        return oldItem.prose.equals(newItem.prose);
      }
  };
}
