package net.ksean222.nullpomino.coldclear.raw;
import com.sun.jna.Structure;

@Structure.FieldOrder({"mode", "spawn_rule", "use_hold", "speculate", "pcloop", "min_nodes", "max_nodes", "threads"})
public class CCOptions extends Structure {
    public CCMovementMode mode;
    public CCSpawnRule spawn_rule;
    public byte use_hold;
    public byte speculate;
    public byte pcloop;
    public int min_nodes;
    public int max_nodes;
    public int threads;

    public static class ByReference extends CCOptions implements Structure.ByReference {

    };
    public static class ByValue extends CCOptions implements Structure.ByValue {

    };
}
