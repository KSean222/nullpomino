package net.ksean222.nullpomino.coldclear.raw;
import com.sun.jna.Structure;

@Structure.FieldOrder({"back_to_back", "bumpiness", "bumpiness_sq", "row_transitions", "height", "top_half", "top_quarter", "jeopardy", "cavity_cells", "cavity_cells_sq", "overhang_cells", "overhang_cells_sq", "covered_cells", "covered_cells_sq", "tslot", "well_depth", "max_well_depth", "well_column", "b2b_clear", "clear1", "clear2", "clear3", "clear4", "tspin1", "tspin2", "tspin3", "mini_tspin1", "mini_tspin2", "perfect_clear", "combo_garbage", "move_time", "wasted_t", "use_bag", "timed_jeopardy", "stack_pc_damage"})
public class CCWeights extends Structure {
    public int back_to_back;
    public int bumpiness;
    public int bumpiness_sq;
    public int row_transitions;
    public int height;
    public int top_half;
    public int top_quarter;
    public int jeopardy;
    public int cavity_cells;
    public int cavity_cells_sq;
    public int overhang_cells;
    public int overhang_cells_sq;
    public int covered_cells;
    public int covered_cells_sq;
    public int[] tslot = new int[4];
    public int well_depth;
    public int max_well_depth;
    public int[] well_column = new int[10];
    public int b2b_clear;
    public int clear1;
    public int clear2;
    public int clear3;
    public int clear4;
    public int tspin1;
    public int tspin2;
    public int tspin3;
    public int mini_tspin1;
    public int mini_tspin2;
    public int perfect_clear;
    public int combo_garbage;
    public int move_time;
    public int wasted_t;
    public byte use_bag;
    public byte timed_jeopardy;
    public byte stack_pc_damage;

    public static class ByReference extends CCOptions implements Structure.ByReference {

    };
    public static class ByValue extends CCOptions implements Structure.ByValue {

    };
}
