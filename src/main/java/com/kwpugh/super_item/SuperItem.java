package com.kwpugh.super_item;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SuperItem implements ModInitializer
{
    public static final String MOD_ID = "super_item";
    public static final SuperItemConfig CONFIG = AutoConfig.register(SuperItemConfig.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new)).getConfig();
    public static final Item SUPER_ITEM = new ItemSuperItem(new Item.Settings().group(ItemGroup.MISC));

    @Override
    public void onInitialize()
    {
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "super_item"), SUPER_ITEM);
    }
}