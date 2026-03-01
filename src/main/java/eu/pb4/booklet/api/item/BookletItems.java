package eu.pb4.booklet.api.item;

import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import static eu.pb4.booklet.impl.BookletImplUtil.id;

public class BookletItems {
    public static final DataComponentType<Identifier> PAGE_COMPONENT = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("page"),
            DataComponentType.<Identifier>builder().persistent(Identifier.CODEC).build());

    public static final GuideBookItem GUIDE_BOOK = register("guidebook", settings -> new GuideBookItem(settings.stacksTo(1)
            .component(BookletItems.PAGE_COMPONENT, id("index"))));

    static {
        PolymerComponent.registerDataComponent(PAGE_COMPONENT);
    }

    @ApiStatus.Internal
    public static void register() {
    }

    private static <T extends Item> T register(Identifier id, Function<Item.Properties, T> function) {
        var item = function.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)));
        Registry.register(BuiltInRegistries.ITEM, id, item);
        return item;
    }

    private static <T extends Item> T register(String path, Function<Item.Properties, T> function) {
        return register(id(path), function);
    }
}
