package eu.pb4.booklet.impl.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import it.unimi.dsi.fastutil.chars.Char2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2CharMap;
import it.unimi.dsi.fastutil.ints.Int2CharOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Tuple;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static eu.pb4.booklet.impl.BookletImplUtil.id;


public class UiResourceCreator {
    public static final Style STYLE = Style.EMPTY.withColor(0xFFFFFF).withFont(new FontDescription.Resource(id("gui"))).withShadowColor(0);

    private static final Char2IntMap SPACES = new Char2IntOpenHashMap();
    private static final Int2CharMap SPACES_BY_WIDTH = new Int2CharOpenHashMap();
    private static final List<FontTexture> FONT_TEXTURES = new ArrayList<>();
    private static char character = '\u0100';

    private static final char CHEST_SPACE0 = space(-8);
    private static final char CHEST_SPACE1 = space(-168);
    private static final char ANVIL_SPACE0 = space(-60);
    private static final char ANVIL_SPACE1 = space(-119);

    public static Function<Component, Component> background(String path) {
        var builder = new StringBuilder().append(CHEST_SPACE0);
        var c = (character++);
        builder.append(c);
        builder.append(CHEST_SPACE1);

        var texture = new FontTexture(id("sgui/" + path), 13, 256, new char[][]{new char[]{c}});

        FONT_TEXTURES.add(texture);
        return new TextBuilders(Component.literal(builder.toString()).setStyle(STYLE));
    }

    public static Function<Component, Component> backgroundAnvil(String path) {
        var builder = new StringBuilder().append(ANVIL_SPACE0);
        var c = (character++);
        builder.append(c);
        builder.append(ANVIL_SPACE1);

        var texture = new FontTexture(id("sgui/" + path), 13, 256, new char[][]{new char[]{c}});

        FONT_TEXTURES.add(texture);
        return new TextBuilders(Component.literal(builder.toString()).setStyle(STYLE));
    }

    public static char font(Identifier path, int ascent, int height) {
        var c = (character++);
        var texture = new FontTexture(path, ascent, height, new char[][]{new char[]{c}});
        FONT_TEXTURES.add(texture);
        return c;
    }

    public static Tuple<Component, Component> polydexBackground(String path) {
        var c = (character++);
        var d = (character++);

        var texture = new FontTexture(id("sgui/polydex/" + path), -4, 128, new char[][]{new char[]{c}, new char[]{d}});

        FONT_TEXTURES.add(texture);

        return new Tuple<>(
                Component.literal(Character.toString(c)).setStyle(STYLE),
                Component.literal(Character.toString(d)).setStyle(STYLE)
        );
    }

    public static char space(int width) {
        return SPACES_BY_WIDTH.computeIfAbsent(width, widthx -> {
            var c = character++;
            SPACES.put(c, widthx);
            return c;
        });
    }

    public static void setup() {
        SPACES.put(CHEST_SPACE0, -8);
        SPACES.put(CHEST_SPACE1, -168);
        SPACES.put(ANVIL_SPACE0, -60);
        SPACES.put(ANVIL_SPACE1, -119);
    }


    public static void generateAssets(BiConsumer<String, byte[]> assetWriter) {
        var fontBase = new JsonObject();
        var providers = new JsonArray();

        {
            var spaces = new JsonObject();
            spaces.addProperty("type", "space");
            var advances = new JsonObject();
            SPACES.char2IntEntrySet().stream().sorted(Comparator.comparing(Char2IntMap.Entry::getCharKey)).forEach((c) -> advances.addProperty(Character.toString(c.getCharKey()), c.getIntValue()));
            spaces.add("advances", advances);
            providers.add(spaces);
        }


        FONT_TEXTURES.forEach((entry) -> {
            var bitmap = new JsonObject();
            bitmap.addProperty("type", "bitmap");
            bitmap.addProperty("file", entry.path + ".png");
            bitmap.addProperty("ascent", entry.ascent);
            bitmap.addProperty("height", entry.height);
            var chars = new JsonArray();

            for (var a : entry.chars) {
                var builder = new StringBuilder();
                for (var b : a) {
                    builder.append(b);
                }
                chars.add(builder.toString());
            }

            bitmap.add("chars", chars);
            providers.add(bitmap);
        });

        fontBase.add("providers", providers);

        assetWriter.accept("assets/booklet/font/gui.json", fontBase.toString().getBytes(StandardCharsets.UTF_8));
    }

    private record TextBuilders(Component base) implements Function<Component, Component> {
        @Override
        public Component apply(Component text) {
            return Component.empty().append(base).append(text);
        }
    }

    public record FontTexture(Identifier path, int ascent, int height, char[][] chars) {
    }

}
