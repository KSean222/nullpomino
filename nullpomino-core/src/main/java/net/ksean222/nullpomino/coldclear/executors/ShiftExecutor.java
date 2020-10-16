package net.ksean222.nullpomino.coldclear.executors;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.play.GameEngine;

public class ShiftExecutor implements MovementExecutor {
    private int targetX;

    public ShiftExecutor(GameEngine engine, boolean isRight) {
        targetX = engine.nowPieceX + (isRight ? 1 : -1);
    }

    @Override
    public boolean execute(GameEngine engine, Controller ctrl) {
        if (engine.nowPieceX == targetX) {
            return true;
        } else {
            int button = (engine.nowPieceX > targetX)
                    ? Controller.BUTTON_LEFT
                    : Controller.BUTTON_RIGHT;
            ctrl.setButtonPressed(button);
            return false;
        }
    }
}
