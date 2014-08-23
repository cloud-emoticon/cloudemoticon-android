package org.ktachibana.cloudemoji.events;

public class SearchInitiatedEvent {
    private String mSearchQuery;

    public SearchInitiatedEvent(String searchQuery) {
        mSearchQuery = searchQuery;
    }

    public String getSearchQuery() {
        return mSearchQuery;
    }
}
