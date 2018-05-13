/***
 Copyright (c) 2018 CommonsWare, LLC
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

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import java.util.ArrayList;

public class RecyclerViewEx extends RecyclerView {
  interface OnScrollStateChangedListener {
    void onScrollStateChanged(int state);
  }

  final private ArrayList<OnScrollStateChangedListener> listeners=new ArrayList<>();

  public RecyclerViewEx(Context context) {
    super(context);
  }

  public RecyclerViewEx(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public RecyclerViewEx(Context context, @Nullable AttributeSet attrs,
                        int defStyle) {
    super(context, attrs, defStyle);
  }

  public void addOnScrollStateChangedListener(OnScrollStateChangedListener listener) {
    listeners.add(listener);
  }

  public void removeOnScrollStateChangedListener(OnScrollStateChangedListener listener) {
    listeners.remove(listener);
  }

  @Override
  public void onScrollStateChanged(int state) {
    super.onScrollStateChanged(state);

    for (OnScrollStateChangedListener listener : listeners) {
      listener.onScrollStateChanged(state);
    }
  }
}
