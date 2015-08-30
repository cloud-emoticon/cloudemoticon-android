package org.ktachibana.cloudemoji;

import android.test.AndroidTestCase;
import android.util.Log;

import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.ktachibana.cloudemoji.auth.ParseUserState;
import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.models.remote.ParseBookmark;
import org.ktachibana.cloudemoji.sync.ParseBookmarkManager;

import java.util.ArrayList;
import java.util.List;

public class MergeTest extends AndroidTestCase {
    private List<Favorite> mLocal;
    private List<ParseBookmark> mRemote;
    private ParseUser mOwner;
    private static final int DEFAULT_NUMBER_OF_ITEMS = 5;

    public MergeTest() {
        super();
        ParseObject.registerSubclass(ParseBookmark.class);
    }

    @Override
    protected void setUp() throws Exception {
        mLocal = new ArrayList<>();
        mRemote = new ArrayList<>();
        ParseUser.enableAutomaticUser();
        mOwner = ParseUserState.getLoggedInUser();
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        mLocal = null;
        mRemote = null;
        mOwner = null;
        super.tearDown();
    }

    public void testBothLocalAndRemoteEmpty() {
        ParseBookmarkManager.MergeResult result = ParseBookmarkManager.merge(mLocal, mRemote);
        assertEquals("local unique set should be empty", 0, result.localUnique.size());
        assertEquals("remote unique set should be empty", 0, result.remoteUnique.size());
        assertEquals("local merged set should be empty", 0, result.localMerged.size());
        assertEquals("remote merged set should be empty", 0, result.remoteMerged.size());
    }

    public void testOnlyLocalEmptyAndRemoteFull() {
        for (int i = 0; i < DEFAULT_NUMBER_OF_ITEMS; i++) {
            mRemote.add(generateNewParseBookmark());
        }
        ParseBookmarkManager.MergeResult result = ParseBookmarkManager.merge(mLocal, mRemote);
        assertEquals("local unique set should be the same as remote set",
                Favorite.convert(mRemote),
                result.localUnique
        );
        assertEquals("remote unique set should be empty", 0, result.remoteUnique.size());
        assertEquals("local merged set should be empty", 0, result.localMerged.size());
        assertEquals("remote merged set should be empty", 0, result.remoteMerged.size());
    }

    public void testOnlyLocalFullAndRemoteEmpty() {
        for (int i = 0; i < DEFAULT_NUMBER_OF_ITEMS; i++) {
            mLocal.add(generateNewFavorite());
        }
        ParseBookmarkManager.MergeResult result = ParseBookmarkManager.merge(mLocal, mRemote);
        assertEquals("local unique set should be empty", 0, result.localUnique.size());
        assertEquals("remote unique set should be the same as local set",
                mLocal,
                Favorite.convert(result.remoteUnique)
        );
        assertEquals("local merged set should be empty", 0, result.localMerged.size());
        assertEquals("remote merged set should be empty", 0, result.remoteMerged.size());
    }

    public void testBothLocalAndRemoteTheSame() {
        // TODO
    }

    private ParseBookmark generateNewParseBookmark() {
        ParseBookmark parseBookmark = new ParseBookmark(mOwner);
        parseBookmark.setEmoticon(TestUtils.generateRandomString());
        parseBookmark.setDescription(TestUtils.generateRandomString());
        parseBookmark.setShortcut(TestUtils.generateRandomString());
        return parseBookmark;
    }

    private Favorite generateNewFavorite() {
        return new Favorite(TestUtils.generateRandomString(), TestUtils.generateRandomString(), TestUtils.generateRandomString());
    }
}
