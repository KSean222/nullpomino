package net.ksean222.nullpomino.coldclear.executors;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.play.GameEngine;

public class DropExecutor implements MovementExecutor {
    @Override
    public boolean execute(GameEngine engine, Controller ctrl) {
        if (engine.nowPieceY == engine.nowPieceBottomY) {
            return true;
        } else {
            ctrl.setButtonPressed(engine.getDown());
            return false;
        }
    }
}
