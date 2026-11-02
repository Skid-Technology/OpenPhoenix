package xyz.refinedev.phoenix.commands;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.impl.tags.TagsMenu;
import xyz.refinedev.phoenix.packet.impl.server.ServerCommandPacket;
import xyz.refinedev.phoenix.packet.impl.tags.TagUpdatePacket;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.profile.tag.ITagCategory;
import xyz.refinedev.phoenix.profile.tag.Tag;
import xyz.refinedev.phoenix.profile.tag.TagCategory;
import xyz.refinedev.phoenix.repository.ITagRepository;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.JsonTypes;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PaginatedListClickable;
import xyz.refinedev.phoenix.utils.ScheduleWindow;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Help
@Register(
        name = "tag",
        aliases = {"tags", "prefix", "prefixes"})
public class TagCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender Player player) {
        this.plugin.getMenuHandler().openMenu(new TagsMenu(), player);
    }

    @Command(name = "tools import", description = "Import tags from tags.json", async = true)
    @Requires("core.command.tags.admin")
    public void toolsImport(@Sender CommandSender sender) throws java.io.IOException {
        File file;
        if (!(file = new File("plugins/Phoenix/", "tags.json")).exists()) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.TAGS.FILE_MISSING));
        }
        List<ITag> values =
                (List)
                        this.plugin
                                .getGson()
                                .fromJson(Files.toString(file, Charsets.UTF_8), JsonTypes.type2);
        ITagRepository tagRepository;
        (tagRepository = this.plugin.getTagHandler().getTagRepository()).getCache().clear();
        tagRepository.deleteAll();
        for (ITag tag : values) {
            this.plugin.getTagHandler().getTagRepository().saveSync(tag);
            this.plugin.getPacketHandler().sendSync(new TagUpdatePacket(tag, false));
        }
        this.plugin.getPacketHandler().send(new ServerCommandPacket("all", "stop", "Console"));
        sender.sendMessage(Color.translate(LocaleUtil.tr(sender, MessageConfig.TAGS.IMPORTED)));
    }

    @Command(name = "tools export", description = "Export tags to tags.json", async = true)
    @Requires("core.command.tags.admin")
    public void toolsExport(@Sender CommandSender sender) throws java.io.IOException {
        File file;
        if (!(file = new File("plugins/Phoenix/")).exists()) {
            file.mkdirs();
        }
        File file2;
        if (!(file2 = new File(file, "tags.json")).exists()) {
            this.plugin.saveResource("tags.json", false);
        }
        List<ITag> sortedTags = this.plugin.getTagHandler().getSortedTags();
        Files.write(this.plugin.getGson().toJson(sortedTags), file2, Charsets.UTF_8);
        sender.sendMessage(Color.translate(LocaleUtil.tr(sender, MessageConfig.TAGS.EXPORTED)));
    }

    @Command(name = "list")
    @Requires("core.command.tags.admin")
    public void list(@Sender CommandSender sender, @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable("Tags", null, "/chattagsadmin list");
        ArrayList<Clickable> values = new ArrayList<>();
        for (ITag tag : this.plugin.getTagHandler().getTagRepository().getCache()) {
            Clickable clickable;
            (clickable = new Clickable())
                    .add(LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT));
            clickable.add(tag.getColor() + tag.getName() + " &7(" + tag.getPrefix() + "&7)");
            values.add(clickable);
        }
        paginatedListClickable.display(sender, value, values);
    }

    @Command(name = "delete")
    @Requires("core.command.tags.admin")
    public void delete(@Sender CommandSender sender, ITag tag) {
        this.plugin.getTagHandler().deleteTag(tag);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.TAGS.DELETED)
                                .replace("<tag>", tag.getName())));
    }

    @Command(name = "create")
    @Requires("core.command.tags.admin")
    public void create(@Sender CommandSender sender, @Named("name") String text) {
        ITag tag;
        if ((tag = this.plugin.getTagHandler().getTag(text)) != null) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.TAGS.ALREADY_EXISTS)
                            .replace("<tag>", tag.getName()));
        }
        Tag tag2 = new Tag(text);
        this.plugin.getTagHandler().getTagRepository().save(tag2);
        this.plugin.getPacketHandler().send(new TagUpdatePacket(tag2, false));
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.TAGS.CREATED)
                                .replace("<tag>", tag2.getName())));
    }

    @Command(name = "tag", aliases = "settag")
    @Requires("core.command.tags.admin")
    public void tag(@Sender CommandSender sender, ITag tag2, @Named("prefix") @Text String text) {
        tag2.setPrefix(text);
        this.plugin.getTagHandler().updateTag(tag2);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.TAGS.PREFIX_UPDATED)
                                .replace("<tag>", tag2.getName())
                                .replace("<prefix>", tag2.getPrefix())
                                .replace("<price>", Color.translate(tag2.getPrefix()))));
    }

    @Command(
            name = "schedule",
            aliases = "setschedule",
            description = "Limit a tag to a date window, or 'clear' to lift it")
    @Requires("core.command.tags.admin")
    public void schedule(
            @Sender CommandSender sender,
            ITag tag,
            @Named("from") String text,
            @Optional @Named("until") String text2) {
        if (text.equalsIgnoreCase("clear")) {
            tag.setActiveFrom(null);
            tag.setActiveUntil(null);
            this.plugin.getTagHandler().updateTag(tag);
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.TAGS.SCHEDULE_CLEARED)
                                    .replace("<tag>", tag.getColor() + tag.getName())));
        } else if (text2 != null && ScheduleWindow.isValid(text) && ScheduleWindow.isValid(text2)) {
            tag.setActiveFrom(text.trim());
            tag.setActiveUntil(text2.trim());
            this.plugin.getTagHandler().updateTag(tag);
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.TAGS.SCHEDULED)
                                    .replace("<tag>", tag.getColor() + tag.getName())
                                    .replace("<from>", text.trim())
                                    .replace("<until>", text2.trim())));
        } else {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.TAGS.INVALID_SCHEDULE));
        }
    }

    @Command(name = "price", aliases = "setprice")
    @Requires("core.command.tags.admin")
    public void price(@Sender CommandSender sender, ITag tag, @Named("price") int value) {
        tag.setPrice(value);
        this.plugin.getTagHandler().updateTag(tag);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.TAGS.PRICE_UPDATED)
                                .replace("<tag>", tag.getName())
                                .replace("<price>", String.valueOf(tag.getPrice()))));
    }

    @Command(name = "purchasable", aliases = "setpurchasable")
    @Requires("core.command.tags.admin")
    public void purchasable(
            @Sender CommandSender sender, ITag tag, @Optional @Named("purchasable") String text) {
        boolean matches = !tag.isPurchasable();
        if (text != null) {
            matches = Boolean.parseBoolean(text);
        }
        tag.setPurchasable(matches);
        this.plugin.getTagHandler().updateTag(tag);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.TAGS.PURCHASABLE_UPDATED)
                                .replace("<tag>", tag.getName())
                                .replace("<status>", String.valueOf(matches))));
    }

    @Command(name = "color", aliases = "setcolor")
    @Requires("core.command.tags.admin")
    public void color(@Sender CommandSender sender, ITag tag, @Named("color") String text) {
        tag.setColor(text);
        this.plugin.getTagHandler().updateTag(tag);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.TAGS.COLOR_UPDATED)
                                .replace("<tag>", tag.getName())
                                .replace("<color>", tag.getColor())));
    }

    @Command(name = "priority", aliases = "setpriority")
    @Requires("core.command.tags.admin")
    public void priority(@Sender CommandSender sender, ITag tag, @Named("priority") int value) {
        tag.setPriority(value);
        this.plugin.getTagHandler().updateTag(tag);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.TAGS.PRIORITY_UPDATED)
                                .replace("<tag>", tag.getName())
                                .replace("<priority>", String.valueOf(tag.getPriority()))));
    }

    @Command(name = "category default")
    @Requires("core.command.tags.admin")
    public void categoryDefault(
            @Sender CommandSender sender,
            ITagCategory tagCategory,
            @Named("default") boolean matches) {
        tagCategory.setDef(matches);
        this.plugin.getTagHandler().updateCategory(tagCategory);
        if (tagCategory.isDef()) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.TAGS.CATEGORY.DEFAULT_ON)
                                    .replace("<category>", tagCategory.getName())));
        } else {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.TAGS.CATEGORY.DEFAULT_OFF)
                                    .replace("<category>", tagCategory.getName())));
        }
    }

    @Command(name = "category visible")
    @Requires("core.command.tags.admin")
    public void categoryVisible(
            @Sender CommandSender sender,
            ITagCategory tagCategory,
            @Named("hidden") boolean matches) {
        tagCategory.setVisible(matches);
        this.plugin.getTagHandler().updateCategory(tagCategory);
        if (tagCategory.isVisible()) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.TAGS.CATEGORY.VISIBLE)
                                    .replace("<category>", tagCategory.getName())));
        } else {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.TAGS.CATEGORY.INVISBLE)
                                    .replace("<category>", tagCategory.getName())));
        }
    }

    @Command(name = "category priority")
    @Requires("core.command.tags.admin")
    public void categoryPriority(
            @Sender CommandSender sender, ITagCategory tagCategory, @Named("priority") int value) {
        String.valueOf(tagCategory.getPriority());
        tagCategory.setPriority(value);
        this.plugin.getTagHandler().updateCategory(tagCategory);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.TAGS.CATEGORY.PRIORITY)
                                .replace("<tag>", tagCategory.getName())
                                .replace("<priority>", String.valueOf(tagCategory.getPriority()))));
    }

    @Command(name = "category create")
    @Requires("core.command.tags.admin")
    public void categoryCreate(@Sender CommandSender sender, @Named("name") String text) {
        ITagCategory tagCategory;
        if ((tagCategory = this.plugin.getTagHandler().getTagCategory(text)) != null) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.TAGS.CATEGORY.ALREADY_EXISTS)
                            .replace("<tag>", tagCategory.getName()));
        }
        TagCategory tagCategory2 = new TagCategory(text);
        this.plugin.getTagHandler().updateCategory(tagCategory2);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.TAGS.CATEGORY.CREATED)
                                .replace("<tag>", tagCategory2.getName())));
    }

    @Command(name = "category delete")
    @Requires("core.command.tags.admin")
    public void categoryDelete(@Sender CommandSender sender, ITagCategory tagCategory) {
        if (tagCategory.isDef()) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.TAGS.CATEGORY.CANT_DELETE_DEFAULT));
        }
        this.plugin.getTagHandler().deleteCategory(tagCategory);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.TAGS.CATEGORY.DELETED)
                                .replace("<tag>", tagCategory.getName())));
    }

    @Command(name = "category tags add")
    @Requires("core.command.tags.admin")
    public void categoryTagsAdd(@Sender CommandSender sender, ITagCategory tagCategory, ITag tag) {
        if (tagCategory.getTags().contains(tag.getName())) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.TAGS.CATEGORY.ALREADY_HAS_TAG)
                            .replace("<tag>", tag.getName())
                            .replace("<category>", tagCategory.getName()));
        }
        tagCategory.getTags().add(tag.getName());
        this.plugin.getTagHandler().updateCategory(tagCategory);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.TAGS.CATEGORY.TAG_ADDED)
                                .replace("<tag>", tag.getName())
                                .replace("<category>", tagCategory.getName())));
    }

    @Command(name = "category tags delete")
    @Requires("core.command.tags.admin")
    public void categoryTagsDelete(
            @Sender CommandSender sender, ITagCategory tagCategory, ITag tag) {
        if (!tagCategory.getTags().contains(tag.getName())) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.TAGS.CATEGORY.DOESNT_HAVE_TAG)
                            .replace("<tag>", tag.getName())
                            .replace("<category>", tagCategory.getName()));
        }
        tagCategory.getTags().remove(tag.getName());
        this.plugin.getTagHandler().updateCategory(tagCategory);
        sender.sendMessage(
                Color.translate(LocaleUtil.tr(sender, MessageConfig.TAGS.CATEGORY.TAG_REMOVED))
                        .replace("<tag>", tag.getName())
                        .replace("<category>", tagCategory.getName()));
    }

    @Command(name = "category tags list")
    @Requires("core.command.tags.admin")
    public void categoryTagsList(
            @Sender CommandSender sender,
            ITagCategory tagCategory,
            @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable(
                        "Tags",
                        tagCategory.getName(),
                        "/chattagsadmin category tags list " + tagCategory.getName());
        ArrayList<Clickable> values = new ArrayList<>();
        for (ITag tag : tagCategory.getAsTags()) {
            Clickable clickable;
            (clickable = new Clickable())
                    .add(LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT));
            clickable.add(tag.getColor() + tag.getName() + " &7(" + tag.getPrefix() + "&7)");
            values.add(clickable);
        }
        paginatedListClickable.display(sender, value, values);
    }

    @Command(name = "category list")
    @Requires("core.command.tags.admin")
    public void categoryList(@Sender CommandSender sender, @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable("Categories", null, "/chattagsadmin category list");
        ArrayList<Clickable> values = new ArrayList<>();
        for (ITagCategory tagCategory :
                this.plugin.getTagHandler().getCategoryRepository().getCache()) {
            Clickable clickable;
            (clickable = new Clickable())
                    .add(LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT));
            clickable.add(tagCategory.getName());
            values.add(clickable);
        }
        paginatedListClickable.display(sender, value, values);
    }

    public TagCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
