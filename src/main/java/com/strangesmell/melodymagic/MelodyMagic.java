package com.strangesmell.melodymagic;

import com.mojang.serialization.Codec;
import com.strangesmell.melodymagic.api.MMCriterionTrigger;
import com.strangesmell.melodymagic.api.SoundEffect;
import com.strangesmell.melodymagic.block.FakeNetherPortal;
import com.strangesmell.melodymagic.block.FakeNetherPortalBlockEntity;
import com.strangesmell.melodymagic.block.SoundPlayerBlock;
import com.strangesmell.melodymagic.block.SoundPlayerBlockEntity;
import com.strangesmell.melodymagic.container.WandMenu;
import com.strangesmell.melodymagic.hud.RecordHud;
import com.strangesmell.melodymagic.hud.SelectHud;
import com.strangesmell.melodymagic.item.CollectionItem;
import com.strangesmell.melodymagic.item.RecordBook;
import com.strangesmell.melodymagic.item.SoundCollectionItem;
import com.strangesmell.melodymagic.item.SoundContainerItem;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.sound.SoundEngineLoadEvent;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Rarity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.*;
import java.util.function.Supplier;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

@Mod(MelodyMagic.MODID)
public class MelodyMagic
{
    public static final String MODID = "melodymagic";

    public static Map<HashSet<String>,String> SOUND2KEY= new HashMap<>();
    public static List<HashSet<String>> SOUND_LIST= new ArrayList<>();
    public static Map<String, CompoundTag> CONDITION= new HashMap<>();
    public static Map<String, SoundEffect> KEY2EFFECT= new HashMap<>();
    public static Map<String, Integer> SOUND_INF= new HashMap<>();
    public static Map<String, List<Object>> EFFECT_INF= new HashMap<>();

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredBlock<Block> SOUND_PLAYER_BLOCK = BLOCKS.register("sound_player_block", ()-> new SoundPlayerBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F, 6.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()
                    .requiresCorrectToolForDrops()

            )
    );
    public static final DeferredBlock<Block> FAKE_NETHER_PORTAL = BLOCKS.register("fake_nether_portal", ()-> new FakeNetherPortal(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .randomTicks()
                            .strength(-1.0F)
                            .sound(SoundType.GLASS)
                            .lightLevel(p_50870_ -> 11)
                            .pushReaction(PushReaction.BLOCK)
            )
    );

    public static final DeferredBlock<Block> MORNING_GLORY = BLOCKS.register("morning_glory", ()-> new FlowerBlock(
            MobEffects.LUCK,5
            ,BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION)
            )
    );
    public static final DeferredBlock<Block> POT_MORNING_GLORY = BLOCKS.register("pot_morning_glory", ()-> new FlowerPotBlock(
            ()->((FlowerPotBlock) Blocks.FLOWER_POT),MORNING_GLORY,BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM))
    );



    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<SoundCollectionItem> SOUND_COLLECTION_ITEM = ITEMS.registerItem("sound_collection", SoundCollectionItem::new ,new Item.Properties().stacksTo(1));
    public static final DeferredItem<CollectionItem> COLLECTION_ITEM = ITEMS.registerItem("collection", CollectionItem::new ,new Item.Properties().stacksTo(1));
    public static final DeferredItem<RecordBook> RECORD_BOOK = ITEMS.registerItem("record_book", RecordBook::new ,new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> COLLECTION_DISPLAY_ITEM = ITEMS.registerItem("collection_display", Item::new ,new Item.Properties().stacksTo(1));
    public static final DeferredItem<SoundContainerItem> SOUND_CONTAINER_ITEM = ITEMS.registerItem("sound_container", SoundContainerItem::new ,new Item.Properties().rarity(Rarity.RARE).stacksTo(1));
    public static final DeferredItem<BlockItem> SOUND_PLAYER_ITEM = ITEMS.registerSimpleBlockItem("sound_player_block", SOUND_PLAYER_BLOCK);
    public static final DeferredItem<BlockItem> MORNING_GLORY_ITEM = ITEMS.registerSimpleBlockItem("morning_glory", MORNING_GLORY);


    public static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(Registries.MENU,MODID);
    public static final Supplier<MenuType<WandMenu>> WAND_MENU = MENU_TYPE.register("wand_menu", () -> new MenuType(WandMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,MODID);
    public static final Supplier<BlockEntityType<SoundPlayerBlockEntity>> SOUND_PLAYER_BLOCK_ENTITY = BLOCK_ENTITY.register("sound_player_block_entity", () -> BlockEntityType.Builder.of(SoundPlayerBlockEntity::new, SOUND_PLAYER_BLOCK.get()).build(null));
    public static final Supplier<BlockEntityType<FakeNetherPortalBlockEntity>> FAKE_NETHER_PORTAL_BLOCK_ENTITY = BLOCK_ENTITY.register("fake_nether_portal_block_entity", () -> BlockEntityType.Builder.of(FakeNetherPortalBlockEntity::new, FAKE_NETHER_PORTAL.get()).build(null));

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
    // Serialization via codec
    public static final Supplier<AttachmentType<Integer>> ENTITY_AGE = ATTACHMENT_TYPES.register("entity_age", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES = DeferredRegister.create(Registries.TRIGGER_TYPE, MelodyMagic.MODID);

    public static final Supplier<MMCriterionTrigger> MM_TRIGGER = TRIGGER_TYPES.register("mm_trigger", MMCriterionTrigger::new);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("melodymagic", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.melodymagic")) //The language key for the title of your CreativeModeTab
            .icon(() -> COLLECTION_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(SOUND_COLLECTION_ITEM.get());
                output.accept(COLLECTION_ITEM.get());

                output.accept(COLLECTION_DISPLAY_ITEM.get());
                output.accept(SOUND_CONTAINER_ITEM.get());
                output.accept(SOUND_PLAYER_ITEM.get());
                output.accept(RECORD_BOOK.get());
                output.accept(MORNING_GLORY_ITEM.get());
            }).build());


    public MelodyMagic(IEventBus modEventBus, ModContainer modContainer)
    {
        Init init =new Init();
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        MENU_TYPE.register(modEventBus);
        BLOCK_ENTITY.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        TRIGGER_TYPES.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        init.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        event.enqueueWork(()->{
            ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(MORNING_GLORY.getId(),POT_MORNING_GLORY);
        });
        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        //Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS){

        }

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }




    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }

        @SubscribeEvent
        public static void loadSoundEngine(SoundEngineLoadEvent event)
        {
            LOGGER.info("Add sound listener");
            event.getEngine().addEventListener(SelectHud.getInstance());
        }
        @SubscribeEvent
        public static void registerGuiLayersEvent(RegisterGuiLayersEvent event)
        {
            LOGGER.info("Register hud");
            event.registerAboveAll(fromNamespaceAndPath(MODID,"select_hud"), SelectHud.getInstance());
            event.registerAboveAll(fromNamespaceAndPath(MODID,"record_hud"), RecordHud.getInstance());
        }
    }



}
