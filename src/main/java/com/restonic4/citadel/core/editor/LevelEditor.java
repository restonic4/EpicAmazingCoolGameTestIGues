package com.restonic4.citadel.core.editor;

import com.restonic4.citadel.core.CitadelLauncher;
import com.restonic4.citadel.core.Window;
import com.restonic4.citadel.input.KeyListener;
import com.restonic4.citadel.registries.built_in.managers.ImGuiScreens;
import com.restonic4.citadel.registries.built_in.managers.KeyBinds;
import com.restonic4.citadel.render.Texture;
import com.restonic4.citadel.render.gui.ImGuiHelper;
import com.restonic4.citadel.util.history.HistoryCommandManager;
import com.restonic4.citadel.util.history.commands.DeleteFileHistoryCommand;
import com.restonic4.citadel.util.history.commands.RenameGameObjectHistoryCommand;
import com.restonic4.citadel.world.SceneManager;
import com.restonic4.citadel.world.object.GameObject;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImBoolean;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Files;

import static imgui.flag.ImGuiWindowFlags.*;
import static imgui.flag.ImGuiWindowFlags.NoNavFocus;

// TODO: Make the UIs responsive
// TODO: Make an options/preferences window
// TODO: Make a keybindings UI to configure the keybindings such as F2, ESC and ENTER
public abstract class LevelEditor {
    private static Window window;
    private static HistoryCommandManager historyCommandManager;

    private static GameObject selectedObject;
    private static boolean isRenamingEnabled = false;
    private static boolean isPlaying = false;
    private static boolean isPaused = false;

    static int playButtonTextureId, stopButtonTextureId, pauseButtonTextureId;

    public static void init() {
        window = Window.getInstance();
        window.setCursorLocked(false);

        historyCommandManager = new HistoryCommandManager();

        ImGuiScreens.GAME_VIEWPORT.show();
        ImGuiScreens.EDITOR_INSPECTOR.show();
        ImGuiScreens.EDITOR_PROPERTIES.show();
        ImGuiScreens.EDITOR_ASSETS.show();

        playButtonTextureId = new Texture(true, "assets/textures/icons/play/56.png").getTextureID();
        stopButtonTextureId = new Texture(true, "assets/textures/icons/stop/56.png").getTextureID();
        pauseButtonTextureId = new Texture(true, "assets/textures/icons/pause/56.png").getTextureID();
    }

    // TODO: Weird behaviours in some screens, it should ignore the navbar height
    public static void render() {
        int windowFlags = MenuBar | NoDocking | NoTitleBar | NoCollapse | NoResize | NoMove | NoBringToFrontOnFocus | NoNavFocus;

        float menuBarHeight = ImGui.getFrameHeight();
        float menuBarSize = (menuBarHeight / 2) + ImGui.getStyle().getFramePadding().y;

        ImGui.setNextWindowPos(0.0f, menuBarHeight, ImGuiCond.Always);
        ImGui.setNextWindowSize(window.getWidth(), window.getHeight() - menuBarHeight);

        ImGui.begin("Dockspace Demo", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(3);

        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, menuBarSize, menuBarSize);

        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("Edit")) {
                if (ImGui.menuItem("Undo")) {
                   historyCommandManager.undo();
                }
                ImGui.sameLine();
                ImGuiHelper.coloredText(KeyBinds.UNDO.getKeyString(), ImGuiHelper.getTooltipColor());

                if (ImGui.menuItem("Redo")) {
                    historyCommandManager.redo();
                }
                ImGui.sameLine();
                ImGuiHelper.coloredText(KeyBinds.REDO.getKeyString(), ImGuiHelper.getTooltipColor());

                ImGui.separator();

                if (ImGui.menuItem("Rename")) {
                    handleRenaming();
                }
                ImGui.sameLine();
                ImGuiHelper.coloredText(KeyBinds.RENAME.getKeyString(), ImGuiHelper.getTooltipColor());

                if (ImGui.menuItem("Copy")) {
                    CitadelLauncher.getInstance().handleError("This method is not available!");
                }
                ImGui.sameLine();
                ImGuiHelper.coloredText(KeyBinds.COPY.getKeyString(), ImGuiHelper.getTooltipColor());

                if (ImGui.menuItem("Paste")) {
                    CitadelLauncher.getInstance().handleError("This method is not available!");
                }
                ImGui.sameLine();
                ImGuiHelper.coloredText(KeyBinds.PASTE.getKeyString(), ImGuiHelper.getTooltipColor());

                if (ImGui.menuItem("Duplicate")) {
                    CitadelLauncher.getInstance().handleError("This method is not available!");
                }
                ImGui.sameLine();
                ImGuiHelper.coloredText(KeyBinds.DUPLICATE.getKeyString(), ImGuiHelper.getTooltipColor());

                if (ImGui.menuItem("Delete")) {
                    handleRemoval();
                }
                ImGui.sameLine();
                ImGuiHelper.coloredText(KeyBinds.DELETE.getKeyString(), ImGuiHelper.getTooltipColor());

                ImGui.endMenu();
            }

            if (ImGui.beginMenu("View")) {
                if (ImGui.menuItem("Scene view", ImGuiScreens.GAME_VIEWPORT.isVisible())) {
                    ImGuiScreens.GAME_VIEWPORT.toggle();

                    if (ImGuiScreens.GAME_VIEWPORT.isVisible()) {
                        ImGuiScreens.GAME_VIEWPORT.show();
                    }
                    else {
                        ImGuiScreens.GAME_VIEWPORT.hide();
                    }
                }

                if (ImGui.menuItem("Statistics", ImGuiScreens.RENDER_STATISTICS.isVisible())) {
                    ImGuiScreens.RENDER_STATISTICS.toggle();

                    if (ImGuiScreens.RENDER_STATISTICS.isVisible()) {
                        ImGuiScreens.RENDER_STATISTICS.show();
                    }
                    else {
                        ImGuiScreens.RENDER_STATISTICS.hide();
                    }
                }

                if (ImGui.menuItem("Inspector", ImGuiScreens.EDITOR_INSPECTOR.isVisible())) {
                    ImGuiScreens.EDITOR_INSPECTOR.toggle();

                    if (ImGuiScreens.EDITOR_INSPECTOR.isVisible()) {
                        ImGuiScreens.EDITOR_INSPECTOR.show();
                    }
                    else {
                        ImGuiScreens.EDITOR_INSPECTOR.hide();
                    }
                }

                if (ImGui.menuItem("Properties", ImGuiScreens.EDITOR_PROPERTIES.isVisible())) {
                    ImGuiScreens.EDITOR_PROPERTIES.toggle();

                    if (ImGuiScreens.EDITOR_PROPERTIES.isVisible()) {
                        ImGuiScreens.EDITOR_PROPERTIES.show();
                    }
                    else {
                        ImGuiScreens.EDITOR_PROPERTIES.hide();
                    }
                }

                if (ImGui.menuItem("Assets", ImGuiScreens.EDITOR_ASSETS.isVisible())) {
                    ImGuiScreens.EDITOR_ASSETS.toggle();

                    if (ImGuiScreens.EDITOR_ASSETS.isVisible()) {
                        ImGuiScreens.EDITOR_ASSETS.show();
                    }
                    else {
                        ImGuiScreens.EDITOR_ASSETS.hide();
                    }
                }

                ImGui.separator();

                if (ImGui.beginMenu("Overlay")) {
                    if (ImGui.menuItem("Show Grid")) {
                        CitadelLauncher.getInstance().handleError("This rendering system is not available!");
                    }
                    if (ImGui.menuItem("Show Axis")) {
                        CitadelLauncher.getInstance().handleError("This rendering system is not available!");
                    }
                    ImGui.endMenu();
                }

                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Help")) {
                if (ImGui.menuItem("About")) {
                    ImGuiScreens.EDITOR_ABOUT.show();
                }

                if (ImGui.menuItem("Documentation")) {
                    CitadelLauncher.getInstance().handleError("This UI window is not available!");
                }

                ImGui.endMenu();
            }

            ImGui.beginGroup();

            ImVec2 buttonSize = new ImVec2(16, 16);

            float availableWidth = ImGui.getContentRegionAvail().x;
            ImGui.setCursorPosX((availableWidth - buttonSize.x) / 2);

            if (!isIsPlaying()) {
                ImGui.pushStyleColor(ImGuiCol.Button, new ImVec4(0, 0, 0, 0));
                if (ImGui.imageButton(playButtonTextureId, buttonSize.x, buttonSize.y)) {
                    SceneManager.reloadScene();
                    setIsPlaying(true);
                    setIsPaused(false);
                }
                ImGui.popStyleColor();
            } else {
                ImGui.pushStyleColor(ImGuiCol.Button, new ImVec4(0, 0, 0, 0));
                if (ImGui.imageButton(stopButtonTextureId, buttonSize.x, buttonSize.y)) {
                    setIsPlaying(false);
                    SceneManager.reloadScene();
                }
                ImGui.popStyleColor();

                if (isIsPaused()) {
                    ImGui.pushStyleColor(ImGuiCol.Button, new ImVec4(0.5f, 0.5f, 0.5f, 1));
                } else {
                    ImGui.pushStyleColor(ImGuiCol.Button, new ImVec4(0, 0, 0, 0));
                }

                if (ImGui.imageButton(pauseButtonTextureId, buttonSize.x, buttonSize.y)) {
                    setIsPaused(!isIsPaused());
                }

                ImGui.popStyleColor();
            }

            ImGui.endGroup();

            ImGui.endMainMenuBar();
        }

        ImGui.popStyleVar();

        handleKeyInputs();

        handleHistory();
    }

    private static void handleKeyInputs() {
        if (KeyBinds.RENAME.isPressedOnce()) {
            handleRenaming();
        } else if (KeyBinds.DELETE.isPressedOnce()) {
            handleRemoval();
        } else if (KeyBinds.COPY.isPressedOnce()) {
            CitadelLauncher.getInstance().handleError("This method is not available!");
        } else if (KeyBinds.PASTE.isPressedOnce()) {
            CitadelLauncher.getInstance().handleError("This method is not available!");
        } else if (KeyBinds.DUPLICATE.isPressedOnce()) {
            CitadelLauncher.getInstance().handleError("This method is not available!");
        }
    }

    public static void handleRenaming() {
        if (ImGuiScreens.EDITOR_ASSETS.getHoveringPath() != null) {
            ImGuiScreens.EDITOR_ASSETS.handleAction(
                    false,
                    !Files.isDirectory(ImGuiScreens.EDITOR_ASSETS.getHoveringPath()),
                    ImGuiScreens.EDITOR_ASSETS.getHoveringPath()
            );
        } else if (ImGuiScreens.EDITOR_ASSETS.getLastClickedPath() != null) {
            ImGuiScreens.EDITOR_ASSETS.handleAction(
                    false,
                    !Files.isDirectory(ImGuiScreens.EDITOR_ASSETS.getLastClickedPath()),
                    ImGuiScreens.EDITOR_ASSETS.getLastClickedPath()
            );
        } else if (selectedObject != null) {
            renameAction(getSelectedObject().getName(), () -> {
                String newName = ImGuiScreens.EDITOR_RENAME.getResult();
                historyCommandManager.executeCommand(new RenameGameObjectHistoryCommand(getSelectedObject(), newName));
            });
        }
    }

    public static void handleRemoval() {
        if (ImGuiScreens.EDITOR_ASSETS.getHoveringPath() != null) {
            LevelEditor.getHistoryCommandManager().executeCommand(
                    new DeleteFileHistoryCommand(ImGuiScreens.EDITOR_ASSETS.getHoveringPath().toString())
            );
        } else if (ImGuiScreens.EDITOR_ASSETS.getLastClickedPath() != null) {
            LevelEditor.getHistoryCommandManager().executeCommand(
                    new DeleteFileHistoryCommand(ImGuiScreens.EDITOR_ASSETS.getLastClickedPath().toString())
            );
        } else if (selectedObject != null) {
            // TODO: Object deletion system needed
            CitadelLauncher.getInstance().handleError("GameObject deletion system needed!");
        }
    }

    private static void handleHistory() {
        if (isIsPlaying()) {
            return;
        }

        if (KeyBinds.UNDO.isPressedOnce()) {
            historyCommandManager.undo();
        } else if (KeyBinds.REDO.isPressedOnce()) {
            historyCommandManager.redo();
        } else if (KeyBinds.REDO_ALT.isPressedOnce()) {
            historyCommandManager.redo();
        }
    }

    public static void renameAction(String defaultText, Runnable runnable) {
        ImGuiScreens.EDITOR_RENAME.setDefaultText(defaultText);
        ImGuiScreens.EDITOR_RENAME.setRunnable(runnable);
        ImGuiScreens.EDITOR_RENAME.show();
    }

    public static GameObject getSelectedObject() {
        return selectedObject;
    }

    public static void setSelectedObject(GameObject gameObject) {
        selectedObject = gameObject;
    }

    public static HistoryCommandManager getHistoryCommandManager() {
        return historyCommandManager;
    }

    public static boolean isIsPlaying() {
        return isPlaying;
    }

    public static void setIsPlaying(boolean isPlaying) {
        LevelEditor.isPlaying = isPlaying;
        setSelectedObject(null);
    }

    public static boolean isIsPaused() {
        return isPaused;
    }

    public static void setIsPaused(boolean isPaused) {
        LevelEditor.isPaused = isPaused;
    }
}
