package com.yungnickyoung.minecraft.bettercaves.worldgen.context;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Just a very simple recreation of ScopedValue using ThreadLocal so that I don't have to rewrite usages.
 */
@NullMarked
public class ScopedValue<T> {
    private final ThreadLocal<@Nullable T> threadLocal = new ThreadLocal<>();

    public boolean isBound() {
        return this.threadLocal.get() != null;
    }

    public T get() {
        return Objects.requireNonNull(this.threadLocal.get());
    }

    public static <T> ScopedValue<T> newInstance() {
        return new ScopedValue<>();
    }

    public static <T> Carrier<T> where(ScopedValue<T> scopedValue, T value) {
        return new Carrier<>(scopedValue, value);
    }

    public interface CallableOp<R, X extends Throwable> {
        R call() throws X;
    }

    public static final class Carrier<T> {
        private final ScopedValue<T> scopedValue;
        private final T value;

        private Carrier(ScopedValue<T> scopedValue, T value) {
            this.scopedValue = scopedValue;
            this.value = value;
        }

        public <R, X extends Throwable> R call(CallableOp<R, X> callable) throws X {
            this.scopedValue.threadLocal.set(this.value);
            var result = callable.call();
            this.scopedValue.threadLocal.remove();
            return result;
        }
    }
}
