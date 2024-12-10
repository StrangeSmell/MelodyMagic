package com.strangesmell.melodymagic;

import com.mojang.serialization.Codec;
import com.strangesmell.melodymagic.api.MMCriterionTrigger;
import com.strangesmell.melodymagic.api.SoundEffect;
import com.strangesmell.melodymagic.block.*;
import com.strangesmell.melodymagic.container.ChestConatiner;
import com.strangesmell.melodymagic.container.WandMenu;
import com.strangesmell.melodymagic.entity.FriendlyVex;
import com.strangesmell.melodymagic.hud.DanceHud;
import com.strangesmell.melodymagic.hud.RecordHud;
import com.strangesmell.melodymagic.hud.SelectHud;
import com.strangesmell.melodymagic.item.*;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
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
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.registries.*;

import org.lwjgl.BufferUtils;
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
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

@Mod(MelodyMagic.MODID)
public class MelodyMagic {
    public static final String MODID = "melodymagic";

    public static Map<HashSet<String>, String> SOUND2KEY = new HashMap<>();
    public static List<HashSet<String>> SOUND_LIST = new ArrayList<>();
    public static Map<String, CompoundTag> CONDITION = new HashMap<>();
    public static Map<String, SoundEffect> KEY2EFFECT = new HashMap<>();
    public static Map<String, Integer> SOUND_INF = new HashMap<>();
    public static Map<String, List<Object>> EFFECT_INF = new HashMap<>();

    /*


    public static ByteBuffer PcmData= BufferUtils.createByteBuffer(1024);
    public static ExecutorService getPcmExecutor = Executors.newSingleThreadExecutor();
    public static ExecutorService getPcmExecutor2 = Executors.newSingleThreadExecutor();
    public static ExecutorService getPcmExecutor3 = Executors.newSingleThreadExecutor();
    public static ExecutorService getPcmExecutor4 = Executors.newSingleThreadExecutor();
    public static ExecutorService fftExecutor = Executors.newSingleThreadExecutor();
    public static boolean doFFT = false;
    public static long intSize = Native.getNativeSize(int.class);
    public static long pointerSize = Native.getNativeSize(Pointer.class);
    public static float[][] pcmData = new float[128][];
    public static boolean[] pcmDataCanRender = new boolean[128];
    public static int currentSize=0;
    public static long arraySize=128;
    //指向指针数组的指针
    public static PointerByReference bigArr = new PointerByReference();
    //指针数组大小
    public static IntByReference size = new IntByReference();
    //指针数组中每一个数组的大小
    public static IntByReference smallArrSize = new IntByReference();
    //是否继续监听
    public static WinDef.BOOLByReference flag = new WinDef.BOOLByReference();
*/


    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredBlock<Block> SOUND_PLAYER_BLOCK = BLOCKS.register("sound_player_block", () -> new SoundPlayerBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.DIRT)
                            .instrument(NoteBlockInstrument.BASS)
                            .strength(2.0F, 6.0F)
                            .sound(SoundType.WOOD)
                            .ignitedByLava()
                            .requiresCorrectToolForDrops()

            )
    );
    public static final DeferredBlock<Block> FAKE_NETHER_PORTAL = BLOCKS.register("fake_nether_portal", () -> new FakeNetherPortal(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .randomTicks()
                            .strength(-1.0F)
                            .sound(SoundType.GLASS)
                            .lightLevel(p_50870_ -> 11)
                            .pushReaction(PushReaction.BLOCK)
            )
    );

    public static final DeferredBlock<Block> MORNING_GLORY = BLOCKS.register("morning_glory", () -> new FlowerBlock(
                    MobEffects.LUCK, 5
                    , BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION)
            )
    );
    public static final DeferredBlock<Block> POT_MORNING_GLORY = BLOCKS.register("pot_morning_glory", () -> new FlowerPotBlock(
            () -> ((FlowerPotBlock) Blocks.FLOWER_POT), MORNING_GLORY, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM))
    );
    public static final DeferredBlock<Block> COMPOSITION_WORKBENCH = BLOCKS.register("composition_workbench", () -> new CompositionWorkbenchBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE))
    );


    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final Supplier<EntityType<FriendlyVex>> FRIENDLY_VEX = ENTITIES.register("friendly_vex", () ->
            EntityType.Builder.<FriendlyVex>of(FriendlyVex::new, MobCategory.CREATURE)
                    .fireImmune()
                    .sized(0.4F, 0.8F)
                    .eyeHeight(0.51875F)
                    .passengerAttachments(0.7375F)
                    .ridingOffset(0.04F)
                    .clientTrackingRange(8)
                    .build("friendly_vex")
    );

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<SoundCollectionItem> SOUND_COLLECTION_ITEM = ITEMS.registerItem("sound_collection", SoundCollectionItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<ContinueSoundCollectionItem> CONTINUE_SOUND_COLLECTION_ITEM = ITEMS.registerItem("continue_sound_collection", ContinueSoundCollectionItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<CollectionItem> COLLECTION_ITEM = ITEMS.registerItem("collection", CollectionItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<RecordBook> RECORD_BOOK = ITEMS.registerItem("record_book", RecordBook::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> COLLECTION_DISPLAY_ITEM = ITEMS.registerItem("collection_display", Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<ContinueSoundContainerItem> CONTINUE_SOUND_CONTAINER_ITEM = ITEMS.registerItem("continue_sound_container", ContinueSoundContainerItem::new, new Item.Properties().rarity(Rarity.RARE).stacksTo(1));
    public static final DeferredItem<BlockItem> SOUND_PLAYER_ITEM = ITEMS.registerSimpleBlockItem("sound_player_block", SOUND_PLAYER_BLOCK);
    public static final DeferredItem<BlockItem> MORNING_GLORY_ITEM = ITEMS.registerSimpleBlockItem("morning_glory", MORNING_GLORY);
    public static final DeferredItem<SoundContainerItem> SOUND_CONTAINER_ITEM = ITEMS.registerItem("sound_container", SoundContainerItem::new, new Item.Properties().rarity(Rarity.RARE).stacksTo(1));
    public static final DeferredItem<BlockItem> COMPOSITION_WORKBENCH_ITEM = ITEMS.registerSimpleBlockItem("composition_workbench", COMPOSITION_WORKBENCH);


    public static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(Registries.MENU, MODID);
    public static final Supplier<MenuType<WandMenu>> WAND_MENU = MENU_TYPE.register("wand_menu", () -> new MenuType(WandMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<ChestConatiner>> CHEST_ROW1 = MENU_TYPE.register("chest_row1", () -> new MenuType(ChestConatiner::oneRow, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<ChestConatiner>> CHEST_ROW2 = MENU_TYPE.register("chest_row2", () -> new MenuType(ChestConatiner::twoRows, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<ChestConatiner>> CHEST_ROW3 = MENU_TYPE.register("chest_row3", () -> new MenuType(ChestConatiner::threeRows, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<ChestConatiner>> CHEST_ROW4 = MENU_TYPE.register("chest_row4", () -> new MenuType(ChestConatiner::fourRows, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<ChestConatiner>> CHEST_ROW5 = MENU_TYPE.register("chest_row5", () -> new MenuType(ChestConatiner::fiveRows, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<ChestConatiner>> CHEST_ROW6 = MENU_TYPE.register("chest_row6", () -> new MenuType(ChestConatiner::sixRows, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final Supplier<BlockEntityType<SoundPlayerBlockEntity>> SOUND_PLAYER_BLOCK_ENTITY = BLOCK_ENTITY.register("sound_player_block_entity", () -> BlockEntityType.Builder.of(SoundPlayerBlockEntity::new, SOUND_PLAYER_BLOCK.get()).build(null));
    public static final Supplier<BlockEntityType<FakeNetherPortalBlockEntity>> FAKE_NETHER_PORTAL_BLOCK_ENTITY = BLOCK_ENTITY.register("fake_nether_portal_block_entity", () -> BlockEntityType.Builder.of(FakeNetherPortalBlockEntity::new, FAKE_NETHER_PORTAL.get()).build(null));
    public static final Supplier<BlockEntityType<CompositionWorkbenchBlockEntity>> COMPOSITION_WORKBENCH_BLOCK_ENTITY = BLOCK_ENTITY.register("composition_workbench_block_entity", () -> BlockEntityType.Builder.of(CompositionWorkbenchBlockEntity::new, COMPOSITION_WORKBENCH.get()).build(null));

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
                output.accept(CONTINUE_SOUND_COLLECTION_ITEM.get());
                output.accept(COLLECTION_ITEM.get());

                output.accept(COLLECTION_DISPLAY_ITEM.get());
                output.accept(SOUND_CONTAINER_ITEM.get());
                output.accept(CONTINUE_SOUND_CONTAINER_ITEM.get());
                output.accept(SOUND_PLAYER_ITEM.get());
                output.accept(RECORD_BOOK.get());
                output.accept(MORNING_GLORY_ITEM.get());
                output.accept(COMPOSITION_WORKBENCH_ITEM.get());
            }).build());


    public MelodyMagic(IEventBus modEventBus, ModContainer modContainer) {
        Init init = new Init();
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        MENU_TYPE.register(modEventBus);
        BLOCK_ENTITY.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        TRIGGER_TYPES.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        init.init();



    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        event.enqueueWork(() -> {
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(MORNING_GLORY.getId(), POT_MORNING_GLORY);
        });
        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        //Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
/*    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        // Do something when the server starts
        Native.free(Pointer.nativeValue(bigArr.getPointer()));
        Native.free(Pointer.nativeValue(size.getPointer()));
        Native.free(Pointer.nativeValue(smallArrSize.getPointer()));
        Native.free(Pointer.nativeValue(flag.getPointer()));

    }*/
/*
    @SubscribeEvent
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        if( event.getEntity().level().isClientSide){
            // Do something when the server starts
            getPcmExecutor.close();
            fftExecutor.close();
            Native.free(Pointer.nativeValue(bigArr.getPointer()));
            Native.free(Pointer.nativeValue(size.getPointer()));
            Native.free(Pointer.nativeValue(smallArrSize.getPointer()));
            Native.free(Pointer.nativeValue(flag.getPointer()));
        }



    }
*/


    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
/*        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            System.out.println("star get pcm");
            getPcmExecutor.submit(MelodyMagic::getPcm);

            fftExecutor.submit(MelodyMagic::fftPcm);
        }*/

        @SubscribeEvent
        public static void loadSoundEngine(SoundEngineLoadEvent event) {
            LOGGER.info("Add sound listener");
            event.getEngine().addEventListener(SelectHud.getInstance());
        }

        @SubscribeEvent
        public static void registerGuiLayersEvent(RegisterGuiLayersEvent event) {
            LOGGER.info("Register hud");
            event.registerAboveAll(fromNamespaceAndPath(MODID, "select_hud"), SelectHud.getInstance());
            event.registerAboveAll(fromNamespaceAndPath(MODID, "record_hud"), RecordHud.getInstance());

            event.registerAboveAll(fromNamespaceAndPath(MODID, "dance_hud"), DanceHud.getInstance());
        }
    }

/*    public interface Audio2 extends Library {
        //Audio2 INSTANCE = Native.load("audioDllX6410OUT100", Audio2.class);
        //Audio2 INSTANCE = Native.load("audioDllX64OUT100AndSleep100", Audio2.class);
        //Audio2 INSTANCE = Native.load("audioDllX640Sleep100", Audio2.class);
        //Audio2 INSTANCE = Native.load("audioDllOutTime", Audio2.class);
        //Audio2 INSTANCE = Native.load("audioDllX645", Audio2.class);
        Audio2 INSTANCE = Native.load("audioDllX6410WithOut", Audio2.class);
        //Audio2 INSTANCE = Native.load("audioDllX6410HM", Audio2.class);
        int add(int a,int b);
        void getIntArr(PointerByReference bigArr, IntByReference size, IntByReference smallArrSize, WinDef.BOOLByReference smallArr);
        //void cleanIntArr(Pointer arr);
    }*/


/*
    public static void fftPcm(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while(true){
            while (doFFT){
                //获取指针数组对应指针指向的数组是否有值，没有值则等待数组
                //System.out.print("fft");
                int currentArrSize=0;
                try {
                    currentArrSize = smallArrSize.getPointer().getInt(currentSize*intSize);
                } catch (java.lang.Error e) {
                    continue;
                }
                //System.out.println("currentArrSize:"+currentArrSize);
                if (currentArrSize > 0) {

                    //有值则从bigArr中获取对应指针数组的指针
                    Pointer smallArrPointer = bigArr.getPointer().getPointer(currentSize*pointerSize);

                    float max =1;
                    pcmData[currentSize] = new float[currentArrSize];
                    for (int i = 0; i < currentArrSize; i++) {
                        pcmData[currentSize][i] = smallArrPointer.getFloat(i*intSize);
                        max=Math.max(max,Math.abs(pcmData[currentSize][i]));
                        //System.out.println(smallArrPointer.getFloat(i*intSize) + " i " + i);
                    }
                    //归一化
                    for (int i = 0; i < currentArrSize; i++) {
                        pcmData[currentSize][i] = pcmData[currentSize][i]/max;
                    }
                    FloatFFT_1D fft = new FloatFFT_1D(arraySize);
                    fft.realForward(pcmData[currentSize]);
                    //读完值后且傅里叶变换完成
                    //System.out.println("fft over");
                    pcmDataCanRender[currentSize] = true;
                    //读取完后重新设置为0
                    smallArrSize.getPointer().setInt(currentSize*intSize, 0);
                    currentSize++;
                    //循环读取
                    if (currentSize >= arraySize) currentSize = currentSize - (int) arraySize;
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }*/

/*

    public static void getPcm(){
        long intSize = Native.getNativeSize(int.class);
        long pointerSize = Native.getNativeSize(Pointer.class);
        try (Memory bigArrMemory = new Memory(arraySize * pointerSize)) {
            bigArr.setPointer(bigArrMemory);
            try (Memory smallArrSizeMemory = new Memory(arraySize * intSize)) {
                smallArrSize.setPointer(smallArrSizeMemory);
                for(int m=0;m<arraySize;m++) {
                    smallArrSize.getPointer().setInt(m,0);
                }
                try (Memory sizeMemory = new Memory(intSize)) {
                    size.setPointer(sizeMemory);
                    size.setValue((int) arraySize);
                    try (Memory flagMemory = new Memory( intSize)) {
                        flag.setPointer(flagMemory);
                        flag.setValue(new WinDef.BOOL(true));
                        //开始读取pcm
                        doFFT=true;
                        long startTime = System.nanoTime();

                        MelodyMagic.Audio2.INSTANCE.getIntArr(bigArr, size,smallArrSize,flag);

                        long endTime = System.nanoTime();
                        long duration = endTime - startTime;
                        System.out.println("time :" + duration + "nm");

                    } catch (Exception e) {
                        System.out.print("FlagMemory Error");
                    }
                } catch (Exception e) {
                    System.out.print("SizeMemory Error");
                }
            } catch (Exception e) {
                System.out.print("SmallArrSizeMemory Error");
            }
        } catch (Exception e) {
            System.out.print("Arr Error");
        }
    }*/

}
