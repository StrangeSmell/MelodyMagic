package com.strangesmell.melodymagic.block;

import com.strangesmell.melodymagic.MelodyMagic;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class CompositionWorkbenchBlockEntity extends BlockEntity implements Clearable, MenuProvider {
    public static final int DATA_PAGE = 0;
    public static final int NUM_DATA = 1;
    public static final int SLOT_BOOK = 0;
    public static final int NUM_SLOTS = 1;
    private final Container bookAccess = new Container() {
        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return CompositionWorkbenchBlockEntity.this.book.isEmpty();
        }

        @Override
        public ItemStack getItem(int p_59580_) {
            return p_59580_ == 0 ? CompositionWorkbenchBlockEntity.this.book : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int p_59582_, int p_59583_) {
            if (p_59582_ == 0) {
                ItemStack itemstack = CompositionWorkbenchBlockEntity.this.book.split(p_59583_);
                if (CompositionWorkbenchBlockEntity.this.book.isEmpty()) {
                    CompositionWorkbenchBlockEntity.this.onBookItemRemove();
                }

                return itemstack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        @Override
        public ItemStack removeItemNoUpdate(int p_59590_) {
            if (p_59590_ == 0) {
                ItemStack itemstack = CompositionWorkbenchBlockEntity.this.book;
                CompositionWorkbenchBlockEntity.this.book = ItemStack.EMPTY;
                CompositionWorkbenchBlockEntity.this.onBookItemRemove();
                return itemstack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        @Override
        public void setItem(int p_59585_, ItemStack p_59586_) {
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void setChanged() {
            CompositionWorkbenchBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(Player p_59588_) {
            return Container.stillValidBlockEntity(CompositionWorkbenchBlockEntity.this, p_59588_) && CompositionWorkbenchBlockEntity.this.hasBook();
        }

        @Override
        public boolean canPlaceItem(int p_59592_, ItemStack p_59593_) {
            return false;
        }

        @Override
        public void clearContent() {
        }
    };
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int p_59600_) {
            return p_59600_ == 0 ? CompositionWorkbenchBlockEntity.this.page : 0;
        }

        @Override
        public void set(int p_59602_, int p_59603_) {
            if (p_59602_ == 0) {
                CompositionWorkbenchBlockEntity.this.setPage(p_59603_);
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };
    ItemStack book = ItemStack.EMPTY;
    int page;
    private int pageCount;

    public CompositionWorkbenchBlockEntity(BlockPos pos, BlockState blockState) {
        super(MelodyMagic.COMPOSITION_WORKBENCH_BLOCK_ENTITY.get(), pos, blockState);
    }

    public ItemStack getBook() {
        return this.book;
    }

    public boolean hasBook() {
        return this.book.is(Items.WRITABLE_BOOK) || this.book.is(Items.WRITTEN_BOOK);
    }

    /**
     * Sets the ItemStack in this lectern. Note that this does not update the block state, use {@link net.minecraft.world.level.block.LecternBlock#tryPlaceBook} for that.
     */
    public void setBook(ItemStack stack) {
        this.setBook(stack, null);
    }

    void onBookItemRemove() {
        this.page = 0;
        this.pageCount = 0;
        CompositionWorkbenchBlock.resetBookState(null, this.getLevel(), this.getBlockPos(), this.getBlockState(), false);
    }

    public void setBook(ItemStack stack, @Nullable Player player) {
        this.book = this.resolveBook(stack, player);
        this.page = 0;
        this.pageCount = getPageCount(this.book);
        this.setChanged();
    }

    void setPage(int page) {
        int i = Mth.clamp(page, 0, this.pageCount - 1);
        if (i != this.page) {
            this.page = i;
            this.setChanged();
            CompositionWorkbenchBlock.signalPageChange(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public int getPage() {
        return this.page;
    }

    public int getRedstoneSignal() {
        float f = this.pageCount > 1 ? (float)this.getPage() / ((float)this.pageCount - 1.0F) : 1.0F;
        return Mth.floor(f * 14.0F) + (this.hasBook() ? 1 : 0);
    }

    /**
     * Resolves the contents of the passed ItemStack, if it is a book
     */
    private ItemStack resolveBook(ItemStack stack, @Nullable Player player) {
        if (this.level instanceof ServerLevel && stack.is(Items.WRITTEN_BOOK)) {
            WrittenBookItem.resolveBookComponents(stack, this.createCommandSourceStack(player), player);
        }

        return stack;
    }

    private CommandSourceStack createCommandSourceStack(@Nullable Player player) {
        String s;
        Component component;
        if (player == null) {
            s = "Lectern";
            component = Component.literal("Lectern");
        } else {
            s = player.getName().getString();
            component = player.getDisplayName();
        }

        Vec3 vec3 = Vec3.atCenterOf(this.worldPosition);
        return new CommandSourceStack(CommandSource.NULL, vec3, Vec2.ZERO, (ServerLevel)this.level, 2, s, component, this.level.getServer(), player);
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Book", 10)) {
            this.book = this.resolveBook(ItemStack.parse(registries, tag.getCompound("Book")).orElse(ItemStack.EMPTY), null);
        } else {
            this.book = ItemStack.EMPTY;
        }

        this.pageCount = getPageCount(this.book);
        this.page = Mth.clamp(tag.getInt("Page"), 0, this.pageCount - 1);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.getBook().isEmpty()) {
            tag.put("Book", this.getBook().save(registries));
            tag.putInt("Page", this.page);
        }
    }

    @Override
    public void clearContent() {
        this.setBook(ItemStack.EMPTY);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new LecternMenu(containerId, this.bookAccess, this.dataAccess);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.lectern");
    }

    private static int getPageCount(ItemStack stack) {
        WrittenBookContent writtenbookcontent = stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
        if (writtenbookcontent != null) {
            return writtenbookcontent.pages().size();
        } else {
            WritableBookContent writablebookcontent = stack.get(DataComponents.WRITABLE_BOOK_CONTENT);
            return writablebookcontent != null ? writablebookcontent.pages().size() : 0;
        }
    }
}
