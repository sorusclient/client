package com.github.sorusclient.client.adapter.event;

import com.github.sorusclient.client.adapter.Box;
import com.github.sorusclient.client.event.EventCancelable;

public class BlockOutlineRenderEvent extends EventCancelable {

    private final Box box;

    public BlockOutlineRenderEvent(Box box) {
        this.box = box;
    }

    public Box getBox() {
        return box;
    }

}
