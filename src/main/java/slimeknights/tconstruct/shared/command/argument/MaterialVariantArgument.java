package slimeknights.tconstruct.shared.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/** Argument for a material variant type */
@RequiredArgsConstructor(staticName = "material")
public class MaterialVariantArgument implements ArgumentType<MaterialVariantId> {
  /** Error called on invalid argument */
  private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(Component.translatable("argument.id.invalid"));

  /** Gets the tool stat from the context */
  public static MaterialVariantId getMaterial(CommandContext<CommandSourceStack> context, String name) {
    return context.getArgument(name, MaterialVariantId.class);
  }

  @Override
  public MaterialVariantId parse(StringReader reader) throws CommandSyntaxException {
    MaterialVariantId material = MaterialVariantId.read(TConstruct.MOD_ID, reader);
    if (material == null) {
      throw ERROR_INVALID.createWithContext(reader);
    }
    return material;
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    return TinkerSuggestionProvider.suggestResource(TConstruct.MOD_ID, MaterialRegistry.getInstance().getAllMaterials().stream().map(IMaterial::getIdentifier), builder, id -> id, MaterialTooltipCache::getColoredDisplayName);
  }

  @Override
  public Collection<String> getExamples() {
    return MaterialArgument.EXAMPLES;
  }
}
