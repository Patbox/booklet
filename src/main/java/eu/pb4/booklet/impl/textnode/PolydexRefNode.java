package eu.pb4.booklet.impl.textnode;

import eu.pb4.booklet.impl.BookletImplUtil;
import eu.pb4.booklet.impl.BookletOpenState;
import eu.pb4.booklet.impl.polydex.PolydexCompat;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.polydex.api.v1.recipe.PolydexPageUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public class PolydexRefNode implements TextNode {

    private final Identifier entry;
    private final String type;

    public PolydexRefNode(Identifier entry, String type) {
        this.entry = entry;
        this.type = type;
    }

    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        if (!PolydexCompat.IS_PRESENT) {
            return Component.literal("[! " + this.entry + " | " + this.type + " !]").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_RED)
                    .withStrikethrough(true).withHoverEvent(new HoverEvent.ShowText(Component.translatable("text.booklet.polydex_required_to_work"))));
        }

        var page = PolydexPageUtils.getEntry(this.entry);

        return page != null ? Component.empty().append(page.stack().getName()).setStyle(Style.EMPTY.withColor(TextColor.BLUE).withUnderlined(true)
                .withClickEvent(BookletImplUtil.encodeClickEvent("polydex/" + type, entry, context.getOrThrow(BookletOpenState.KEY), true)))
                : Component.literal("[! " + this.entry + "!]").setStyle(Style.EMPTY.withColor(TextColor.DARK_RED).withItalic(true));
    }
}
