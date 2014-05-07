package org.ktachibana.cloudemoji.models;

import java.util.List;

/**
 * POJO class holding information and a list of categories
 */
public class Source {
    private List<String> information;
    private List<Category> categories;

    public Source(List<String> information, List<Category> categories) {
        this.information = information;
        this.categories = categories;
    }

    public List<String> getInformation() {
        return information;
    }

    public List<Category> getCategories() {
        return categories;
    }
}
