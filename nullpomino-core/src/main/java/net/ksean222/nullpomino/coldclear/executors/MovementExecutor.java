package net.ksean222.nullpomino.coldclear.executors;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.play.GameEngine;

public interface MovementExecutor {
    boolean execute(GameEngine engine, Controller ctrl);
}
