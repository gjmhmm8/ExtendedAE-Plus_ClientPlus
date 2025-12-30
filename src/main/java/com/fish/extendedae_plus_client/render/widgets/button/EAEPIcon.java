package com.fish.extendedae_plus_client.render.widgets.button;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import com.fish.extendedae_plus_client.ExtendedAEPlusClient;
import net.minecraft.resources.ResourceLocation;

public enum EAEPIcon implements IButtonIcon {
    SAVE_CENTER(0, 0),
    SAVE_UP(16, 0),
    SAVE_DOWN(32, 0),

    ;

    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public static final ResourceLocation TEXTURE =
            ExtendedAEPlusClient.getLocation("textures/gui/icons.png");
    public static final int TEXTURE_WIDTH = 64;
    public static final int TEXTURE_HEIGHT = 64;

    EAEPIcon(int x, int y) {
        this(x, y, 16, 16);
    }

    EAEPIcon(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public Blitter getBlitter() {
        return Blitter.texture(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT)
                .src(x, y, width, height);
    }

    @Override
    public Icon getAEIcon() {
        return Icon.INVALID;
    }

    public static IButtonIcon fromAEIcon(Icon aeIcon) {
        return new AEIcon(aeIcon);
    }

    private record AEIcon(Icon aeIcon) implements IButtonIcon {
        @Override
        public Blitter getBlitter() {
            return this.aeIcon.getBlitter();
        }

        @Override
        public Icon getAEIcon() {
            return this.aeIcon;
        }
    }
}
