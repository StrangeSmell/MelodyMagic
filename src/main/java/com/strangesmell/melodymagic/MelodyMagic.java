package com.strangesmell.melodymagic;

import com.strangesmell.melodymagic.api.SoundEffect;
import com.strangesmell.melodymagic.block.SoundPlayerBlock;
import com.strangesmell.melodymagic.block.SoundPlayerBlockEntity;
import com.strangesmell.melodymagic.container.WandMenu;
import com.strangesmell.melodymagic.hud.SelectHud;
import com.strangesmell.melodymagic.item.CollectionItem;
import com.strangesmell.melodymagic.item.SoundContainerItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.sound.SoundEngineLoadEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

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
    public static Map<String, SoundEffect> KEY2EFFECT= new HashMap<>();;

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredBlock<Block> SOUND_PLAYER_BLOCK = BLOCKS.register("sound_player_block", ()-> new SoundPlayerBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    //.instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F, 6.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava())
    );



    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<CollectionItem> COLLECTION_ITEM = ITEMS.registerItem("collection", CollectionItem::new ,new Item.Properties());
    public static final DeferredItem<Item> COLLECTION_DISPLAY_ITEM = ITEMS.registerItem("collection_display", Item::new ,new Item.Properties());
    public static final DeferredItem<SoundContainerItem> SOUND_CONTAINER_ITEM = ITEMS.registerItem("sound_container", SoundContainerItem::new ,new Item.Properties().rarity(Rarity.RARE));
    public static final DeferredItem<BlockItem> SOUND_PLAYER_ITEM = ITEMS.registerSimpleBlockItem("sound_player_block", SOUND_PLAYER_BLOCK);


    public static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(Registries.MENU,MODID);
    public static final Supplier<MenuType<WandMenu>> WAND_MENU = MENU_TYPE.register("wand_menu", () -> new MenuType(WandMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,MODID);
    public static final Supplier<BlockEntityType<SoundPlayerBlockEntity>> SOUND_PLAYER_BLOCK_ENTITY = BLOCK_ENTITY.register("sound_player_block_entity", () -> BlockEntityType.Builder.of(SoundPlayerBlockEntity::new, SOUND_PLAYER_BLOCK.get()).build(null));


    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.melodymagic")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> COLLECTION_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(COLLECTION_ITEM.get());
                output.accept(SOUND_CONTAINER_ITEM.get());
                output.accept(SOUND_PLAYER_ITEM.get());
            }).build());


    public MelodyMagic(IEventBus modEventBus, ModContainer modContainer)
    {

        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        MENU_TYPE.register(modEventBus);
        BLOCK_ENTITY.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        init2Map();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

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
            event.getEngine().addEventListener(SelectHud.getInstance());
            // Some client setup code
            LOGGER.info("Add sound listener");
        }
        @SubscribeEvent
        public static void registerGuiLayersEvent(RegisterGuiLayersEvent event)
        {
            event.registerAboveAll(fromNamespaceAndPath(MODID,"select_hud"), SelectHud.getInstance());

            LOGGER.info("Register hud");
        }

    }

    private void init2Map(){
        SOUND2KEY.put(new HashSet<>(List.of(SoundEvents.COW_AMBIENT.getLocation().toString())),"nine_cow");
        SOUND2KEY.put(new HashSet<>(List.of(SoundEvents.WATER_AMBIENT.getLocation().toString())),"water_breath");
        //SOUND2KEY.put(new HashSet<>(List.of(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation().toString())),"lightning_bolt_thunder");

        SOUND_LIST.add(new HashSet<>(List.of(SoundEvents.COW_AMBIENT.getLocation().toString())));
        SOUND_LIST.add(new HashSet<>(List.of(SoundEvents.WATER_AMBIENT.getLocation().toString())));
        //SOUND_LIST.add(new HashSet<>(List.of(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation().toString())));

        CompoundTag compoundTag =new CompoundTag();
        compoundTag.putInt(SoundEvents.COW_AMBIENT.getLocation()+"num",9);
        CONDITION.put("nine_cow",compoundTag);//数量
        //水下呼吸没有数量条件

        KEY2EFFECT.put("nine_cow", new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1));
            }
            @Override
            public String text(Player player, Level level, InteractionHand pUsedHand, CollectionItem collectionItem) {
                return "nine cow";
            }
        });


        KEY2EFFECT.put("water_breath", new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 1));
            }
            @Override
            public String text(Player player, Level level, InteractionHand pUsedHand, CollectionItem collectionItem) {
                return "water breathing";
            }
        });
    }
}
