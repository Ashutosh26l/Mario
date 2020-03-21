package com.ui.buttons;

import com.file.Parser;
import com.jade.Scene;
import com.jade.Window;
import com.renderer.quads.Label;
import com.renderer.quads.Rectangle;
import com.util.Constants;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;

import java.io.File;

import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.util.nfd.NativeFileDialog.*;

public class NewLevelButton extends JButton {
    String text = "New Level";
    Vector2f stringPos = new Vector2f();

    private Label label;

    public NewLevelButton() {
        super();
        this.mainComp = new Rectangle(Constants.BUTTON_COLOR, new Vector4f(10, 10, 10, 10), Constants.BUTTON_COLOR, 0.1f);
        this.mainComp.setSize(new Vector2f(86, 20));
        this.renderComponents.add(mainComp);

        stringPos.x = (this.mainComp.getWidth() / 2.0f) - (Constants.DEFAULT_FONT_TEXTURE.getWidthOf(text) / 2.0f) + this.mainComp.getPosX();
        stringPos.y = (this.mainComp.getHeight() / 2.0f) - (Constants.DEFAULT_FONT_TEXTURE.getLineHeight() / 2.0f) + this.mainComp.getPosY();
        this.label = new Label(Constants.DEFAULT_FONT_TEXTURE, text, stringPos);
        this.label.setZIndex(3);
        this.renderComponents.addAll(this.label.getRenderComponents());
    }

    @Override
    public void setPosX(float x) {
        this.mainComp.setPosX(x);
        stringPos.x = (this.mainComp.getWidth() / 2.0f) - (Constants.DEFAULT_FONT_TEXTURE.getWidthOf(text) / 2.0f) + this.mainComp.getPosX();
        this.label.setPosX(stringPos.x);
    }

    @Override
    public void setPosY(float y) {
        this.mainComp.setPosY(y);
        stringPos.y = (this.mainComp.getHeight() / 2.0f) - (Constants.DEFAULT_FONT_TEXTURE.getLineHeight() / 2.0f) + this.mainComp.getPosY() + 2;
        this.label.setPosY(stringPos.y);
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();
        builder.append(beginObjectProperty("NewLevelButton", tabSize));
        builder.append(closeObjectProperty(tabSize));
        return builder.toString();
    }

    public static NewLevelButton deserialize() {
        Parser.consumeEndObjectProperty();

        return new NewLevelButton();
    }

    @Override
    public void clicked() {
        PointerBuffer outPath = memAllocPointer(1);
        try {
            boolean fileClicked = checkResult(
                    NFD_SaveDialog("zip", null, outPath),
                    outPath
            );

            if (fileClicked) {
                String filePath = outPath.getStringUTF8();
                File file = new File(filePath);
                String filename = file.getName();
                filename = filename.replaceFirst("[.][^.]+$", "");
                Constants.CURRENT_LEVEL = filename;
                Window.getScene().importLevel(filename);
            }

        } finally {
            memFree(outPath);
        }
    }

    private static boolean checkResult(int result, PointerBuffer path) {
        switch (result) {
            case NFD_OKAY:
                nNFD_Free(path.get(0));
                return true;
            case NFD_CANCEL:
                return false;
            default: // NFD_ERROR
                System.err.format("Error: %s\n", NFD_GetError());
                return false;
        }
    }
}
