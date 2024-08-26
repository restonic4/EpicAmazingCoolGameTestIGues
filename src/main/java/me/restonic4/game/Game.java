package me.restonic4.game;

import me.restonic4.engine.SceneManager;
import me.restonic4.engine.util.debug.diagnosis.Logger;
import me.restonic4.game.core.registries.RegistryManager;
import me.restonic4.game.core.scenes.WorldScene;

public abstract class Game {
    public static void start() {
        Logger.log("Starting the game");

        RegistryManager.registerAll();

        SceneManager.loadScene(new WorldScene());
    }
}
