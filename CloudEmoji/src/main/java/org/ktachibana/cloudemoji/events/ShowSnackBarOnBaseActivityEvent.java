package org.ktachibana.cloudemoji.events;

public class ShowSnackBarOnBaseActivityEvent {
    private String message;

    public ShowSnackBarOnBaseActivityEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
