package slimeknights.tconstruct.library.recipe.gtceu;

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
import slimeknights.tconstruct.library.fluid.GTConstructFluid;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerToolParts;

import java.util.Map;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.EXTRUDER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES;

public class GTRecipes {

  public static void register(Consumer<FinishedRecipe> provider) {
    // 1. 获取所有Tinkers Construct的流体
    Map<ResourceLocation, Fluid> tinkersFluids = GTConstructFluid.getAllTinkersFluids();

    // 2. 遍历每一个流体，为其生成部件制作配方
    for (Map.Entry<ResourceLocation, Fluid> entry : tinkersFluids.entrySet()) {
      ResourceLocation fluidId = entry.getKey();
      Fluid fluid = entry.getValue();

      // 我们只关心可以用来制作工具的熔融金属/合金
      // 通过简单的名称过滤来排除一些非制作材料的流体（如食物、药水等）
      if (!isCraftableFluid(fluidId.getPath())) {
        continue;
      }

      // 3. 为该流体生成所有可能的部件配方
      generateRecipesForFluid(provider, fluid, fluidId);
    }
  }

  /**
   * 判断一个流体是否应该被用于制作部件。
   * 这是一个纯黑名单过滤，除了被排除的，其他所有流体都会生成配方。
   */
  private static boolean isCraftableFluid(String fluidPath) {
    // 黑名单：排除明显不能制作工具的流体
    return !(fluidPath.equals("venom") ||
      fluidPath.equals("honey") ||
      fluidPath.equals("potion") ||
      fluidPath.equals("beetroot_soup") ||
      fluidPath.equals("mushroom_stew") ||
      fluidPath.equals("rabbit_stew") ||
      fluidPath.equals("meat_soup") ||
      fluidPath.equals("powdered_snow"));
  }

  /**
   * 为单个流体生成所有相关配方
   */
  private static void generateRecipesForFluid(Consumer<FinishedRecipe> provider, Fluid fluid, ResourceLocation fluidId) {
    String materialName = GTConstructFluid.extractMaterialName(fluidId.getPath());

    // 通用修复包配方
    generateSolidifierRecipes(fluid, TinkerToolParts.repairKit, 2, TinkerSmeltery.repairKitCast, "repair_kit", provider);

    // 工具部件配方 (为所有流体生成，不做材质判断)
    generateSolidifierRecipes(fluid, TinkerToolParts.pickHead, 2, TinkerSmeltery.pickHeadCast, "pick_head", provider);
    generateSolidifierRecipes(fluid, TinkerToolParts.hammerHead, 8, TinkerSmeltery.hammerHeadCast, "hammer_head", provider);
    generateSolidifierRecipes(fluid, TinkerToolParts.smallAxeHead, 2, TinkerSmeltery.smallAxeHeadCast, "small_axe_head", provider);
    generateSolidifierRecipes(fluid, TinkerToolParts.broadAxeHead, 8, TinkerSmeltery.broadAxeHeadCast, "broad_axe_head", provider);
    generateSolidifierRecipes(fluid, TinkerToolParts.smallBlade, 2, TinkerSmeltery.smallBladeCast, "small_blade", provider);
    generateSolidifierRecipes(fluid, TinkerToolParts.broadBlade, 8, TinkerSmeltery.broadBladeCast, "broad_blade", provider);
    generateSolidifierRecipes(fluid, TinkerToolParts.bowLimb, 2, TinkerSmeltery.bowLimbCast, "bow_limb", provider);
    generateSolidifierRecipes(fluid, TinkerToolParts.bowGrip, 2, TinkerSmeltery.bowGripCast, "bow_grip", provider);
    generateSolidifierRecipes(fluid, TinkerToolParts.toolBinding, 1, TinkerSmeltery.toolBindingCast, "tool_binding", provider);
    generateSolidifierRecipes(fluid, TinkerToolParts.toughBinding, 3, TinkerSmeltery.toughBindingCast, "tough_binding", provider);
    generateSolidifierRecipes(fluid, TinkerToolParts.adzeHead, 2, TinkerSmeltery.adzeHeadCast, "adze_head", provider);
    generateSolidifierRecipes(fluid, TinkerToolParts.largePlate, 4, TinkerSmeltery.largePlateCast, "large_plate", provider);
    generateSolidifierRecipes(fluid, TinkerToolParts.toolHandle, 1, TinkerSmeltery.toolHandleCast, "tool_handle", provider);
    generateSolidifierRecipes(fluid, TinkerToolParts.toughHandle, 3, TinkerSmeltery.toughHandleCast, "tough_handle", provider);

    // 护甲部件配方 (为所有流体生成)
    generateArmorSolidificationRecipes(fluid, TinkerToolParts.plating.get(ArmorItem.Type.HELMET), 3, TinkerSmeltery.helmetPlatingCast, "helmet_plating", provider);
    generateArmorSolidificationRecipes(fluid, TinkerToolParts.plating.get(ArmorItem.Type.CHESTPLATE), 6, TinkerSmeltery.chestplatePlatingCast, "chestplate_plating", provider);
    generateArmorSolidificationRecipes(fluid, TinkerToolParts.plating.get(ArmorItem.Type.LEGGINGS), 5, TinkerSmeltery.leggingsPlatingCast, "leggings_plating", provider);
    generateArmorSolidificationRecipes(fluid, TinkerToolParts.plating.get(ArmorItem.Type.BOOTS), 2, TinkerSmeltery.bootsPlatingCast, "boots_plating", provider);
    generateSolidifierRecipes(fluid, TinkerToolParts.maille, 2, TinkerSmeltery.mailleCast, "maille", provider);
  }

  // --- 以下是配方生成方法，现在只使用流体固化机 ---

  private static void generateSolidifierRecipes(Fluid inputFluid, ItemObject<?> toolPartStack, int materialCost, CastItemObject cast, String path, Consumer<FinishedRecipe> provider) {
    // 关键修改：创建一个 FluidStack 对象
    FluidStack fluidInput = new FluidStack(inputFluid, materialCost * L);

    FLUID_SOLIDFICATION_RECIPES.recipeBuilder(TConstruct.getResource("solidify_" + ForgeRegistries.FLUIDS.getKey(inputFluid).getPath() + "_to_" + path))
      .inputFluids(fluidInput) // 使用 FluidStack
      .notConsumable(cast)
      .outputItems(getToolStack(toolPartStack.asItem(), inputFluid))
      .duration((int) (20 * 1000 * materialCost))
      .EUt(VA[MV])
      .save(provider);
  }

  private static void generateArmorSolidificationRecipes(Fluid inputFluid, ToolPartItem toolPartStack, int materialCost, CastItemObject cast, String path, Consumer<FinishedRecipe> provider) {
    // 关键修改：创建一个 FluidStack 对象
    FluidStack fluidInput = new FluidStack(inputFluid, materialCost * L);

    FLUID_SOLIDFICATION_RECIPES.recipeBuilder(TConstruct.getResource("solidify_" + ForgeRegistries.FLUIDS.getKey(inputFluid).getPath() + "_to_" + path))
      .inputFluids(fluidInput) // 使用 FluidStack
      .notConsumable(cast)
      .outputItems(getToolStack(toolPartStack, inputFluid))
      .duration((int) (20 * 1000 * materialCost))
      .EUt(VA[MV])
      .save(provider);
  }

  // --- getToolStack方法，用于生成带有正确Material NBT的物品 ---

  private static ItemStack getToolStack(net.minecraft.world.item.Item toolPart, Fluid fluid) {
    // 从流体ID推断材料ID，例如 tconstruct:molten_iron -> tconstruct:iron
    ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(fluid);
    String materialName = GTConstructFluid.extractMaterialName(fluidId.getPath());
    MaterialId matId = materialId(materialName);

    ItemStack stack = new ItemStack(toolPart);
    stack.getOrCreateTag().putString("Material", matId.toString());
    return stack;
  }

  private static MaterialId materialId(String location) {
    return new MaterialId(TConstruct.MOD_ID, location);
  }
}
