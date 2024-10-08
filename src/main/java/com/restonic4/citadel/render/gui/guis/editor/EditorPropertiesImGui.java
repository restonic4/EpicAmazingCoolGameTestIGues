package com.restonic4.citadel.render.gui.guis.editor;

import com.restonic4.citadel.core.editor.LevelEditor;
import com.restonic4.citadel.render.gui.ImGuiHelper;
import com.restonic4.citadel.render.gui.guis.ToggleableImGuiScreen;
import com.restonic4.citadel.util.CitadelConstants;
import com.restonic4.citadel.util.StringBuilderHelper;
import com.restonic4.citadel.util.history.commands.RenameGameObjectHistoryCommand;
import com.restonic4.citadel.world.object.Component;
import com.restonic4.citadel.world.object.GameObject;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import org.joml.Vector3f;

import java.util.List;

public class EditorPropertiesImGui extends ToggleableImGuiScreen {
    @Override
    public void render() {
        if (!isVisible()) {
            return;
        }

        ImGui.begin("Properties");

        GameObject selectedGameobject = LevelEditor.getSelectedObject();

        if (selectedGameobject == null) {
            ImGuiHelper.textTruncated("Select a GameObject to modify it's properties!");
            ImGui.end();
            return;
        }

        if (ImGui.collapsingHeader("Internal", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.indent(CitadelConstants.IM_GUI_INDENT);

            ImGui.text(StringBuilderHelper.concatenate("ID: ", selectedGameobject.getId()));
            ImGui.text(StringBuilderHelper.concatenate("On frustum: ", selectedGameobject.isInsideFrustum()));

            ImGui.unindent(CitadelConstants.IM_GUI_INDENT);
        }

        if (ImGui.collapsingHeader("General", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.indent(CitadelConstants.IM_GUI_INDENT);

            ImString nameBuffer = new ImString(selectedGameobject.getName(), 256);

            ImGui.text("Name:");
            ImGui.sameLine();
            if (ImGui.inputText("##name", nameBuffer, ImGuiInputTextFlags.EnterReturnsTrue)) {
                LevelEditor.getHistoryCommandManager().executeCommand(new RenameGameObjectHistoryCommand(selectedGameobject, nameBuffer.get()));
            }

            ImGui.checkbox("Static", selectedGameobject.isStatic());

            ImGui.unindent(CitadelConstants.IM_GUI_INDENT);
        }

        if (ImGui.collapsingHeader("Transform", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.indent(CitadelConstants.IM_GUI_INDENT);

            ImGuiHelper.renderPropertyRow("Position", () -> {
                float[] posArray = {
                        selectedGameobject.transform.getPosition().x,
                        selectedGameobject.transform.getPosition().y,
                        selectedGameobject.transform.getPosition().z
                };
                ImGui.dragFloat3("##PositionDrag", posArray, 0.1f, -1000.0f, 1000.0f, "%.3f");
            });

            ImGuiHelper.renderPropertyRow("Rotation", () -> {
                Vector3f euler = new Vector3f();
                selectedGameobject.transform.getRotation().getEulerAnglesXYZ(euler);
                float[] rotArray = { euler.x, euler.y, euler.z };
                ImGui.dragFloat3("##RotationDrag", rotArray, 0.1f, -1000.0f, 1000.0f, "%.3f");
            });

            ImGuiHelper.renderPropertyRow("Scale", () -> {
                float[] scaleArray = {
                        selectedGameobject.transform.getScale().x,
                        selectedGameobject.transform.getScale().y,
                        selectedGameobject.transform.getScale().z
                };
                ImGui.dragFloat3("##ScaleDrag", scaleArray, 0.1f, -1000.0f, 1000.0f, "%.3f");
            });

            ImGui.unindent(CitadelConstants.IM_GUI_INDENT);
        }

        if (ImGui.collapsingHeader("Components", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.indent(CitadelConstants.IM_GUI_INDENT);

            List<Component> components = selectedGameobject.getComponents();

            for (int i = 0; i < components.size(); i++) {
                Component component = components.get(i);

                if (ImGui.collapsingHeader(StringBuilderHelper.concatenate(component.getClass().getSimpleName(), "##", component.getId()))) {
                    ImGui.indent(CitadelConstants.IM_GUI_INDENT);

                    component.renderEditorUI();

                    ImGui.unindent(CitadelConstants.IM_GUI_INDENT);
                }
            }

            ImGui.unindent(CitadelConstants.IM_GUI_INDENT);
        }

        ImGui.end();
    }
}
