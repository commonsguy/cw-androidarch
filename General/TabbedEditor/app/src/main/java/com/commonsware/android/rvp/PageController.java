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

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

class PageController extends RecyclerView.ViewHolder {
  private final EditText editor;
  private final ProseWatcher watcher=new ProseWatcher();

  PageController(View itemView) {
    super(itemView);

    editor=itemView.findViewById(R.id.editor);
    editor.addTextChangedListener(watcher);
  }

  void bind(EditBuffer buffer) {
    watcher.setBuffer(buffer);
    editor.setText(buffer.getProse());
  }

  private static class ProseWatcher implements TextWatcher {
    private EditBuffer buffer;

    void setBuffer(EditBuffer buffer) {
      this.buffer=buffer;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      // unused
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      // unused
    }

    @Override
    public void afterTextChanged(Editable s) {
      if (buffer!=null) {
        buffer.setProse(s);
      }
    }
  }
}
