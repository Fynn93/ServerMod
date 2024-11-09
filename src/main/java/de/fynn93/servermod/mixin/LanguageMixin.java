package de.fynn93.servermod.mixin;

import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.locale.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.function.BiConsumer;

@Mixin(Language.class)
public class LanguageMixin {
    @Inject(method = "parseTranslations", at = @At("HEAD"), cancellable = true)
    private static void parseTranslations(BiConsumer<String, String> biConsumer, String string, CallbackInfo ci) {
        ModContainer container = FabricLoader.getInstance().getModContainer("servermod").orElse(null);
        if (container == null) {
            LogUtils.getLogger().error("Couldn't find mod container for servermod");
            ci.cancel();
            return;
        }
        container.findPath("de_de.json").ifPresent(path -> {
            try (InputStream inputStream = Files.newInputStream(path)) {
                Language.loadFromJson(inputStream, biConsumer);
            } catch (JsonParseException | IOException exception) {
                LogUtils.getLogger().error("Couldn't read strings from {}", path, exception);
            }
        });
        ci.cancel();
    }
}
