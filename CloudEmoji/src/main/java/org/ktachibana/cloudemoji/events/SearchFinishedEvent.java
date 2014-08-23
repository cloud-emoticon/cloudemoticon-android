package org.ktachibana.cloudemoji.events;

import org.ktachibana.cloudemoji.models.Entry;

import java.util.List;

public class SearchFinishedEvent {
    private List<Entry> mResults;

    public SearchFinishedEvent(List<Entry> results) {
        this.mResults = results;
    }

    public List<Entry> getResults() {
        return mResults;
    }
}
