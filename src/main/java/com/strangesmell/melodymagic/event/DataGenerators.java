package com.strangesmell.melodymagic.event;

import com.google.common.collect.Iterables;
import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.api.MMCriterionTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.commands.execution.tasks.BuildContexts;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.strangesmell.melodymagic.MelodyMagic.*;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var packOutput = gen.getPackOutput();
        var helper = event.getExistingFileHelper();
        var lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        gen.addProvider(event.includeClient(), new EnglishLanguageProvider(packOutput));
        gen.addProvider(event.includeClient(), new ChineseLanguageProvider(packOutput));
        gen.addProvider(event.includeClient(), new ModelProvider(packOutput, helper));
        gen.addProvider(event.includeClient(), new StateProvider(packOutput, helper));
        gen.addProvider(event.includeServer(), new LootProvider(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new CustomBlockTag(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new CustomAdvancementProvider(packOutput, lookupProvider,existingFileHelper));
        gen.addProvider(event.includeServer(), new MMWorldGen(packOutput, lookupProvider));


    }

    public static class EnglishLanguageProvider extends LanguageProvider {
        public EnglishLanguageProvider(PackOutput gen) {
            super(gen, "melodymagic", "en_us");
        }

        @Override
        protected void addTranslations() {
            //add(POT_MORNING_GLORY.get(),"Potted Morning Glory" );
            //this.add(SULFUR_BLOCK.get(), "Sulfur Block");
        }
    }

    public static class ChineseLanguageProvider extends LanguageProvider {
        public ChineseLanguageProvider(PackOutput gen) {
            super(gen, "melodymagic", "zh_cn");
        }

        @Override
        protected void addTranslations() {
            //add(POT_MORNING_GLORY.get(),"盆装牵牛花" );
            //this.add(SULFUR_BLOCK.get(), "硫磺块");
        }
    }

    public static class ModelProvider extends ItemModelProvider {
        public ModelProvider(PackOutput gen, ExistingFileHelper helper) {
            super(gen, "melodymagic", helper);
        }

        @Override
        protected void registerModels() {
            //this.singleTexture(SULFUR_DUST_ID, ResourceLocation.withDefaultNamespace("item/generated"), "layer0", ResourceLocation.fromNamespaceAndPath("xiaozhong", "item/" + SULFUR_DUST_ID));

        }
    }

    public static class StateProvider extends BlockStateProvider {
        public StateProvider(PackOutput gen, ExistingFileHelper helper) {
            super(gen, "melodymagic", helper);
        }

        @Override
        protected void registerStatesAndModels() {
            generateMorningGloryBlockState(MORNING_GLORY.get());
            generatePottedMorningGloryBlockState(POT_MORNING_GLORY.get());
            //BlockModelBuilder modelBuilder =models().cross(name(MORNING_GLORY.get()),blockTexture(MORNING_GLORY.get())).renderType("cutout");
            //this.simpleBlockItem(SULFUR_BLOCK.get(), this.cubeAll(SULFUR_BLOCK.get()));
        }
        public void generateMorningGloryBlockState(Block block) {
            BlockModelBuilder model = models().cross(name(block), blockTexture(block)).renderType("cutout");
            simpleBlock(block,model);
            //this.simpleBlockItem(block,model);
        }

        public void generatePottedMorningGloryBlockState(Block block) {
            simpleBlock(block,models().singleTexture(name(block), ResourceLocation.withDefaultNamespace("block/flower_pot_cross"),"plant", ResourceLocation.fromNamespaceAndPath(MODID,"block/morning_glory")).renderType("cutout"));

        }
        private ResourceLocation key(Block block) {
            return BuiltInRegistries.BLOCK.getKey(block);
        }

        private String name(Block block) {
            return key(block).getPath();
        }
    }

    public static class LootProvider extends LootTableProvider {
        public LootProvider(PackOutput gen, CompletableFuture<HolderLookup.Provider> lookup) {
            super(gen, Set.of(), List.of(new SubProviderEntry(CustomBlockLoot::new, LootContextParamSets.BLOCK)), lookup);
        }

        @Override
        protected void validate(WritableRegistry<LootTable> registry, ValidationContext context, ProblemReporter.Collector collector) {
            // map.forEach((key, value) -> LootTables.validate(context, key, value));
        }
    }

    public static class CustomBlockLoot extends BlockLootSubProvider {
        protected CustomBlockLoot(HolderLookup.Provider lookupProvider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
        }

        @Override
        protected void generate() {
            this.dropSelf(SOUND_PLAYER_BLOCK.get());
            this.dropSelf(MORNING_GLORY.get());
            this.dropSelf(POT_MORNING_GLORY.get());
            this.dropOther(FAKE_NETHER_PORTAL.get(),Items.AIR);

        /*
        // 如欲在非精准采集的情况下掉落九个 xiaozhong:sulfur_dust，请使用以下代码：
        this.add(SULFUR_BLOCK.get(), block -> createSingleItemTableWithSilkTouch(block, SULFUR_DUST_ITEM.get(), ConstantValue.exactly(9f)));
        */
        }

        @Nonnull
        @Override
        protected Iterable<Block> getKnownBlocks() {
            // 模组自定义的方块战利品表必须覆盖此方法，以绕过对原版方块战利品表的检查（此处返回该模组的所有方块）
            return Iterables.transform(BLOCKS.getEntries(), DeferredHolder::get);
        }
    }

    public static class CustomBlockTag extends VanillaBlockTagsProvider {
        public CustomBlockTag(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider) {
            super(pOutput, pLookupProvider);
        }

        protected void addTags(HolderLookup.Provider pProvider) {
            this.tag(BlockTags.MINEABLE_WITH_AXE).add(
                    SOUND_PLAYER_BLOCK.get()
            );
            this.tag(BlockTags.FLOWERS).add(
                    MORNING_GLORY.get()
            );
        }
    }

    public static class CustomAdvancementProvider extends AdvancementProvider {
        // Parameters can be obtained from GatherDataEvent.
        public CustomAdvancementProvider(PackOutput output,
                                     CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, existingFileHelper, List.of(new CustomAdvancementGenerator()));
        }

        private static final class CustomAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
            @Override
            public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
                // Generate your advancements here.
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putString("adv","lightning_bolt_thunder");


                Advancement.Builder builder = Advancement.Builder.advancement();

                builder.display(
                                COLLECTION_ITEM,
                                Component.translatable("adv.melodymagic.thunder"),
                                Component.translatable("adv.melodymagic.thunder"),
                                 ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/adventure.png"),// The background texture. Use null if you don't want a background texture (for non-root advancements).
                                AdvancementType.GOAL,// 框架类型. Valid values are AdvancementType.TASK, CHALLENGE, or GOAL.
                                true,// 是否祝贺
                                true,// 在聊天栏中显示.
                                false)// 隐藏成就.
                        .addCriterion("lightning_bolt_thunder", MMCriterionTrigger.MMTriggerInstance.instance(
                                ItemCustomDataPredicate.customData(new NbtPredicate(compoundTag))
                        ))
                        .save(saver, ResourceLocation.fromNamespaceAndPath("melodymagic", "lightning_bolt_thunder"), existingFileHelper);;

            }
        }
    }

}



