/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _Android's Architecture Components_
 https://commonsware.com/AndroidArch
 */

package com.commonsware.android.work.download;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.commonsware.android.work.download.databinding.ActivityMainBinding;
import androidx.work.WorkInfo;

public class MainActivity extends AppCompatActivity {
  @BindingAdapter("android:enabled")
  public static void setEnabled(View v, WorkInfo info) {
    if (info==null) {
      v.setEnabled(true);
    }
    else {
      v.setEnabled(info.getState().isFinished());
    }
  }

  private ActivityMainBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final DownloadViewModel vm=ViewModelProviders.of(this).get(DownloadViewModel.class);

    binding=ActivityMainBinding.inflate(getLayoutInflater());
    binding.setViewModel(vm);
    binding.setLifecycleOwner(this);

    setContentView(binding.getRoot());

    vm.liveWorkStatus.observe(this, workStatus -> {
      if (workStatus!=null && workStatus.getState().isFinished()) {
        Toast.makeText(this, R.string.msg_done, Toast.LENGTH_LONG).show();
      }
    });
  }
}
