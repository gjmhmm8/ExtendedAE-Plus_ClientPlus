package com.fish.extendedae_plus_client.mixin.core.ae.menu;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.storage.ITerminalHost;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.sync.packets.MEInteractionPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.RestrictedInputSlot;
import appeng.parts.encoding.EncodingMode;
import com.fish.extendedae_plus_client.config.EAEPCConfig;
import com.fish.extendedae_plus_client.config.enums.AutoUploadMode;
import com.fish.extendedae_plus_client.impl.cache.CacheProvider;
import com.fish.extendedae_plus_client.mixin.impl.bridge.BridgePlanToEncode;
import com.fish.extendedae_plus_client.mixin.core.ae.accessor.AccessorAEBaseScreen;
import com.fish.extendedae_plus_client.mixin.impl.helper.AutoEncodingStage;
import com.fish.extendedae_plus_client.mixin.impl.helper.HelperEncodingTerminal;
import com.fish.extendedae_plus_client.mixin.impl.helper.HelperPatternMoving;
import com.fish.extendedae_plus_client.mixin.impl.helper.WTLibHelper;
import com.fish.extendedae_plus_client.render.screen.ScreenProviderList;
import com.fish.extendedae_plus_client.util.UtilKeyBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import appeng.core.sync.network.NetworkHandler;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PatternEncodingTermMenu.class, remap = false)
public abstract class MixinEncodingTerminal extends MEStorageMenu implements BridgePlanToEncode, HelperEncodingTerminal {
    @Shadow
    public EncodingMode mode;
    @Shadow
    @Final
    private RestrictedInputSlot encodedPatternSlot;
    @Shadow
    @Final
    private RestrictedInputSlot blankPatternSlot;
    @Unique
    private boolean eaep$flagPatternSelection;
    @Unique
    private AutoEncodingStage eaep$autoEncoding = AutoEncodingStage.None;

    public MixinEncodingTerminal(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host);
    }

    @Shadow
    public abstract void encode();

    @Shadow
    protected abstract ItemStack encodePattern();

    @Inject(method = "encode", at = @At("HEAD"), cancellable = true)
    private void onEncode(CallbackInfo ci) {
        if (this.isServerSide()) return;

        eaep$fillPattern();

        if (!EAEPCConfig.encodingTiggerMode.get().shouldTigger() && eaep$autoEncoding == AutoEncodingStage.None) return;

        if (CacheProvider.isEmpty()) {
            this.getPlayer().displayClientMessage(
                    UtilKeyBuilder.of(UtilKeyBuilder.message)
                            .addStr("provider_list")
                            .addStr("empty_list")
                            .build(),
                    false
            );
            ci.cancel();
            return;
        }

        var pattern = this.encodePattern();
        if (pattern == null) return;

        if (this.encodedPatternSlot.hasItem()) {
            var is = this.encodedPatternSlot.getItem();
            var patternDetailsSlot = PatternDetailsHelper.decodePattern(is, this.getPlayer().level());
            if (patternDetailsSlot != null) CacheProvider.unmarkPattern(patternDetailsSlot);
        }

        var patternDetails = PatternDetailsHelper.decodePattern(pattern, this.getPlayer().level());
        if (patternDetails == null || CacheProvider.hasPattern(patternDetails)) {
            this.getPlayer().displayClientMessage(
                    UtilKeyBuilder.of(UtilKeyBuilder.message)
                            .addStr("pattern")
                            .addStr("already")
                            .build(),
                    false
            );
            ci.cancel();
            return;
        }
        this.eaep$flagPatternSelection = true;
        if (!this.encodedPatternSlot.hasItem()) return;
        if (ItemStack.isSameItemSameTags(encodedPatternSlot.getItem(), pattern)) {
            this.eaep$makePattern();
            ci.cancel();
        }
    }

    @Inject(method = "onSlotChange", at = @At("TAIL"))
    private void onSlotChange(Slot slot, CallbackInfo ci) {
        if (this.isServerSide()) return;

        if (!this.encodedPatternSlot.equals(slot)) return;

        if (!this.eaep$flagPatternSelection) return;

        if (!this.encodedPatternSlot.hasItem()) return;
        this.eaep$makePattern();
    }

    @Unique
    private void eaep$makePattern() {
        this.eaep$flagPatternSelection = false;
        if (CacheProvider.isEmpty()) return;

        var existingPattern = this.encodedPatternSlot.getItem();
        if (!PatternDetailsHelper.isEncodedPattern(existingPattern)) return;

        if (!EncodingMode.PROCESSING.equals(this.mode)) {
            for (var group : CacheProvider.getGroups()) {
                var icon = group.icon();
                if (icon == null) continue;
                switch (icon.getId().toString()) {
                    case "extendedae_plus:assembler_matrix_pattern_plus":
                    case "expatternprovider:assembler_matrix_pattern":
                    case "ae2:molecular_assembler":
                    case "expatternprovider:ex_molecular_assembler":
                        if (CacheProvider.getAvailableSlots(group) > 0) {
                            eaep$makePatternAuto(existingPattern, group);
                            break;
                        }
                    default:
                        continue;
                }
                break;
            }
            return;
        }

        if (!(Minecraft.getInstance().screen instanceof PatternEncodingTermScreen<?> screen)) return;
        var screenProviderList = new ScreenProviderList<>(screen,
                CacheProvider.getGroups(),
                group -> {
                    if (group == null) return;
                    eaep$makePatternAuto(existingPattern, group);
                }
        );
        if (this.eaep$autoEncoding == AutoEncodingStage.None || !screenProviderList.tryAutoEncoding()) {
            ((AccessorAEBaseScreen) screen).eaep$switchToScreen(screenProviderList);
        }
    }

    @Unique
    private void eaep$makePatternAuto(ItemStack pattern, PatternContainerGroup group) {
        var patternDetails = PatternDetailsHelper.decodePattern(pattern, this.getPlayer().level());
        if (patternDetails == null) return;
        CacheProvider.markPattern(patternDetails, group);
        var mode = EAEPCConfig.autoUploadMode.get();

        if (mode == AutoUploadMode.EAEP_BY_NAME && ModList.get().isLoaded("extendedae_plus")
                && HelperPatternMoving.eaepUploadPatternByName(group, patternDetails)) {
            return;
        }

        if (mode == AutoUploadMode.WHEN_OPEN || mode == AutoUploadMode.AUTO_OPEN) {
            Minecraft.getInstance().player.connection.send(new ServerboundContainerClickPacket(
                    containerId, 1, this.encodedPatternSlot.index,
                    0, ClickType.QUICK_MOVE, this.getCarried(), new Int2ObjectOpenHashMap<>()
            ));
            if (mode == AutoUploadMode.AUTO_OPEN) {
                WTLibHelper.openTerminalCyc(WTLibHelper.PATTERN_ACCESS);
            }
        }
    }

    @Override
    public void eaep$autoEncoding() {
        this.eaep$autoEncoding = AutoEncodingStage.Init;
    }

    @Unique
    @Override
    public void eaep$tick() {
        if (this.eaep$autoEncoding != AutoEncodingStage.None) {
            this.eaep$autoEncoding = AutoEncodingStage.values()[(eaep$autoEncoding.ordinal() + 1) % AutoEncodingStage.values().length];
        }
        if (eaep$autoEncoding == AutoEncodingStage.Encode) {
            encode();
        }
    }

    @Unique
    public void eaep$fillPattern() {
        if (!getCarried().isEmpty()) return;
        if (blankPatternSlot.getItem().getCount() > 0) return;
        var patternIngredient = Ingredient.of(AEItems.BLANK_PATTERN);
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        var clientRepo = getClientRepo();
        if (clientRepo == null) return;
        var patternSlotList = clientRepo.getByIngredient(patternIngredient);
        if (patternSlotList.isEmpty()) return;
        var patternSlot = patternSlotList.toArray(new GridInventoryEntry[0])[0];
        NetworkHandler.instance().sendToServer(new MEInteractionPacket(
                containerId,
                patternSlot.getSerial(),
                InventoryAction.PICKUP_OR_SET_DOWN
        ));
        player.connection.send(new ServerboundContainerClickPacket(
                containerId, 1, this.blankPatternSlot.index,
                0, ClickType.PICKUP, this.getCarried(), new Int2ObjectOpenHashMap<>()
        ));
    }
}
