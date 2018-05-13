/***
 Copyright (c) 2016-2018 CommonsWare, LLC
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

package com.commonsware.android.rvp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

class PageAdapter extends RecyclerView.Adapter<PageController> {
  private final EditorViewModel viewModel;
  private final LayoutInflater inflater;

  PageAdapter(EditorViewModel viewModel, LayoutInflater inflater) {
    this.viewModel=viewModel;
    this.inflater=inflater;
  }

  @NonNull
  @Override
  public PageController onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new PageController(inflater.inflate(R.layout.editor, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull PageController holder, int position) {
    holder.bind(viewModel.getBuffer(position));
  }

  @Override
  public int getItemCount() {
    return viewModel.getBufferCount();
  }

  String getTabText(int position) {
    return viewModel.getBuffer(position).toString();
  }

  void insert(int position) {
    viewModel.insert(position);
    notifyItemInserted(position);
  }

  void copy(int position) {
    viewModel.copy(position);
    notifyItemInserted(position+1);
  }

  void swap(int first, int second) {
    viewModel.swap(first, second);
    notifyItemChanged(first);
    notifyItemChanged(second);
  }

  void remove(int position) {
    viewModel.remove(position);
    notifyItemRemoved(position);
  }
}
