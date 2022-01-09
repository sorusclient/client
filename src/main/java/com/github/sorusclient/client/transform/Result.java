package com.github.sorusclient.client.transform;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class Result<T> {

    private final List<T> results;

    public Result(List<T> results) {
        this.results = results;
    }

    public void apply(Consumer<? extends T> consumer) {
        for (T result : this.results) {
            ((Consumer<T>) consumer).accept(result);
        }
    }

    public Result<T> nth(int index) {
        return new Result<>(Collections.singletonList(this.results.get(index)));
    }

}
