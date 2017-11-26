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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ViewModelTests {
  private static final Uri ASSETS=
    Uri.parse("file:///android_asset/eff_short_wordlist_2_0.txt");
  private List<String> result;
  private CountDownLatch countDownLatch=new CountDownLatch(1);

  @Test
  public void words() throws InterruptedException {
    PassphraseViewModel vm=
      new PassphraseViewModel(InstrumentationRegistry.getTargetContext(), null);
    final LiveData<List<String>> words=vm.words();
    final Observer<List<String>> observer=strings -> {
      result=strings;
      countDownLatch.countDown();
    };

    words.observeForever(observer);
    assertTrue(countDownLatch.await(2, TimeUnit.SECONDS));
    assertEquals(6, result.size());

    countDownLatch=new CountDownLatch(1);

    List<String> originalWords=result;

    vm.setCount(7);
    assertTrue(countDownLatch.await(2, TimeUnit.SECONDS));
    assertEquals(7, result.size());

    InstrumentationRegistry.getInstrumentation()
      .runOnMainSync(() -> words.removeObserver(observer));
  }
}
