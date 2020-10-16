package net.ksean222.nullpomino.coldclear.raw;

import com.sun.jna.Structure;

@Structure.FieldOrder({"piece", "tspin", "expected_x", "expected_y", "cleared_lines"})
public class CCPlanPlacement extends Structure {
    public CCPiece piece;
    public CCTspinStatus tspin;
    /**
     * Expected cell coordinates of placement, (0, 0) being the bottom left<br>
     * C type : uint8_t[4]
     */
    public byte[] expected_x = new byte[4];
    /// C type : uint8_t[4]
    public byte[] expected_y = new byte[4];
    /**
     * Expected lines that will be cleared after placement, with -1 indicating no line<br>
     * C type : int32_t[4]
     */
    public int[] cleared_lines = new int[4];
}
