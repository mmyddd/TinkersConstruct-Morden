package slimeknights.tconstruct.library.data.tinkering;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/** Data generator for mappings from enchantments to modifiers */
public abstract class AbstractEnchantmentToModifierProvider extends GenericDataProvider {
  /** Compiled JSON to save, no need to do anything fancier, it already does merging for us */
  private final JsonObject enchantmentMap = new JsonObject();

  public AbstractEnchantmentToModifierProvider(PackOutput packOutput) {
    super(packOutput, Target.DATA_PACK, "tinkering");
  }

  /** Add any mappings */
  protected abstract void addEnchantmentMappings();

  @Override
  public CompletableFuture<?> run(CachedOutput pCache) {
    enchantmentMap.entrySet().clear();
    addEnchantmentMappings();
    return saveJson(pCache, TConstruct.getResource("enchantments_to_modifiers"), enchantmentMap);
  }

  /* Helpers */

  /** Helper to append the ? for optional modifiers */
  private static String optionalId(ResourceLocation modifierId, boolean optional) {
    return optional ? modifierId.toString() + '?' : modifierId.toString();
  }

  /** Adds the given enchantment */
  protected void add(Enchantment enchantment, ModifierId modifierId) {
    add(enchantment, modifierId, false);
  }

  /** Adds the given enchantment, allowing making the modifier optional */
  protected void add(Enchantment enchantment, ModifierId modifierId, boolean optionalModifier) {
    String key = Objects.requireNonNull(BuiltInRegistries.ENCHANTMENT.getKey(enchantment)).toString();
    if (enchantmentMap.has(key) || enchantmentMap.has(key + '?')) {
      throw new IllegalArgumentException("Duplicate enchantment " + key);
    }
    enchantmentMap.addProperty(key, optionalId(modifierId, optionalModifier));
  }

  /** Adds the given optional enchantment, ignoring errors if missing */
  protected void addOptional(ResourceLocation enchantment, ModifierId modifierId, boolean optionalModifier) {
    String key = enchantment.toString();
    if (enchantmentMap.has(key) || enchantmentMap.has(key + '?')) {
      throw new IllegalArgumentException("Duplicate enchantment " + key);
    }
    enchantmentMap.addProperty(key + '?', optionalId(modifierId, optionalModifier));
  }

  /** Adds the given enchantment tag */
  protected void add(TagKey<Enchantment> tag, ModifierId modifierId) {
    add(tag, modifierId, false);
  }

  /** Adds the given enchantment tag, allowing making the modifier optional */
  protected void add(TagKey<Enchantment> tag, ModifierId modifierId, boolean optionalModifier) {
    String key = "#" + tag.location();
    if (enchantmentMap.has(key)) {
      throw new IllegalArgumentException("Duplicate enchantment tag " + tag.location());
    }
    enchantmentMap.addProperty(key, optionalId(modifierId, optionalModifier));
  }

  /** Adds the given enchantment tag */
  protected void add(ResourceLocation tag, ModifierId modifierId) {
    add(tag, modifierId, false);
  }

  /** Adds the given enchantment tag, allowing making the modifier optional */
  protected void add(ResourceLocation tag, ModifierId modifierId, boolean optionalModifier) {
    add(TagKey.create(Registries.ENCHANTMENT, tag), modifierId, optionalModifier);
  }
}
