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

package com.commonsware.android.diceware;

import java.io.IOException;
import java.security.MessageDigest;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class PwnedCheck {
  private final OkHttpClient okHttpClient;

  PwnedCheck(OkHttpClient okHttpClient) {
    this.okHttpClient=okHttpClient;
  }

  Observable<Integer> score(String passphrase) {
    return Observable.just(passphrase)
      .map(PwnedCheck::getSha1Hex)
      .flatMap(this::fetchCandidates)
      .map(PwnedCheck::findCount);
  }

  Observable<String> validate(final String passphrase) {
    return score(passphrase).map(score -> {
      if (score>0) {
        throw new PwnedException();
      }

      return passphrase;
    });
  }

  private static class PwnedException extends RuntimeException {

  }

  // based on https://stackoverflow.com/a/33260623/115145

  private static String getSha1Hex(String original) throws Exception {
    MessageDigest messageDigest=MessageDigest.getInstance("SHA-1");

    messageDigest.update(original.getBytes("UTF-8"));

    byte[] bytes=messageDigest.digest();
    StringBuilder buffer=new StringBuilder();

    for (byte b : bytes) {
      buffer.append(Integer.toString((b & 0xff)+0x100, 16).substring(1));
    }

    return buffer.toString();
  }

  private Observable<FetchResult> fetchCandidates(String sha1) throws IOException {
    String url="https://api.pwnedpasswords.com/range/"+sha1.substring(0, 5);
    Request request=new Request.Builder().url(url).build();

    return Observable.fromCallable(
      () -> new FetchResult(okHttpClient.newCall(request).execute(), sha1));
  }

  private static int findCount(FetchResult fetch) throws IOException {
    String candidates=fetch.response.body().string();
    String suffix=fetch.sha1.substring(5).toUpperCase();

    for (String line : candidates.split("\r\n")) {
      if (line.startsWith(suffix)) {
        return(Integer.parseInt(line.split(":")[1]));
      }
    }

    return 0;
  }

  private static class FetchResult {
    final Response response;
    final String sha1;

    private FetchResult(Response response, String sha1) {
      this.response=response;
      this.sha1=sha1;
    }
  }
}
