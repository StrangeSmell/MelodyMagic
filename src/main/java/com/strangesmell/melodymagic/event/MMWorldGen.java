package com.strangesmell.melodymagic.event;

import com.strangesmell.melodymagic.world.feature.MMConfigureFeature;
import com.strangesmell.melodymagic.world.feature.MMPlacedFeature;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;

public class MMWorldGen extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, MMConfigureFeature::bootstrap)
            .add(Registries.PLACED_FEATURE, MMPlacedFeature::bootstrap);

    public MMWorldGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(MODID));

    }
}