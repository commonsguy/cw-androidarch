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

package com.commonsware.android.todo.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import com.commonsware.android.todo.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BackupRestoreActivity extends FragmentActivity {
  private static final String EXTRA_IS_BACKUP="isBackup";
  private static final String EXTRA_MAIN_PID="pid";

  static Intent newIntent(Context ctxt, boolean isBackup) {
    return new Intent(ctxt, BackupRestoreActivity.class)
      .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK)
      .putExtra(EXTRA_IS_BACKUP, isBackup)
      .putExtra(EXTRA_MAIN_PID, Process.myPid());
  }

  static void copy(File src, File dest) throws IOException {
    if (src.isDirectory()) {
      if (!dest.exists()) {
        dest.mkdir();
      }

      for (File f : src.listFiles()) {
        copy(f, new File(dest, f.getName()));
      }
    }
    else {
      try (
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dest)
      ) {
        byte[] buf = new byte[8192];
        int length;

        while ((length = in.read(buf)) > 0) {
          out.write(buf, 0, length);
        }
      }
    }
  }

  static void delete(File f) throws IOException {
    if (f.isDirectory()) {
      for (File c : f.listFiles()) {
        delete(c);
      }
    }

    if (!f.delete()) {
      throw new IOException("delete failed for " + f);
    }
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_progress);

    VMFactory factory=
      new VMFactory(getApplication(),
        getIntent().getBooleanExtra(EXTRA_IS_BACKUP, true),
        getIntent().getIntExtra(EXTRA_MAIN_PID, -1));
    VM vm=ViewModelProviders.of(this, factory).get(VM.class);

    vm.results.observe(this, unused -> {
      startActivity(MainActivity.newIntent(this));
      finish();
    });
  }

  public static class VM extends AndroidViewModel {
    final LiveData<Boolean> results;

    public VM(@NonNull Application application, boolean isBackup, int pid) {
      super(application);

      Single<Boolean> backup=
        Single.create((SingleOnSubscribe<Boolean>)emitter -> {
          process(isBackup, pid);
          emitter.onSuccess(true);
        })
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread());

      results=LiveDataReactiveStreams.fromPublisher(backup.toFlowable());
    }

    private void process(boolean isBackup, int pid) throws IOException {
      SystemClock.sleep(1000);  // wait for things to settle

      Process.killProcess(pid);

      File dbDir=getApplication().getDatabasePath("foo").getParentFile();
      File extDir=getApplication().getExternalFilesDir(null);
      File backupDir=new File(extDir, "db-backup");

      if (isBackup) {
        if (backupDir.exists()) {
          delete(backupDir);
        }

        backupDir.mkdirs();
        copy(dbDir, backupDir);
      }
      else {
        if (dbDir.exists()) {
          delete(dbDir);
        }

        dbDir.mkdirs();
        copy(backupDir, dbDir);
      }
    }
  }

  private class VMFactory extends ViewModelProvider.AndroidViewModelFactory {
    private final boolean isBackup;
    private final int pid;

    public VMFactory(@NonNull Application app, boolean isBackup, int pid) {
      super(app);
      this.isBackup=isBackup;
      this.pid=pid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
      return (T)new VM(getApplication(), isBackup, pid);
    }
  }
}
