package com.github.sorusclient.client.event;

public class EventCancelable extends Event {

    private boolean canceled = false;

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCanceled() {
        return canceled;
    }

}
