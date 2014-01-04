package org.ktachibana.cloudemoji;

/**
 * Activities implementing this interface should be responsible for handling exceptions
 * Multiple fragments can utilize this interface
 */
public interface OnExceptionListener {
    /**
     * Handles an exception
     *
     * @param e Exception handled
     */
    public void onException(Exception e);
}
