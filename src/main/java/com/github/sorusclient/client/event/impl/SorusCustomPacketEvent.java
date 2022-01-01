package com.github.sorusclient.client.event.impl;

import com.github.sorusclient.client.event.Event;

public class SorusCustomPacketEvent extends Event {

    private final String channel;
    private final String contents;

    public SorusCustomPacketEvent(String channel, String contents) {
        this.channel = channel;
        this.contents = contents;
    }

    public String getChannel() {
        return channel;
    }

    public String getContents() {
        return contents;
    }

    public enum ChannelMain {

    }

}
