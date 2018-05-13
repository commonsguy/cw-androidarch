package com.commonsware.android.room.fts;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class BookTests {
  @Before
  public void setUp() {
    InstrumentationRegistry.getTargetContext()
      .getDatabasePath(BookDatabase.DB_NAME)
      .delete();
  }

  @Test
  public void testContents() {
    BookDatabase db=BookDatabase.get(InstrumentationRegistry.getTargetContext());

    assertEquals(17, db.store().chapterCount());

    List<ChapterEntity> chapters=db.store().chapters();

    assertEquals(17, chapters.size());

    for (ChapterEntity chapter : chapters) {
      assertTrue(db.store().paragraphCount(chapter.id)>0);
    }

    List<BookSearchResult> results=db.store().searchSynchronous("vague");

    assertEquals(6, results.size());
  }
}
