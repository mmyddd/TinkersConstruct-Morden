package slimeknights.tconstruct.library.data.recipe;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ItemExistsCondition;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.recipe.condition.TagCombinationCondition;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.mantle.recipe.data.ItemNameIngredient;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static slimeknights.mantle.Mantle.commonResource;
import static slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe.getTemperature;

/** Helper for building melting and casting recipes for a fluid */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
@CanIgnoreReturnValue
public class SmelteryRecipeBuilder {
  /** Consumer for recipe results */
  private final Consumer<FinishedRecipe> consumer;
  /** Resource name, domain is location for results and name is tag root */
  private final ResourceLocation name;
  /** Fluid object to generate results and ingredients, takes top priority */
  @Nullable
  private final FluidObject<?> fluidObject;
  /** Fluid to generate results and ingredients. If set along tag, tag is priorized for ingredients. */
  @Nullable
  private final Fluid fluid;
  /** Fluid tag to generate results and ingredients. If set along fluid, fluid is prioritized for results */
  @Nullable
  private final TagKey<Fluid> fluidTag;
  /** Temperature for recipes */
  @Setter
  private int temperature;
  /** If true, all recipes are optional. If false only unsupported types are optional */
  @Setter
  private boolean optional = false;
  /** If true, ore recipes should be added */
  private boolean hasOre = false;
  /** List of byproducts for these recipes */
  private IByproduct[] byproducts = new IByproduct[0];
  /** Folder to save melting recipes */
  private String meltingFolder = "melting/";
  /** Folder to save casting recipes */
  private String castingFolder = "casting/";
  /** Keeps track of whether this builder is used for gems or metals */
  private OreRateType oreRate = null;
  /** Base unit value for builder */
  private int baseUnit = 0;
  /** Base unit value for builder */
  private int damageUnit = 0;

  /* Constructors */

  /** Creates a builder for the given fluid object */
  @CheckReturnValue
  public static SmelteryRecipeBuilder fluid(Consumer<FinishedRecipe> consumer, ResourceLocation name, FluidObject<?> fluid) {
    return new SmelteryRecipeBuilder(consumer, name, fluid, null, null).temperature(getTemperature(fluid));
  }

  /** Creates a builder for the given fluid and tags. Tag will be used for inputs and fluid for outputs */
  @CheckReturnValue
  public static SmelteryRecipeBuilder fluid(Consumer<FinishedRecipe> consumer, ResourceLocation name, @Nullable Fluid fluid, @Nullable TagKey<Fluid> fluidTag) {
    assert fluid != null || fluidTag != null;
    SmelteryRecipeBuilder builder = new SmelteryRecipeBuilder(consumer, name, null, fluid, fluidTag);
    if (fluid != null) {
      builder.temperature(getTemperature(fluid));
    }
    return builder;
  }

  /** Creates a builder for the given fluid, used as input and output */
  @CheckReturnValue
  public static SmelteryRecipeBuilder fluid(Consumer<FinishedRecipe> consumer, ResourceLocation name, Fluid fluid) {
    return fluid(consumer, name, fluid, null);
  }

  /** Creates a builder for the given fluid tags, used as input and output */
  @CheckReturnValue
  public static SmelteryRecipeBuilder fluid(Consumer<FinishedRecipe> consumer, ResourceLocation name, TagKey<Fluid> fluidTag) {
    return fluid(consumer, name, null, fluidTag);
  }


  /* Setters */

  /** Sets all recipes to optional, used for compat */
  public SmelteryRecipeBuilder optional() {
    return optional(true);
  }

  /** Sets the byproducts for following recipes */
  public SmelteryRecipeBuilder ore(IByproduct... byproducts) {
    this.byproducts = byproducts;
    this.hasOre = true;
    return this;
  }

  /** Sets the output prefix for melting recipes */
  public SmelteryRecipeBuilder meltingFolder(String meltingFolder) {
    this.meltingFolder = meltingFolder + '/';
    return this;
  }

  /** Sets the output prefix for casting recipes */
  public SmelteryRecipeBuilder castingFolder(String castingFolder) {
    this.castingFolder = castingFolder + '/';
    return this;
  }


  /* Basic helpers */

  /** Creates a recipe result for melting */
  @CheckReturnValue
  private FluidOutput result(int amount) {
    if (fluidObject != null) {
      return fluidObject.result(amount);
    }
    if (fluid != null) {
      return FluidOutput.fromFluid(fluid, amount);
    }
    assert fluidTag != null;
    return FluidOutput.fromTag(fluidTag, amount);
  }

  /** Creates an ingredient input for casting */
  @CheckReturnValue
  private FluidIngredient ingredient(int amount) {
    if (fluidObject != null) {
      return fluidObject.ingredient(amount);
    }
    if (fluidTag != null) {
      return FluidIngredient.of(fluidTag, amount);
    }
    assert fluid != null;
    return FluidIngredient.of(fluid, amount);
  }

  /** Adds the given conditions to the given builder */
  @CheckReturnValue
  private Consumer<FinishedRecipe> withCondition(ICondition... conditions) {
    ConsumerWrapperBuilder builder = ConsumerWrapperBuilder.wrap();
    for (ICondition condition : conditions) {
      builder.addCondition(condition);
    }
    return builder.build(consumer);
  }

  /** Creates a condition for a tag being empty */
  @CheckReturnValue
  public static ICondition tagCondition(ResourceLocation tag) {
    return new TagFilledCondition<>(ItemTags.create(tag));
  }

  /** Creates a condition for a tag being empty */
  @CheckReturnValue
  public static ICondition tagCondition(String name) {
    return tagCondition(commonResource(name));
  }

  /** Creates a tag key for an item */
  @CheckReturnValue
  public static TagKey<Item> itemTag(String name) {
    return ItemTags.create(commonResource(name));
  }

  /** Creates a location under the given domain with the passed prefix  */
  @CheckReturnValue
  private ResourceLocation location(String folder, String variant) {
    return name.withPath(folder + name.getPath() + '/' + variant);
  }


  /* Melting helpers */

  /** Adds a recipe for melting a list of items. Never optional */
  private void minecraftArmorMelting(int cost, String prefix, String name) {
    Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(prefix + '_' + name));
    if (item == Items.AIR) {
      throw new IllegalArgumentException("Unknown item name minecraft:" + name);
    }
    MeltingRecipeBuilder.melting(Ingredient.of(item), result(cost * baseUnit), temperature, (float)Math.sqrt(cost))
                        .setDamagable(damageUnit)
                        .save(consumer, location(meltingFolder, name));
  }

  /** Adds a recipe for melting an item by ID. Automatically optional */
  private void itemMelting(int amount, String output, float factor, ResourceLocation itemName, boolean damagable) {
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(itemName), result(amount), temperature, factor)
                        .setDamagable(damagable ? damageUnit : 0)
                        .save(withCondition(new ItemExistsCondition(itemName)), location(meltingFolder, output));
  }

  /** Adds a recipe for melting an item from a tag */
  private void tagMelting(int amount, String output, float factor, String tagName, boolean forceOptional) {
    tagMelting(amount, output, factor, commonResource(tagName), false, forceOptional);
  }

  /** Adds a recipe for melting an item from a tag */
  private void tagMelting(int amount, String output, float factor, ResourceLocation tagName, boolean damagable, boolean forceOptional) {
    Consumer<FinishedRecipe> wrapped = optional || forceOptional ? withCondition(tagCondition(tagName)) : consumer;
    MeltingRecipeBuilder.melting(Ingredient.of(ItemTags.create(tagName)), result(amount), temperature, factor)
                        .setDamagable(damagable ? damageUnit : 0)
                        .save(wrapped, location(meltingFolder, output));
  }

  /** Adds recipes to melt an ore item with byproducts */
  private void oreMelting(float scale, String tagName, @Nullable TagKey<Item> size, float factor, String output, boolean forceOptional) {
    assert oreRate != null;
    assert baseUnit != 0;
    Consumer<FinishedRecipe> wrapped;
    Ingredient baseIngredient = Ingredient.of(itemTag(tagName));
    Ingredient ingredient;
    // not everyone sets size, so treat singular as the fallback, means we want anything in the tag that is not sparse or dense
    if (size == Tags.Items.ORE_RATES_SINGULAR) {
      ingredient = DifferenceIngredient.of(baseIngredient, Ingredient.of(TinkerTags.Items.NON_SINGULAR_ORE_RATES));
      wrapped = withCondition(TagCombinationCondition.difference(itemTag(tagName), TinkerTags.Items.NON_SINGULAR_ORE_RATES));
      // size tag means we want an intersection between the tag and that size
    } else if (size != null) {
      ingredient = IntersectionIngredient.of(baseIngredient, Ingredient.of(size));
      wrapped = withCondition(TagCombinationCondition.intersection(itemTag(tagName), size));
      // default only need it to be in the tag
    } else {
      ingredient = baseIngredient;
      wrapped = optional || forceOptional ? withCondition(tagCondition(tagName)) : consumer;
    }
    Supplier<MeltingRecipeBuilder> supplier = () -> MeltingRecipeBuilder.melting(ingredient, result((int)(baseUnit * scale)), temperature, factor).setOre(oreRate);
    ResourceLocation location = location(meltingFolder, output);

    // if no byproducts, just build directly
    if (byproducts.length == 0) {
      supplier.get().save(wrapped, location);
      // if first option is always present, only need that one
    } else if (byproducts[0].isAlwaysPresent()) {
      supplier.get()
              .addByproduct(byproducts[0].getFluid(scale))
              .setOre(oreRate, byproducts[0].getOreRate())
              .save(wrapped, location);
    } else {
      // multiple options, will need a conditonal recipe
      ConditionalRecipe.Builder builder = ConditionalRecipe.builder();
      boolean alwaysPresent = false;
      for (IByproduct byproduct : byproducts) {
        // found an always present byproduct? no need to tag and we are done
        alwaysPresent = byproduct.isAlwaysPresent();
        if (alwaysPresent) {
          builder.addCondition(TrueCondition.INSTANCE);
        } else {
          builder.addCondition(tagCondition("ingots/" + byproduct.getName()));
        }
        builder.addRecipe(supplier.get().addByproduct(byproduct.getFluid(scale)).setOre(oreRate, byproduct.getOreRate())::save);

        if (alwaysPresent) {
          break;
        }
      }
      // not always present? add a recipe with no byproducts as a final fallback
      if (!alwaysPresent) {
        builder.addCondition(TrueCondition.INSTANCE);
        builder.addRecipe(supplier.get()::save);
      }
      builder.build(wrapped, location);
    }
  }


  /* Casting helpers */

  /** Recipe to cast using a cast */
  private void tagCasting(int amount, String outputPrefix, CastItemObject cast, String tagName, boolean forceOptional) {
    Consumer<FinishedRecipe> wrapped = optional || forceOptional ? withCondition(tagCondition(tagName)) : consumer;
    ItemOutput output = ItemOutput.fromTag(itemTag(tagName));
    FluidIngredient fluid = ingredient(amount);
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluid(fluid)
                            .setCoolingTime(temperature, amount)
                            .setCast(cast.getMultiUseTag(), false)
                            .save(wrapped, location(castingFolder, outputPrefix + "_gold_cast"));
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluid(fluid)
                            .setCoolingTime(temperature, amount)
                            .setCast(cast.getSingleUseTag(), true)
                            .save(wrapped, location(castingFolder, outputPrefix + "_sand_cast"));
  }

  /** Recipe to cast using a basin */
  private void basinCasting(int amount, String output, String tagName, boolean forceOptional) {
    Consumer<FinishedRecipe> wrapped = optional || forceOptional ? withCondition(tagCondition(tagName)) : consumer;
    ItemCastingRecipeBuilder.basinRecipe(ItemOutput.fromTag(itemTag(tagName)))
                            .setFluid(ingredient(amount))
                            .setCoolingTime(temperature, amount)
                            .save(wrapped, location(castingFolder, output));
  }


  /* Joint helpers */

  /** Adds melting and casting recipes for the given object */
  public SmelteryRecipeBuilder meltingCasting(float scale, String tagPrefix, CastItemObject cast, float factor, boolean forceOptional) {
    assert baseUnit != 0;
    int amount = (int)(baseUnit * scale);
    String tagName = tagPrefix + "s/" + name.getPath();
    tagMelting(amount, tagPrefix, factor, tagName, forceOptional);
    tagCasting(amount, tagPrefix, cast,   tagName, forceOptional);
    return this;
  }

  /** Adds melting and casting recipes for the given object */
  public SmelteryRecipeBuilder meltingCasting(float scale, CastItemObject cast, float factor, boolean forceOptional) {
    return meltingCasting(scale, cast.getName().getPath(), cast, factor, forceOptional);
  }


  /* Melting helpers */

  /** Adds a recipe for melting a tool from the given mod */
  public SmelteryRecipeBuilder itemMelting(float scale, String domain, String path, boolean damagable) {
    itemMelting((int)(baseUnit * scale), domain + '_' + path, (float)Math.sqrt(scale), new ResourceLocation(domain, path), damagable);
    return this;
  }

  /** Adds a recipe melting a tag item */
  public SmelteryRecipeBuilder melting(float scale, String output, ResourceLocation tagName, float factor, boolean damagable, boolean forceOptional) {
    assert baseUnit != 0;
    tagMelting((int)(baseUnit * scale), output, factor, tagName, damagable, forceOptional);
    return this;
  }

  /** Adds a recipe melting a tag item */
  public SmelteryRecipeBuilder melting(float scale, String output, String tagPrefix, float factor, boolean damagable, boolean forceOptional) {
    return melting(scale, output, commonResource(tagPrefix + '/' + name.getPath()), factor, damagable, forceOptional);
  }

  /** Adds a recipe melting a tag item */
  public SmelteryRecipeBuilder melting(float scale, String output, String tagPrefix, boolean damagable, boolean forceOptional) {
    return melting(scale, output, tagPrefix, (float)Math.sqrt(scale), damagable, forceOptional);
  }

  /** Adds a recipe melting a tag item */
  public SmelteryRecipeBuilder melting(float scale, String tagPrefix, float factor, boolean forceOptional) {
    return melting(scale, tagPrefix, tagPrefix + "s", factor, false, forceOptional);
  }


  /* Tool melting */

  /** Adds a recipe for melting a tool from the given mod, automatically prefixing the metal into the name */
  public SmelteryRecipeBuilder toolItemMelting(int cost, String domain, String path) {
    itemMelting(baseUnit * cost, domain + '_' + path, (float)Math.sqrt(cost), new ResourceLocation(domain, this.name.getPath() + '_' + path), true);
    return this;
  }

  /** Adds a recipe melting a tool with the given cost using the common tools tag. See {@link #melting(float, String, String, boolean, boolean)} for armor as names are less standard */
  public SmelteryRecipeBuilder toolTagMelting(int cost, String name) {
    return melting(cost, name, "tools/" + name + 's', true, true);
  }

  /** Adds a recipe melting a tool with the given cost using the local tools_costing tag */
  public SmelteryRecipeBuilder toolCostMelting(int cost, String name, boolean forceOptional) {
    return melting((float)cost, name, this.name.withPath("melting/" + this.name.getPath() + "/tools_costing_" + cost), (float)Math.sqrt((float)cost), true, forceOptional);
  }

  /** Adds a recipe melting a tool with the given cost using the local tools_costing tag */
  public SmelteryRecipeBuilder toolCostMelting(int cost, String name) {
    return toolCostMelting(cost, name, true);
  }


  /* Main recipes */

  /**
   * Adds the raw ore and raw ore block metal melting recipes.
   * This is automatically called by {@link #metal()} if {@link #hasOre}, which is set automatically by {@link #ore(IByproduct...)}.
   * Provided for non-standard ores (like gold).
   */
  public SmelteryRecipeBuilder rawOre() {
    assert oreRate != null;
    assert baseUnit != 0;
    String name = this.name.getPath();
    oreMelting(1, "raw_materials/" + name,      null, 1.5f, "raw",       false);
    oreMelting(9, "storage_blocks/raw_" + name, null, 6.0f, "raw_block", false);
    return this;
  }

  /** Adds the sparse ore recipe at the given scale. Automatcally called by {@link #metal()} and {@link #gem(int)}, so only needed if doing unusual things. */
  public SmelteryRecipeBuilder sparseOre(float scale) {
    oreMelting(scale, "ores/" + name.getPath(), Tags.Items.ORE_RATES_SPARSE, 1.5f, "ore_sparse", false);
    return this;
  }

  /** Adds the sparse ore recipe at the given scale. Automatcally called by {@link #metal()} and {@link #gem(int)}, so only needed if doing unusual things. */
  public SmelteryRecipeBuilder singularOre(float scale) {
    oreMelting(scale, "ores/" + name.getPath(), Tags.Items.ORE_RATES_SINGULAR, 2.5f, "ore_singular", false);
    return this;
  }

  /** Adds the sparse ore recipe at the given scale. Automatcally called by {@link #metal()} and {@link #gem(int)}, so only needed if doing unusual things. */
  public SmelteryRecipeBuilder denseOre(float scale) {
    oreMelting(scale, "ores/" + name.getPath(), Tags.Items.ORE_RATES_DENSE, 4.5f, "ore_dense", false);
    return this;
  }

  /** Adds standard metal recipes for melting ingots, nuggets, and blocks */
  public SmelteryRecipeBuilder metal() {
    oreRate = OreRateType.METAL;
    baseUnit = FluidValues.INGOT;
    damageUnit = FluidValues.NUGGET;
    String name = this.name.getPath();
    tagMelting(FluidValues.METAL_BLOCK, "block", 3.0f, "storage_blocks/" + name, false);
    basinCasting(FluidValues.METAL_BLOCK, "block", "storage_blocks/" + name, false);
    meltingCasting(1,      TinkerSmeltery.ingotCast,  1.0f, false);
    meltingCasting(1 / 9f, TinkerSmeltery.nuggetCast, 1 / 3f, false);
    // if we set byproducts, we are an ore
    if (hasOre) {
      rawOre();
      sparseOre(1);
      singularOre(2);
      denseOre(6);
    }
    return this;
  }

  /** Adds basic recipes for gems */
  public SmelteryRecipeBuilder gem(int storageSize) {
    oreRate = OreRateType.GEM;
    baseUnit = FluidValues.GEM;
    damageUnit = FluidValues.GEM_SHARD;
    String name = this.name.getPath();
    tagMelting(FluidValues.GEM * storageSize, "block", (float)Math.sqrt(storageSize), "storage_blocks/" + name, false);
    basinCasting(FluidValues.GEM * storageSize, "block", "storage_blocks/" + name, false);
    meltingCasting(1, TinkerSmeltery.gemCast, 1.0f, false);
    // if we set byproducts, we are an ore
    if (hasOre) {
      sparseOre(0.5f);
      singularOre(1);
      denseOre(3);
    }
    return this;
  }

  /** Adds basic recipes for a amethyst/quartz style gem */
  public SmelteryRecipeBuilder smallGem() {
    return gem(4);
  }

  /** Adds basic recipes for a diamond/emerald style gem */
  public SmelteryRecipeBuilder largeGem() {
    return gem(9);
  }

  /** Adds geore recipes. Will add either metal or gem based on the ore rate set. */
  public SmelteryRecipeBuilder geore() {
    assert oreRate != null;
    assert baseUnit != 0;
    String name = this.name.getPath();
    // base - no byproducts
    tagMelting(baseUnit, "geore/shard", 1.0f, "geore_shards/" + name, true);
    tagMelting(baseUnit * 4, "geore/block", 2.0f, "geore_blocks/" + name, true);
    // clusters - ores with byproducts
    oreMelting(4, "geore_clusters/" + name,    null, 2.5f, "geore/cluster",    true);
    oreMelting(1, "geore_small_buds/" + name,  null, 1.0f, "geore/bud_small",  true);
    oreMelting(2, "geore_medium_buds/" + name, null, 1.5f, "geore/bud_medium", true);
    oreMelting(3, "geore_large_buds/" + name,  null, 2.0f, "geore/bud_large",  true);
    return this;
  }

  /** Adds recipes to melt oreberries */
  public SmelteryRecipeBuilder oreberry() {
    assert baseUnit == FluidValues.INGOT;
    itemMelting(FluidValues.NUGGET, "oreberry", 1 / 3f, new ResourceLocation("oreberriesreplanted", name.getPath() + "_oreberry"), false);
    return this;
  }

  /** Adds a recipe for melting dust */
  public SmelteryRecipeBuilder dust() {
    return melting(1, "dust", 0.75f, true);
  }

  /** Adds a recipe for melting plates */
  public SmelteryRecipeBuilder plate() {
    return meltingCasting(1, TinkerSmeltery.plateCast, 1, true);
  }

  /** Adds a recipe for melting gears */
  public SmelteryRecipeBuilder gear() {
    return meltingCasting(4, TinkerSmeltery.gearCast, 2, true);
  }

  /** Adds a recipe for melting rods from IE */
  public SmelteryRecipeBuilder rod() {
    return meltingCasting(0.5f, TinkerSmeltery.rodCast, 1 / 5f, true);
  }

  /** Adds a recipe for melting sheetmetal from IE */
  public SmelteryRecipeBuilder sheetmetal() {
    return melting(1, "sheetmetal", 1, true);
  }

  /** Adds a recipe for melting coins from thermal */
  public SmelteryRecipeBuilder coin() {
    return meltingCasting(1 / 3f, TinkerSmeltery.coinCast, 2/3f, true);
  }

  /** Adds a recipe for melting wires from IE */
  public SmelteryRecipeBuilder wire() {
    return meltingCasting(0.5f, TinkerSmeltery.wireCast, 1 / 5f, true);
  }

  /** Adds vanilla tools with the given prefix for item IDs */
  @Internal
  public SmelteryRecipeBuilder minecraftTools(String prefix) {
    // shovel needs the cost tag for tool's complement knife
    toolCostMelting(1, "shovel", false);
    // sword recipe also handles hoe
    toolCostMelting(2, "sword", false);
    // axe and pickaxe together
    toolCostMelting(3, "axes", false);
    // armor
    minecraftArmorMelting(5, prefix, "helmet");
    minecraftArmorMelting(8, prefix, "chestplate");
    minecraftArmorMelting(4, prefix, "boots");
    // mekanism adds paxels for all vanilla tools, so use a tag to make supporting that easy
    toolCostMelting(7, "leggings", false);
    return this;
  }

  /** Adds vanilla tools with the default prefix for item IDs */
  @Internal
  public SmelteryRecipeBuilder minecraftTools() {
    return minecraftTools(name.getPath());
  }


  /* Modded tools */

  /** Applies the list of common recipes */
  public SmelteryRecipeBuilder common(CommonRecipe... recipes) {
    for (CommonRecipe recipe : recipes) {
      recipe.accept(this);
    }
    return this;
  }

  /** Avoids generic array creation for a recipe consumer */
  public interface CommonRecipe extends Consumer<SmelteryRecipeBuilder> {}

  /** Consumer melting a tool via the common tag */
  public record ToolTagMelting(int cost, String name) implements CommonRecipe {
    @Override
    public void accept(SmelteryRecipeBuilder builder) {
      builder.toolTagMelting(cost, name);
    }
  }

  /** Consumer melting a tool via the common tag */
  public record ArmorTagMelting(int cost, String name, String tag) implements CommonRecipe {
    @Override
    public void accept(SmelteryRecipeBuilder builder) {
      builder.melting(cost, name, "armors/" + tag, true, true);
    }
  }

  /** Consumer for melting a set of tools using the local tag */
  public record ToolCostMelting(int cost, String name) implements CommonRecipe {
    @Override
    public void accept(SmelteryRecipeBuilder builder) {
      builder.toolCostMelting(cost, name, true);
    }
  }

  /** Consumer for melting a specific tool from a mod with the metal prefix */
  public record ToolItemMelting(int cost, String domain, String name) implements CommonRecipe {
    @Override
    public void accept(SmelteryRecipeBuilder builder) {
      builder.toolItemMelting(cost, domain, name);
    }
  }

  /* Common tool melting */

  // common
  public static final ToolTagMelting SHOVEL = new ToolTagMelting(1, "shovel");
  public static final ToolCostMelting SHOVEL_PLUS = new ToolCostMelting(1, "shovel");
  public static final ToolCostMelting SWORD = new ToolCostMelting(2, "sword");
  public static final ToolCostMelting AXES = new ToolCostMelting(3, "axes");
  public static final CommonRecipe[] TOOLS = { SHOVEL, SWORD, AXES };
  // armor
  public static final ArmorTagMelting HELMET = new ArmorTagMelting(5, "helmet", "helmets");
  public static final ArmorTagMelting CHESTPLATE = new ArmorTagMelting(8, "chestplate", "chestplates");
  public static final ArmorTagMelting LEGGINGS = new ArmorTagMelting(7, "leggings", "leggings");
  public static final ToolCostMelting LEGGINGS_PLUS = new ToolCostMelting(7, "leggings");
  public static final ArmorTagMelting BOOTS = new ArmorTagMelting(4, "boots", "boots");
  public static final CommonRecipe[] ARMOR = { HELMET, CHESTPLATE, LEGGINGS, BOOTS };
}
