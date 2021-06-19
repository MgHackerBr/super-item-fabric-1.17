package com.kwpugh.super_item;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "super_item")
public class SuperItemConfig extends PartitioningSerializer.GlobalData
{
    public General GENERAL = new General();

    @Config(name = "general")
    public static class General implements ConfigData
    {
        @Comment("\n"
                +"\n"
                +"\n******************************"
                +"\nItem Settings"
                +"\n******************************")
        public boolean enableInvulnerable = true;
        public boolean enableConduitPower = true;
        public boolean enableEnderchest = true;
        public boolean enableTorchPlacing = true;
        public boolean enableHealthRestore = true;
        public boolean enableNightVision = true;
        public boolean enableAbsorptionHearts = true;
    }
}