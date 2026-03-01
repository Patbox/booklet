package eu.pb4.booklet.impl.datagen;

import com.google.common.hash.HashCode;
import eu.pb4.booklet.api.item.BookletItems;
import eu.pb4.booklet.impl.ui.UiResourceCreator;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.BasicItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.tint.ConstantTintSource;
import eu.pb4.polymer.resourcepack.extras.api.format.item.tint.MapColorTintSource;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

class AssetProvider implements DataProvider {
    private final PackOutput output;

    public AssetProvider(FabricDataOutput output) {
        this.output = output;
    }

    public static void runWriters(BiConsumer<String, byte[]> assetWriter) {
        var map = new HashMap<Identifier, ItemAsset>();
        createItems(map::put);
        UiResourceCreator.generateAssets(assetWriter);
        map.forEach((id, asset) -> assetWriter.accept(AssetPaths.itemAsset(id), asset.toJson().getBytes(StandardCharsets.UTF_8)));
    }

    private static void createItems(BiConsumer<Identifier, ItemAsset> consumer) {
        var fromItem = new BiConsumer<Item, Function<Identifier, ItemAsset>>() {
            @Override
            public void accept(Item item, Function<Identifier, ItemAsset> function) {
                var id = BuiltInRegistries.ITEM.getKey(item);
                consumer.accept(id, function.apply(id));
            }
        };

        fromItem.accept(BookletItems.GUIDE_BOOK,
                ide -> new ItemAsset(new BasicItemModel(ide.withPrefix("item/"), List.of(new ConstantTintSource(-1), new MapColorTintSource(-1))))
        );
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        BiConsumer<String, byte[]> assetWriter = (path, data) -> {
            try {
                writer.writeIfNeeded(this.output.getOutputFolder().resolve(path), data, HashCode.fromBytes(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        return CompletableFuture.runAsync(() -> {
            runWriters(assetWriter);
        }, Util.backgroundExecutor());
    }

    @Override
    public String getName() {
        return "polyfactory:assets";
    }
}
