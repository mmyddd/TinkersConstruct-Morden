package slimeknights.tconstruct.library.materials;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import java.util.Map;

/**
 * 材料配方验证器
 * 负责判断某个材料是否可以用于制作特定的部件，并处理特殊产品的材料替换。
 */
public final class GTMaterialRecipeValidator {

  /**
   * 定义特殊流体应该产出的产品材料（变体ID），而不是默认材料。
   * 例如：天空史莱姆流体 -> 产出天空史莱姆藤皮材料
   */
  private static final Map<Fluid, MaterialVariantId> SPECIAL_FLUID_OUTPUT_MATERIALS = Map.of(
    TinkerFluids.skySlime.get(), MaterialIds.skySlimeskin,
    TinkerFluids.enderSlime.get(), MaterialIds.enderSlimeskin
    // 注意：earthSlime 没有对应的 vine#slimeskin，所以不在此列表中，它会产出默认的 earthslime 材料
  );

  /**
   * 检查给定的材料是否可以制作指定的部件。
   * 这个方法会调用 TCon 内置的、在具体部件类中重写的验证逻辑。
   *
   * @param materialId 要检查的材料ID。
   * @param partItem   要检查的部件物品。
   * @return 如果可以制作，返回 true；否则返回 false。
   */
  public static boolean canCraftPart(MaterialId materialId, Item partItem) {
    // 1. 检查物品是否为 TCon 部件
    if (!(partItem instanceof IMaterialItem materialItem)) {
      return false;
    }
    // 2. 调用 TCon 的内置检查方法
    return materialItem.canUseMaterial(materialId);
  }

  /**
   * 根据流体确定最终产出的材料变体ID。
   * 如果流体是特殊的，则返回特殊材料；否则返回默认材料。
   *
   * @param fluid     输入的流体。
   * @param defaultId 默认的材料ID。
   * @return 最终的产出材料变体ID。
   */
  public static MaterialVariantId resolveOutputMaterial(Fluid fluid, MaterialId defaultId) {
    // 使用 getOrDefault，如果没找到特殊映射，就用默认的
    return SPECIAL_FLUID_OUTPUT_MATERIALS.getOrDefault(fluid, defaultId);
  }
}
