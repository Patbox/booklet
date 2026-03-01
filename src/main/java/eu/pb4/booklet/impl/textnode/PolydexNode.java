package eu.pb4.booklet.impl.textnode;

import eu.pb4.booklet.impl.polydex.PolydexCompat;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.booklet.impl.BookletOpenState;
import eu.pb4.booklet.impl.BookletImplUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public class PolydexNode extends ParentNode {

    private final Identifier entry;
    private final String type;

    public PolydexNode(Identifier entry, String type, TextNode... node) {
        super(node);
        this.entry = entry;
        this.type = type;
    }

    @Override
    protected Style applyFormatting(Style style, ParserContext context) {
        if (!PolydexCompat.IS_PRESENT) {
            return style.withColor(ChatFormatting.DARK_RED)
                    .withStrikethrough(true).withHoverEvent(new HoverEvent.ShowText(Component.translatable("text.booklet.polydex_required_to_work")));
        }

        return style.withClickEvent(BookletImplUtil.encodeClickEvent("polydex/" + type, entry, context.getOrThrow(BookletOpenState.KEY), true));
    }

    @Override
    public boolean isDynamicNoChildren() {
        return true;
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new PolydexNode(entry, type, children);
    }
}
