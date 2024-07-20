package com.clientcrafting.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CraftingMenu.class)
public class CraftingMixin
{
    @Unique
    private static boolean queued = false;

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
      final ResultContainer resultContainer, @Nullable RecipeHolder<CraftingRecipe> recipeHolder, final CallbackInfo ci)
    {
        if (level.isClientSide())
        {
            if (!queued)
            {
                queued = true;
                Minecraft.getInstance().submit(() ->
                {
                    ItemStack itemStack = ItemStack.EMPTY;
                    Optional<RecipeHolder<CraftingRecipe>> optional = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container.asCraftInput(), level, recipeHolder);
                    if (optional.isPresent())
                    {
                        RecipeHolder<CraftingRecipe> craftingrecipe = optional.get();
                        if (setRecipeUsedClientCheck(level, (LocalPlayer) player, craftingrecipe, resultContainer))
                        {
                            final ItemStack resultItem = craftingrecipe.value().assemble(container.asCraftInput(), level.registryAccess());
                            if (resultItem.isItemEnabled(level.enabledFeatures()))
                            {
                                itemStack = resultItem;
                            }
                        }
                    }

                    resultContainer.setItem(0, itemStack);
                    menu.setRemoteSlot(0, itemStack);

                    queued = false;
                });
            }
        }
    }

    @Unique
    private static boolean setRecipeUsedClientCheck(
      final Level level,
      final LocalPlayer player,
      final RecipeHolder<CraftingRecipe> craftingrecipe,
      final ResultContainer resultContainer)
    {
        if (!craftingrecipe.value().isSpecial() && level.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING) && !player.getRecipeBook().contains(craftingrecipe))
        {
            return false;
        }
        resultContainer.setRecipeUsed(craftingrecipe);
        return true;
    }
}
