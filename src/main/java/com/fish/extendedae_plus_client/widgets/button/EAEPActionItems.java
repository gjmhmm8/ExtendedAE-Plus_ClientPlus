package com.fish.extendedae_plus_client.widgets.button;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import com.fish.extendedae_plus_client.util.UtilKeyBuilder;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EAEPActionItems {
    BACKING_OUT(EAEPIcon.fromAEIcon(Icon.INVALID), Component.empty(), Component.empty(), ""),

    ALIAS_RELOAD(EAEPIcon.SAVE_CENTER, "recipe_alias", "reload"),
    ALIAS_ADD(EAEPIcon.SAVE_UP, "recipe_alias", "add"),
    ALIAS_REMOVE(EAEPIcon.SAVE_DOWN, "recipe_alias", "remove")

    ;

    private final IButtonIcon icon;
    private final Component name;
    private final Component tooltip;
    private final String actionGroup;

    public static final Map<String, List<EAEPActionItems>> GROUPED_ACTIONS = new HashMap<>();

    static {
        for (EAEPActionItems action : EAEPActionItems.values()) {
            if (!action.actionGroup.isEmpty())
                GROUPED_ACTIONS.computeIfAbsent(action.actionGroup,
                        ignored -> new ArrayList<>()).add(action);
        }
    }

    EAEPActionItems(IButtonIcon icon, String actionGroup) {
        this(icon, Component.empty(), Component.empty(), actionGroup);
    }

    EAEPActionItems(IButtonIcon icon, String actionGroup, String additionalKey) {
        this(
                icon,
                UtilKeyBuilder.of(UtilKeyBuilder.screenTooltip)
                        .addStr(actionGroup)
                        .build(),
                UtilKeyBuilder.of(UtilKeyBuilder.screenTooltip)
                        .addStr(actionGroup)
                        .addStr(additionalKey)
                        .build(),
                actionGroup
        );
    }

    EAEPActionItems(IButtonIcon icon, Component name, Component tooltip, String actionGroup) {
        this.icon = icon;
        this.name = name;
        this.tooltip = tooltip;
        this.actionGroup = actionGroup;
    }

    public Blitter getIconBlitter() {
        if (icon != null) return icon.getBlitter();
        else return Icon.INVALID.getBlitter();
    }

    public String getGroup() {
        return actionGroup;
    }

    public IButtonIcon getIcon() {
        return icon;
    }
    public Icon getAEIcon() {
        return icon.getAEIcon();
    }

    public boolean hasName() {
        return !name.getString().isEmpty();
    }
    public Component getName() {
        return name;
    }
    public Component getTooltip() {
        return tooltip;
    }
}
