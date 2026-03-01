package eu.pb4.booklet.impl.polydex;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class PolydexCompat {
    public static final boolean IS_PRESENT = FabricLoader.getInstance().isModLoaded("polydex2");


    public static void register() {
        if (IS_PRESENT) {
            PolydexCompatImpl.register();
        }
    }

    public static void openUsagePage(ServerPlayer player, Identifier entry, Runnable runnable) {
        if (IS_PRESENT) {
            PolydexCompatImpl.openUsagePage(player, entry, runnable);
        } else {
            runnable.run();
        }
    }

    public static void openResultPage(ServerPlayer player, Identifier entry, Runnable runnable) {
        if (IS_PRESENT) {
            PolydexCompatImpl.openResultPage(player, entry, runnable);
        } else {
            runnable.run();
        }
    }

    public static void openCategoryPage(ServerPlayer player, Identifier category, Runnable runnable) {
        if (IS_PRESENT) {
            PolydexCompatImpl.openCategoryPage(player, category, runnable);
        } else {
            runnable.run();
        }
    }
}
