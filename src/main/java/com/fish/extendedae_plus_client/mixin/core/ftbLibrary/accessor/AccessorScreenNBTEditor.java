package com.fish.extendedae_plus_client.mixin.core.ftbLibrary.accessor;

import dev.ftb.mods.ftblibrary.nbtedit.NBTEditorScreen;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = NBTEditorScreen.class, remap = false)
public interface AccessorScreenNBTEditor {
    @Accessor("accepted")
    boolean isAccepted();

    @Accessor("buttonNBTRoot")
    NBTEditorScreen.ButtonNBTMap getButtonRoot();

    @Accessor("callback")
    NBTEditorScreen.NBTCallback getCallback();

    @Mixin(targets = "dev.ftb.mods.ftblibrary.nbtedit.NBTEditorScreen$ButtonNBTMap", remap = false)
    interface AccessorButtonNBTMap {
        @Accessor("map")
        CompoundTag getMap();
    }
}
