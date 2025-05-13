package nl.melonstudios.create.util;

import com.google.common.collect.ImmutableList;
import com.melonstudios.melonlib.misc.Localizer;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TextBuilder {
    private final List<String> finishedLines = new ArrayList<>();
    private String currentlyBuilding = "";

    public TextBuilder() {

    }

    public List<String> build() {
        if (!this.currentlyBuilding.isEmpty()) {
            this.finishedLines.add(this.currentlyBuilding);
            this.currentlyBuilding = "";
        }
        return ImmutableList.copyOf(this.finishedLines);
    }

    public TextBuilder enter() {
        this.finishedLines.add(this.currentlyBuilding);
        this.currentlyBuilding = "";
        return this;
    }

    public TextBuilder space() {
        return this.text(" ");
    }
    public TextBuilder text(String text) {
        currentlyBuilding += text;
        return this;
    }
    public TextBuilder formatting(TextFormatting formatting) {
        return this.text(formatting.toString());
    }
    public TextBuilder resetFormat() {
        return this.formatting(TextFormatting.RESET);
    }

    public TextBuilder translate(String translate, Object... format) {
        return this.text(Localizer.translate(translate, format));
    }

    public TextBuilder number(int num) {
        return this.text(String.valueOf(num));
    }
    public TextBuilder number(long num) {
        return this.text(String.valueOf(num));
    }
    public TextBuilder number(float num) {
        return this.text(String.valueOf(num));
    }
    public TextBuilder number(double num) {
        return this.text(String.valueOf(num));
    }

    public TextBuilder object(@Nullable Object obj) {
        return this.text(obj == null ? "null" : obj.toString());
    }
}
