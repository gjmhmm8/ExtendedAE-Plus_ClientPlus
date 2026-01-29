package com.fish.extendedae_plus_client.render.screen

import appeng.api.config.Settings
import appeng.api.config.TerminalStyle
import appeng.api.implementations.blockentities.PatternContainerGroup
import appeng.api.stacks.AEItemKey
import appeng.client.gui.AESubScreen
import appeng.client.gui.Icon
import appeng.client.gui.me.items.PatternEncodingTermScreen
import appeng.client.gui.style.PaletteColor
import appeng.client.gui.widgets.AETextField
import appeng.client.gui.widgets.Scrollbar
import appeng.client.gui.widgets.SettingToggleButton
import appeng.client.gui.widgets.TabButton
import appeng.core.AEConfig
import appeng.core.AppEng
import appeng.core.localization.GuiText
import appeng.menu.me.items.PatternEncodingTermMenu
import com.fish.extendedae_plus_client.impl.AliasGetter
import com.fish.extendedae_plus_client.impl.cache.CacheProvider
import com.fish.extendedae_plus_client.render.widgets.button.EAEPActionButton
import com.fish.extendedae_plus_client.render.widgets.button.EAEPActionItems
import com.fish.extendedae_plus_client.util.UtilKeyBuilder
import guideme.document.LytRect
import guideme.render.SimpleRenderContext
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ComponentPath
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.renderer.Rect2i
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.sounds.SoundManager
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import org.lwjgl.glfw.GLFW
import java.util.function.Consumer
import kotlin.math.max

class ScreenProviderList<TMenu : PatternEncodingTermMenu, TScreen : PatternEncodingTermScreen<TMenu>>(
    previous: TScreen,
    private val providersRaw: Set<PatternContainerGroup>,
    private val applier: Consumer<PatternContainerGroup?>
) : AESubScreen<TMenu, TScreen>(previous, PATH_STYLE) {
    private val fieldSearch: AETextField
    private val fieldAlias: AETextField
    private val scrollbar: Scrollbar

    private var visibleRows = 0
    private var focusedRow = -1

    private val queries: Int2ObjectSortedMap<Component> = Int2ObjectRBTreeMap ()
    private var selectedQueryIndex: Int
    private var customQuery = ""
    private var queryRefresh = false

    private val providers: MutableMap<String, InfoProvider> = HashMap<String, InfoProvider>()
    private val providersFiltered: MutableList<InfoProvider> = ArrayList<InfoProvider>()

    init {
        this.imageWidth = GUI_WIDTH

        val styleTerminal = AEConfig.instance().terminalStyle
        this.addToLeftToolbar(
            SettingToggleButton(
                Settings.TERMINAL_STYLE,
                styleTerminal,
                ::toggleTerminalStyle
            )
        )

        this.widgets.add(
            "button_back",
            TabButton(
                Icon.BACK,
                this.getMenu().host.mainMenuIcon.hoverName
            ) { _ -> this.returnToParent() }
        )

        this.addToLeftToolbar(
            EAEPActionButton(
                EAEPActionItems.ALIAS_ADD
            ) { _ -> this.addMapping() }
        )
        this.addToLeftToolbar(
            EAEPActionButton(
                EAEPActionItems.ALIAS_REMOVE
            ) { _ -> this.removeMappings() }
        )

        this.scrollbar = this.widgets.addScrollBar("scrollbar", Scrollbar.BIG)
        this.scrollbar.setHeight(5 * ROW_HEIGHT)

        this.queries.putAll(AliasGetter.getRecipeKeywords())
        this.selectedQueryIndex = if(queries.containsKey(1)) 1 else if (this.queries.isEmpty()) -1 else 0

        this.fieldSearch = this.widgets.addTextField("field_search")
        this.fieldSearch.setMaxLength(64)
        if(queries.isNotEmpty()) this.fieldSearch.value=queries.values.elementAtOrNull(0)!!.string
        this.fieldSearch.setResponder { value: String ->
            if(!fieldAlias.value.isBlank())return@setResponder
            if (value == this.selectedQuery().string) return@setResponder
            if (this.queries.containsValue(Component.literal(value))) return@setResponder
            this.selectedQueryIndex = -1
            this.customQuery = value
            this.queryRefresh = true
        }
        this.fieldSearch.placeholder = GuiText.SearchPlaceholder.text()

        this.fieldAlias = this.widgets.addTextField("field_alias")
        this.fieldAlias.setMaxLength(64)
        this.fieldAlias.setResponder { value: String ->
            if (value == this.selectedQuery().string) return@setResponder
            if (this.queries.containsValue(Component.literal(value))) return@setResponder
            this.selectedQueryIndex = -1
            this.customQuery = value
            this.queryRefresh = true
        }
        this.fieldAlias.placeholder = UtilKeyBuilder.of(UtilKeyBuilder.screen)
            .addStr("provider_list")
            .addStr("alias")
            .build()
    }

    override fun init() {
        this.visibleRows = max(
            6, config.terminalStyle.getRows(
                (this.height - GUI_HEADER_HEIGHT - GUI_FOOTER_HEIGHT - GUI_TOP_AND_BOTTOM_PADDING) / ROW_HEIGHT
            )
        )
        this.imageHeight = GUI_HEADER_HEIGHT + GUI_FOOTER_HEIGHT + this.visibleRows * ROW_HEIGHT
        super.init()

        this.queryRefresh = true
    }

    override fun containerTick() {
        super.containerTick()
        if (this.queryRefresh) {
            this.queryRefresh = false
            if(this.selectedQueryIndex!=-1) this.fieldAlias.value = this.selectedQuery().string
            this.rebuildKeywordsTooltip()
            this.updateInfo()
            this.scrollbar.setRange(0, this.providersFiltered.size - this.visibleRows, 2)
        }
    }

    override fun drawFG(guiGraphics: GuiGraphics, offsetX: Int, offsetY: Int, mouseX: Int, mouseY: Int) {
        val textColor = this.style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB()
        val indexScroll = this.scrollbar.currentScroll

        for (indexRow in 0..<this.visibleRows) {
            if (indexScroll + indexRow >= this.providersFiltered.size) continue

            val provider = this.providersFiltered[indexScroll + indexRow]

            if (provider.icon != null) {
                val renderContext = SimpleRenderContext(LytRect.empty(), guiGraphics)
                renderContext.renderItem(
                    provider.icon.readOnlyStack,
                    GUI_PADDING_X + PATTERN_PROVIDER_NAME_MARGIN_X,
                    GUI_PADDING_Y + GUI_HEADER_HEIGHT + indexRow * ROW_HEIGHT - 1,
                    8f,
                    8f
                )
            }

            val name = provider.name.copy()
                .append(" [≈${provider.availableSlots}]")

            val text = Language.getInstance().getVisualOrder(
                this.font.substrByWidth(name, TEXT_MAX_WIDTH - 10)
            )

            guiGraphics.drawString(
                this.font, text, GUI_PADDING_X + PATTERN_PROVIDER_NAME_MARGIN_X + 10,
                GUI_PADDING_Y + GUI_HEADER_HEIGHT + indexRow * ROW_HEIGHT - 1, textColor, false
            )
        }
    }

    override fun drawBG(
        guiGraphics: GuiGraphics,
        offsetX: Int,
        offsetY: Int,
        mouseX: Int,
        mouseY: Int,
        partialTicks: Float
    ) {
        this.blit(guiGraphics, offsetX, offsetY, AREA_HEAD)

        val indexScroll = this.scrollbar.currentScroll
        var currentY: Int = offsetY + GUI_HEADER_HEIGHT

        this.blit(guiGraphics, offsetX, currentY + this.visibleRows * ROW_HEIGHT, AREA_TAIL)

        for (indexRow in 0..<this.visibleRows) {
            this.blit(guiGraphics, offsetX, currentY, this.getRowRenderPart(indexRow, indexScroll))
            currentY += ROW_HEIGHT
        }
    }

    private fun getRowRenderPart(indexRow: Int, indexScroll: Int): Rect2i {
        val flagFocused = indexRow + indexScroll == this.focusedRow
        if (indexRow == 0) return if (flagFocused)
            AREA_SCROLLER_HEAD_PROVIDER_FOCUSED
        else
            AREA_SCROLLER_HEAD_PROVIDER
        else if (indexRow < 5) return if (flagFocused)
            AREA_SCROLLER_PROVIDER_FOCUSED
        else
            AREA_SCROLLER_PROVIDER
        else if (indexRow == 5) return if (flagFocused)
            AREA_SCROLLER_TAIL_PROVIDER_FOCUSED
        else
            AREA_SCROLLER_TAIL_PROVIDER
        else return if (flagFocused)
            AREA_PROVIDER_FOCUSED
        else
            AREA_PROVIDER
    }

    private fun updateInfo() {
        if (this.providers.isEmpty()) {
            this.providersRaw.forEach { group ->
                this.providers.computeIfAbsent(
                    group.name().string
                ) { _ -> InfoProvider(group) }
            }
        }

        this.providersFiltered.clear()
        this.providers.forEach { (string: String, infoProvider: InfoProvider) ->
            if (!AliasGetter.matches(this.selectedQuery(),string, infoProvider.i18nKey(),infoProvider.icon?.id?.toString() ?: "")) return@forEach
            this.providersFiltered.add(infoProvider)
        }
        this.focusedRow = if (this.providersFiltered.isEmpty()) -1 else 0
    }

    private fun select(indexProvider: Int) {
        val provider = this.providersFiltered[indexProvider]

        this.applier.accept(provider.group)
        this.returnToParent()
    }

    private fun selectedQuery(): Component {
        return this.queries.values.elementAtOrNull(this.selectedQueryIndex)?:Component.literal(this.customQuery)
    }

    private fun toggleTerminalStyle(button: SettingToggleButton<TerminalStyle>, backwards: Boolean) {
        val next = button.getNextValue(backwards)
        AEConfig.instance().terminalStyle = next
        button.set(next)
        this.reinitialize()
    }

    private fun reinitialize() {
        this.children().removeAll { child ->
            this.renderables.any { renderable -> renderable === child }
        }
        this.renderables.clear()
        this.init()
    }

    private fun rebuildKeywordsTooltip() {
        if (this.queries.size <= 1) {
            this.fieldSearch.tooltip = Tooltip.create(Component.empty())
            return
        }

        val candidateQuery = UtilKeyBuilder.of(UtilKeyBuilder.screenTooltip)
            .addStr("provider_list")
            .addStr("candidate_keywords")
            .build()
        for (i in this.queries.values.indices) {
            val group = this.queries.values.elementAtOrNull(i) ?: continue
            if (i == selectedQueryIndex) candidateQuery
                .append(Component.literal("\n→ ").withStyle(ChatFormatting.GREEN))
                .append(group)
            else candidateQuery.append("\n").append(group.copy().withStyle(ChatFormatting.GRAY))
        }
        this.fieldAlias.tooltip = Tooltip.create(candidateQuery)
    }

    private fun getHoveredLineIndex(x: Double, y: Double): Int {
        var x = x
        var y = y
        x = x - leftPos - GUI_PADDING_X
        y = y - topPos - GUI_HEADER_HEIGHT
        if (x < 0 || y < 0) {
            return -1
        }
        if (x >= ROW_HEIGHT * COLUMNS || y >= visibleRows * ROW_HEIGHT) {
            return -1
        }

        val rowIndex: Double = this.scrollbar.currentScroll + y / ROW_HEIGHT
        if (rowIndex < 0 || rowIndex >= this.providersFiltered.size) {
            return -1
        }
        return rowIndex.toInt()
    }

    private fun blit(guiGraphics: GuiGraphics, offsetX: Int, offsetY: Int, srcRect: Rect2i) {
        val texture = AppEng.makeId("textures/guis/extendedae_plus_client/provider_list.png")
        guiGraphics.blit(
            texture,
            offsetX, offsetY,
            srcRect.x, srcRect.y,
            srcRect.width, srcRect.height
        )
    }

    private fun addMapping() {
        val selectedQuery = this.selectedQuery()
        val searchKey = this.fieldSearch.value
        val aliasToSet = this.fieldAlias.value.trim()
        val player = Minecraft.getInstance().player

        if (selectedQuery.string.isEmpty() || aliasToSet.isEmpty()) {
            player?.displayClientMessage(
                UtilKeyBuilder.of(UtilKeyBuilder.message)
                    .addStr("provider_list")
                    .addStr("add_alias")
                    .addStr(selectedQuery.string.isEmpty(), "empty_query", "empty_alias")
                    .build(),
                false
            )
            return
        }

        if (AliasGetter.addOrUpdateAlias(searchKey, aliasToSet)) {
            player?.displayClientMessage(
                UtilKeyBuilder.of(UtilKeyBuilder.message)
                    .addStr("provider_list")
                    .addStr("add_alias")
                    .addStr("success")
                    .args(searchKey, aliasToSet)
                    .build(),
                false
            )
            val newAliasGroup = Component.literal(aliasToSet)
            this.queries[1]=newAliasGroup
            this.selectedQueryIndex = 1
            this.queryRefresh = true
        } else {
            player?.displayClientMessage(
                UtilKeyBuilder.of(UtilKeyBuilder.message)
                    .addStr("provider_list")
                    .addStr("add_alias")
                    .addStr("failed")
                    .args(aliasToSet)
                    .build(),
                false
            )
        }
    }

    private fun removeMappings() {
        val aliasToDelete = this.fieldAlias.value
        val player = Minecraft.getInstance().player
        if (aliasToDelete.isEmpty()) {
            player?.displayClientMessage(
                UtilKeyBuilder.of(UtilKeyBuilder.message)
                    .addStr("provider_list")
                    .addStr("delete_alias")
                    .addStr("empty_alias")
                    .build(),
                false
            )
            return
        }
        val removed = AliasGetter.removeAliases(aliasToDelete)
        if (removed > 0) {
            player?.displayClientMessage(
                UtilKeyBuilder.of(UtilKeyBuilder.message)
                    .addStr("provider_list")
                    .addStr("delete_alias")
                    .addStr("success")
                    .args(removed, aliasToDelete)
                    .build(),
                false
            )
            this.queries.remove(1)
            this.selectedQueryIndex = if (this.queries.isEmpty()) -1 else 0
            this.fieldAlias.value=selectedQuery().string
            this.queryRefresh = true
        } else {
            player?.displayClientMessage(
                UtilKeyBuilder.of(UtilKeyBuilder.message)
                    .addStr("provider_list")
                    .addStr("delete_alias")
                    .addStr("failed")
                    .args(aliasToDelete)
                    .build(),
                false
            )
        }
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (this.focusedRow >= 0
            && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            this.select(this.focusedRow)
            return true
        }

        if (this.providersFiltered.isEmpty())
            return super.keyPressed(keyCode, scanCode, modifiers)

        val direction = when (keyCode) {
            GLFW.GLFW_KEY_UP -> -1
            GLFW.GLFW_KEY_DOWN -> 1
            else -> return super.keyPressed(keyCode, scanCode, modifiers)
        }
        val indexScroll = this.scrollbar.currentScroll
        if ((this.focusedRow == this.visibleRows + indexScroll - 1 && direction == 1)
            || (this.focusedRow == indexScroll && direction == -1)
        ) this.scrollbar.setCurrentScroll(indexScroll + direction)
        this.focusedRow = Math.clamp((this.focusedRow + direction).toLong(), 0, this.providersFiltered.size - 1)
        return true
    }

    override fun mouseClicked(xCoord: Double, yCoord: Double, button: Int): Boolean {
        if (button == 1) {
            if (this.fieldSearch.isMouseOver(xCoord, yCoord)) {
                this.fieldSearch.value = ""
            } else if (this.fieldAlias.isMouseOver(xCoord, yCoord)) {
                this.fieldAlias.value = ""
            }
        } else if (button == 0) {
            val indexProvider = this.getHoveredLineIndex(xCoord, yCoord)
            if (indexProvider >= 0) {
                this.focusedRow = indexProvider + this.scrollbar.currentScroll
                this.playDownSound(Minecraft.getInstance().soundManager)
                return true
            }
        }

        return super.mouseClicked(xCoord, yCoord, button)
    }

    private fun playDownSound(handler: SoundManager) {
        handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            val indexProvider = this.getHoveredLineIndex(mouseX, mouseY)
            if (indexProvider >= 0 && this.focusedRow == indexProvider + this.scrollbar.currentScroll) {
                this.select(indexProvider)
                return true
            }
        }

        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(x: Double, y: Double, deltaX: Double, deltaY: Double): Boolean {
        if (this.queries.size <= 1) return super.mouseScrolled(x, y, deltaX, deltaY)
        Minecraft.getInstance().player?.playSound(
            SoundEvents.UI_BUTTON_CLICK.value(),
            0.1f,
            1.0f
        )

        val reverse = deltaY > 0

        if (this.fieldAlias.isMouseOver(x, y)) {
            if (this.selectedQueryIndex == -1) this.selectedQueryIndex = 0

            if (reverse && this.selectedQueryIndex == 0) this.selectedQueryIndex = this.queries.size - 1
            else if (reverse) this.selectedQueryIndex--
            else if (this.selectedQueryIndex == this.queries.size - 1) this.selectedQueryIndex = 0
            else this.selectedQueryIndex++

            this.queryRefresh = true
            return true
        }

        return super.mouseScrolled(x, y, deltaX, deltaY)
    }

    override fun changeFocus(path: ComponentPath) {
        super.changeFocus(path)
        this.focusedRow = -1
    }

    override fun onClose() {
        this.applier.accept(null)
        this.returnToParent()
    }

    private class InfoProvider(val group: PatternContainerGroup) {
        val name: Component = group.name()
        val icon: AEItemKey? = group.icon()
        var availableSlots: Int = CacheProvider.getAvailableSlots(group)

        fun i18nKey(): String {
            if (this.icon == null) return ""
            return this.icon.id.toLanguageKey()
        }
    }

    companion object {
        const val PATH_STYLE: String = "/screens/extendedae_plus_client/provider_list.json"

        private const val GUI_WIDTH = 195
        private const val GUI_TOP_AND_BOTTOM_PADDING = 54
        private const val GUI_PADDING_X = 8
        private const val GUI_PADDING_Y = 6
        private const val GUI_HEADER_HEIGHT = 34
        private const val GUI_FOOTER_HEIGHT = 8
        private const val COLUMNS = 9
        private const val PATTERN_PROVIDER_NAME_MARGIN_X = 2
        private const val TEXT_MAX_WIDTH = 155
        private const val ROW_HEIGHT = 18

        private val AREA_HEAD = Rect2i(0, 0, GUI_WIDTH, GUI_HEADER_HEIGHT)
        private val AREA_TAIL = Rect2i(0, 162 + 16, GUI_WIDTH, GUI_FOOTER_HEIGHT)
        private val AREA_SCROLLER_HEAD_PROVIDER_FOCUSED = Rect2i(0, 34, GUI_WIDTH, ROW_HEIGHT)
        private val AREA_SCROLLER_HEAD_PROVIDER = Rect2i(0, 52, GUI_WIDTH, ROW_HEIGHT)
        private val AREA_SCROLLER_PROVIDER_FOCUSED = Rect2i(0, 70, GUI_WIDTH, ROW_HEIGHT)
        private val AREA_SCROLLER_PROVIDER = Rect2i(0, 88, GUI_WIDTH, ROW_HEIGHT)
        private val AREA_SCROLLER_TAIL_PROVIDER_FOCUSED = Rect2i(0, 106, GUI_WIDTH, ROW_HEIGHT)
        private val AREA_SCROLLER_TAIL_PROVIDER = Rect2i(0, 124, GUI_WIDTH, ROW_HEIGHT)
        private val AREA_PROVIDER_FOCUSED = Rect2i(0, 142, GUI_WIDTH, ROW_HEIGHT)
        private val AREA_PROVIDER = Rect2i(0, 160, GUI_WIDTH, ROW_HEIGHT)
    }
}
