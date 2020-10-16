package net.ksean222.nullpomino.coldclear.raw;

import com.sun.jna.Structure;

@Structure.FieldOrder({"hold", "expected_x", "expected_y", "movement_count", "movements", "nodes", "depth", "original_rank"})
public class CCMove extends Structure {
    /// Whether hold is required
    public byte hold;
    /// Expected x positions of placement, 0 being the left
    public byte[] expected_x = new byte[4];
    /// Expected y positions of placement, 0 being the bottom
    public byte[] expected_y = new byte[4];
    /// Number of moves in the path
    public byte movement_count;

    public CCMovement[] movements = new CCMovement[32];

    public int nodes;
    public int depth;
    public int original_rank;
}
