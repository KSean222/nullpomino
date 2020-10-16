package net.ksean222.nullpomino.coldclear.raw;

import com.sun.jna.FromNativeContext;
import com.sun.jna.NativeMapped;

public enum CCSpawnRule implements NativeMapped {
    ROW_19_OR_20,
    ROW_21_AND_FALL;

    @Override
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        return values()[(Integer) nativeValue];
    }

    @Override
    public Object toNative() {
        return ordinal();
    }

    @Override
    public Class<?> nativeType() {
        return Integer.class;
    }
}
