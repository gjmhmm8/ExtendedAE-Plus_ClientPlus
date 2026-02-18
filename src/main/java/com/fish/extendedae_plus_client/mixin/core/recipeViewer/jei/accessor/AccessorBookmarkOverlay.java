package com.fish.extendedae_plus_client.mixin.core.recipeViewer.jei.accessor;

import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BookmarkOverlay.class, remap = false)
public interface AccessorBookmarkOverlay {
    @Accessor("bookmarkList")
    BookmarkList getBookmarkList();
}
