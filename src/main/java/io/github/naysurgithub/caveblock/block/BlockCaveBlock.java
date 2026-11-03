package io.github.naysurgithub.caveblock.block;

import org.jetbrains.annotations.NotNull;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.customblock.CustomBlock;
import org.powernukkitx.block.customblock.CustomBlockDefinition;
import org.powernukkitx.block.customblock.data.Geometry;
import org.powernukkitx.block.customblock.data.Materials;
import org.powernukkitx.item.customitem.data.CreativeCategory;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * A block you see through, along with the rock behind it.
 *
 * <p>The model is two cubes in the same spot: cube 0 is invisible, cube 1 is the visible frame.
 * The block declares a full-cube culling shape, so blocks that cull with voxel shapes drop the
 * faces they share with it; since stone never draws the faces between two stones, looking into
 * the block shows straight through the rock into the caves behind. Both cubes have culling
 * rules, so the frame is only drawn on faces open to the air: two Cave Blocks side by side show
 * no frame between them and can be looked through one after the other.
 */
public class BlockCaveBlock extends Block implements CustomBlock {

    public static final String ID = "caveblock:cave_block";
    public static final BlockProperties PROPERTIES = new BlockProperties(ID);

    private static volatile CustomBlockDefinition definition;

    public BlockCaveBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCaveBlock(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Cave Block";
    }

    @Override
    public CustomBlockDefinition getDefinition() {
        CustomBlockDefinition local = definition;
        if (local == null) {
            local = CustomBlockDefinition.builder(this)
                    .name("Cave Block")
                    .materials(Materials.builder().any(Materials.RenderMethod.BLEND, "cave_block"))
                    .geometry(new Geometry("geometry.caveblock.cave_block")
                            .culling("caveblock:culling.cave_block")
                            .cullingShape("minecraft:unit_cube"))
                    .destructibleByMining((float) (getHardness() * 1.5))
                    .lightDampening(0)
                    .creativeCategory(CreativeCategory.CONSTRUCTION)
                    .build();
            // PNX's material builder only knows "*" and the six faces: add the named "clear"
            // instance used by the invisible cube by hand.
            CompoundTag materials = local.nbt().getCompound("components")
                    .getCompound("minecraft:material_instances").getCompound("materials");
            CompoundTag clear = materials.getCompound("*").copy();
            clear.putString("texture", "cave_block_clear");
            clear.putString("render_method", "alpha_test");
            materials.putCompound("clear", clear);
            local.nbt().getCompound("components").putCompound("minecraft:item_visual", itemVisual(materials));
            definition = local;
        }
        return local;
    }

    /**
     * How the block looks as an item (dropped, held, in the inventory): a plain cube with the frame
     * texture. The placed model draws its frame blended, which the item renderer leaves invisible.
     */
    private static CompoundTag itemVisual(CompoundTag blockMaterials) {
        CompoundTag frame = blockMaterials.getCompound("*").copy();
        frame.putString("render_method", "alpha_test");
        CompoundTag geometry = new CompoundTag()
                .putString("identifier", "minecraft:geometry.full_block")
                .putByte("legacyBlockLightAbsorption", 0)
                .putByte("legacyTopRotation", 0);
        CompoundTag instances = new CompoundTag()
                .putCompound("mappings", new CompoundTag())
                .putCompound("materials", new CompoundTag().putCompound("*", frame));
        return new CompoundTag()
                .putCompound("geometryDescription", geometry)
                .putCompound("materialInstancesDescription", instances);
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 0.5;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }
}
