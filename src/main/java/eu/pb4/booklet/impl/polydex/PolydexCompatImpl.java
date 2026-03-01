package eu.pb4.booklet.impl.polydex;

import eu.pb4.polydex.api.v1.recipe.PolydexPageUtils;
import eu.pb4.polydex.impl.PolydexImpl;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class PolydexCompatImpl {

    public static void register() {

    }

    public static void openUsagePage(ServerPlayer player, Identifier entry, Runnable runnable) {
        var val = PolydexPageUtils.getEntry(entry);
        if (val != null) {
            PolydexPageUtils.openUsagesListUi(player, val, runnable);
        }
    }

    public static void openResultPage(ServerPlayer player, Identifier entry, Runnable runnable) {
        var val = PolydexPageUtils.getEntry(entry);
        if (val != null) {
            PolydexPageUtils.openRecipeListUi(player, val, runnable);
        }
    }

    public static void openCategoryPage(ServerPlayer player, Identifier category, Runnable runnable) {
        var val = PolydexImpl.CATEGORY_BY_ID.get(category);
        if (val != null) {
            PolydexPageUtils.openCategoryUi(player, val, runnable);
        }
    }
}
