package org.ktachibana.cloudemoji.events;

public class UpdateCheckedEvent {
    private int mVercode;

    public UpdateCheckedEvent(int vercode) {
        mVercode = vercode;
    }

    public int getVercode() {
        return mVercode;
    }
}
