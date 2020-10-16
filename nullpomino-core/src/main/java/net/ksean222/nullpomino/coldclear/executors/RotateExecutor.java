package net.ksean222.nullpomino.coldclear.executors;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.play.GameEngine;

public class RotateExecutor implements MovementExecutor {
    private int initialDirection;
    private boolean isRight;

    public RotateExecutor(GameEngine engine, boolean isRight) {
        initialDirection = engine.nowPieceObject.direction;
        this.isRight = isRight;
    }

    @Override
    public boolean execute(GameEngine engine, Controller ctrl) {
        if (engine.nowPieceObject.direction != initialDirection) {
            return true;
        } else {
            boolean inverted = !engine.isRotateButtonDefaultRight();
            int button = (isRight ^ inverted)
                    ? Controller.BUTTON_A
                    : Controller.BUTTON_B;
            ctrl.setButtonPressed(button);
            return false;
        }
    }
}
