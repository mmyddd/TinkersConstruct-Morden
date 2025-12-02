package slimeknights.tconstruct.library.recipe.gtceu;

import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerToolParts;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;

/**
 * 类似于 GTCEu 的 GTRecipeTypes，提供用于注册 Tinkers' Construct 配方的静态方法。
 * 这是我们自定义的配方注册API入口。
 */
public final class GTConstructRecipeTypes {

  private GTConstructRecipeTypes() {} // 防止实例化

  // *** 核心改动: 新的动态配方构建器入口 ***
  public static final DynamicRecipeBuilder builder() {
    return new DynamicRecipeBuilder();
  }

  /**
   * 一个完全动态的配方构建器，允许自由组合流体、材料和机器类型。
   */
  public static class DynamicRecipeBuilder {
    private Fluid inputFluid;
    private MaterialId baseMaterial;
    private MaterialVariantId outputMaterial;
    private int voltage = LV;
    private int durationMultiplier = 1;
    private boolean useVacuum = false;

    private DynamicRecipeBuilder() {}

    // --- 链式调用的配置方法 ---
    public DynamicRecipeBuilder inputFluids(Fluid fluid) { this.inputFluid = fluid; return this; }
    public DynamicRecipeBuilder baseMaterial(MaterialId material) { this.baseMaterial = material; return this; }
    public DynamicRecipeBuilder outputMaterial(MaterialVariantId material) { this.outputMaterial = material; return this; }
    public DynamicRecipeBuilder voltage(int voltageTier) { this.voltage = voltageTier; if (voltageTier > LV) { this.useVacuum = true; } return this; }
    public DynamicRecipeBuilder duration(int secondsPerIngot) { this.durationMultiplier = secondsPerIngot; return this; }
    public DynamicRecipeBuilder inVacuumFreezer() { this.useVacuum = true; return this; }
    public DynamicRecipeBuilder inSolidifier() { this.useVacuum = false; return this; }

    // --- 注册所有配方的方法 ---
    public void register(Consumer<FinishedRecipe> provider) {
      if (inputFluid == null || outputMaterial == null) { throw new IllegalStateException("InputFluid and OutputMaterial must be set!"); }

      String recipeTypeName = useVacuum ? "vacuum_freeze" : "solidify";

      // --- 注册所有部件的配方 ---
      registerPart(provider, TinkerToolParts.repairKit, 2, TinkerSmeltery.repairKitCast, "repair_kit", recipeTypeName);
      registerPart(provider, TinkerToolParts.pickHead, 2, TinkerSmeltery.pickHeadCast, "pick_head", recipeTypeName);
      registerPart(provider, TinkerToolParts.hammerHead, 8, TinkerSmeltery.hammerHeadCast, "hammer_head", recipeTypeName);
      registerPart(provider, TinkerToolParts.smallAxeHead, 2, TinkerSmeltery.smallAxeHeadCast, "small_axe_head", recipeTypeName);
      registerPart(provider, TinkerToolParts.broadAxeHead, 8, TinkerSmeltery.broadAxeHeadCast, "broad_axe_head", recipeTypeName);
      registerPart(provider, TinkerToolParts.smallBlade, 2, TinkerSmeltery.smallBladeCast, "small_blade", recipeTypeName);
      registerPart(provider, TinkerToolParts.broadBlade, 8, TinkerSmeltery.broadBladeCast, "broad_blade", recipeTypeName);
      registerPart(provider, TinkerToolParts.bowLimb, 2, TinkerSmeltery.bowLimbCast, "bow_limb", recipeTypeName);
      registerPart(provider, TinkerToolParts.bowGrip, 2, TinkerSmeltery.bowGripCast, "bow_grip", recipeTypeName);
      registerPart(provider, TinkerToolParts.toolBinding, 1, TinkerSmeltery.toolBindingCast, "tool_binding", recipeTypeName);
      registerPart(provider, TinkerToolParts.toughBinding, 3, TinkerSmeltery.toughBindingCast, "tough_binding", recipeTypeName);
      registerPart(provider, TinkerToolParts.adzeHead, 2, TinkerSmeltery.adzeHeadCast, "adze_head", recipeTypeName);
      registerPart(provider, TinkerToolParts.largePlate, 4, TinkerSmeltery.largePlateCast, "large_plate", recipeTypeName);
      registerPart(provider, TinkerToolParts.toolHandle, 1, TinkerSmeltery.toolHandleCast, "tool_handle", recipeTypeName);
      registerPart(provider, TinkerToolParts.toughHandle, 3, TinkerSmeltery.toughHandleCast, "tough_handle", recipeTypeName);

      registerArmorPart(provider, TinkerToolParts.plating.get(ArmorItem.Type.HELMET), 3, TinkerSmeltery.helmetPlatingCast, "helmet_plating", recipeTypeName);
      registerArmorPart(provider, TinkerToolParts.plating.get(ArmorItem.Type.CHESTPLATE), 6, TinkerSmeltery.chestplatePlatingCast, "chestplate_plating", recipeTypeName);
      registerArmorPart(provider, TinkerToolParts.plating.get(ArmorItem.Type.LEGGINGS), 5, TinkerSmeltery.leggingsPlatingCast, "leggings_plating", recipeTypeName);
      registerArmorPart(provider, TinkerToolParts.plating.get(ArmorItem.Type.BOOTS), 2, TinkerSmeltery.bootsPlatingCast, "boots_plating", recipeTypeName);
      registerPart(provider, TinkerToolParts.maille, 2, TinkerSmeltery.mailleCast, "maille", recipeTypeName);
    }

    // *** 核心修改：重写方法，根据是否有 baseMaterial 来决定配方类型 ***
    private void registerPart(Consumer<FinishedRecipe> provider, ItemObject<?> toolPartStack, int materialCost, CastItemObject cast, String path, String recipeTypeName) {
      FluidStack fluidInput = new FluidStack(inputFluid, materialCost * L);
      int duration = materialCost * durationMultiplier * 20;

      var recipeType = useVacuum ? GTRecipeTypes.VACUUM_RECIPES : GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES;
      String recipePath = recipeTypeName + "_" + ForgeRegistries.FLUIDS.getKey(inputFluid).getPath() + "_to_" + path;

      // *** 关键逻辑：判断是否为复合配方 ***
      if (baseMaterial != null) {
        // --- 复合配方：流体 + 基底物品 → 输出 ---
        MaterialVariantId baseMaterialVariantId = MaterialVariantId.tryParse(baseMaterial.toString());
        recipeType.recipeBuilder(recipePath)
          .outputItems(getToolStack(toolPartStack.asItem(), outputMaterial))
          .duration(duration)
          .EUt(VA[voltage])
          .inputFluids(FluidIngredient.of(fluidInput))
          .inputItems(getToolStack(toolPartStack.asItem(), baseMaterialVariantId))
          .save(provider);
      } else {
        // --- 固化配方：流体 + (不消耗的)铸型 → 输出 ---
        recipeType.recipeBuilder(recipePath)
          .outputItems(getToolStack(toolPartStack.asItem(), outputMaterial))
          .duration(duration)
          .EUt(VA[voltage])
          .inputFluids(fluidInput)
          .notConsumable(cast)
          .save(provider);
      }
    }

    private void registerArmorPart(Consumer<FinishedRecipe> provider, ToolPartItem toolPartStack, int materialCost, CastItemObject cast, String path, String recipeTypeName) {
      FluidStack fluidInput = new FluidStack(inputFluid, materialCost * L);
      int duration = materialCost * durationMultiplier * 20;

      var recipeType = useVacuum ? GTRecipeTypes.VACUUM_RECIPES : GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES;
      String recipePath = recipeTypeName + "_" + ForgeRegistries.FLUIDS.getKey(inputFluid).getPath() + "_to_" + path;

      if (baseMaterial != null) {
        // --- 复合配方 ---
        MaterialVariantId baseMaterialVariantId = MaterialVariantId.tryParse(baseMaterial.toString());
        recipeType.recipeBuilder(recipePath)
          .outputItems(getToolStack(toolPartStack, outputMaterial))
          .duration(duration)
          .EUt(VA[voltage])
          .inputFluids(FluidIngredient.of(fluidInput))
          .inputItems(getToolStack(toolPartStack, baseMaterialVariantId))
          .save(provider);
      } else {
        // --- 固化配方 ---
        recipeType.recipeBuilder(recipePath)
          .outputItems(getToolStack(toolPartStack, outputMaterial))
          .duration(duration)
          .EUt(VA[voltage])
          .inputFluids(fluidInput)
          .notConsumable(cast)
          .save(provider);
      }
    }
  }

  // --- 辅助方法 ---
  private static ItemStack getToolStack(net.minecraft.world.item.Item toolPart, MaterialVariantId matVariantId) {
    ItemStack stack = new ItemStack(toolPart);
    stack.getOrCreateTag().putString("Material", matVariantId.toString());
    return stack;
  }
}
