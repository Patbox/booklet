package eu.pb4.booklet.api.item;


import eu.pb4.booklet.impl.BookletOpenState;
import eu.pb4.booklet.impl.BookletImplUtil;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.Consumer;

public class GuideBookItem extends SimplePolymerItem {
    public GuideBookItem(Properties settings, Identifier identifier) {
        super(settings.component(BookletItems.PAGE_COMPONENT, identifier));
    }

    public GuideBookItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var id = player.getItemInHand(hand).get(BookletItems.PAGE_COMPONENT);
        if (player instanceof ServerPlayer serverPlayer && id != null) {
            BookletImplUtil.openPage(serverPlayer, id, BookletOpenState.DEFAULT);
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public Component getName(ItemStack stack) {
        var id = stack.get(BookletItems.PAGE_COMPONENT);
        if (id != null) {
            var ctx = PacketContext.get();

            var page = BookletImplUtil.getPage(id, ctx != null && ctx.getClientOptions() != null ? ctx.getClientOptions().language() : "en_us");
            if (page != null) {
                return page.info().getExternalTitle();
            }
        }
        return super.getName(stack);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        var id = stack.get(BookletItems.PAGE_COMPONENT);
        if (id != null) {
            var ctx = PacketContext.get();

            var page = BookletImplUtil.getPage(id, ctx != null && ctx.getClientOptions() != null ? ctx.getClientOptions().language() : "en_us");
            if (page != null && page.info().description().isPresent()) {
                tooltipAdder.accept(Component.empty().append(page.info().description().orElseThrow()).withStyle(ChatFormatting.GRAY));
            }
        }
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        var id = stack.get(BookletItems.PAGE_COMPONENT);
        if (id != null) {
            var ctx = PacketContext.get();

            var page = BookletImplUtil.getPage(id, ctx != null && ctx.getClientOptions() != null ? ctx.getClientOptions().language() : "en_us");
            if (page != null && page.info().modelOverride().isPresent()) {
                return page.info().modelOverride().get();
            }
        }

        return super.getPolymerItemModel(stack, context);
    }

    @Override
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context) {
        super.modifyBasePolymerItemStack(out, stack, context);
        var id = stack.get(BookletItems.PAGE_COMPONENT);
        int color = 0xFFFFFF;

        if (id != null) {
            var ctx = PacketContext.get();

            var page = BookletImplUtil.getPage(id, ctx != null && ctx.getClientOptions() != null ? ctx.getClientOptions().language() : "en_us");
            if (page != null) {
                color = page.info().color();
            }
        }

        out.set(DataComponents.MAP_COLOR, new MapItemColor(color | 0xFF000000));
    }
}
