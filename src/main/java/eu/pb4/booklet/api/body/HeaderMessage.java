package eu.pb4.booklet.api.body;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.booklet.impl.language.LanguageHandler;
import eu.pb4.booklet.impl.language.TextTranslationUtils;
import eu.pb4.booklet.api.TextUncenterer;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import eu.pb4.polymer.core.api.other.PolymerMapCodec;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.body.DialogBody;
import net.minecraft.server.dialog.body.PlainMessage;
import xyz.nucleoid.server.translations.api.LocalizationTarget;

public record HeaderMessage(Component contents, TextColor color, int width) implements DialogBody {
    public HeaderMessage(Component contents, int width) {
        this(contents, TextColor.fromRgb(0xFFFFFF), width);
    }

    public static final MapCodec<HeaderMessage> MAP_CODEC = PolymerMapCodec.ofDialogBody(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ComponentSerialization.CODEC.fieldOf("contents").forGetter(HeaderMessage::contents),
                    TextColor.CODEC.optionalFieldOf("color", TextColor.fromRgb(0xFFFFFF)).forGetter(HeaderMessage::color),
                    Dialog.WIDTH_CODEC.optionalFieldOf("width", 310).forGetter(HeaderMessage::width)
            ).apply(instance, HeaderMessage::new)), HeaderMessage::asVanillaBody);

    public MapCodec<HeaderMessage> mapCodec() {
        return MAP_CODEC;
    }

    public PlainMessage asVanillaBody(PacketContext context) {
        var language = LocalizationTarget.of(context).getLanguageCode();
        var title = Component.literal(" ")
                .append(TextTranslationUtils.toTranslatedComponent(LanguageHandler.get(language), contents))
                .append(" ");
        var sides = TextUncenterer.filler((this.width - DefaultFonts.REGISTRY.getWidth(title, 8) - 8) / 2);
        sides.setStyle(sides.getStyle().withStrikethrough(true).withShadowColor(0).withColor(this.color));
        return new PlainMessage(Component.empty().append(sides).append(title).append(sides), this.width);
    }
}
