package eu.pb4.booklet.impl.textnode;

import eu.pb4.booklet.impl.BookletImplUtil;
import eu.pb4.booklet.impl.BookletOpenState;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public class OpenPageRefNode implements TextNode {

    private final Identifier entry;

    public OpenPageRefNode(Identifier page) {
        this.entry = page;
    }

    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        var player = Objects.requireNonNull(context.getOrThrow(ServerPlaceholderContext.SERVER_KEY).serverPlayer());
        var page = BookletImplUtil.getPage(this.entry, player.clientInformation().language());

        return page != null ? Component.empty().append(page.info().getExternalTitle()).setStyle(Style.EMPTY.withColor(TextColor.BLUE).withUnderlined(true)
                .withClickEvent(BookletImplUtil.encodeClickEvent("open_page", entry, context.getOrThrow(BookletOpenState.KEY), true)))
                : Component.literal("[! " + this.entry + "!]").setStyle(Style.EMPTY.withColor(TextColor.DARK_RED).withItalic(true));
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
