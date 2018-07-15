/***
 Copyright (c) 2012-18 CommonsWare, LLC
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

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Menu;
import android.view.MenuItem;
import com.commonsware.cwac.crossport.design.widget.TabLayout;
import com.commonsware.cwac.crossport.design.widget.TabLayoutLite;

public class MainActivity extends FragmentActivity {
  private final SnapHelper snapperCarr=new PagerSnapHelper();
  private RecyclerViewEx pager;
  private PageAdapter adapter;
  private LinearLayoutManager layoutManager;
  private TabLayoutLite tabs;
  private MenuItem remove;
  private EditorViewModel viewModel;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    pager=findViewById(R.id.pager);
    layoutManager=
      new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    pager.setLayoutManager(layoutManager);
    snapperCarr.attachToRecyclerView(pager);

    viewModel=ViewModelProviders.of(this).get(EditorViewModel.class);

    if (savedInstanceState!=null) {
      viewModel.restoreInstanceState(savedInstanceState);
    }

    adapter=new PageAdapter(viewModel, getLayoutInflater());
    pager.setAdapter(adapter);
    pager.setHasFixedSize(true);
    tabs=findViewById(R.id.tabs);

    for (int i=0; i<adapter.getItemCount(); i++) {
      tabs.addTab(tabs.newTab().setText(adapter.getTabText(i)));
    }

    tabs.addOnTabSelectedListener(new TabLayoutLite.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayoutLite.Tab tab) {
        pager.smoothScrollToPosition(tab.getPosition());
      }

      @Override
      public void onTabUnselected(TabLayoutLite.Tab tab) {
        // unused
      }

      @Override
      public void onTabReselected(TabLayoutLite.Tab tab) {
        // unused
      }
    });

    pager.addOnScrollStateChangedListener(
      scrollState -> {
        if (scrollState==RecyclerView.SCROLL_STATE_IDLE) {
          int tab=getCurrentPosition();

          if (tab>=0 && tab<tabs.getTabCount()) {
            tabs.getTabAt(tab).select();
          }
        }
      });
  }

  @Override
  protected void onSaveInstanceState(Bundle state) {
    super.onSaveInstanceState(state);

    viewModel.saveInstanceState(state);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);
    remove=menu.findItem(R.id.remove);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.add:
        add();
        return true;

      case R.id.split:
        split();
        return true;

      case R.id.swap:
        swap();
        return true;

      case R.id.remove:
        remove();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private int getCurrentPosition() {
    return layoutManager.findFirstCompletelyVisibleItemPosition();
  }

  private void add() {
    int current=adapter.getItemCount()==0 ? 0 : getCurrentPosition();

    adapter.insert(current);
    tabs.addTab(tabs.newTab().setText(adapter.getTabText(current)), current);

    if (current<adapter.getItemCount()) {
      pager.scrollToPosition(current);
    }

    updateRemoveMenuItem();
  }

  private void split() {
    int newPosition=getCurrentPosition()+1;

    adapter.copy(newPosition-1);
    tabs.addTab(tabs.newTab().setText(adapter.getTabText(newPosition)),
      newPosition);
    updateRemoveMenuItem();
  }

  private void swap() {
    int first=getCurrentPosition();
    int second;

    if (first >= adapter.getItemCount()-1) {
      second=first;
      first--;
    }
    else {
      second=first+1;
    }

    adapter.swap(first, second);

    TabLayoutLite.Tab firstTab=tabs.getTabAt(first);
    TabLayoutLite.Tab secondTab=tabs.getTabAt(second);
    CharSequence firstText=firstTab.getText();

    firstTab.setText(secondTab.getText());
    secondTab.setText(firstText);
  }

  private void remove() {
    final int current=getCurrentPosition();

    tabs.removeTabAt(current);
    adapter.remove(current);

    if (current<adapter.getItemCount()) {
      pager.scrollToPosition(current);
    }

    updateRemoveMenuItem();
  }

  private void updateRemoveMenuItem() {
    remove.setEnabled(adapter.getItemCount()>1);
  }
}