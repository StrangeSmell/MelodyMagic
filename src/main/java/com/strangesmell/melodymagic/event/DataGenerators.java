package com.strangesmell.melodymagic.event;

import com.google.common.collect.Iterables;
import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.api.MMCriterionTrigger;
import com.strangesmell.melodymagic.block.CompositionWorkbenchBlock;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.*;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
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
import java.util.function.BiFunction;
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

        CompletableFuture<HolderLookup.Provider> completablefuture1 = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());

        DataGenerator.PackGenerator datagenerator$packgenerator2 = gen.getVanillaPack(event.includeServer());
        TagsProvider<Block> tagsprovider4 = datagenerator$packgenerator2.addProvider(bindRegistries(VanillaBlockTagsProvider::new, completablefuture1));

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        gen.addProvider(event.includeClient(), new EnglishLanguageProvider(packOutput));
        gen.addProvider(event.includeClient(), new ChineseLanguageProvider(packOutput));
        gen.addProvider(event.includeClient(), new ModelProvider(packOutput, helper));
        gen.addProvider(event.includeClient(), new StateProvider(packOutput, helper));
        gen.addProvider(event.includeServer(), new LootProvider(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new CustomBlockTag(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new CustomItemTag(packOutput, lookupProvider, tagsprovider4.contentsGetter()));
        gen.addProvider(event.includeServer(), new CustomAdvancementProvider(packOutput, lookupProvider,existingFileHelper));
        gen.addProvider(event.includeServer(), new MMWorldGen(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new CustomRecipeProvider(packOutput, lookupProvider));


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
            //simpleBlock(COMPOSITION_WORKBENCH.get(),models().singleTexture(name(COMPOSITION_WORKBENCH.get(), ResourceLocation.withDefaultNamespace("block/composition_workbench"),"composition_workbench", ResourceLocation.fromNamespaceAndPath(MODID,"block/composition_workbench")).renderType("cutout"));

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
            this.dropSelf(COMPOSITION_WORKBENCH.get());
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

    public static class CustomItemTag extends VanillaItemTagsProvider {
        public CustomItemTag(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> p_275572_) {
            super(pOutput, pLookupProvider, p_275572_);
        }

        protected void addTags(HolderLookup.Provider pProvider) {
            this.tag(ItemTags.ARROWS).add(
                    RECON_BOLT_ITEM.get()
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

    public static class CustomRecipeProvider extends RecipeProvider {

        public CustomRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
            super(pOutput, pRegistries);
        }

        @Override
        protected void buildRecipes(RecipeOutput pRecipeOutput) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SOUND_COLLECTION_ITEM.get())
                    .pattern(" a ") // 创建配方图案
                    .pattern(" b ") // 创建配方图案
                    .define('a', MORNING_GLORY_ITEM) // 定义符号代表什么
                    .define('b', Items.AMETHYST_SHARD) // 定义符号代表什么
                    .unlockedBy(getHasName(MORNING_GLORY_ITEM), has(MORNING_GLORY_ITEM)) // 该配方如何解锁
                    .save(pRecipeOutput); // 将数据加入生成器

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, COLLECTION_DISPLAY_ITEM.get())
                    .pattern(" ca") // 创建配方图案
                    .pattern(" ab") // 创建配方图案
                    .pattern("b  ") // 创建配方图案
                    .define('a', Items.STICK) // 定义符号代表什么
                    .define('b', Items.GLOW_BERRIES) // 定义符号代表什么
                    .define('c', Items.SWEET_BERRIES) // 定义符号代表什么
                    .unlockedBy(getHasName(Items.GLOW_BERRIES), has(Items.GLOW_BERRIES)) // 该配方如何解锁
                    .unlockedBy(getHasName(Items.SWEET_BERRIES), has(Items.SWEET_BERRIES)) // 该配方如何解锁
                    .save(pRecipeOutput); // 将数据加入生成器

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, COLLECTION_ITEM.get())
                    .requires(COLLECTION_DISPLAY_ITEM)
                    .requires(Items.AMETHYST_SHARD)
                    .unlockedBy(getHasName(COLLECTION_DISPLAY_ITEM), has(COLLECTION_DISPLAY_ITEM)) // 该配方如何解锁
                    .unlockedBy(getHasName(Items.AMETHYST_SHARD), has(Items.AMETHYST_SHARD)) // 该配方如何解锁
                    .save(pRecipeOutput); // 将数据加入生成器
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CONTINUE_SOUND_COLLECTION_ITEM.get())
                    .pattern("aaa") // 创建配方图案
                    .pattern("aba") // 创建配方图案
                    .pattern("aaa") // 创建配方图案
                    .define('a', Items.AMETHYST_SHARD) // 定义符号代表什么
                    .define('b', MORNING_GLORY_ITEM) // 定义符号代表什么
                    .unlockedBy(getHasName(Items.AMETHYST_SHARD), has(Items.AMETHYST_SHARD)) // 该配方如何解锁
                    .unlockedBy(getHasName(MORNING_GLORY_ITEM), has(MORNING_GLORY_ITEM)) // 该配方如何解锁
                    .save(pRecipeOutput); // 将数据加入生成器

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, RECORD_BOOK.get())
                    .requires(Items.BOOK)
                    .requires(Items.AMETHYST_SHARD)
                    .unlockedBy(getHasName(Items.BOOK), has(Items.BOOK)) // 该配方如何解锁
                    .unlockedBy(getHasName(Items.AMETHYST_SHARD), has(Items.AMETHYST_SHARD)) // 该配方如何解锁
                    .save(pRecipeOutput); // 将数据加入生成器

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SOUND_PLAYER_BLOCK.get())
                    .pattern("aaa") // 创建配方图案
                    .pattern("aba") // 创建配方图案
                    .pattern("aaa") // 创建配方图案
                    .define('a', MORNING_GLORY_ITEM) // 定义符号代表什么
                    .define('b', Items.JUKEBOX) // 定义符号代表什么
                    .unlockedBy(getHasName(MORNING_GLORY_ITEM), has(MORNING_GLORY_ITEM)) // 该配方如何解锁
                    .unlockedBy(getHasName(Items.JUKEBOX), has(Items.JUKEBOX)) // 该配方如何解锁
                    .save(pRecipeOutput); // 将数据加入生成器

        }


    }
    private static <T extends DataProvider> DataProvider.Factory<T> bindRegistries(
            BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> tagProviderFactory, CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        return p_255476_ -> tagProviderFactory.apply(p_255476_, lookupProvider);
    }

}



