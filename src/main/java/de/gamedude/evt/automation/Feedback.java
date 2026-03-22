package de.gamedude.evt.automation;

public enum Feedback {

    SUCCESS(0),
    FAILURE(1);


    private String reason;
    private final int status;

    Feedback(int status) {
        this.status = status;
    }

    public Feedback with(String reason) {
        this.reason = reason;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public boolean success() {
        return this == SUCCESS;
    }

}
