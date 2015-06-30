package org.ktachibana.cloudemoji;

import android.test.AndroidTestCase;

import org.ktachibana.cloudemoji.models.memory.Category;
import org.ktachibana.cloudemoji.models.memory.Entry;
import org.ktachibana.cloudemoji.models.memory.Source;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SourceInMemoryCacheTest extends AndroidTestCase {
    protected SourceInMemoryCache mCache;

    @Override
    protected void setUp() throws Exception {
        mCache = new SourceInMemoryCache();
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        mCache = null;
        super.tearDown();
    }

    public void testInitialization() {
        long key = generateRandomLong();
        individuallyTestSize(0);
        assertNull("get() should return null", mCache.get(key));
        assertNull("contains() should return null", mCache.contains(key));
    }

    public void testAddingOnePair() {
        // Put a pair in cache
        long key = generateRandomLong();
        Source value = generateRandomSource();
        mCache.put(key, value);
        individuallyTestSize(1);
        individuallyTestOnePair(0, key, value);

        // Replace the value with same key
        Source newValue = generateRandomSource();
        mCache.put(key, newValue);
        individuallyTestSize(1);
        individuallyTestOnePair(0, key, newValue);
    }

    public void testAddingTwoPairs() {
        // Put first pair in cache
        long firstKey = generateRandomLong();
        Source firstValue = generateRandomSource();
        mCache.put(firstKey, firstValue);

        // Put second pair in cache
        long secondKey = generateRandomLong();
        Source secondValue = generateRandomSource();
        mCache.put(secondKey, secondValue);
        individuallyTestSize(2);
        individuallyTestOnePair(0, firstKey, firstValue);
        individuallyTestOnePair(1, secondKey, secondValue);
    }

    public void testAddingManyPairs() {
        int numberToExceed = 4;
        Map<Long, Source> pairs = new HashMap<Long, Source>();
        for (int i = 0; i < numberToExceed * 2; i++) {
            pairs.put(generateRandomLong(), generateRandomSource());
        }

        // Put many pairs in cache
        for (Map.Entry entry : pairs.entrySet()) {
            mCache.put((Long) entry.getKey(), (Source) entry.getValue());
        }

        individuallyTestSize(pairs.size());
        int i = 0;
        for (Map.Entry entry : pairs.entrySet()) {
            individuallyTestOnePair(i++, (Long) entry.getKey(), (Source) entry.getValue());
        }
    }

    private void individuallyTestOnePair(int id, long key, Source value) {
        assert mCache != null;
        assertEquals(
                String.format("get() should return corresponding value for id=%d", id),
                value,
                mCache.get(key));
        assertNotNull(
                String.format("contains() should NOT return null for id=%d", id),
                mCache.get(key)
        );
    }

    private void individuallyTestSize(int expectedSize) {
        assert mCache != null;
        assertEquals(
                String.format("size() should return %d", expectedSize),
                expectedSize,
                mCache.size()
        );
    }

    private long generateRandomLong() {
        return new Random().nextLong();
    }

    private Source generateRandomSource() {
        return new Source(generateRandomInformation(5), generateRandomCategories(10));
    }

    private ArrayList<String> generateRandomInformation(int count) {
        ArrayList<String> information = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            information.add(generateRandomString(5));
        }
        return information;
    }

    private List<Category> generateRandomCategories(int count) {
        List<Category> categories = new ArrayList<Category>();
        for (int i = 0; i < count; i++) {
            categories.add(generateRandomCategory(20));
        }
        return categories;
    }

    private Category generateRandomCategory(int count) {
        String name = generateRandomString(10);
        List<Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            entries.add(new Entry(generateRandomString(10), generateRandomString(10)));
        }
        return new Category(name, entries);
    }

    private String generateRandomString(int count) {
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            char c = alphabet[random.nextInt(alphabet.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }
}
