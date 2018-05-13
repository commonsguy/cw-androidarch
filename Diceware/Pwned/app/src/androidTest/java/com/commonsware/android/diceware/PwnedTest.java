package com.commonsware.android.diceware;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Stack;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PwnedTest {
  private static final String INVALID="password";
  private static final String VALID_UNTIL_SOMEBODY_USES_IT=
    "gesture federal geographer mapmaker arachnid skulk";

  @Test
  public void basic() throws Exception {
    PwnedCheck checker=new PwnedCheck(new OkHttpClient());

    assertTrue(checker.score(INVALID).subscribeOn(Schedulers.io()).blockingFirst()>=3303003);
    assertEquals(0, checker.score(VALID_UNTIL_SOMEBODY_USES_IT).subscribeOn(Schedulers.io()).blockingFirst().intValue());
  }

  @Test
  public void retry() {
    Stack<String> tests=new Stack<>();
    PwnedCheck checker=new PwnedCheck(new OkHttpClient());

    tests.push(VALID_UNTIL_SOMEBODY_USES_IT);
    tests.push(INVALID);

    String result=Observable.just(tests)
      .map(Stack::pop)
      .flatMap(checker::validate)
      .retryWhen(errors -> errors.zipWith(Observable.range(1, 3), (n, i) -> i))
      .blockingFirst();

    assertEquals(VALID_UNTIL_SOMEBODY_USES_IT, result);
  }
}
