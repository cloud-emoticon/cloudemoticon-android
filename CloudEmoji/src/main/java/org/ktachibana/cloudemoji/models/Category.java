package org.ktachibana.cloudemoji.models;

import java.util.List;

/**
 * POJO class holding a name and a list of entries
 */
public class Category {
    private String name;
    private List<Entry> entries;

    public Category(String name, List<Entry> entries) {
        this.name = name;
        this.entries = entries;
    }

    public String getName() {
        return name;
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
