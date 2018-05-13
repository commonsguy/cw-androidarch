package com.commonsware.android.diceware;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.reactivex.Observable;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Function;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;

@RunWith(AndroidJUnit4.class)
public class ScrapTest {
  private String stringResult=null;
  private Throwable throwableResult=null;

  @Test
  public void normal() {
    Observable.just("foo")
      .map(this::doSomethingUseful)
      .retryWhen(errors -> errors.zipWith(Observable.range(1, 3), (n, i) -> i))
      .subscribe(s -> stringResult=s, throwable -> throwableResult=throwable);

    assertEquals("FOO", stringResult);
    assertNull(throwableResult);
  }

  @Test
  public void whereIsTheThrowable() {
    Observable.just("foo")
      .map(this::justBlowUp)
      .retryWhen(errors -> errors.zipWith(Observable.range(1, 3), (n, i) -> i))
      .subscribe(s -> stringResult=s, throwable -> throwableResult=throwable);

    assertNull(stringResult);
    assertNotNull(throwableResult);
  }

  @Test
  public void whereIsTheThrowable2() {
    Observable.just("foo")
      .map(this::justBlowUp)
      .retryWhen(errors -> errors.flatMap(new Function<Throwable, Observable<Integer>>() {
        int count;
        @Override
        public Observable<Integer> apply(Throwable error) {
          if (count++ < 3) {
            return Observable.just(count);
          }
          return Observable.error(error);
        }
      }))
      .subscribe(s -> stringResult=s, throwable -> throwableResult=throwable);

    assertNull(stringResult);
    assertNotNull(throwableResult);
  }

  @Test
  public void retryUntil() {
    Observable.just("foo")
      .map(this::justBlowUp)
      .retryUntil(() -> true)
      .subscribe(s -> stringResult=s, throwable -> throwableResult=throwable);

    assertNull(stringResult);
    assertNotNull(throwableResult);
  }

  private String doSomethingUseful(String s) {
    return s.toUpperCase();
  }

  private String justBlowUp(String s) {
    throw new RuntimeException();
  }
}
