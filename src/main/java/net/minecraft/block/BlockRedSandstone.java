package net.minecraft.block;

import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import java.util.List;

public class BlockRedSandstone extends Block {
    public static final PropertyEnum<BlockRedSandstone.EnumType> TYPE = PropertyEnum.create("type", BlockRedSandstone.EnumType.class);

    public BlockRedSandstone() {
        super(Material.rock, BlockSand.EnumType.RED_SAND.getMapColor());
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockRedSandstone.EnumType.DEFAULT));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).getMetadata();
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (BlockRedSandstone.EnumType blockredsandstone$enumtype : BlockRedSandstone.EnumType.values()) {
            list.add(new ItemStack(itemIn, 1, blockredsandstone$enumtype.getMetadata()));
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, BlockRedSandstone.EnumType.byMetadata(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMetadata();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, TYPE);
    }

    public enum EnumType implements IStringSerializable {
        DEFAULT(0, "red_sandstone", "default"),
        CHISELED(1, "chiseled_red_sandstone", "chiseled"),
        SMOOTH(2, "smooth_red_sandstone", "smooth");

        private static final BlockRedSandstone.EnumType[] META_LOOKUP = new BlockRedSandstone.EnumType[values().length];

        static {
            for (BlockRedSandstone.EnumType blockredsandstone$enumtype : values()) {
                META_LOOKUP[blockredsandstone$enumtype.getMetadata()] = blockredsandstone$enumtype;
            }
        }

        private final int meta;
        @Getter
        private final String name;
        @Getter
        private final String unlocalizedName;

        EnumType(int meta, String name, String unlocalizedName) {
            this.meta = meta;
            this.name = name;
            this.unlocalizedName = unlocalizedName;
        }

        public static BlockRedSandstone.EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public int getMetadata() {
            return this.meta;
        }

        public String toString() {
            return this.name;
        }

    }
}
