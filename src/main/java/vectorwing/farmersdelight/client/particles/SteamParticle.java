package vectorwing.farmersdelight.client.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class SteamParticle extends SpriteTexturedParticle
{
	protected SteamParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		super(world, x, y, z);
		this.scale(2.0F);
		this.setSize(0.25F, 0.25F);

		this.lifetime = this.random.nextInt(50) + 80;

		this.gravity = 3.0E-6F;
		this.xd = motionX;
		this.yd = motionY + (double)(this.random.nextFloat() / 500.0F);
		this.zd = motionZ;
	}

	@Override
	@Nonnull
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
			this.xd += this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1);
			this.zd += this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1);
			this.yd -= this.gravity;
			this.move(this.xd, this.yd, this.zd);
			if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
				this.alpha -= 0.02F;
			}
		} else {
			this.remove();
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType>
	{
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite sprite) {
			this.spriteSet = sprite;
		}

		@Override
		public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			SteamParticle particle = new SteamParticle(worldIn, x, y + 0.3D, z, xSpeed, ySpeed, zSpeed);
			particle.setAlpha(0.6F);
			particle.pickSprite(this.spriteSet);
			return particle;
		}
	}
}
