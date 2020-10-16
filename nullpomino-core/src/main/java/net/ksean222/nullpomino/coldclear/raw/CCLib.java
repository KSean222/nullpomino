package net.ksean222.nullpomino.coldclear.raw;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface CCLib extends Library {
    CCLib INSTANCE = Native.load("cold_clear", CCLib.class);

    /** Launches a bot thread with a blank board, empty queue, and all seven pieces in the bag, using the
     * specified options and weights.
     *
     * You pass the returned pointer with `cc_destroy_async` when you are done with the bot instance.
     *
     * Lifetime: The returned pointer is valid until it is passed to `cc_destroy_async`.
     */
    CCAsyncBot cc_launch_async(CCOptions options, CCWeights weights);

    /** Terminates the bot thread and frees the memory associated with the bot.
     */
    void cc_destroy_async(CCAsyncBot bot);

    /** Resets the playfield, back-to-back status, and combo count.
     *
     * This should only be used when garbage is received or when your client could not place the
     * piece in the correct position for some reason (e.g. 15 move rule), since this forces the
     * bot to throw away previous computations.
     *
     * Note: combo is not the same as the displayed combo in guideline games. Here, it is the
     * number of consecutive line clears achieved. So, generally speaking, if "x Combo" appears
     * on the screen, you need to use x+1 here.
     *
     * The field parameter is a pointer to the start of an array of 400 booleans in row major order,
     * with index 0 being the bottom-left cell.
     */
    void cc_reset_async(CCAsyncBot bot, byte[] field, byte b2b, int combo);

    /** Adds a new piece to the end of the queue.
     *
     * If speculation is enabled, the piece must be in the bag. For example, if you start a new
     * game with starting sequence IJOZT, the first time you call this function you can only
     * provide either an L or an S piece.
     */
    void cc_add_next_piece_async(CCAsyncBot bot, CCPiece piece);

    /** Request the bot to provide a move as soon as possible.
     *
     * In most cases, "as soon as possible" is a very short amount of time, and is only longer if
     * the provided lower limit on thinking has not been reached yet or if the bot cannot provide
     * a move yet, usually because it lacks information on the next pieces.
     *
     * For example, in a game with zero piece previews and hold enabled, the bot will never be able
     * to provide the first move because it cannot know what piece it will be placing if it chooses
     * to hold. Another example: in a game with zero piece previews and hold disabled, the bot
     * will only be able to provide a move after the current piece spawns and you provide the piece
     * information to the bot using `cc_add_next_piece_async`.
     *
     * It is recommended that you call this function the frame before the piece spawns so that the
     * bot has time to finish its current thinking cycle and supply the move.
     *
     * Once a move is chosen, the bot will update its internal state to the result of the piece
     * being placed correctly and the move will become available by calling `cc_poll_next_move`.
     *
     * The incoming parameter specifies the number of lines of garbage the bot is expected to receive
     * after placing the next piece.
     */
    void cc_request_next_move(CCAsyncBot bot, int incoming);

    /* Checks to see if the bot has provided the previously requested move yet.
     *
     * The returned move contains both a path and the expected location of the placed piece. The
     * returned path is reasonably good, but you might want to use your own pathfinder to, for
     * example, exploit movement intricacies in the game you're playing.
     *
     * If the piece couldn't be placed in the expected location, you must call `cc_reset_async` to
     * reset the game field, back-to-back status, and combo values.
     *
     * If `plan` and `plan_length` are not `NULL` and this function provides a move, a placement plan
     * will be returned in the array pointed to by `plan`. `plan_length` should point to the length
     * of the array, and the number of plan placements provided will be returned through this pointer.
     *
     * If the move has been provided, this function will return `CC_MOVE_PROVIDED`.
     * If the bot has not produced a result, this function will return `CC_WAITING`.
     * If the bot has found that it cannot survive, this function will return `CC_BOT_DEAD`
     */
    CCBotPollStatus cc_poll_next_move(
            CCAsyncBot bot,
            CCMove move,
            CCPlanPlacement[] plan,
            Integer plan_length
    );

    /** This function is the same as `cc_poll_next_move` except when `cc_poll_next_move` would return
     * `CC_WAITING` it instead waits until the bot has made a decision.
     *
     * If the move has been provided, this function will return `CC_MOVE_PROVIDED`.
     * If the bot has found that it cannot survive, this function will return `CC_BOT_DEAD`
     */
    CCBotPollStatus cc_block_next_move(
            CCAsyncBot bot,
            CCMove move,
            CCPlanPlacement[] plan,
            Integer plan_length
    );

    /**
     * Returns the default options in the options parameter<br>
     */
    void cc_default_options(CCOptions options);

    /**
     * Returns the default weights in the weights parameter<br>
     */
    void cc_default_weights(CCWeights weights);

    /**
     * Returns the fast game config weights in the weights parameter<br>
     */
    void cc_fast_weights(CCWeights weights);
}

