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

package com.commonsware.android.todo.util;

import com.commonsware.android.todo.ReportWriter;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class GistReportWriter implements ReportWriter<String> {
  @Override
  public Single<String> write(String report) {
    Gist gist=new Gist(report);
    Retrofit retrofit=
      new Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build();
    GistApi api=retrofit.create(GistApi.class);

    return(api.gistify(gist)
      .map(response -> {
        if (response.code()==201) {
          return(response.body().html_url);
        }

        throw new IllegalStateException("Expected 201 response, received "+response.code());
      }));
  }

  interface GistApi {
    @POST("/gists")
    Single<Response<GistResponse>> gistify(@Body Gist gist);
  }

  private static class Gist {
    final String description="Some to-do snapshot";
    @SerializedName("public")
    final boolean isPublic=true;
    final HashMap<String, FileEntry> files=new HashMap<>();

    private Gist(String report) {
      files.put("report.html", new FileEntry(report));
    }
  }

  private static class FileEntry {
    final String content;

    private FileEntry(String content) {
      this.content=content;
    }
  }

  private static class GistResponse {
    String html_url;
  }
}
