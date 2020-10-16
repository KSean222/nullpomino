package net.ksean222.nullpomino.coldclear.executors;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.play.GameEngine;

public class HoldExecutor implements MovementExecutor {
    private boolean pressed = false;

    @Override
    public boolean execute(GameEngine engine, Controller ctrl) {
        if (engine.holdDisable) {
            return true;
        } else {
            pressed = !pressed;
            ctrl.setButtonState(Controller.BUTTON_D, pressed);
            return false;
        }
    }
}
