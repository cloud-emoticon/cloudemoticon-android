package org.ktachibana.cloudemoji.utils;

import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

public class UncheckableSecondaryDrawerItem extends SecondaryDrawerItem {
    public UncheckableSecondaryDrawerItem() {
        this.withSelectable(false);
    }
}
