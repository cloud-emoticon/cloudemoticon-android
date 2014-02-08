package org.ktachibana.cloudemoji.helpers;

/**
 * A class for a menu item in menu drawer
 * Holding it's item name, its corresponding type and Category it's holding if the type is CATEGORY
 */
public class MyMenuItem {
    public static final int SECTION_HEADER_TYPE = 0;
    public static final int CATEGORY_TYPE = 1;
    public static final int FAV_TYPE = 2;

    private String itemName;
    private int type;
    private RepoXmlParser.Category category;

    public MyMenuItem(String itemName, int type) {
        this.itemName = itemName;
        this.type = type;
    }

    public MyMenuItem(String itemName, int type, RepoXmlParser.Category category) {
        this.itemName = itemName;
        this.type = type;
        this.category = category;
    }

    public String getItemName() {
        return itemName;
    }

    public int getType() {
        return type;
    }

    public RepoXmlParser.Category getCategory() {
        return category;
    }
}
