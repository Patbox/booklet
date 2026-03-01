package eu.pb4.booklet.impl.ui;

import eu.pb4.sgui.api.elements.GuiElement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class GuiUtils {
    public static final GuiElement EMPTY = GuiElement.EMPTY;

    public static void playClickSound(ServerPlayer player) {
        playSoundToPlayer(player, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.UI, 0.5f, 1);
    }

    public static void playSoundToPlayer(Player player, SoundEvent soundEvent, SoundSource category, float volume, float pitch) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSoundEntityPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEvent), category, player, volume, pitch, player.getRandom().nextLong()));
        }
    }
}

