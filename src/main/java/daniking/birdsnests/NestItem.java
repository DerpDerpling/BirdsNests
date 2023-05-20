package daniking.birdsnests;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class NestItem extends Item {

    public NestItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        final ItemStack stack = user.getStackInHand(hand);
        if (!user.isCreative()) {
            stack.decrement(1);
        }
        world.playSound(user, user.getBlockPos(), SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        if (!world.isClient()) {
            spawnLoot((ServerWorld) world, user);
            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        } else {
            return super.use(world, user, hand);
        }
    }

    private static void spawnLoot(ServerWorld world, PlayerEntity player) {
        final LootTable table = world.getServer().getLootManager().getLootTable(new Identifier(BirdsNests.MODID, "nest/nest_loot"));
        final List<ItemStack> loot = table.generateLoot(new LootContextParameterSet.Builder(world).build(LootContextType.create().build()));
        if (!loot.isEmpty()) {
            final Random random  = player.getRandom();
            for (final ItemStack entry : loot) {
                final ItemEntity entity = new ItemEntity(world, player.getX(), player.getY() + 1.5D, player.getZ(), entry);
                entity.setVelocity(random.nextGaussian() * 0.05F, 0.2D, random.nextGaussian() * 0.05F);
                world.spawnEntity(entity);
            }
        }
    }
}
