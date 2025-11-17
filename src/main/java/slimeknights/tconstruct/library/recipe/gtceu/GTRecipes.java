package slimeknights.tconstruct.library.recipe.gtceu;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerToolParts;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.gem;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.ingot;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.EXTRUDER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES;

public class GTRecipes {

  public static void register(Consumer<FinishedRecipe> provider) {
    for(Material material : GTMaterial.getRegisteredMaterials()) {
      if (material == null) {
        continue;
      }

      MaterialEntry inputMaterial;
      if(material.hasProperty(PropertyKey.GEM)) {
        inputMaterial = new MaterialEntry(gem, material);
      } else {
        inputMaterial = new MaterialEntry(ingot, material);
      }

        generateExtruderRecipes(inputMaterial, TinkerToolParts.repairKit, 2, TinkerSmeltery.repairKitCast, "repair_kit", provider);

        if(material.hasProperty(PropertyKey.TOOL) && material.hasProperty(PropertyKey.FLUID)) {
          generateExtruderRecipes(inputMaterial, TinkerToolParts.pickHead, 2, TinkerSmeltery.pickHeadCast, "pick_head", provider);
          generateExtruderRecipes(inputMaterial, TinkerToolParts.hammerHead, 8, TinkerSmeltery.hammerHeadCast, "hammer_head", provider);
          generateExtruderRecipes(inputMaterial, TinkerToolParts.smallAxeHead, 2, TinkerSmeltery.smallAxeHeadCast, "small_axe_head", provider);
          generateExtruderRecipes(inputMaterial, TinkerToolParts.broadAxeHead, 8, TinkerSmeltery.broadAxeHeadCast, "broad_axe_head", provider);
          generateExtruderRecipes(inputMaterial, TinkerToolParts.smallBlade, 2, TinkerSmeltery.smallBladeCast, "small_blade", provider);
          generateExtruderRecipes(inputMaterial, TinkerToolParts.broadBlade, 8, TinkerSmeltery.broadBladeCast, "broad_blade", provider);
          generateExtruderRecipes(inputMaterial, TinkerToolParts.bowLimb, 2, TinkerSmeltery.bowLimbCast, "bow_limb", provider);
          generateExtruderRecipes(inputMaterial, TinkerToolParts.bowGrip, 2, TinkerSmeltery.bowGripCast, "bow_grip", provider);

          generateExtruderRecipes(inputMaterial, TinkerToolParts.toolBinding, 1, TinkerSmeltery.toolBindingCast, "tool_binding", provider);
          generateExtruderRecipes(inputMaterial, TinkerToolParts.toughBinding, 3, TinkerSmeltery.toughBindingCast, "tough_binding", provider);
          generateExtruderRecipes(inputMaterial, TinkerToolParts.adzeHead, 2, TinkerSmeltery.adzeHeadCast, "adze_head", provider);
          generateExtruderRecipes(inputMaterial, TinkerToolParts.largePlate, 4, TinkerSmeltery.largePlateCast, "large_plate", provider);
          generateExtruderRecipes(inputMaterial, TinkerToolParts.toolHandle, 1, TinkerSmeltery.toolHandleCast, "tool_handle", provider);
          generateExtruderRecipes(inputMaterial, TinkerToolParts.toughHandle, 3, TinkerSmeltery.toughHandleCast, "tough_handle", provider);
        }

        if(material.hasProperty(PropertyKey.ARMOR) && material.hasProperty(PropertyKey.FLUID)) {
          generateArmorExtruderRecipes(inputMaterial, TinkerToolParts.plating.get(Type.HELMET), 3, TinkerSmeltery.helmetPlatingCast, "helmet_plating", provider);
          generateArmorExtruderRecipes(inputMaterial, TinkerToolParts.plating.get(Type.CHESTPLATE), 6, TinkerSmeltery.chestplatePlatingCast, "chestplate_plating", provider);
          generateArmorExtruderRecipes(inputMaterial, TinkerToolParts.plating.get(Type.LEGGINGS), 5, TinkerSmeltery.leggingsPlatingCast, "leggings_plating", provider);
          generateArmorExtruderRecipes(inputMaterial, TinkerToolParts.plating.get(Type.BOOTS), 2, TinkerSmeltery.bootsPlatingCast, "boots_plating", provider);

          generateExtruderRecipes(inputMaterial, TinkerToolParts.maille, 2, TinkerSmeltery.mailleCast, "maille", provider);
        }


        generateSolidifierRecipes(inputMaterial, TinkerToolParts.repairKit, 2, TinkerSmeltery.repairKitCast, "repair_kit", provider);

        if(inputMaterial.material().hasProperty(PropertyKey.TOOL) && material.hasProperty(PropertyKey.FLUID)) {
          generateSolidifierRecipes(inputMaterial, TinkerToolParts.pickHead, 2, TinkerSmeltery.pickHeadCast, "pick_head", provider);
          generateSolidifierRecipes(inputMaterial, TinkerToolParts.hammerHead, 8, TinkerSmeltery.hammerHeadCast, "hammer_head", provider);
          generateSolidifierRecipes(inputMaterial, TinkerToolParts.smallAxeHead, 2, TinkerSmeltery.smallAxeHeadCast, "small_axe_head", provider);
          generateSolidifierRecipes(inputMaterial, TinkerToolParts.broadAxeHead, 8, TinkerSmeltery.broadAxeHeadCast, "broad_axe_head", provider);
          generateSolidifierRecipes(inputMaterial, TinkerToolParts.smallBlade, 2, TinkerSmeltery.smallBladeCast, "small_blade", provider);
          generateSolidifierRecipes(inputMaterial, TinkerToolParts.broadBlade, 8, TinkerSmeltery.broadBladeCast, "broad_blade", provider);
          generateSolidifierRecipes(inputMaterial, TinkerToolParts.bowLimb, 2, TinkerSmeltery.bowLimbCast, "bow_limb", provider);
          generateSolidifierRecipes(inputMaterial, TinkerToolParts.bowGrip, 2, TinkerSmeltery.bowGripCast, "bow_grip", provider);

          generateSolidifierRecipes(inputMaterial, TinkerToolParts.toolBinding, 1, TinkerSmeltery.toolBindingCast, "tool_binding", provider);
          generateSolidifierRecipes(inputMaterial, TinkerToolParts.toughBinding, 3, TinkerSmeltery.toughBindingCast, "tough_binding", provider);
          generateSolidifierRecipes(inputMaterial, TinkerToolParts.adzeHead, 2, TinkerSmeltery.adzeHeadCast, "adze_head", provider);
          generateSolidifierRecipes(inputMaterial, TinkerToolParts.largePlate, 4, TinkerSmeltery.largePlateCast, "large_plate", provider);
          generateSolidifierRecipes(inputMaterial, TinkerToolParts.toolHandle, 1, TinkerSmeltery.toolHandleCast, "tool_handle", provider);
          generateSolidifierRecipes(inputMaterial, TinkerToolParts.toughHandle, 3, TinkerSmeltery.toughHandleCast, "tough_handle", provider);
        }
        if(inputMaterial.material().hasProperty(PropertyKey.ARMOR) && material.hasProperty(PropertyKey.FLUID)) {
          generateArmorSolidificationRecipes(inputMaterial, TinkerToolParts.plating.get(Type.HELMET), 3, TinkerSmeltery.helmetPlatingCast, "helmet_plating", provider);
          generateArmorSolidificationRecipes(inputMaterial, TinkerToolParts.plating.get(Type.CHESTPLATE), 6, TinkerSmeltery.chestplatePlatingCast, "chestplate_plating", provider);
          generateArmorSolidificationRecipes(inputMaterial, TinkerToolParts.plating.get(Type.LEGGINGS), 5, TinkerSmeltery.leggingsPlatingCast, "leggings_plating", provider);
          generateArmorSolidificationRecipes(inputMaterial, TinkerToolParts.plating.get(Type.BOOTS), 2, TinkerSmeltery.bootsPlatingCast, "boots_plating", provider);

          generateSolidifierRecipes(inputMaterial, TinkerToolParts.maille, 2, TinkerSmeltery.mailleCast, "maille", provider);
        }

    }
  }

  private static void generateExtruderRecipes(MaterialEntry inputMaterial, ItemObject<?> toolPartStack, int materialCost, CastItemObject cast, String path, Consumer<FinishedRecipe> provider) {
    if (!inputMaterial.material().hasProperty(PropertyKey.FLUID)) {
      return;
    }
    EXTRUDER_RECIPES.recipeBuilder(TConstruct.getResource("extrude_" + inputMaterial.material().getName() + "_to_" + path))
      .inputItems(inputMaterial, materialCost)
      .notConsumable(cast)
      .outputItems(getToolStack(toolPartStack.asItem(), inputMaterial.material()))
      .duration((int) (20 * inputMaterial.material().getMass() * materialCost))
      .EUt(VA[MV])
      .save(provider);
  }

  private static void generateArmorExtruderRecipes(MaterialEntry inputMaterial, ToolPartItem toolPartStack, int materialCost, CastItemObject cast, String path, Consumer<FinishedRecipe> provider) {
    if (!inputMaterial.material().hasProperty(PropertyKey.FLUID)) {
      return;
    }
    EXTRUDER_RECIPES.recipeBuilder(TConstruct.getResource("extrude_" + inputMaterial.material().getName() + "_to_" + path))
      .inputItems(inputMaterial, materialCost)
      .notConsumable(cast)
      .outputItems(getToolStack(toolPartStack.asItem(), inputMaterial.material()))
      .duration((int) (20 * inputMaterial.material().getMass() * materialCost))
      .EUt(VA[MV])
      .save(provider);
  }

  private static void generateSolidifierRecipes(MaterialEntry inputMaterial, ItemObject<?> toolPartStack, int materialCost, CastItemObject cast, String path, Consumer<FinishedRecipe> provider) {
    if (!inputMaterial.material().hasProperty(PropertyKey.FLUID)) {
      return;
    }
    FLUID_SOLIDFICATION_RECIPES.recipeBuilder(TConstruct.getResource("solidify_" + inputMaterial.material().getName() + "_to_" + path))
      .inputFluids(inputMaterial.material().getFluid(materialCost * L))
      .notConsumable(cast)
      .outputItems(getToolStack(toolPartStack.asItem(), inputMaterial.material()))
      .duration((int) (20 * inputMaterial.material().getMass() * materialCost))
      .EUt(VA[MV])
      .save(provider);
  }

  private static void generateArmorSolidificationRecipes(MaterialEntry inputMaterial, ToolPartItem toolPartStack, int materialCost, CastItemObject cast, String path, Consumer<FinishedRecipe> provider) {
    if (!inputMaterial.material().hasProperty(PropertyKey.FLUID)) {
      return;
    }
    FLUID_SOLIDFICATION_RECIPES.recipeBuilder(TConstruct.getResource("solidify_" + inputMaterial.material().getName() + "_to_" + path))
      .inputFluids(inputMaterial.material().getFluid(materialCost * L))
      .notConsumable(cast)
      .outputItems(getToolStack(toolPartStack, inputMaterial.material()))
      .duration((int) (20 * inputMaterial.material().getMass() * materialCost))
      .EUt(VA[MV])
      .save(provider);
  }

  private static ItemStack getToolStack(Item toolPart, Material material) {
    ItemStack stack = new ItemStack(toolPart);
    stack.getOrCreateTag().putString("Material", materialId(material.getName()).toString());
    return stack;
  }

  private static MaterialId materialId(String location) {
    return new MaterialId(TConstruct.MOD_ID, location);
  }
}
