package slimeknights.tconstruct.library.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.utils.Util; // 【新增】导入Util

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Logger; // 【新增】导入Logger

/**
 * 自动扫描Tinkers' Construct流体并建立映射的工具类。
 */
public class GTConstructFluid {

  private static final Map<ResourceLocation, Fluid> ALL_TCON_FLUIDS = new HashMap<>();
  private static final Logger LOGGER = Util.getLogger("GTConstructFluid"); // 【新增】创建Logger实例

  static {
    initializeFluidMappings();
  }

  /**
   * 使用反射自动初始化所有Tinkers流体的映射关系。
   */
  private static void initializeFluidMappings() {
    LOGGER.info("Initializing Tinkers' Construct fluid mappings via reflection...");
    int foundCount = 0;
    try {
      Field[] fields = TinkerFluids.class.getDeclaredFields();
      for (Field field : fields) {
        int modifiers = field.getModifiers();
        if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
          Class<?> fieldType = field.getType();
          if (FluidObject.class.isAssignableFrom(fieldType) || FlowingFluidObject.class.isAssignableFrom(fieldType)) {
            Object fluidObjectInstance = field.get(null);
            if (fluidObjectInstance != null) {
              Fluid fluid = ((FluidObject<?>) fluidObjectInstance).get();
              if (fluid != null) {
                ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(fluid);
                if (fluidId != null) {
                  ALL_TCON_FLUIDS.put(fluidId, fluid);
                  foundCount++;
                }
              }
            }
          }
        }
      }
    } catch (IllegalAccessException e) {
      LOGGER.error("Failed to initialize fluid mappings via reflection!", e);
    }
    LOGGER.info("Successfully initialized {} Tinkers' Construct fluid mappings.", foundCount);
  }

  /**
   * 获取所有扫描到的Tinkers Construct流体。
   * @return 包含所有流体的Map
   */
  public static Map<ResourceLocation, Fluid> getAllTinkersFluids() {
    return Map.copyOf(ALL_TCON_FLUIDS);
  }

  /**
   * 从流体路径中提取材料名，用于生成MaterialId。
   * 例如: "molten_iron" -> "iron", "earth_slime" -> "earth_slime"
   */
  public static String extractMaterialName(String path) {
    if (path.startsWith("molten_")) {
      return path.substring("molten_".length());
    }
    // 对于史莱姆和其他特殊流体，保留全名
    return path;
  }
}
