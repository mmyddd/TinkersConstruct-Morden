package slimeknights.tconstruct.library.recipe.gtceu;

import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.fluid.GTConstructFluid;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.Logger;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES;

public class GTConstructRecipes {

  private static final Logger LOGGER = Util.getLogger("GTRecipes");

  // 黑名单保持不变
  private static final Set<String> FLUID_BLACKLIST = Set.of(
    "venom", "honey", "potion", "beetroot_soup", "mushroom_stew", "rabbit_stew", "meat_soup", "powdered_snow",
    "knightslime", "ichor", "magma","signalum","duralumin","refined_glowstone",
    "quartz", "debris", "clay", "glass", "liquid_soul", "obsidian", "ender",
    "emerald", "amethyst", "diamond", "nichrome", "soulsteel","refined_obsidian",
    "netherite", "tin", "zinc", "nickel", "brass", "porcelain", "uranium", "enderium", "lumium"
  );

  private static final Set<Fluid> SPECIAL_FLUIDS = Set.of(
    TinkerFluids.moltenSlimesteel.get(),
    TinkerFluids.moltenQueensSlime.get(),
    TinkerFluids.skySlime.get(),
    TinkerFluids.enderSlime.get(),
    TinkerFluids.earthSlime.get(),
    TinkerFluids.searedStone.get(),
    TinkerFluids.scorchedStone.get(),
    TinkerFluids.moltenPigIron.get(),
    TinkerFluids.moltenCinderslime.get(),
    TinkerFluids.blazingBlood.get()
  );

  private static final Map<Fluid, MaterialId> SPECIAL_FLUID_BASE_MATERIALS = Map.of(
    TinkerFluids.skySlime.get(), MaterialIds.wood,
    TinkerFluids.enderSlime.get(), MaterialIds.leather,
    TinkerFluids.earthSlime.get(), MaterialIds.wood,
    TinkerFluids.blazingBlood.get(), MaterialIds.necroticBone
  );

  private static final Map<Fluid, MaterialVariantId> SPECIAL_FLUID_OUTPUT_MATERIALS = Map.of(
    TinkerFluids.skySlime.get(), MaterialIds.skySlimeskin,
    TinkerFluids.enderSlime.get(), MaterialIds.enderSlimeskin,
    TinkerFluids.earthSlime.get(), MaterialIds.slimewoodComposite,
    TinkerFluids.blazingBlood.get(), MaterialIds.blazingBone
  );

  public static void register(Consumer<FinishedRecipe> provider) {
    LOGGER.info("Starting recipe generation for Tinkers' Construct fluids...");
    Map<ResourceLocation, Fluid> tinkersFluids = GTConstructFluid.getAllTinkersFluids();
    int totalFluids = tinkersFluids.size();
    int processedFluids = 0;
    int skippedFluids = 0;

    for (Map.Entry<ResourceLocation, Fluid> entry : tinkersFluids.entrySet()) {
      ResourceLocation fluidId = entry.getKey();
      String fluidPath = fluidId.getPath();
      Fluid fluid = entry.getValue();

      String materialName = GTConstructFluid.extractMaterialName(fluidPath);

      if (FLUID_BLACKLIST.contains(materialName)) {
        LOGGER.debug("Skipping blacklisted fluid: {} (material: {})", fluidId, materialName);
        skippedFluids++;
        continue;
      }

      LOGGER.info("Processing craftable fluid: {}", fluidId);
      processedFluids++;

      boolean isSpecial = SPECIAL_FLUIDS.contains(fluid);
      generateRecipesForFluid(provider, fluid, fluidId, isSpecial);
    }

    LOGGER.info("Recipe generation complete. Processed {} fluids out of {} total. Skipped {}.", processedFluids, totalFluids, skippedFluids);
  }

  private static void generateRecipesForFluid(Consumer<FinishedRecipe> provider, Fluid fluid, ResourceLocation fluidId, boolean isSpecial) {
    String materialName = GTConstructFluid.extractMaterialName(fluidId.getPath());
    MaterialId matId = materialId(materialName);
    MaterialVariantId defaultMatVariantId = MaterialVariantId.tryParse(TConstruct.MOD_ID + ":" + materialName);

    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.repairKit, 2, TinkerSmeltery.repairKitCast, "repair_kit", provider, isSpecial);
    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.pickHead, 2, TinkerSmeltery.pickHeadCast, "pick_head", provider, isSpecial);
    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.hammerHead, 8, TinkerSmeltery.hammerHeadCast, "hammer_head", provider, isSpecial);
    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.smallAxeHead, 2, TinkerSmeltery.smallAxeHeadCast, "small_axe_head", provider, isSpecial);
    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.broadAxeHead, 8, TinkerSmeltery.broadAxeHeadCast, "broad_axe_head", provider, isSpecial);
    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.smallBlade, 2, TinkerSmeltery.smallBladeCast, "small_blade", provider, isSpecial);
    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.broadBlade, 8, TinkerSmeltery.broadBladeCast, "broad_blade", provider, isSpecial);
    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.bowLimb, 2, TinkerSmeltery.bowLimbCast, "bow_limb", provider, isSpecial);
    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.bowGrip, 2, TinkerSmeltery.bowGripCast, "bow_grip", provider, isSpecial);
    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.toolBinding, 1, TinkerSmeltery.toolBindingCast, "tool_binding", provider, isSpecial);
    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.toughBinding, 3, TinkerSmeltery.toughBindingCast, "tough_binding", provider, isSpecial);
    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.adzeHead, 2, TinkerSmeltery.adzeHeadCast, "adze_head", provider, isSpecial);
    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.largePlate, 4, TinkerSmeltery.largePlateCast, "large_plate", provider, isSpecial);
    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.toolHandle, 1, TinkerSmeltery.toolHandleCast, "tool_handle", provider, isSpecial);
    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.toughHandle, 3, TinkerSmeltery.toughHandleCast, "tough_handle", provider, isSpecial);

    generateArmorSolidificationRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.plating.get(ArmorItem.Type.HELMET), 3, TinkerSmeltery.helmetPlatingCast, "helmet_plating", provider, isSpecial);
    generateArmorSolidificationRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.plating.get(ArmorItem.Type.CHESTPLATE), 6, TinkerSmeltery.chestplatePlatingCast, "chestplate_plating", provider, isSpecial);
    generateArmorSolidificationRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.plating.get(ArmorItem.Type.LEGGINGS), 5, TinkerSmeltery.leggingsPlatingCast, "leggings_plating", provider, isSpecial);
    generateArmorSolidificationRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.plating.get(ArmorItem.Type.BOOTS), 2, TinkerSmeltery.bootsPlatingCast, "boots_plating", provider, isSpecial);
    generateSolidifierRecipes(fluid, matId, defaultMatVariantId, TinkerToolParts.maille, 2, TinkerSmeltery.mailleCast, "maille", provider, isSpecial);
  }


  private static void generateSolidifierRecipes(Fluid inputFluid, MaterialId matId, MaterialVariantId defaultMatVariantId, ItemObject<?> toolPartStack, int materialCost, CastItemObject cast, String path, Consumer<FinishedRecipe> provider, boolean isSpecial) {
    FluidStack fluidInput = new FluidStack(inputFluid, materialCost * L);

    MaterialVariantId outputMaterialVariantId;
    if (isSpecial) {
      outputMaterialVariantId = SPECIAL_FLUID_OUTPUT_MATERIALS.getOrDefault(inputFluid, defaultMatVariantId);
    } else {
      outputMaterialVariantId = defaultMatVariantId;
    }

    var builder = FLUID_SOLIDFICATION_RECIPES.recipeBuilder(TConstruct.getResource("solidify_" + ForgeRegistries.FLUIDS.getKey(inputFluid).getPath() + "_to_" + path))
      .outputItems(getToolStack(toolPartStack.asItem(), outputMaterialVariantId))
      .duration((int) (40 * materialCost))
      .EUt(VA[LV]);

    if (isSpecial) {
      MaterialId baseMaterial = SPECIAL_FLUID_BASE_MATERIALS.get(inputFluid);
      if (baseMaterial != null) {
        MaterialVariantId baseMaterialVariantId = MaterialVariantId.tryParse(baseMaterial.toString());
        builder.inputFluids(FluidIngredient.of(fluidInput))
          .inputItems(getToolStack(toolPartStack.asItem(), baseMaterialVariantId));
      } else {
        builder.inputFluids(FluidIngredient.of(fluidInput))
          .notConsumable(cast);
      }
    } else {
      builder.inputFluids(fluidInput)
        .notConsumable(cast);
    }

    builder.save(provider);
  }

  private static void generateArmorSolidificationRecipes(Fluid inputFluid, MaterialId matId, MaterialVariantId defaultMatVariantId, ToolPartItem toolPartStack, int materialCost, CastItemObject cast, String path, Consumer<FinishedRecipe> provider, boolean isSpecial) {
    FluidStack fluidInput = new FluidStack(inputFluid, materialCost * L);

    MaterialVariantId outputMaterialVariantId;
    if (isSpecial) {
      outputMaterialVariantId = SPECIAL_FLUID_OUTPUT_MATERIALS.getOrDefault(inputFluid, defaultMatVariantId);
    } else {
      outputMaterialVariantId = defaultMatVariantId;
    }

    var builder = FLUID_SOLIDFICATION_RECIPES.recipeBuilder(TConstruct.getResource("solidify_" + ForgeRegistries.FLUIDS.getKey(inputFluid).getPath() + "_to_" + path))
      .outputItems(getToolStack(toolPartStack, outputMaterialVariantId))
      .duration((int) (40 * materialCost))
      .EUt(VA[LV]);

    if (isSpecial) {
      MaterialId baseMaterial = SPECIAL_FLUID_BASE_MATERIALS.get(inputFluid);
      if (baseMaterial != null) {
        MaterialVariantId baseMaterialVariantId = MaterialVariantId.tryParse(baseMaterial.toString());
        builder.inputFluids(FluidIngredient.of(fluidInput))
          .inputItems(getToolStack(toolPartStack, baseMaterialVariantId));
      } else {
        builder.inputFluids(FluidIngredient.of(fluidInput))
          .notConsumable(cast);
      }
    } else {
      builder.inputFluids(fluidInput)
        .notConsumable(cast);
    }

    builder.save(provider);
  }

  private static ItemStack getToolStack(net.minecraft.world.item.Item toolPart, MaterialVariantId matVariantId) {
    ItemStack stack = new ItemStack(toolPart);
    stack.getOrCreateTag().putString("Material", matVariantId.toString());
    return stack;
  }

  private static MaterialId materialId(String location) {
    return new MaterialId(TConstruct.MOD_ID, location);
  }
}
