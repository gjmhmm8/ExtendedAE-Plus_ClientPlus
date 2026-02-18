package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Dependency provider for <b>advancedAE</b> with <b>curse.maven:advancedae-1084104</b> coordinates and
     * with version reference <b>advancedAE</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getAdvancedAE() {
        return create("advancedAE");
    }

    /**
     * Dependency provider for <b>ae2</b> with <b>curse.maven:ae2-223794</b> coordinates and
     * with version reference <b>ae2</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getAe2() {
        return create("ae2");
    }

    /**
     * Dependency provider for <b>ae2wt</b> with <b>curse.maven:ae2wtlib-459929</b> coordinates and
     * with version reference <b>ae2wt</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getAe2wt() {
        return create("ae2wt");
    }

    /**
     * Dependency provider for <b>appliedFlux</b> with <b>curse.maven:applied-flux-965012</b> coordinates and
     * with version reference <b>appliedFlux</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getAppliedFlux() {
        return create("appliedFlux");
    }

    /**
     * Dependency provider for <b>appliedMekanistics</b> with <b>curse.maven:applied-mekanistics-574300</b> coordinates and
     * with version reference <b>appliedMekanistics</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getAppliedMekanistics() {
        return create("appliedMekanistics");
    }

    /**
     * Dependency provider for <b>architectury</b> with <b>curse.maven:architectury-api-419699</b> coordinates and
     * with version reference <b>architectury</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getArchitectury() {
        return create("architectury");
    }

    /**
     * Dependency provider for <b>clothConfig</b> with <b>me.shedaniel.cloth:cloth-config-forge</b> coordinates and
     * with version reference <b>clothConfig</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getClothConfig() {
        return create("clothConfig");
    }

    /**
     * Dependency provider for <b>curios</b> with <b>curse.maven:curios-309927</b> coordinates and
     * with version reference <b>curios</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getCurios() {
        return create("curios");
    }

    /**
     * Dependency provider for <b>eaep</b> with <b>curse.maven:eaep-1337639</b> coordinates and
     * with version reference <b>eaep</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getEaep() {
        return create("eaep");
    }

    /**
     * Dependency provider for <b>emi</b> with <b>dev.emi:emi-forge</b> coordinates and
     * with version reference <b>emi</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getEmi() {
        return create("emi");
    }

    /**
     * Dependency provider for <b>expandedAE</b> with <b>curse.maven:expanded-ae-1213351</b> coordinates and
     * with version reference <b>expandedAE</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getExpandedAE() {
        return create("expandedAE");
    }

    /**
     * Dependency provider for <b>extendedAE</b> with <b>curse.maven:ex-pattern-provider-892005</b> coordinates and
     * with version reference <b>extendedAE</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getExtendedAE() {
        return create("extendedAE");
    }

    /**
     * Dependency provider for <b>ftbLibrary</b> with <b>dev.ftb.mods:ftb-library-forge</b> coordinates and
     * with version reference <b>ftbLibrary</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getFtbLibrary() {
        return create("ftbLibrary");
    }

    /**
     * Dependency provider for <b>ftbTeams</b> with <b>dev.ftb.mods:ftb-teams-forge</b> coordinates and
     * with version reference <b>ftbTeams</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getFtbTeams() {
        return create("ftbTeams");
    }

    /**
     * Dependency provider for <b>geckolib</b> with <b>curse.maven:geckolib-388172</b> coordinates and
     * with version reference <b>geckolib</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getGeckolib() {
        return create("geckolib");
    }

    /**
     * Dependency provider for <b>glodium</b> with <b>curse.maven:glodium-957920</b> coordinates and
     * with version reference <b>glodium</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getGlodium() {
        return create("glodium");
    }

    /**
     * Dependency provider for <b>guideME</b> with <b>org.appliedenergistics:guideme</b> coordinates and
     * with version reference <b>guideME</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getGuideME() {
        return create("guideME");
    }

    /**
     * Dependency provider for <b>jade</b> with <b>curse.maven:jade-324717</b> coordinates and
     * with version reference <b>jade</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getJade() {
        return create("jade");
    }

    /**
     * Dependency provider for <b>jech</b> with <b>curse.maven:just-enough-characters-250702</b> coordinates and
     * with version reference <b>jech</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getJech() {
        return create("jech");
    }

    /**
     * Dependency provider for <b>jei</b> with <b>mezz.jei:jei-1.20.1-forge</b> coordinates and
     * with version reference <b>jei</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getJei() {
        return create("jei");
    }

    /**
     * Dependency provider for <b>kotlinForForge</b> with <b>thedarkcolour:kotlinforforge</b> coordinates and
     * with version reference <b>kotlinForForge</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getKotlinForForge() {
        return create("kotlinForForge");
    }

    /**
     * Dependency provider for <b>megaCells</b> with <b>curse.maven:mega-cells-622112</b> coordinates and
     * with version reference <b>megaCells</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getMegaCells() {
        return create("megaCells");
    }

    /**
     * Dependency provider for <b>mekanism</b> with <b>mekanism:Mekanism</b> coordinates and
     * with version reference <b>mekanism</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getMekanism() {
        return create("mekanism");
    }

    /**
     * Dependency provider for <b>modernUI</b> with <b>curse.maven:modern-ui-352491</b> coordinates and
     * with version reference <b>modernUI</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getModernUI() {
        return create("modernUI");
    }

    /**
     * Dependency provider for <b>rei</b> with <b>me.shedaniel:RoughlyEnoughItems-forge</b> coordinates and
     * with version reference <b>rei</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getRei() {
        return create("rei");
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class VersionAccessors extends VersionFactory  {

        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>advancedAE</b> with value <b>7159779</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAdvancedAE() { return getVersion("advancedAE"); }

        /**
         * Version alias <b>ae2</b> with value <b>7148487</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAe2() { return getVersion("ae2"); }

        /**
         * Version alias <b>ae2wt</b> with value <b>7251206</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAe2wt() { return getVersion("ae2wt"); }

        /**
         * Version alias <b>appliedFlux</b> with value <b>7072853</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAppliedFlux() { return getVersion("appliedFlux"); }

        /**
         * Version alias <b>appliedMekanistics</b> with value <b>7244744</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAppliedMekanistics() { return getVersion("appliedMekanistics"); }

        /**
         * Version alias <b>architectury</b> with value <b>5137938</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getArchitectury() { return getVersion("architectury"); }

        /**
         * Version alias <b>clothConfig</b> with value <b>11.1.136</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getClothConfig() { return getVersion("clothConfig"); }

        /**
         * Version alias <b>curios</b> with value <b>6418456</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getCurios() { return getVersion("curios"); }

        /**
         * Version alias <b>eaep</b> with value <b>7327716</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getEaep() { return getVersion("eaep"); }

        /**
         * Version alias <b>emi</b> with value <b>1.1.22+1.20.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getEmi() { return getVersion("emi"); }

        /**
         * Version alias <b>expandedAE</b> with value <b>7266780</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getExpandedAE() { return getVersion("expandedAE"); }

        /**
         * Version alias <b>extendedAE</b> with value <b>7472662</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getExtendedAE() { return getVersion("extendedAE"); }

        /**
         * Version alias <b>ftbLibrary</b> with value <b>2001.2.12</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFtbLibrary() { return getVersion("ftbLibrary"); }

        /**
         * Version alias <b>ftbTeams</b> with value <b>2001.3.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFtbTeams() { return getVersion("ftbTeams"); }

        /**
         * Version alias <b>geckolib</b> with value <b>7553267</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getGeckolib() { return getVersion("geckolib"); }

        /**
         * Version alias <b>glodium</b> with value <b>5226922</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getGlodium() { return getVersion("glodium"); }

        /**
         * Version alias <b>guideME</b> with value <b>20.1.14</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getGuideME() { return getVersion("guideME"); }

        /**
         * Version alias <b>jade</b> with value <b>6855440</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJade() { return getVersion("jade"); }

        /**
         * Version alias <b>jech</b> with value <b>7424367</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJech() { return getVersion("jech"); }

        /**
         * Version alias <b>jei</b> with value <b>15.20.0.129</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJei() { return getVersion("jei"); }

        /**
         * Version alias <b>kotlinForForge</b> with value <b>4.12.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getKotlinForForge() { return getVersion("kotlinForForge"); }

        /**
         * Version alias <b>megaCells</b> with value <b>6175008</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getMegaCells() { return getVersion("megaCells"); }

        /**
         * Version alias <b>mekanism</b> with value <b>1.20.1-10.4.16.80</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getMekanism() { return getVersion("mekanism"); }

        /**
         * Version alias <b>modernUI</b> with value <b>6956345</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getModernUI() { return getVersion("modernUI"); }

        /**
         * Version alias <b>rei</b> with value <b>12.1.785</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getRei() { return getVersion("rei"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
