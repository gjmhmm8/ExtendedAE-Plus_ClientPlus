package com.fish.extendedae_plus_client.integration.recipeViewer.jei

import com.fish.extendedae_plus_client.mixin.core.recipeViewer.jei.accessor.AccessorBookmarkOverlay
import mezz.jei.api.ingredients.IIngredientType
import mezz.jei.api.ingredients.ITypedIngredient
import mezz.jei.api.runtime.IJeiRuntime
import mezz.jei.common.Internal
import mezz.jei.gui.bookmarks.IngredientBookmark
import mezz.jei.gui.overlay.elements.IElement
import java.util.*
import java.util.function.Consumer
import kotlin.concurrent.Volatile

/**
 * 线程安全地缓存并访问 JEI Runtime。
 */
object HelperJeiRuntime {
    @Volatile
    private var RUNTIME: IJeiRuntime? = null

    fun setRuntime(runtime: IJeiRuntime?) {
        RUNTIME = runtime
    }

    fun get(): IJeiRuntime? {
        return RUNTIME
    }

    val ingredientUnderMouse: Optional<out ITypedIngredient<*>?>
        get() {
            val rt = RUNTIME ?: return Optional.empty<ITypedIngredient<*>?>()

            val list = rt.ingredientListOverlay
            if (list != null) {
                val ing = list.ingredientUnderMouse
                if (ing.isPresent) return ing
            }
            val bm = rt.bookmarkOverlay
            if (bm != null) {
                val ing = bm.ingredientUnderMouse
                if (ing.isPresent) return ing
            }
            return Optional.empty<ITypedIngredient<*>?>()
        }

    val jeiCheatModeEnabled: Boolean
        /**
         * 检测 JEI 是否开启了作弊模式（给物品）。
         * 使用 JEI 内部开关，若 JEI 未初始化或异常则返回 false。
         */
        get() {
            return try {
                // 使用完全限定名以避免在源码缺失时的编译依赖问题
                Internal.getClientToggleState().isCheatItemsEnabled
            } catch (_: Throwable) {
                false
            }
        }

    /**
     * 将文本写入 JEI 的搜索过滤框。
     * 若 JEI runtime 不可用则静默返回。
     */
    fun setIngredientFilterText(text: String) {
        val rt = RUNTIME ?: return
        try {
            rt.ingredientFilter.filterText = text
        } catch (_: Throwable) {
            // 兼容不同 JEI 版本或在启动阶段尚未就绪
        }
    }

    val bookmarkList: MutableList<out ITypedIngredient<*>>
        /**
         * 获取JEI书签列表
         */
        get() {
            val rt = RUNTIME ?: return mutableListOf<ITypedIngredient<*>>()
            val bookmarkOverlay = rt.bookmarkOverlay
            if (bookmarkOverlay is AccessorBookmarkOverlay) {
                val bookmarkList = bookmarkOverlay.getBookmarkList()
                return bookmarkList.elements.stream()
                    .map(IElement<*>::getTypedIngredient)
                    .toList()
            }
            return mutableListOf()
        }

    fun <TStack> addFavorite(stack: TStack, type: IIngredientType<TStack>) {
        val runtime = RUNTIME ?: return
        if (runtime.bookmarkOverlay !is AccessorBookmarkOverlay) return

        runtime.ingredientManager.createTypedIngredient<TStack>(type, stack)
            .ifPresent(Consumer { ingredient: ITypedIngredient<TStack> ->
                (runtime.bookmarkOverlay as AccessorBookmarkOverlay).getBookmarkList().add(
                    IngredientBookmark.create<TStack>(ingredient, runtime.ingredientManager)
                )
            })
    }
}