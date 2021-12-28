package com.github.sorusclient.client.util;

import java.util.Objects;

public class Pair<A, B> {

    private final A first;
    private final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        return this.getFirst().equals(((Pair<?, ?>) o).getFirst()) && this.getSecond().equals(((Pair<?, ?>) o).getSecond());
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

}
