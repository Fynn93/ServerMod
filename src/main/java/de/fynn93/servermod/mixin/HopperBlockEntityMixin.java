package de.fynn93.servermod.mixin;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.apache.commons.io.FilenameUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Mixin(value = HopperBlockEntity.class, priority = 949)
public class HopperBlockEntityMixin {
    @Unique
    private static String getItemName(String translationKey) {
        if (translationKey == null) return null;

        String[] names = translationKey.split("\\.");
        return names[names.length - 1];
    }

    @Unique
    private static boolean filterMatch(String filterString, String fullItemName) {
        String itemName = getItemName(fullItemName);
        String[] filter = filterString.split(",");
        return Arrays.stream(filter).anyMatch((filter_i) -> {
            if (filter_i.startsWith("$")) {
                return tagMatch(itemName, filter_i.substring(1));
            } else if (filter_i.startsWith("!")) {
                return !FilenameUtils.wildcardMatch(itemName, filter_i.substring(1));
            }
            return FilenameUtils.wildcardMatch(itemName, filter_i);
        });
    }

    @Unique
    private static boolean tagMatch(String itemName, String filterI) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(itemName));

        List<Field> fields = Arrays.stream(ItemTags.class.getFields()).toList();
        for (Field field : fields) {
            String name = field.getName();
            String filter = filterI.toUpperCase();

            if (!name.equals(filter)) continue;
            try {
                TagKey<Item> tag = (TagKey<Item>) field.get(null);
                return new ItemStack(item).is(tag);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        /* 1.21.3
        Holder<Item> item = null;
        try {
             item = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(itemName)).orElseThrow();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        if (item == null) return false;
        List<TagKey<Item>> list = item.tags().toList();

        for (TagKey<Item> tag : list) {
            if (new ItemStack(item).is(tag)) {
                return true;
            }
        }*/

        return false;
    }

    // pick up items
    @Inject(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/item/ItemEntity;)Z", at = @At("HEAD"), cancellable = true)
    private static void addItem(Container container, ItemEntity itemEntity, CallbackInfoReturnable<Boolean> cir) {
        if (container instanceof HopperBlockEntity hopperBlockEntity) {
            if (hopperBlockEntity.getCustomName() != null) {
                //String itemName = getItemName(itemEntity.getItem().getDescriptionId());
                String itemName = getItemName(itemEntity.getItem().getItem().getDescriptionId());
                if (!filterMatch(hopperBlockEntity.getCustomName().getString(), itemName)) {
                    cir.cancel();
                }
            }
        }
    }

    // transfer items
    @Inject(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/Container;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private static void addItem(Container container, Container container2, ItemStack itemStack, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
        if (container2 instanceof HopperBlockEntity hopperBlockEntity) {
            if (hopperBlockEntity.getCustomName() != null) {
                if (!filterMatch(hopperBlockEntity.getCustomName().getString(), getItemName(itemStack.getItem().getDescriptionId()))) {
                    cir.setReturnValue(itemStack);
                }
            }
        }
    }
}
