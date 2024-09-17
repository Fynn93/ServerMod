package de.fynn93.servermod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract boolean equals(Object object);

    @Inject(method = "setShiftKeyDown", at = @At("HEAD"))
    public void onShift(boolean bl, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof ServerPlayer serverPlayer)) return;

        ServerLevel serverLevel = serverPlayer.serverLevel();
        BlockPos origin = new BlockPos(serverPlayer.getBlockX(), serverPlayer.getBlockY(), serverPlayer.getBlockZ());
        // iterate through blocks in a 5x2x5 radius
        for (int indexY = 0; indexY < 2; indexY++) {
            for (int indexX = -5; indexX <= 5; indexX++) {
                for (int indexZ = -5; indexZ <= 5; indexZ++) {
                    BlockPos newPos = origin.offset(indexX, indexY, indexZ);
                    BlockState state = serverLevel.getBlockState(newPos);
                    if (state.getBlock() instanceof CropBlock cropBlock) {
                        if (cropBlock.getAge(state) + 1 > cropBlock.getMaxAge()) {
                            continue;
                        }
                        serverLevel.setBlock(newPos, cropBlock.getStateForAge(cropBlock.getAge(state) + 1), 2);
                    } else if (state.getBlock() instanceof SugarCaneBlock) {
                        int age = state.getValue(SugarCaneBlock.AGE);
                        if (age == 15) {
                            serverLevel.setBlockAndUpdate(newPos.above(), state.getBlock().defaultBlockState());
                            serverLevel.setBlock(newPos, state.setValue(SugarCaneBlock.AGE, 0), 4);
                        } else {
                            serverLevel.setBlock(newPos, state.setValue(SugarCaneBlock.AGE, age + 1), 4);
                        }
                    } else if (state.getBlock() instanceof SaplingBlock saplingBlock) {
                        saplingBlock.advanceTree(serverLevel, newPos, state, serverLevel.random);
                    } else if (state.getBlock() instanceof NetherWartBlock) {
                        int age = state.getValue(NetherWartBlock.AGE);
                        if (age < NetherWartBlock.MAX_AGE) {
                            serverLevel.setBlock(newPos, state.setValue(NetherWartBlock.AGE, age + 1), 2);
                        }
                    } else if (state.getBlock() instanceof CactusBlock) {
                        int age = state.getValue(CactusBlock.AGE);
                        if (age == 15) {
                            serverLevel.setBlockAndUpdate(newPos.above(), state.getBlock().defaultBlockState());
                            serverLevel.setBlock(newPos, state.setValue(CactusBlock.AGE, 0), 4);
                            serverLevel.neighborChanged(state.setValue(CactusBlock.AGE, 0), newPos.above(), state.getBlock(), newPos, false);
                        } else {
                            serverLevel.setBlock(newPos, state.setValue(CactusBlock.AGE, age + 1), 4);
                        }
                    }
                }
            }
        }
    }
}
