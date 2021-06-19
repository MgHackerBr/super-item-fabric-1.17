package com.kwpugh.super_item;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSuperItem extends Item
{
    public static final TranslatableText TITLE = new TranslatableText("item.super_item.super_item.enderchest");

    static boolean enableInvulnerable = SuperItem.CONFIG.GENERAL.enableInvulnerable;
    static boolean enableConduitPower = SuperItem.CONFIG.GENERAL.enableConduitPower;
    static boolean enableEnderchest = SuperItem.CONFIG.GENERAL.enableEnderchest;
    static boolean enableTorchPlacing = SuperItem.CONFIG.GENERAL.enableTorchPlacing;
    static boolean enableHealthRestore = SuperItem.CONFIG.GENERAL.enableHealthRestore;
    static boolean enableNightVision = SuperItem.CONFIG.GENERAL.enableNightVision;
    static boolean enableAbsorptionHearts = SuperItem.CONFIG.GENERAL.enableAbsorptionHearts;

    public ItemSuperItem(Settings settings)
    {
        super(settings);
    }

    // Player is invulnerable and gets Conduit Power while submerged with item in offhand
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        if (!world.isClient)
        {
            if(entity instanceof PlayerEntity)
            {
                PlayerEntity player = (PlayerEntity) entity;

                Boolean inMainhand = player.getMainHandStack().getItem()== SuperItem.SUPER_ITEM;
                Boolean inOffhand = player.getOffHandStack().getItem() == SuperItem.SUPER_ITEM;

                if(enableInvulnerable && inOffhand)
                {
                    player.setInvulnerable(true);
                }
                else
                {
                    player.setInvulnerable(false);
                }

                if(enableConduitPower && player.isSubmergedInWater() && inOffhand)
                {
                    StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 8, 0, false, false);

                    {
                        player.addStatusEffect(effect);
                    }
                }
            }
        }
    }

    // Right-click in mainhand to heal, sneak + right-click in mainhand for Night Vision
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        ItemStack mainHand = player.getStackInHand(hand);

        Boolean inMainhand = player.getMainHandStack().getItem()== SuperItem.SUPER_ITEM;
        Boolean inOffhand = player.getOffHandStack().getItem() == SuperItem.SUPER_ITEM;

        if(!world.isClient && inMainhand)
        {
            if(!player.isSneaking() && enableHealthRestore)
            {
                player.setHealth(20);
                player.getHungerManager().setFoodLevel(20);
                player.sendMessage((new TranslatableText("item.super_item.super_item.status1")), true);
            }

            if(player.isSneaking() && enableNightVision)
            {
                StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.NIGHT_VISION, 3600, 0, false, false);
                {
                    player.addStatusEffect(effect);
                    player.sendMessage((new TranslatableText("item.super_item.super_item.status3")), true);
                }
            }
        }



//        if(enableHealthRestore && !world.isClient && inMainhand && !player.isSneaking())
//        {
//            player.setHealth(20);
//            player.getHungerManager().setFoodLevel(20);
//            player.sendMessage((new TranslatableText("item.super_item.super_item.status1")), true);
//        }
//
//        if(enableHealthRestore && !world.isClient && inMainhand && player.isSneaking())
//        {
//            StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.NIGHT_VISION, 3600, 0, false, false);
//            {
//                player.addStatusEffect(effect);
//                player.sendMessage((new TranslatableText("item.super_item.super_item.status3")), true);
//            }
//        }

        // Offhand right-click opens Enderchest
        EnderChestInventory enderChest = player.getEnderChestInventory();
        if(enableEnderchest && !world.isClient && inOffhand && !player.isSneaking() && enderChest != null)
        {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) -> {
                return GenericContainerScreenHandler.createGeneric9x3(i, playerInventory, enderChest);
            }, TITLE));
        }

        // Offhand sneak + right-click full absorption
        if(!world.isClient && inOffhand && player.isSneaking())
        {
            if(enableHealthRestore)
            {
                player.setHealth(20);
                player.getHungerManager().setFoodLevel(20);

                if(enableAbsorptionHearts)
                {
                    player.setAbsorptionAmount(60);
                }

                player.sendMessage((new TranslatableText("item.super_item.super_item.status2")), true);
            }
        }

        return TypedActionResult.success(mainHand);
    }

    // Place a torch on right click of item on a block
    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        World world = context.getWorld();

        if(enableTorchPlacing && !world.isClient)
        {
            BlockPos torchPos;
            BlockPos pos = context.getBlockPos();
            BlockState state = context.getWorld().getBlockState(pos);

            if(context.getWorld().getBlockState(pos).getBlock() == Blocks.TORCH
                    || context.getWorld().getBlockState(pos).getBlock() == Blocks.WALL_TORCH)
            {
                return ActionResult.FAIL;
            }

            Boolean isWallTorch = false;

            switch(context.getSide())
            {
                case DOWN:
                    return ActionResult.FAIL;
                case UP:
                    torchPos = new BlockPos(pos.getX(), pos.getY() +1, pos.getZ());
                    break;
                case NORTH:
                    torchPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ() -1);
                    isWallTorch = true;
                    break;
                case SOUTH:
                    torchPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ() +1);
                    isWallTorch = true;
                    break;
                case WEST:
                    torchPos = new BlockPos(pos.getX() -1, pos.getY(), pos.getZ());
                    isWallTorch = true;
                    break;
                case EAST:
                    torchPos = new BlockPos(pos.getX() +1, pos.getY(), pos.getZ());
                    isWallTorch = true;
                    break;
                default:
                    return ActionResult.FAIL;
            }

            if(context.getWorld().getBlockState(torchPos).isAir() || context.getWorld().getBlockState(torchPos).getFluidState().isStill())
            {
                if(state.isSolidBlock(world, pos))
                {
                    if(isWallTorch)
                    {
                        context.getWorld().setBlockState(torchPos, Blocks.WALL_TORCH.getDefaultState().with(HorizontalFacingBlock.FACING, context.getSide()));
                        context.getWorld().playSound(null, context.getPlayer().getBlockPos(), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.NEUTRAL, 8.0F, (float) (0.7F + (Math.random()*0.3D)));
                    }
                    else
                    {
                        context.getWorld().setBlockState(torchPos, Blocks.TORCH.getDefaultState());
                        context.getWorld().playSound(null, context.getPlayer().getBlockPos(), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.NEUTRAL, 8.0F, (float) (0.7F + (Math.random()*0.3D)));
                    }
                }

                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        }

        return ActionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        tooltip.add(new TranslatableText("item.super_item.super_item.tip1").formatted(Formatting.YELLOW));
        tooltip.add(new TranslatableText("item.super_item.super_item.tip2").formatted(Formatting.GREEN));
        tooltip.add(new TranslatableText("item.super_item.super_item.tip3").formatted(Formatting.GREEN));
        tooltip.add(new TranslatableText("item.super_item.super_item.tip4").formatted(Formatting.GREEN));
        tooltip.add(new TranslatableText("item.super_item.super_item.tip5").formatted(Formatting.RED));
        tooltip.add(new TranslatableText("item.super_item.super_item.tip6").formatted(Formatting.RED));
        tooltip.add(new TranslatableText("item.super_item.super_item.tip7").formatted(Formatting.BLUE));
    }
}