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

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.Bundle;
import android.support.annotation.NonNull;
import java.util.ArrayList;

public class EditorViewModel extends AndroidViewModel {
  private static final String STATE_BUFFERS="buffers";
  private static final String STATE_TAB_NUM="nextTabNumber";
  private int nextTabNumber=1;
  private ArrayList<EditBuffer> buffers=new ArrayList<>();

  public EditorViewModel(@NonNull Application application) {
    super(application);
  }

  EditBuffer getBuffer(int position) {
    return buffers.get(position);
  }

  int getBufferCount() {
    return buffers.size();
  }

  void insert(int position) {
    buffers.add(position, new EditBuffer(getNextTitle()));
  }

  void copy(int position) {
    EditBuffer newBuffer=new EditBuffer(getNextTitle(),
      buffers.get(position).getProse());

    buffers.add(position+1, newBuffer);
  }

  void swap(int first, int second) {
    EditBuffer firstBuffer=buffers.get(first);
    EditBuffer secondBuffer=buffers.get(second);

    buffers.set(first, secondBuffer);
    buffers.set(second, firstBuffer);
  }

  void remove(int position) {
    buffers.remove(position);
  }

  private String getNextTitle() {
    return getApplication().getString(R.string.hint, nextTabNumber++);
  }

  void saveInstanceState(Bundle state) {
    state.putInt(STATE_TAB_NUM, nextTabNumber);
    state.putParcelableArrayList(STATE_BUFFERS, buffers);
  }

  void restoreInstanceState(Bundle state) {
    nextTabNumber=state.getInt(STATE_TAB_NUM, 1);
    buffers=state.getParcelableArrayList(STATE_BUFFERS);
  }
}
