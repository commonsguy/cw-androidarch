/***
 Copyright (c) 2017-2019 CommonsWare, LLC
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

package com.commonsware.android.diceware;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import java.util.List;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;

class PassphraseViewModel extends ViewModel {
  private static final String STATE_SOURCE="source";
  private static final String STATE_COUNT="count";
  private final MutableLiveData<List<String>> liveWords=new MutableLiveData<>();
  private final Repository repo;
  private Uri source=Uri.parse("file:///android_asset/eff_short_wordlist_2_0.txt");
  private int count=6;
  private Disposable sub=Disposables.empty();

  PassphraseViewModel(Context ctxt, Bundle state) {
    repo=Repository.get(ctxt);

    if (state!=null) {
      source=state.getParcelable(STATE_SOURCE);
      count=state.getInt(STATE_COUNT, 6);
    }

    refresh();
  }

  @Override
  protected void onCleared() {
    sub.dispose();
  }

  void onSaveInstanceState(Bundle state) {
    state.putParcelable(STATE_SOURCE, source);
    state.putInt(STATE_COUNT, count);
  }

  void setSource(Uri source) {
    this.source=source;
    refresh();
  }

  void setCount(int count) {
    this.count=count;
    refresh();
  }

  int getCount() {
    return(count);
  }

  LiveData<List<String>> words() {
    return(liveWords);
  }

  void refresh() {
    sub.dispose();
    sub=repo.getWords(source, count)
      .observeOn(Schedulers.io())
      .subscribe(liveWords::postValue);
  }

  static class Factory implements ViewModelProvider.Factory {
    private final Bundle state;
    private final Context ctxt;

    Factory(Context ctxt, Bundle state) {
      this.ctxt=ctxt.getApplicationContext();
      this.state=state;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
      return((T)new PassphraseViewModel(ctxt, state));
    }
  }
}
