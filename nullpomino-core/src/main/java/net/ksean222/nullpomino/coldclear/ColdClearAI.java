package net.ksean222.nullpomino.coldclear;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.subsystem.ai.DummyAI;
import net.ksean222.nullpomino.coldclear.executors.*;
import net.ksean222.nullpomino.coldclear.raw.*;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class ColdClearAI extends DummyAI {
    private static final Logger log = Logger.getLogger(ColdClearAI.class);
    private static final CCPiece[] nullpoToCCPieceMap = new CCPiece[CCPiece.values().length];
    private CCAsyncBot bot;
    private boolean dead;
    private Queue<CCMovement> movements = new LinkedList<>();
    private boolean hold = false;
    private MovementExecutor executor = null;
    private boolean hardDrop = false;
    private boolean hardDropPressed = false;
    private int prevQueuePosition;
    private int delay = 0;

    static {
        nullpoToCCPieceMap[Piece.PIECE_I] = CCPiece.I;
        nullpoToCCPieceMap[Piece.PIECE_L] = CCPiece.L;
        nullpoToCCPieceMap[Piece.PIECE_O] = CCPiece.O;
        nullpoToCCPieceMap[Piece.PIECE_Z] = CCPiece.Z;
        nullpoToCCPieceMap[Piece.PIECE_T] = CCPiece.T;
        nullpoToCCPieceMap[Piece.PIECE_J] = CCPiece.J;
        nullpoToCCPieceMap[Piece.PIECE_S] = CCPiece.S;
    }

    @Override
    public String getName() {
        return "COLD CLEAR";
    }

    @Override
    public void init(GameEngine engine, int playerID) {
        CCOptions options = new CCOptions();
        CCLib.INSTANCE.cc_default_options(options);
        CCWeights weights = new CCWeights();
        CCLib.INSTANCE.cc_fast_weights(weights);
        bot = CCLib.INSTANCE.cc_launch_async(options, weights);
        prevQueuePosition = engine.nextPieceCount;
    }

    @Override
    public void shutdown(GameEngine engine, int playerID) {
        if (bot != null) {
            CCLib.INSTANCE.cc_destroy_async(bot);
            bot = null;
        }
        movements.clear();
        dead = false;
        hold = false;
        hardDrop = false;
        hardDropPressed = false;
        executor = null;
        prevQueuePosition = 0;
        delay = 0;
    }

    @Override
    public void setControl(GameEngine engine, int playerID, Controller ctrl) {
//        try {
//            Thread.sleep(116);
//        } catch (InterruptedException e) {
//            log.error(e);
//        }
        ctrl.reset();
        if (!dead) {
            if (executor != null) {
                if (executor.execute(engine, ctrl)) {
//                    log.debug("Finished:");
//                    log.debug(executor);
                    executor = null;
                }
            } else {
                if (delay < 0) {
                    delay += 1;
                } else {
                    if (hold) {
                        executor = new HoldExecutor();
                        hold = false;
                    } else {
                        CCMovement movement = movements.poll();
                        if (movement != null) {
                            switch (movement) {
                                case LEFT:
                                case RIGHT:
                                    executor = new ShiftExecutor(engine, movement == CCMovement.RIGHT);
                                    break;
                                case CW:
                                case CCW:
                                    executor = new RotateExecutor(engine, movement == CCMovement.CW);
                                    break;
                                case DROP:
                                    executor = new DropExecutor();
                                    break;
                            }
                        } else if (hardDrop) {
                            hardDropPressed = !hardDropPressed;
                            ctrl.setButtonState(engine.getUp(), hardDropPressed);
                        }
                    }
                    delay = 0;
                }
            }
        }
    }

    @Override
    public void newPiece(GameEngine engine, int playerID) {
        if (!dead) {
            while (prevQueuePosition < engine.nextPieceCount + 5) {
                int piece = engine.getNextID(prevQueuePosition);
                CCLib.INSTANCE.cc_add_next_piece_async(bot, nullpoToCCPieceMap[piece]);
                prevQueuePosition += 1;
            }
            if (movements.isEmpty() && executor == null) {
                CCMove move = new CCMove();
                CCLib.INSTANCE.cc_request_next_move(bot, 0);
                dead = CCLib.INSTANCE.cc_block_next_move(bot, move, null, null) == CCBotPollStatus.BOT_DEAD;
                if (!dead) {
                    hardDrop = true;
                    hardDropPressed = false;
                    hold = move.hold != 0;
                    movements.addAll(Arrays.asList(move.movements).subList(0, move.movement_count));
                }
            }
        }
    }

//    @Override
//    public void renderState(GameEngine engine, int playerID) {
//        if (move != null && engine.nowPieceObject != null) {
//            for (int i = 0; i < 4; i++) {
//                int x = move.expected_x[i];
//                int y = move.expected_y[i];
//                int offsetX = engine.owner.receiver.getFieldDisplayPositionX(engine, playerID);
//                int offsetY = engine.owner.receiver.getFieldDisplayPositionY(engine, playerID);
//                int w = engine.owner.receiver.getBlockGraphicsWidth(engine, playerID);
//                int h = engine.owner.receiver.getBlockGraphicsHeight(engine, playerID);
//                engine.owner.receiver.drawSingleBlock(
//                        engine,
//                        playerID,
//                        x * w + offsetX,
//                        (22 - y) * h + offsetY,
//                        0,
//                        0,
//                        true,
//                        0,
//                        0.8f,
//                        1f
//                );
//            }
//        }
//    }
}
