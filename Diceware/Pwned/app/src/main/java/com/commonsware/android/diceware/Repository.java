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

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

class Repository {
  private static final String ASSET_FILENAME="eff_short_wordlist_2_0.txt";
  private static volatile Repository INSTANCE;
  private final Application ctxt;
  private final ConcurrentHashMap<Uri, List<String>> cache=new ConcurrentHashMap<>();
  private SecureRandom random=new SecureRandom();
  private final PwnedCheck checker=new PwnedCheck(new OkHttpClient());

  synchronized static Repository get(Context ctxt) {
    if (INSTANCE==null) {
      INSTANCE=new Repository(ctxt);
    }

    return(INSTANCE);
  }

  private Repository(Context ctxt) {
    this.ctxt=(Application)ctxt.getApplicationContext();
  }

  Observable<String> getPassphrase(Uri source, final int count) {
    return(getWordsFromSource(source)
      .map(strings -> (randomSubset(strings, count)))
      .map(pieces -> TextUtils.join(" ", pieces))
      .flatMap(checker::validate)
      .retryWhen(errors -> errors.retry(3)));
  }

  synchronized private Observable<List<String>> getWordsFromSource(Uri source) {
    List<String> words=cache.get(source);
    final Observable<List<String>> result;

    if (words==null) {
      result=Observable.just(source)
        .subscribeOn(Schedulers.io())
        .map(uri -> (open(uri)))
        .map(in -> (readWords(in)))
        .doOnNext(strings -> cache.put(source, strings));
    }
    else {
      result=Observable.just(words);
    }

    return(result);
  }

  private InputStream open(Uri uri) throws IOException {
    String scheme=uri.getScheme();
    String path=uri.getPath();

    if ("file".equals(scheme) && path.startsWith("/android_asset")) {
      return(ctxt.getAssets().open(ASSET_FILENAME));
    }

    ContentResolver cr=ctxt.getContentResolver();

    cr.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

    return(cr.openInputStream(uri));
  }

  private static List<String> readWords(InputStream in) throws IOException {
    InputStreamReader isr=new InputStreamReader(in);
    BufferedReader reader=new BufferedReader(isr);
    String line;
    List<String> result=new ArrayList<>();

    while ((line = reader.readLine())!=null) {
      String[] pieces=line.split("\\s");

      if (pieces.length==2) {
        result.add(pieces[1]);
      }
    }

    return(result);
  }

  private List<String> randomSubset(List<String> words, int count) {
    List<String> result=new ArrayList<>();
    int size=words.size();

    for (int i=0;i<count;i++) {
      result.add(words.get(random.nextInt(size)));
    }

    return(result);
  }
}
