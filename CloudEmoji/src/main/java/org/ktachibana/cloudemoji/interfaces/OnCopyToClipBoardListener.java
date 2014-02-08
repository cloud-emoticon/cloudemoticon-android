package org.ktachibana.cloudemoji.interfaces;

/**
 * Activities implementing this interface should be responsible for copying to clip board given a string
 * Multiple fragments can utilize this interface
 */
public interface OnCopyToClipBoardListener {
    /**
     * Copies string to clip boards
     *
     * @param copied String copied
     */
    public void onCopyToClipBoard(String copied);
}
