package eu.pb4.booklet.impl.textnode;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import net.minecraft.network.chat.Component;
import xyz.nucleoid.server.translations.api.LocalizationTarget;

public record LangCheckNode(String language, boolean equals, TextNode... children) implements ParentTextNode {
    @Override
    public boolean isDynamicNoChildren() {
        return true;
    }

    @Override
    public TextNode[] getChildren() {
        return this.children;
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new LangCheckNode(this.language, this.equals, children);
    }

    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        var player = context.contains(ServerPlaceholderContext.SERVER_KEY) ? context.getOrThrow(ServerPlaceholderContext.SERVER_KEY).serverPlayer() : null;
        var lang = player != null ? LocalizationTarget.of(player).getLanguageCode() : "en_us";


        if (lang.equals(this.language) == this.equals) {
            return  TextNode.asSingle(this.children).toComponent(context, removeBackslashes);
        }

        return Component.empty();
    }
}
