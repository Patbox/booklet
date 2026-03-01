package eu.pb4.booklet.api;

import eu.pb4.booklet.impl.language.LanguageHandler;
import eu.pb4.booklet.impl.language.TextTranslationUtils;
import eu.pb4.booklet.impl.ui.GuiTextures;
import eu.pb4.booklet.impl.ui.UiResourceCreator;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TextUncenterer {
    public static List<Component> getLeftAligned(Component component, int width, String language) {
        return getLeftAligned(component, width, 0, language);
    }
    public static List<Component> getLeftAligned(Component component, int width, int nonFirstLineMargin, String language) {
        return getAligned(component, width, nonFirstLineMargin, language,  (val, filler, fillerExtra) -> Component.empty().append(fillerExtra).append(val).append(filler));
    }

    public static List<Component> getRightAligned(Component component, int width, String language) {
        return getRightAligned(component, width, 0, language);

    }
    public static List<Component> getRightAligned(Component component, int width, int nonFirstLineMargin, String language) {
        return getAligned(component, width, nonFirstLineMargin, language, (val, filler, fillerExtra) -> Component.empty().append(filler).append(val).append(fillerExtra));
    }

    public static List<Component> splitLines(Component component, int width, String language) {
        return getAligned(component, width, 0, language, (val, filler, fillerExtra) -> val);
    }

    private static List<Component> getAligned(Component component, int width, int nonFirstLineMargin, String language, Merger merger) {
        var registry = DefaultFonts.REGISTRY;

        var list = new ArrayList<Component>();

        var obj = new FormattedText.StyledContentConsumer<>() {
            MutableComponent line = Component.empty();
            boolean hadNewLine = false;

            @Override
            public Optional<Object> accept(Style style, String string) {
                int newLine = string.indexOf('\n');

                var text = newLine != -1 ? string.substring(0, newLine) : string;

                var comp = TextTranslationUtils.componentify(text, style);

                var w = registry.getWidth(Component.empty().append(line).append(comp), 8);

                int extraFiller = hadNewLine ? nonFirstLineMargin : 0;

                if (w > width && comp.getContents() instanceof PlainTextContents) {
                    var spaceIndex = text.indexOf(' ');
                    if (spaceIndex == -1) {
                        if (!line.getSiblings().isEmpty()
                                && line.getSiblings().getLast().getContents() instanceof PlainTextContents contents
                                && contents.text().equals(" ")) {
                            line.getSiblings().removeLast();
                        }

                        list.add(merger.apply(line, filler(width - registry.getWidth(line, 8) - extraFiller), filler(extraFiller)));
                        line = Component.empty();
                        line.append(comp);
                    } else {
                        while (spaceIndex != -1) {
                            accept(style, text.substring(0, spaceIndex));
                            if (!line.getSiblings().isEmpty()) {
                                line.append(Component.literal(" ").setStyle(style));
                            }
                            text = text.substring(spaceIndex + 1);
                            spaceIndex = text.indexOf(' ');
                        }
                        accept(style, text);
                    }
                } else if (w > width) {
                    if (!line.getSiblings().isEmpty()
                            && line.getSiblings().getLast().getContents() instanceof PlainTextContents contents
                            && contents.text().equals(" ")) {
                        line.getSiblings().removeLast();
                    }
                    list.add(merger.apply(line, filler(width - registry.getWidth(line, 8) - extraFiller), filler(extraFiller)));

                    line = Component.empty();
                    line.append(comp);
                } else {
                    line.append(comp);
                }

                if (newLine != -1) {
                    if (!line.getSiblings().isEmpty()
                            && line.getSiblings().getLast().getContents() instanceof PlainTextContents contents
                            && contents.text().equals(" ")) {
                        line.getSiblings().removeLast();
                    }
                    list.add(merger.apply(line, filler(width - registry.getWidth(line, 8) - extraFiller), filler(extraFiller)));
                    line = Component.empty();
                    hadNewLine = true;
                    accept(style, string.substring(newLine + 1));
                }

                return Optional.empty();
            }
        };


        TextTranslationUtils.visitText(LanguageHandler.get(language), component, obj);

        if (!obj.line.getSiblings().isEmpty()) {
            if (obj.line.getSiblings().getLast().getContents() instanceof PlainTextContents contents
                    && contents.text().equals(" ")) {
                obj.line.getSiblings().removeLast();
            }
            int extraFiller = obj.hadNewLine ? nonFirstLineMargin : 0;
            list.add(merger.apply(obj.line, filler(width - registry.getWidth(obj.line, 8) - extraFiller), filler(extraFiller)));
        }

        return list;
    }

    public static MutableComponent filler(int width) {
        if (width == 0) {
            return Component.empty();
        }

        var b = new StringBuilder();
        while (width > 0) {
            if (width >= 100) {
                b.append(GuiTextures.SPACE_100);
                width -= 100;
            } else if (width >= 50) {
                b.append(GuiTextures.SPACE_50);
                width -= 50;
            } else if (width >= 20) {
                b.append(GuiTextures.SPACE_20);
                width -= 20;
            } else if (width >= 10) {
                b.append(GuiTextures.SPACE_10);
                width -= 10;
            } else if (width >= 5) {
                b.append(GuiTextures.SPACE_5);
                width -= 5;
            } else {
                b.append(GuiTextures.SPACE_1);
                width -= 1;
            }
        }
        return Component.literal(b.toString()).setStyle(UiResourceCreator.STYLE);
    }

    private interface Merger {
        Component apply(Component text, Component fillerMain, Component fillerExtra);
    }
}
