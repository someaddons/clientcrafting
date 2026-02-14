package com.clientcrafting.mixin;

import com.clientcrafting.ClientCraftingMod;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(CraftingMenu.class)
public class CraftingMixin
{
    @Unique
    private static List<ItemStack> lastItems;
    @Unique
    private static long            lastTickCount = 0;
    @Unique
    private static Optional<CraftingRecipe> lastRecipe = Optional.empty();

    @Shadow
    @Final
    @Mutable
    private ContainerLevelAccess access;

    @Shadow
    @Final
    private Player player;


    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("RETURN"))
    private void onInitContainerAccess(final int p_39356_, final Inventory p_39357_, final ContainerLevelAccess containerLevelAccess, final CallbackInfo ci)
    {
        if (containerLevelAccess == ContainerLevelAccess.NULL)
        {
            this.access = ContainerLevelAccess.create(player.level(), player.blockPosition());
        }
    }

    @Inject(method = "slotChangedCraftingGrid", at = @At("RETURN"))
    private static void showClientRecipe(
      final AbstractContainerMenu menu,
      final Level level,
      final Player player,
      final CraftingContainer container,
      final ResultContainer resultContainer, final CallbackInfo ci)
    {
        if (level.isClientSide())
        {
            if (lastTickCount != level.getGameTime())
            {
                lastTickCount = level.getGameTime();
                lastItems = container.getItems();
                lastRecipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container, level);
            }
            else
            {
                boolean matches = true;
                List<ItemStack> current = container.getItems();
                if (current.size() == lastItems.size())
                {
                    for (int i = 0; i < current.size(); i++)
                    {
                        if (!ItemStack.isSameItemSameTags(current.get(i), lastItems.get(i)))
                        {
                            matches = false;
                        }
                    }
                }
                else
                {
                    matches = false;
                }

                if (!matches)
                {
                    lastRecipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container, level);
                }
            }

            if (lastRecipe.isPresent())
            {
                CraftingRecipe craftingrecipe = lastRecipe.get();
                if (setRecipeUsedClientCheck(level, (LocalPlayer) player, craftingrecipe))
                {
                    final ItemStack itemstack = craftingrecipe.assemble(container, level.registryAccess());
                    resultContainer.setItem(0, itemstack);
                    menu.setRemoteSlot(0, itemstack);
                }
            }
            else
            {
                resultContainer.setItem(0, ItemStack.EMPTY);
                menu.setRemoteSlot(0, ItemStack.EMPTY);
            }
        }
    }

    @Unique
    private static boolean setRecipeUsedClientCheck(final Level level, final LocalPlayer player, final CraftingRecipe craftingrecipe)
    {
        if (!craftingrecipe.isSpecial() && level.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING) && !player.getRecipeBook().contains(craftingrecipe))
        {
            return false;
        }
        return true;
    }
}
