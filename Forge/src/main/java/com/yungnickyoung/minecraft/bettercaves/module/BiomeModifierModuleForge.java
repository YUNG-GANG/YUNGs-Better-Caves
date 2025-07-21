package com.yungnickyoung.minecraft.bettercaves.module;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.biomemodifier.AddCarversBiomeModifierForge;
import com.yungnickyoung.minecraft.bettercaves.biomemodifier.RemoveCarversBiomeModifierForge;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BiomeModifierModuleForge {
    private static final DeferredRegister<Codec<? extends BiomeModifier>> REGISTER = DeferredRegister.create(
            ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,
            BetterCavesCommon.MOD_ID);

    public static RegistryObject<Codec<AddCarversBiomeModifierForge>> ADD_CARVERS = REGISTER.register(
            "add_carvers",
            () -> AddCarversBiomeModifierForge.CODEC);

    public static RegistryObject<Codec<RemoveCarversBiomeModifierForge>> REMOVE_CARVERS = REGISTER.register(
            "remove_carvers",
            () -> RemoveCarversBiomeModifierForge.CODEC);

    public static void init() {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
