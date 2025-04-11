package de.fynn93.servermod.dispenser;

import de.fynn93.servermod.ServerMod;
import de.fynn93.servermod.util.Utils;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DispenserBehavior {
    @NotNull
    public static DispenseItemBehavior getDispenserBehavior(FakePlayer player) {
        ServerPlayerGameMode manager = new ServerPlayerGameMode(player);
        return (pointer, stack) -> {
            player.setServerLevel(pointer.level());
            player.setItemInHand(player.getUsedItemHand(), stack);

            Direction direction = pointer.state().getValue(DispenserBlock.FACING);
            BlockPos relative = pointer.pos().relative(direction, 1);
            List<LivingEntity> entities = pointer.level().getEntitiesOfClass(LivingEntity.class, new AABB(relative));
            if ((stack.getItem().getDescriptionId().endsWith("_axe") || stack.getItem().getDescriptionId().endsWith("sword")) && !entities.isEmpty()) {
                for (Entity entity : entities) {
                    int damage = Utils.getDamage(stack);
                    entity.hurt(pointer.level().damageSources().generic(), damage);
                }
            } else if (stack.getItem().getDescriptionId().endsWith("axe") || stack.getItem().getDescriptionId().endsWith("shovel")) {
                BlockState toBreak = pointer.level().getBlockState(relative);

                // Check if the block can be broken with a pickaxe, axe or shovel
                if (toBreak.is(BlockTags.MINEABLE_WITH_PICKAXE) || // Pickaxe
                        toBreak.is(BlockTags.MINEABLE_WITH_AXE) ||     // Axe
                        toBreak.is(BlockTags.MINEABLE_WITH_SHOVEL)) {  // Shovel

                    manager.destroyBlock(relative);

                    // Reset durability
                    if (!ServerMod.usesDurability) stack.setDamageValue(stack.getDamageValue() - 1);
                }
            } else if (stack.getItem() instanceof BlockItem) {
                // Place block on top if the front
                Direction direction2 = pointer.level().isEmptyBlock(relative) ? direction : Direction.UP;

                try {
                    ((BlockItem) stack.getItem()).place(new DirectionalPlaceContext(pointer.level(), relative, direction, stack, direction2));
                } catch (Exception ignored) {
                }
            }
            return stack;
        };
    }
}
