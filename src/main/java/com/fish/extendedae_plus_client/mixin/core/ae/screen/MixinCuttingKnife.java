package com.fish.extendedae_plus_client.mixin.core.ae.screen;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.ids.AETags;
import appeng.api.stacks.AEItemKey;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.QuartzKnifeScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.implementations.QuartzKnifeMenu;
import com.fish.extendedae_plus_client.config.EAEPCConfig;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = QuartzKnifeScreen.class, remap = false)
public abstract class MixinCuttingKnife extends AEBaseScreen<QuartzKnifeMenu> {
    @Shadow
    @Final
    private EditBox name;

    @Unique
    private final List<String> eaep$customNames = new ArrayList<>();
    @Unique
    private final IntArrayList eaep$ingotsSlots = new IntArrayList();
    @Unique
    private boolean eaep$moving;
    @Unique
    private int eaep$repeating = -1;

    public MixinCuttingKnife(QuartzKnifeMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(QuartzKnifeMenu menu,
                        Inventory playerInventory,
                        Component title,
                        ScreenStyle style,
                        CallbackInfo ci) {
        for (int indexSlot = 0; indexSlot < playerInventory.items.size(); indexSlot++) {
            var item = playerInventory.items.get(indexSlot);

            if (item.is(AETags.METAL_INGOTS)) {
                this.eaep$ingotsSlots.add(indexSlot);
                continue;
            }

            var pattern = PatternDetailsHelper.decodePattern(item, Minecraft.getInstance().level);
            if (pattern == null) continue;
            for (var input : pattern.getInputs()) {
                for (var stack : input.getPossibleInputs()) {
                    var key = stack.what();
                    if (!(key instanceof AEItemKey itemKey)) continue;
                    var itemStack = itemKey.toStack();
                    if (!itemStack.hasCustomHoverName()) continue;
                    this.eaep$customNames.add(itemStack.getHoverName().getString());
                }
            }
            for (var output : pattern.getOutputs()) {
                var key = output.what();
                if (!(key instanceof AEItemKey itemKey)) continue;
                var itemStack = itemKey.toStack();
                if (!itemStack.hasCustomHoverName()) continue;
                this.eaep$customNames.add(itemStack.getHoverName().getString());
            }
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (!(this.eaep$customNames.isEmpty() || this.eaep$ingotsSlots.isEmpty()))
            this.name.setFocused(false);
    }

    @Inject(method = "containerTick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        var gameMode = Minecraft.getInstance().gameMode;
        if (player == null || gameMode == null) return;

        if (!this.eaep$moving) {
            if (this.eaep$customNames.isEmpty() || this.eaep$ingotsSlots.isEmpty()) return;

            this.eaep$moving = true;

            this.getMenu().setName(this.eaep$customNames.get(0));

            if (EAEPCConfig.autoPlateRepeat.get() > 1) {
                if (this.eaep$repeating == -1) {
                    this.eaep$repeating = EAEPCConfig.autoPlateRepeat.get() - 1;
                } else if (this.eaep$repeating == 0) {
                    this.eaep$customNames.remove(0);
                }
                this.eaep$repeating--;
            } else this.eaep$customNames.remove(0);

            if (this.getMenu().getPlayerInventory().items
                    .get(this.eaep$ingotsSlots.getInt(0)).isEmpty())
                this.eaep$ingotsSlots.removeInt(0);

            gameMode.handleInventoryMouseClick(
                    this.getMenu().containerId,
                    this.eaep$ingotsSlots.getInt(0) + 2,
                    GLFW.GLFW_MOUSE_BUTTON_LEFT,
                    ClickType.QUICK_MOVE,
                    player
            );
        } else {
            this.eaep$moving = false;
            gameMode.handleInventoryMouseClick(
                    this.getMenu().containerId,
                    1,
                    GLFW.GLFW_MOUSE_BUTTON_LEFT,
                    ClickType.QUICK_MOVE,
                    player
            );
        }
    }
}
