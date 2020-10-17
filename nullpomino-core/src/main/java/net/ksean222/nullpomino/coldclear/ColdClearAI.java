package net.ksean222.nullpomino.coldclear;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.subsystem.ai.DummyAI;
import net.ksean222.nullpomino.coldclear.executors.*;
import net.ksean222.nullpomino.coldclear.raw.*;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class ColdClearAI extends DummyAI {
    private static final Logger log = Logger.getLogger(ColdClearAI.class);
    private static final CCPiece[] nullpoToCCPieceMap = new CCPiece[CCPiece.values().length];

    private CCAsyncBot bot;
    private boolean dead;

    private Queue<CCMovement> movements = new LinkedList<>();
    private MovementExecutor executor = null;
    private boolean hold = false;
    private boolean hardDrop = false;
    private boolean hardDropPressed = false;
    private int delay = 0;
    private List<boolean[]> expectedField = new ArrayList<>();

    private int prevQueuePosition;

    static {
        nullpoToCCPieceMap[Piece.PIECE_I] = CCPiece.I;
        nullpoToCCPieceMap[Piece.PIECE_L] = CCPiece.L;
        nullpoToCCPieceMap[Piece.PIECE_O] = CCPiece.O;
        nullpoToCCPieceMap[Piece.PIECE_Z] = CCPiece.Z;
        nullpoToCCPieceMap[Piece.PIECE_T] = CCPiece.T;
        nullpoToCCPieceMap[Piece.PIECE_J] = CCPiece.J;
        nullpoToCCPieceMap[Piece.PIECE_S] = CCPiece.S;
    }

    public ColdClearAI() {
        for (int i = 0; i < 40; i++) {
            expectedField.add(new boolean[10]);
        }
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
        dead = false;

        movements.clear();
        hold = false;
        hardDrop = false;
        hardDropPressed = false;
        executor = null;
        for (boolean[] row : expectedField) {
            Arrays.fill(row, false);
        }

        prevQueuePosition = 0;
        delay = 0;
    }

    @Override
    public void setControl(GameEngine engine, int playerID, Controller ctrl) {
        ctrl.reset();
        if (!dead && engine.nowPieceObject != null) {
            if (executor != null) {
                if (executor.execute(engine, ctrl)) {
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
            if (!engine.holdDisable) {
                movements.clear();
                executor = null;
                boolean reset = false;
                for (int y = 0; y < 40; y++) {
                    for (int x = 0; x < 10; x++) {
                        boolean cell = !engine.field.getBlockEmpty(x, engine.fieldHeight - y - 1);
                        boolean expected = expectedField.get(y)[x];
                        if (cell != expected) {
                            expectedField.get(y)[x] = cell;
                            reset = true;
                        }
                    }
                }
                if (reset) {
                    byte[] field = new byte[400];
                    for (int y = 0; y < 40; y++) {
                        for (int x = 0; x < 10; x++) {
                            field[y * 10 + x] = (byte)(expectedField.get(y)[x] ? 1 : 0);
                        }
                    }
                    CCLib.INSTANCE.cc_reset_async(bot, field, (byte)(engine.b2b ? 1 : 0), engine.combo);
                }

                CCMove move = new CCMove();
                CCLib.INSTANCE.cc_request_next_move(bot, engine.owner.mode.getGarbage(playerID));
                dead = CCLib.INSTANCE.cc_block_next_move(bot, move, null, null) == CCBotPollStatus.BOT_DEAD;
                if (!dead) {
                    hardDrop = true;
                    hardDropPressed = false;
                    hold = move.hold != 0;
                    movements.addAll(Arrays.asList(move.movements).subList(0, move.movement_count));
                    for (int i = 0; i < 4; i++) {
                        int x = move.expected_x[i];
                        int y = move.expected_y[i];
                        expectedField.get(y)[x] = true;
                    }
                    expectedField = expectedField
                            .stream()
                            .filter(row -> {
                                for (boolean cell : row) {
                                    if (!cell) {
                                        return true;
                                    }
                                }
                                return false;
                            })
                            .collect(Collectors.toList());
                    while (expectedField.size() < 40) {
                        expectedField.add(new boolean[10]);
                    }
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
