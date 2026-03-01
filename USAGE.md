# Getting Started
Currently, Booklet operates purely on pages and pages only. There is no centralized "book" definition, only page 
definitions. To create a connected book you just need to link pages to each other, either manually or via categories.

## Defining a page.
Pages are defined with a datapack. You put them into `data/<namespace>/booklet/pages/<lang>/<pagename>.txt`,
with `<namespace>` being the namespace of your project/mod/etc, `<lang>` being the language code (`en_us`, `pl_pl` and so on)
and `<pagename>` representing the page id/path. The id of the page used for lookup consists of `<namespace>:<pagename>`,
with language being automatically determined by current client language. Same as with MC translations, if page is not defined
for player selected language, it will default to `en_us` one instead. This also means if your content is intended to be
written in only a single language, then you should put it in `en_us`.

To define information about the page, you need to write a `PageInfo` section. To do so, you just write
`### Section: PageInfo` at the start of it and then `### EndSection: PageInfo`.
To define information, you write it as a `key=value` pairs inside the section.
All keys/values and entire page info section is technically optional, but you want to write it likely anyway.

Supported keys include:
- `title` - Title of the page, used in the ui header. Supports formatting.
- `external_title` - Title used for category entries and book item name. Supports formatting.
- `description` - A description of the page, used for category entries and book item tooltip. Supports formatting.
- `category` - Comma (`,`) separated list of identifiers (`namespace:value`), defines category of this page. Main pages should be in `booklet:main_page` category.
- `icon` - Item stack definition used for page icon in category entries. Used same format as /give commands item definition.
- `book_color` - Changes the default color of the guidebook item.
- `model_overrider` - Replaces the model of the guidebook item.

Example PageInfo section:
```
### Section: PageInfo
title=My Fun Guidebook
description=Guide to The Mod by The Author.
category=booklet:main_page
icon=polyfactory:guidebook
book_color=0xFFAAAA
### EndSection: PageInfo
```

## Writing page contents.
For page's text, you can use [QuickText](https://placeholders.pb4.eu/user/quicktext/) and markdown to customize it as you need it.
Additionally, all [Text Placeholder API placeholders](https://placeholders.pb4.eu/user/default-placeholders/) are also supported and will fill depending
on player using the guidebook.

Booklet extends the `QuickText` format with additional tags:
- `<nl>` - Creates a new line.
- `<nl2>` - Creates a double new line.
- `<item 'item:id'>` or `<item id:'item:id' components={'minecraft:name':'Hello'}>` - Writes an item, either using default item instance or customized one.
- `<citem 'item:id>` or `<citem id:'item:id' components={'minecraft:name':'Hello'}>` - Same as above, but colored yellow.
- `<polydex 'mod:entry'>...</>` - When clicked opens all recipes for given entry. Requires Polydex mod.
- `<polydex 'mod:entry' 'usage'>` - When clicked opens all recipes using given entry as ingredient. Requires Polydex mod.
- `<polydex 'mod:category' 'category'>` - When clicked opens all recipes within given category. Requires Polydex mod.
- `<pagelink 'mod:page'>` - When clicked, opens a booklet page with given id.

With this you should be able to write information as you need. Through if you want to add more custom elements, booklet
also provides these custom body elements and modifiers. These need to be placed on new line to work.

- `### Header: Formatted Text` - Creates a header element (centered text with lines on the sides).
- `### Align: Left/Center/Right` - Makes text align to left/center/right. Splits the text into a new body element.
- `### Width: 300` - Changes the width of the body. Defaults to 300
- `### Body:` - Splits into different body element.
- `### Reset:` - Resets all modifiers (width = 300, align=left, etc).
- `### Image: mod:image Optional subtitle` - Inserts an image into the page (read more below).
- `### Category Entries: mod:category` - Inserts link-entries for pages within given category.

### Inserting images.
Booklet has support for inserting images. By themselves they will be displayed with target width
of 300 ui pixels (300 for ui scale 1, 600 for ui scale 2 and so on). To add an image you need to use 
the `### Image: mod:image` custom body element and put the image itself into `assets/<namespace>/textures/booklet
/image/` folder in a way that makes it added into Polymer generated server resource pack. See [Polymer's guide](https://polymer.pb4.eu/latest/user/resource-pack-custom-assets/) in case of
wanting to do so yourself on a custom server. Booklet will then process the image and allow embedding it within pages.
The suggested dimensions of the image are using a width slightly below or equal to multiplies of 300 (for example 300, 540, 295),
as it will allow them to display bigger on the screen.

## Accessing the page.
Simplest to access pages is via the Guidebook item. It doesn't have a recipe by default, so unless you add it yourself you can get it via
`/give @s booklet:guidebook` command. By default, it will show Booklet-provided page that shows all pages within the `booklet:main_page` category.
To change which page it will point to, you need to define the `booklet:page` component as the id of the page (example command 
`/give @s booklet:guidebook[booklet:page='polyfactory:main_page']`). The Guidebooks name, description and look will then update to one defined by the page.
With this you can for example add a custom recipe that sets that component.

Other way is to use `booklet:open_page` custom click action with `{entry: "namespace:path"}` tag value.

## Example page definitions.
As an example/reference you can check the PolyFactory's booklet page definitions, which are stored here:
- https://github.com/Patbox/PolyFactory/tree/master/src/main/resources/data/polyfactory/booklet/pages/

You can also see used images here:
- https://github.com/Patbox/PolyFactory/tree/master/src/main/resources/assets/polyfactory/textures/booklet/image