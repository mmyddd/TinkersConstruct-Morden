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
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.Logger;

import static com.gregtechceu.gtceu.api.GTValues.*;

public class GTConstructRecipes {

  private static final Logger LOGGER = Util.getLogger("GTRecipes");

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
    TinkerFluids.searedStone.get(),
    TinkerFluids.scorchedStone.get(),
    TinkerFluids.moltenPigIron.get(),
    TinkerFluids.moltenCinderslime.get()
  );

  public static void register(Consumer<FinishedRecipe> provider) {
    LOGGER.info("Starting recipe generation for Tinkers' Construct fluids...");
    Map<ResourceLocation, Fluid> tinkersFluids = GTConstructFluid.getAllTinkersFluids();
    int totalFluids = tinkersFluids.size();
    int skippedFluids = 0;
    int specialProcessed = 0;
    int standardProcessed = 0;

    registerSpecialRecipes(provider);

    Set<Fluid> processedSpecialFluids = Set.of(
      TinkerFluids.moltenPlatinum.get(),
      TinkerFluids.skySlime.get(),
      TinkerFluids.enderSlime.get(),
      TinkerFluids.earthSlime.get(),
      TinkerFluids.blazingBlood.get()
    );

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

      if (processedSpecialFluids.contains(fluid)) {
        continue;
      }

      boolean isSpecial = SPECIAL_FLUIDS.contains(fluid);
      if (isSpecial) {
        LOGGER.info("Processing SPECIAL fluid with standard solidifier: {}", fluidId);
        specialProcessed++;
      } else {
        LOGGER.info("Processing normal fluid with standard solidifier: {}", fluidId);
        standardProcessed++;
      }

      generateStandardSolidifierRecipes(provider, fluid, isSpecial);
    }

    LOGGER.info("Recipe generation complete. Processed {} fluids ({} special, {} standard) out of {} total. Skipped {}.",
      specialProcessed + standardProcessed, specialProcessed, standardProcessed, totalFluids, skippedFluids);
  }

  private static void registerSpecialRecipes(Consumer<FinishedRecipe> provider) {
    LOGGER.info("Registering hardcoded special/hot fluid recipes...");

    GTConstructRecipeType.builder()
      .inputFluids(TinkerFluids.moltenPlatinum.get())
      .outputMaterial(MaterialIds.platinum)
      .voltage(LuV)
      .inVacuumFreezer()
      .register(provider);

    GTConstructRecipeType.builder()
      .inputFluids(TinkerFluids.skySlime.get())
      .baseMaterial(MaterialIds.wood)
      .outputMaterial(MaterialIds.skySlimeskin)
      .voltage(LV)
      .register(provider);

    GTConstructRecipeType.builder()
      .inputFluids(TinkerFluids.enderSlime.get())
      .baseMaterial(MaterialIds.leather)
      .outputMaterial(MaterialIds.enderSlimeskin)
      .voltage(LV)
      .register(provider);

    GTConstructRecipeType.builder()
      .inputFluids(TinkerFluids.earthSlime.get())
      .baseMaterial(MaterialIds.wood)
      .outputMaterial(MaterialIds.slimewoodComposite)
      .voltage(LV)
      .register(provider);

    GTConstructRecipeType.builder()
      .inputFluids(TinkerFluids.blazingBlood.get())
      .baseMaterial(MaterialIds.necroticBone)
      .outputMaterial(MaterialIds.blazingBone)
      .voltage(LV)
      .register(provider);
  }

  private static void generateStandardSolidifierRecipes(Consumer<FinishedRecipe> provider, Fluid fluid, boolean isSpecial) {
    String materialName = GTConstructFluid.extractMaterialName(ForgeRegistries.FLUIDS.getKey(fluid).getPath());
    MaterialVariantId outputMaterialVariantId = MaterialVariantId.tryParse(TConstruct.MOD_ID + ":" + materialName);

    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.repairKit, 2, TinkerSmeltery.repairKitCast, "repair_kit", isSpecial);
    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.pickHead, 2, TinkerSmeltery.pickHeadCast, "pick_head", isSpecial);
    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.hammerHead, 8, TinkerSmeltery.hammerHeadCast, "hammer_head", isSpecial);
    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.smallAxeHead, 2, TinkerSmeltery.smallAxeHeadCast, "small_axe_head", isSpecial);
    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.broadAxeHead, 8, TinkerSmeltery.broadAxeHeadCast, "broad_axe_head", isSpecial);
    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.smallBlade, 2, TinkerSmeltery.smallBladeCast, "small_blade", isSpecial);
    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.broadBlade, 8, TinkerSmeltery.broadBladeCast, "broad_blade", isSpecial);
    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.bowLimb, 2, TinkerSmeltery.bowLimbCast, "bow_limb", isSpecial);
    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.bowGrip, 2, TinkerSmeltery.bowGripCast, "bow_grip", isSpecial);
    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.toolBinding, 1, TinkerSmeltery.toolBindingCast, "tool_binding", isSpecial);
    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.toughBinding, 3, TinkerSmeltery.toughBindingCast, "tough_binding", isSpecial);
    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.adzeHead, 2, TinkerSmeltery.adzeHeadCast, "adze_head", isSpecial);
    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.largePlate, 4, TinkerSmeltery.largePlateCast, "large_plate", isSpecial);
    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.toolHandle, 1, TinkerSmeltery.toolHandleCast, "tool_handle", isSpecial);
    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.toughHandle, 3, TinkerSmeltery.toughHandleCast, "tough_handle", isSpecial);

    generateArmorSolidificationRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.plating.get(ArmorItem.Type.HELMET), 3, TinkerSmeltery.helmetPlatingCast, "helmet_plating", isSpecial);
    generateArmorSolidificationRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.plating.get(ArmorItem.Type.CHESTPLATE), 6, TinkerSmeltery.chestplatePlatingCast, "chestplate_plating", isSpecial);
    generateArmorSolidificationRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.plating.get(ArmorItem.Type.LEGGINGS), 5, TinkerSmeltery.leggingsPlatingCast, "leggings_plating", isSpecial);
    generateArmorSolidificationRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.plating.get(ArmorItem.Type.BOOTS), 2, TinkerSmeltery.bootsPlatingCast, "boots_plating", isSpecial);
    generateSolidifierRecipes(provider, fluid, outputMaterialVariantId, TinkerToolParts.maille, 2, TinkerSmeltery.mailleCast, "maille", isSpecial);
  }

  private static void generateSolidifierRecipes(Consumer<FinishedRecipe> provider, Fluid inputFluid, MaterialVariantId outputMaterialVariantId, ItemObject<?> toolPartStack, int materialCost, CastItemObject cast, String path, boolean isSpecial) {
    FluidStack fluidInput = new FluidStack(inputFluid, materialCost * L);
    String recipePath = "solidify_" + ForgeRegistries.FLUIDS.getKey(inputFluid).getPath() + "_to_" + path;

    var builder = GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES.recipeBuilder(recipePath)
      .outputItems(getToolStack(toolPartStack.asItem(), outputMaterialVariantId))
      .duration(40 * materialCost)
      .EUt(VA[LV]);

    if (isSpecial) {
      builder.inputFluids(FluidIngredient.of(fluidInput))
        .notConsumable(cast);
    } else {
      builder.inputFluids(fluidInput)
        .notConsumable(cast);
    }

    builder.save(provider);
  }

  private static void generateArmorSolidificationRecipes(Consumer<FinishedRecipe> provider, Fluid inputFluid, MaterialVariantId outputMaterialVariantId, ToolPartItem toolPartStack, int materialCost, CastItemObject cast, String path, boolean isSpecial) {
    FluidStack fluidInput = new FluidStack(inputFluid, materialCost * L);
    String recipePath = "solidify_" + ForgeRegistries.FLUIDS.getKey(inputFluid).getPath() + "_to_" + path;

    var builder = GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES.recipeBuilder(recipePath)
      .outputItems(getToolStack(toolPartStack, outputMaterialVariantId))
      .duration(40 * materialCost)
      .EUt(VA[LV]);

    if (isSpecial) {
      builder.inputFluids(FluidIngredient.of(fluidInput))
        .notConsumable(cast);
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
}
