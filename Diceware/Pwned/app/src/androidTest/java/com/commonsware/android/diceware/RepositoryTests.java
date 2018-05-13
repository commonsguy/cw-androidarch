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

import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RepositoryTests {
  private static final Uri ASSETS=
    Uri.parse("file:///android_asset/eff_short_wordlist_2_0.txt");

  @Test
  public void words() {
    Repository repo=Repository.get(InstrumentationRegistry.getTargetContext());
    final String passphrase=repo.getPassphrase(ASSETS, 6).blockingFirst();

    assertTrue(passphrase.length()>11);

    final String passphrase2=repo.getPassphrase(ASSETS, 9).blockingFirst();

    assertFalse(passphrase.equals(passphrase2));
    assertTrue(passphrase.length()>17);
  }
}
