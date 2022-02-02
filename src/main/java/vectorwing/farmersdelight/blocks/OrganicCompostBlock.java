package vectorwing.farmersdelight.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vectorwing.farmersdelight.registry.ModBlocks;
import vectorwing.farmersdelight.utils.tags.ModTags;

import java.util.Random;

import net.minecraft.block.AbstractBlock.Properties;

@SuppressWarnings("deprecation")
public class OrganicCompostBlock extends Block
{
	public static IntegerProperty COMPOSTING = IntegerProperty.create("composting", 0, 7);

	public OrganicCompostBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(super.defaultBlockState().setValue(COMPOSTING, 0));
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(COMPOSTING);
		super.createBlockStateDefinition(builder);
	}

	public int getMaxCompostingStage() {
		return 7;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		if (worldIn.isClientSide) return;

		float chance = 0F;
		boolean hasWater = false;
		int maxLight = 0;

		for (BlockPos neighborPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
			BlockState neighborState = worldIn.getBlockState(neighborPos);
			if (neighborState.is(ModTags.COMPOST_ACTIVATORS)) {
				chance += 0.02F;
			}
			if (neighborState.getFluidState().is(FluidTags.WATER)) {
				hasWater = true;
			}
			int light = worldIn.getBrightness(LightType.SKY, neighborPos.above());
			if (light > maxLight) {
				maxLight = light;
			}
		}

		chance += maxLight > 12 ? 0.1F : 0.05F;
		chance += hasWater ? 0.1F : 0.0F;

		if (worldIn.getRandom().nextFloat() <= chance) {
			if (state.getValue(COMPOSTING) == this.getMaxCompostingStage())
				worldIn.setBlock(pos, ModBlocks.RICH_SOIL.get().defaultBlockState(), 2); // finished
			else
				worldIn.setBlock(pos, state.setValue(COMPOSTING, state.getValue(COMPOSTING) + 1), 2); // next stage
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
		return (getMaxCompostingStage() + 1 - blockState.getValue(COMPOSTING));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		super.animateTick(stateIn, worldIn, pos, rand);
		if (rand.nextInt(10) == 0) {
			worldIn.addParticle(ParticleTypes.MYCELIUM, (double) pos.getX() + (double) rand.nextFloat(), (double) pos.getY() + 1.1D, (double) pos.getZ() + (double) rand.nextFloat(), 0.0D, 0.0D, 0.0D);
		}
	}
}
