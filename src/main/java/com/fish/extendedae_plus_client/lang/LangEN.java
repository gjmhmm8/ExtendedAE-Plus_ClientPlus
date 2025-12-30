package com.fish.extendedae_plus_client.lang;

import appeng.core.definitions.AEItems;
import com.fish.extendedae_plus_client.ExtendedAEPlusClient;
import com.fish.extendedae_plus_client.util.UtilKeyBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class LangEN extends LanguageProvider {
    public LangEN(PackOutput output) {
        super(output, ExtendedAEPlusClient.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        UtilKeyBuilder.BuilderDataGen.bindTranslator("en_us", this::add);

        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.tooltip)
                .item(AEItems.PROCESSING_PATTERN)
                .branch("selected_provider", "§7SelectedProvider: §r%s")
                .branch("auto_completable", "§7Auto Completable");
        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.tooltip)
                .item(AEItems.CERTUS_QUARTZ_KNIFE)
                .addStr("block_name_coping")
                .branch("failed", "Failed to copy BlockName{%s}")
                .branch("success", "Copied BlockName{%s} to the clipboard");

        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.message)
                .addStr("provider_list")
                .branch("remap_success", "[EAEP] Remap succeed")
                .branch("remap_failed", "[EAEP] Failed to remap");
        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.message)
                .addStr("provider_list")
                .addStr("add_alias")
                .branch("empty_query", "[EAEP] Empty query, please input the KeywordToMap first")
                .branch("empty_alias", "[EAEP] Empty alias, please input the AliasToMap")
                .branch("success", "[EAEP] Alias{%s → %s}")
                .branch("failed", "[EAEP] Failed to map Alias{%s}");
        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.message)
                .addStr("provider_list")
                .addStr("delete_alias")
                .branch("empty_alias", "[EAEP] Empty alias, please input the AliasToDelete")
                .branch("success", "[EAEP] Alias{%s × %s} was deleted")
                .branch("failed", "[EAEP] Failed to delete Alias{%s}");
        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.message)
                .addStr("provider_list")
                .addStr("empty_list")
                .buildInto("[EAEP] Empty local providers cache, please open the pattern access terminal to create a cache");

        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.screen)
                .addStr("provider_list")
                .branch("alias", "Input Alias...")
                .buildInto("Select a Provider");
        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.screen)
                .addStr("stacks_reproperties")
                .buildInto("ReProperties");
        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.screen)
                .addStr("auto_completion")
                .branch("off", "Auto Completion: False")
                .branch("on", "Auto Completion: True")
                .branch("description", "Tasks that crafts this item will be completed automatically after all the patterns have been pushed and the terminal is opened")
                .branch("only_completion", "This pattern only accepts automatic completion or manual cancellation, and cannot be detected to return to completion");

        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.screenTooltip)
                .addStr("provider_list")
                .addStr("candidate_keywords")
                .buildInto("§f§lKeywords");

        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.screenTooltip)
                .addStr("recipe_alias")
                .branch("reload", "Reload Mappings")
                .branch("add", "Add a Mapping")
                .branch("remove", "Remove Mappings")
                .buildInto("Alias Actions");

        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.keyCategory)
                .buildInto("ExtendedAE Plus [Client]");
        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.key)
                .addStr("fill_to_search_field")
                .buildInto("Search hovered item");

        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.config)
                .addStr("autoPlateRepeat")
                .branch("tooltip", "Quartz Cutting Knife automatically makes quantities of each Inscriber Name Press for processing patterns with custom-named Items")
                .buildInto("Auto Plating Repeat");
        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.config)
                .addStr("modeEncodingTransfer")
                .branch("tooltip", """
                        Controls how items are automatically merged by the terminal when using [Shift] for recipe transfer.
                        NONE - No changes made
                        MERGE_ADJACENCY - Only merges adjacent items
                        INDEPENDENCE - No merging at all""")
                .buildInto("Recipe Transfer Merge Mode");

        UtilKeyBuilder.BuilderDataGen.destroy("en_us");
    }
}
