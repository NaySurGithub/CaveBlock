package io.github.naysurgithub.caveblock;

import io.github.naysurgithub.caveblock.block.BlockCaveBlock;
import org.cloudburstmc.protocol.bedrock.data.payload.experiment.ExperimentToggle;
import org.powernukkitx.plugin.PluginBase;
import org.powernukkitx.registry.RegisterException;
import org.powernukkitx.registry.Registries;

import java.util.List;

public final class CaveBlockPlugin extends PluginBase {

    /**
     * The client only reads a custom block's culling shape with the "Voxel Shapes" experiment on.
     * This is the name the 26.x client gives it in the StartGame experiment list.
     */
    private static final String VOXEL_SHAPES = "voxel_shapes";

    @Override
    public void onLoad() {
        enableVoxelShapes();
        try {
            Registries.BLOCK.registerCustomBlock(this, BlockCaveBlock.class);
        } catch (RegisterException e) {
            getLogger().error("Failed to register the cave block", e);
        }
    }

    /** Adds the experiment to the list the server sends at login, unless pnx.yml already has it. */
    private void enableVoxelShapes() {
        List<ExperimentToggle> experiments = getServer().getExperiments();
        for (ExperimentToggle experiment : experiments) {
            if (experiment.getName().equals(VOXEL_SHAPES) && experiment.isEnabled()) {
                return;
            }
        }
        experiments.removeIf(experiment -> experiment.getName().equals(VOXEL_SHAPES));
        experiments.add(new ExperimentToggle(VOXEL_SHAPES, true));
    }
}
