package eu.pb4.booklet.impl;

import eu.pb4.booklet.api.item.BookletItems;
import eu.pb4.booklet.impl.language.VanillaLanguageDownloader;
import eu.pb4.booklet.impl.polydex.PolydexCompat;
import eu.pb4.booklet.api.body.AlignedItemBody;
import eu.pb4.booklet.api.body.AlignedMessage;
import eu.pb4.booklet.api.body.HeaderMessage;
import eu.pb4.booklet.api.body.ImageBody;
import eu.pb4.booklet.impl.ui.GuiTextures;
import eu.pb4.booklet.impl.ui.GuiUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

import static eu.pb4.booklet.impl.BookletImplUtil.id;


public class BookletInit  {
    public static final HashMap<Identifier, Map<String, BookletPage>> PAGES = new HashMap<>();
    public static final Map<Identifier, List<Identifier>> CATEGORIES = new HashMap<>();

    public static void init() {
        Registry.register(BuiltInRegistries.DIALOG_BODY_TYPE, id("aligned_message"), AlignedMessage.MAP_CODEC);
        Registry.register(BuiltInRegistries.DIALOG_BODY_TYPE, id("header_message"), HeaderMessage.MAP_CODEC);
        Registry.register(BuiltInRegistries.DIALOG_BODY_TYPE, id("aligned_item"), AlignedItemBody.MAP_CODEC);
        Registry.register(BuiltInRegistries.DIALOG_BODY_TYPE, id("image"), ImageBody.MAP_CODEC);

        ServerLifecycleEvents.SERVER_STARTED.register(BookletInit::loadPages);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, s, g) -> loadPages(server));
        BookletItems.register();
        GuiTextures.register();
        BookletImageHandler.init();
        PolydexCompat.register();

        PolymerResourcePackUtils.addModAssets("booklet");

        if (PolydexCompat.IS_PRESENT) {
            // Polydex does the same thing, so just reuse that.
            VanillaLanguageDownloader.markReady();
        } else {
            VanillaLanguageDownloader.setup();
        }
    }

    private static void loadPages(MinecraftServer server) {
        try {
            var pages = server.getResourceManager().listResources("booklet/pages", x -> x.getPath().endsWith(".txt") && x.getPath().indexOf('/') != -1);
            PAGES.clear();
            CATEGORIES.clear();
            var parser = new PageParser(server.registryAccess());
            var order = new Object2IntOpenHashMap<Identifier>();
            for (var entry : pages.entrySet()) {
                try {
                    var pathWithLang = entry.getKey().getPath().substring("booklet/pages/".length());
                    var langIndex = pathWithLang.indexOf('/');
                    var lang = pathWithLang.substring(0, langIndex);

                    var id = entry.getKey().withPath(
                            pathWithLang.substring(
                                    langIndex + 1,
                                    pathWithLang.length() - ".txt".length()
                            )
                    );
                    var string = new String(entry.getValue().open().readAllBytes(), StandardCharsets.UTF_8);
                    var page = parser.readPage(id, string);

                    order.put(id, page.info().order());
                    PAGES.computeIfAbsent(id, x -> new HashMap<>()).put(lang, page);

                    for (var cat : page.info().categories()) {
                        var list = CATEGORIES.computeIfAbsent(cat, c -> new ArrayList<>());
                        if (!list.contains(id)) {
                            list.add(id);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            var comparator = Comparator.<Identifier>comparingInt(order::getInt).thenComparing(Function.identity());

            for (var list : CATEGORIES.values()) {
                list.sort(comparator);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void handleAction(ServerPlayer player, String path, Optional<Tag> payload) {
        var state = BookletOpenState.decode(payload);
        Identifier entry = Identifier.tryParse("");
        String entryRaw = "";
        if (payload.isPresent() && payload.get() instanceof CompoundTag tag) {
            entryRaw = tag.getStringOr("entry", "");
            entry = Identifier.tryParse(entryRaw);

            if (tag.getBooleanOr("play_click_sound", false)) {
                GuiUtils.playClickSound(player);
            }
        }

        switch (path) {
            case "open_page" -> BookletImplUtil.openPage(player, entry, state);
            case "polydex/usage" -> BookletImplUtil.openPolydexUsagePage(player, entry, state);
            case "polydex/result" -> BookletImplUtil.openPolydexResultPage(player, entry, state);
            case "polydex/category" -> BookletImplUtil.openPolydexCategoryPage(player, entry, state);
            case "polydex/search" -> BookletImplUtil.openPolydexSearchPage(player, entryRaw, state);
            case "close" -> player.closeContainer();
        }
    }
}
