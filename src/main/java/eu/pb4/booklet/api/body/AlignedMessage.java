package eu.pb4.booklet.api.body;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.booklet.api.TextUncenterer;
import eu.pb4.polymer.core.api.other.PolymerMapCodec;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.body.DialogBody;
import net.minecraft.server.dialog.body.PlainMessage;
import net.minecraft.util.StringRepresentable;
import xyz.nucleoid.server.translations.api.LocalizationTarget;

import java.util.Locale;

public record AlignedMessage(Component contents, int width, Align align, int nonFirstLineMargin) implements DialogBody {
    public static final MapCodec<AlignedMessage> MAP_CODEC = PolymerMapCodec.ofDialogBody(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ComponentSerialization.CODEC.fieldOf("contents").forGetter(AlignedMessage::contents),
                    Dialog.WIDTH_CODEC.optionalFieldOf("width", 200).forGetter(AlignedMessage::width),
                    StringRepresentable.fromEnum(Align::values).optionalFieldOf("align", Align.LEFT).forGetter(AlignedMessage::align),
                    Codec.INT.optionalFieldOf("non_first_line_margin", 0).forGetter(AlignedMessage::nonFirstLineMargin)
            ).apply(instance, AlignedMessage::new)), AlignedMessage::asVanillaBody);

    public static final Codec<AlignedMessage> CODEC = Codec.withAlternative(MAP_CODEC.codec(),
            ComponentSerialization.CODEC.xmap(x -> new AlignedMessage(x, 280, Align.LEFT), AlignedMessage::contents));


    public AlignedMessage(Component contents, int width, Align align) {
        this(contents, width, align, 0);
    }

    public MapCodec<AlignedMessage> mapCodec() {
        return MAP_CODEC;
    }


    public PlainMessage asVanillaBody(PacketContext context) {
        var language = LocalizationTarget.of(context).getLanguageCode();
        return new PlainMessage(switch (this.align) {
            case LEFT ->
                    CommonComponents.joinLines(TextUncenterer.getLeftAligned(this.contents, this.width - 4 * 2, language));
            case RIGHT ->
                    CommonComponents.joinLines(TextUncenterer.getRightAligned(this.contents, this.width - 4 * 2, language));
            case CENTER -> this.contents;
        }, this.width);
    }

    public enum Align implements StringRepresentable {
        LEFT,
        CENTER,
        RIGHT;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
