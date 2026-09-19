package net.xun.lib.common.api.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SpriteButton extends Button {
    public enum StateLayout {
        HORIZONTAL,
        VERTICAL
    }

    public enum ButtonState {
        NORMAL(0),
        HOVERED(1),
        DISABLED(2);

        private final int index;

        ButtonState(int index) {
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        }
    }

    private final ResourceLocation texture;
    private final int textureX;
    private final int textureY;
    private final int uWidth;
    private final int vHeight;
    private final int textureWidth;
    private final int textureHeight;
    private final StateLayout layout;
    private final boolean drawText;

    protected SpriteButton(Builder builder) {
        super(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress, builder.narrationSupplier);
        this.texture = builder.texture;
        this.textureX = builder.textureX;
        this.textureY = builder.textureY;
        this.uWidth = builder.uWidth > 0 ? builder.uWidth : builder.width;
        this.vHeight = builder.vHeight > 0 ? builder.vHeight : builder.height;
        this.textureWidth = builder.textureWidth;
        this.textureHeight = builder.textureHeight;
        this.layout = builder.layout;
        this.drawText = builder.drawText;

        if (builder.tooltip != null) {
            this.setTooltip(builder.tooltip);
        }
    }

    /**
     * Determines the current button state based on widget interaction.
     */
    public ButtonState getCurrentState() {
        if (!this.active) {
            return ButtonState.DISABLED;
        }
        if (this.isHovered()) {
            return ButtonState.HOVERED;
        }
        return ButtonState.NORMAL;
    }

    /**
     * Calculates the UV offsets based on current state and layout.
     */
    protected int getUOffset(ButtonState state) {
        return this.layout == StateLayout.HORIZONTAL ? state.getIndex() * this.uWidth : 0;
    }

    protected int getVOffset(ButtonState state) {
        return this.layout == StateLayout.VERTICAL ? state.getIndex() * this.vHeight : 0;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ButtonState state = getCurrentState();

        int renderU = this.textureX + getUOffset(state);
        int renderV = this.textureY + getVOffset(state);

        // Blit sprite scaled to widget dimensions
        guiGraphics.blit(
                this.texture,
                this.getX(), this.getY(),
                this.width, this.height,
                (float) renderU, (float) renderV,
                this.uWidth, this.vHeight,
                this.textureWidth, this.textureHeight
        );

        // Render button label text if enabled
        if (this.drawText && !this.getMessage().getString().isEmpty()) {
            int textColor = this.active ? 0xFFFFFFFF : 0xA0A0A0FF;
            guiGraphics.drawCenteredString(
                    Minecraft.getInstance().font,
                    this.getMessage(),
                    this.getX() + this.width / 2,
                    this.getY() + (this.height - 8) / 2,
                    textColor
            );
        }
    }

    public static Builder builder(ResourceLocation texture, OnPress onPress) {
        return new Builder(texture, onPress);
    }

    // Builder Class
    public static class Builder {
        private final ResourceLocation texture;
        private final OnPress onPress;
        private int x = 0;
        private int y = 0;
        private int width = 20;
        private int height = 20;
        private int textureX = 0;
        private int textureY = 0;
        private int uWidth = 0;
        private int vHeight = 0;
        private int textureWidth = 256;
        private int textureHeight = 256;
        private StateLayout layout = StateLayout.HORIZONTAL;
        private boolean drawText = false;
        private Component message = Component.empty();
        private @Nullable Tooltip tooltip = null;
        private final CreateNarration narrationSupplier = DEFAULT_NARRATION;

        public Builder(ResourceLocation texture, OnPress onPress) {
            this.texture = texture;
            this.onPress = onPress;
        }

        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder bounds(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder uv(int u, int v) {
            this.textureX = u;
            this.textureY = v;
            return this;
        }

        public Builder uvSize(int uWidth, int vHeight) {
            this.uWidth = uWidth;
            this.vHeight = vHeight;
            return this;
        }

        public Builder sheetSize(int textureWidth, int textureHeight) {
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        public Builder layout(StateLayout layout) {
            this.layout = layout;
            return this;
        }

        public Builder text(Component message, boolean drawText) {
            this.message = message;
            this.drawText = drawText;
            return this;
        }

        public Builder tooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public SpriteButton build() {
            return new SpriteButton(this);
        }
    }
}
