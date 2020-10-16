package net.ksean222.nullpomino.coldclear.raw;

import com.sun.jna.FromNativeContext;
import com.sun.jna.NativeMapped;

public enum CCPiece implements NativeMapped {
    I, O, T, L, J, S, Z;

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
