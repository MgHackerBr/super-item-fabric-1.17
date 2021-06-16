package com.kwpugh.super_item;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SuperItem implements ModInitializer {

    public static final String MOD_ID = "super_item";
    public static final Item SUPER_ITEM = new ItemSuperItem(new Item.Settings().group(ItemGroup.MISC));

    @Override
    public void onInitialize() {

        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "super_item"), SUPER_ITEM);
    }
}