package me.restonic4.citadel.world;

import me.restonic4.citadel.util.debug.diagnosis.Logger;

import static org.lwjgl.opengl.GL11.glFinish;

public abstract class SceneManager {
    private static Scene currentScene;
    public static int changes = 0;

    public static Scene getCurrentScene() {
        return currentScene;
    }

    private static void setScene(Scene scene) {
        changes++;
        currentScene = scene;
    }

    public static void loadScene(Scene scene) {
        Logger.log("Loading the scene: " + scene);

        /*for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep((long) (1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        if (currentScene != null) {
            currentScene.unload();
        }

        setScene(scene);

        // Makes sure OpenGL finishes his things, so it doesn't break
        glFinish();

        scene.init();
        scene.activate();
    }

    public static void unLoadScene(Scene scene) {
        scene.unload();

        if (currentScene == scene) {
            currentScene = null;
        }
    }

    public static void unLoadCurrentScene() {
        if (currentScene != null) {
            unLoadScene(currentScene);
        }
    }
}