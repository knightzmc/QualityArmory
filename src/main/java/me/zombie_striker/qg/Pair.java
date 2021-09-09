package me.zombie_striker.qg;

import java.util.Objects;

public class Pair<A, B> {
    private final A a;
    private final B b;

    public Pair(A a, B b ) {
        this.a = a;
        this.b = b;
    }

    public A getKey() {
        return a;
    }

    public B getValue() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(a, pair.a) && Objects.equals(b, pair.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
