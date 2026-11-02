package xyz.refinedev.phoenix.commands;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.bukkit.Bukkit;
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
import xyz.refinedev.phoenix.commands.api.annotations.RequireSecurity;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.handler.ChatHandler;
import xyz.refinedev.phoenix.menu.impl.rank.RankEditorMenu;
import xyz.refinedev.phoenix.packet.impl.ranks.RankUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.server.ServerCommandPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.rank.Rank;
import xyz.refinedev.phoenix.rank.permission.IPermission;
import xyz.refinedev.phoenix.rank.permission.Permission;
import xyz.refinedev.phoenix.repository.IRankRepository;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.scope.ScopeExpression;
import xyz.refinedev.phoenix.scope.ScopeParser;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.JsonTypes;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PaginatedListClickable;
import xyz.refinedev.phoenix.utils.StringUtils;
import xyz.refinedev.phoenix.utils.chat.Clickable;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Help
@RequireSecurity
@Register(name = "rank", aliases = "ranks")
@Requires("core.command.rank")
public class RankCommand {

    private final Phoenix plugin;

    boolean enabled = false;

    @Command(name = "editor")
    public void editor(@Sender Player player) {
        this.plugin.getMenuHandler().openMenu(new RankEditorMenu(), player);
    }

    @Command(name = "create", description = "Create a rank")
    public void create(@Sender CommandSender sender, @Named("name") String text) {
        if (this.plugin.getRankHandler().getRank(text) != null) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.ALREADY_EXISTS)
                            .replace("<rank>", text));
        }
        Rank rank = new Rank(text);
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.CREATED).replace("<rank>", text)));
    }

    @Command(name = "delete", description = "Delete an existing rank")
    public void delete(@Sender CommandSender sender, IRank rank) {
        this.plugin.getRankHandler().deleteRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.DELETED)
                                .replace("<rank>", rank.getDisplayName())));
    }

    @Command(name = "staff", description = "Marks the rank as a staff rank")
    public void staff(
            @Sender CommandSender sender, IRank rank, @Optional @Named("staff") String text) {
        boolean matches = !rank.isStaff();
        if (text != null) {
            matches = Boolean.parseBoolean(text);
        }
        rank.setStaff(matches);
        this.plugin.getRankHandler().updateRank(rank);
        if (rank.isStaff()) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.RANK_STATE.STAFF_ON)
                                    .replace("<rank>", rank.getDisplayName())));
        } else {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.RANK_STATE.STAFF_OFF)
                                    .replace("<rank>", rank.getDisplayName())));
        }
    }

    @Command(
            name = "default",
            description = "Makes the rank the default rank that is given to everyone")
    public void setDefault(
            @Sender CommandSender sender, IRank rank, @Optional @Named("default") String text) {
        boolean matches = !rank.isDefaultRank();
        if (text != null) {
            matches = Boolean.parseBoolean(text);
        }
        rank.setDefaultRank(matches);
        this.plugin.getRankHandler().updateRank(rank, true);
        if (rank.isDefaultRank()) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.RANK_STATE.DEFAULT_ON)
                                    .replace("<rank>", rank.getDisplayName())));
        } else {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.RANK_STATE.DEFAULT_OFF)
                                    .replace("<rank>", rank.getDisplayName())));
        }
    }

    @Command(name = "price", description = "Sets the price of purchasable rank")
    public void price(@Sender CommandSender sender, IRank rank, @Named("price") int value) {
        rank.setPrice(value);
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.PRICE_UPDATED)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<price>", String.valueOf(rank.getPrice()))));
    }

    @Command(name = "grantable", description = "Allows the rank to be granted")
    public void grantable(
            @Sender CommandSender sender, IRank rank, @Optional @Named("grantable") String text) {
        boolean matches = !rank.isGrantable();
        if (text != null) {
            matches = Boolean.parseBoolean(text);
        }
        rank.setGrantable(matches);
        this.plugin.getRankHandler().updateRank(rank);
        if (rank.isGrantable()) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.RANK_STATE.GRANTABLE_ON)
                                    .replace("<rank>", rank.getDisplayName())));
        } else {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.RANK_STATE.GRANTABLE_OFF)
                                    .replace("<rank>", rank.getDisplayName())));
        }
    }

    @Command(name = "priority", description = "Sets the priority of rank")
    public void priority(@Sender CommandSender sender, IRank rank, @Named("priority") int value) {
        rank.setPriority(value);
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.PRIORITY_UPDATED)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<priority>", String.valueOf(rank.getPriority()))));
    }

    @Command(name = "scope add", description = "Adds rank to a server or a group")
    public void scopeAdd(@Sender CommandSender sender, IRank rank, IScope scope) {
        if (rank.hasScope(scope)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.RANK_SCOPES.ALREADY_HAS)
                            .replace("<scope>", scope.getDisplayName())
                            .replace("<rank>", rank.getDisplayName()));
        }
        rank.getScopes().add(scope);
        this.plugin.getRankHandler().updateRank(rank, true);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.RANK_SCOPES.ADDED)
                                .replace("<scope>", scope.getDisplayName())
                                .replace("<rank>", rank.getDisplayName())));
    }

    @Command(name = "scope delete", description = "Removes rank from server or a group")
    public void scopeDelete(@Sender CommandSender sender, IRank rank, IScope scope) {
        if (!rank.hasScope(scope)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.RANK_SCOPES.DOESNT_HAVE)
                            .replace("<scope>", scope.getDisplayName())
                            .replace("<rank>", rank.getDisplayName()));
        }
        rank.getScopes().remove(scope);
        this.plugin.getRankHandler().updateRank(rank, true);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.RANK_SCOPES.REMOVED)
                                .replace("<scope>", scope.getDisplayName())
                                .replace("<rank>", rank.getDisplayName())));
    }

    @Command(name = "scope list", description = "View servers on what the rank applies")
    public void scopeList(
            @Sender CommandSender sender, IRank rank, @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable(
                        "Scopes", rank.getDisplayName(), "/rank scopes list " + rank.getName());
        ArrayList<Clickable> values = new ArrayList<>();
        for (IScope scope : rank.getScopes()) {
            Clickable clickable;
            (clickable = new Clickable())
                    .add(LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT));
            if (scope.isServer()) {
                clickable.add(
                        this.plugin.getNetworkHandler().isServerOnline(scope.getServerName())
                                ? "&a"
                                : "&c");
            }
            clickable.add(scope.getDisplayName());
            values.add(clickable);
        }
        paginatedListClickable.display(sender, value, values);
    }

    @Command(name = "perm cmd add", description = "Adds the command permission")
    public void permCmdAdd(
            @Sender CommandSender sender,
            IRank rank,
            @Named("command") String text,
            @Default("global") @Named("scopes") String text2) {
        org.bukkit.command.Command command2;
        if ((command2 =
                        this.plugin
                                .getCommandHandler()
                                .getRegistry()
                                .getCommandMap()
                                .getCommand(text))
                == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.RANK_PERMS.COMMAND_DOESNT_EXIST));
        }
        String text3;
        if ((text3 = command2.getPermission()) != null && !text3.isEmpty()) {
            this.permAdd(sender, rank, text3, text2);
        } else {
            throw new ExitMessage(
                    LocaleUtil.tr(
                            sender, MessageConfig.RANK.RANK_PERMS.COMMAND_DOESNT_HAVE_PERMISSION));
        }
    }

    @Command(name = "perm cmd delete", description = "Removes the command permission")
    public void permCmdDelete(
            @Sender CommandSender sender, IRank rank, @Named("command") String text) {
        org.bukkit.command.Command command2;
        if ((command2 =
                        this.plugin
                                .getCommandHandler()
                                .getRegistry()
                                .getCommandMap()
                                .getCommand(text))
                == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.RANK_PERMS.COMMAND_DOESNT_EXIST));
        }
        String text2;
        if ((text2 = command2.getPermission()) != null && !text2.isEmpty()) {
            this.permDelete(sender, rank, text2);
        } else {
            throw new ExitMessage(
                    LocaleUtil.tr(
                            sender, MessageConfig.RANK.RANK_PERMS.COMMAND_DOESNT_HAVE_PERMISSION));
        }
    }

    @Command(name = "perm add", description = "Adds permission to a rank")
    public void permAdd(
            @Sender CommandSender sender,
            IRank rank,
            @Named("permission") String text,
            @Default("global") @Named("scopes") String text2) {
        if (rank.hasPermission(text)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.RANK_PERMS.ALREADY_HAS_PERMISSION)
                            .replace("<perm>", text)
                            .replace("<rank>", rank.getDisplayName()));
        }
        if (!IScope.isValidMultiple(text2)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.ERROR.INVALID_SCOPES)
                            .replace("<input>", text2));
        }
        rank.getPermissions().add(new Permission(text, Scope.findList(text2)));
        this.plugin.getRankHandler().updateRank(rank, true);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.RANK_PERMS.ADDED)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<perm>", text)));
    }

    @Command(name = "perm mass-add", description = "Mass add a permission to a rank")
    public void permMassAdd(
            @Sender CommandSender sender,
            IRank rank,
            @Named("permission (Split with ;)") String text,
            @Optional @Named("scopes") String text2) {
        if (!text.contains(";")) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.ERROR.SPLIT_PERMISSIONS));
        }
        if (text2 != null && !IScope.isValidMultiple(text2)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.ERROR.INVALID_SCOPES)
                            .replace("<input>", text2));
        }
        List values =
                text2 != null ? Scope.findList(text2) : Collections.singletonList(Scope.scopeScope);
        String[] texts;
        int value = (texts = text.split(";")).length;
        for (int index = 0; index < value; index++) {
            String text3 = texts[index];
            if (rank.hasPermission(text3)) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(
                                                sender,
                                                MessageConfig.RANK
                                                        .RANK_PERMS
                                                        .ALREADY_HAS_PERMISSION)
                                        .replace("<perm>", text)
                                        .replace("<rank>", rank.getDisplayName())));
            } else {
                rank.getPermissions().add(new Permission(text3, values));
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.RANK.RANK_PERMS.ADDED)
                                        .replace("<rank>", rank.getDisplayName())
                                        .replace("<perm>", text3)));
            }
        }
        this.plugin.getRankHandler().updateRank(rank, true);
    }

    @Command(name = "perm set-scopes", description = "Set scopes for a permission")
    public void permSetScopes(
            @Sender CommandSender sender,
            IRank rank,
            @Named("permission") String text,
            @Named("scopes") String text2) {
        if (!rank.hasPermission(text)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.RANK_PERMS.DOESNT_HAVE_PERMISSION)
                            .replace("<perm>", text)
                            .replace("<rank>", rank.getDisplayName()));
        }
        if (!IScope.isValidMultiple(text2)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.ERROR.INVALID_SCOPES)
                            .replace("<input>", text2));
        }
        List<IScope> values = Scope.findList(text2);
        rank.getPermission(text).setScopes(values);
        this.plugin.getRankHandler().updateRank(rank, true);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.RANK_PERMS.SET_SCOPES)
                                .replace("<scopes>", Scope.findList(values).toString())
                                .replace("<perm>", text)));
    }

    @Command(name = "perm delete", description = "Removes permission from rank")
    public void permDelete(
            @Sender CommandSender sender, IRank rank, @Named("permission") String text) {
        if (!rank.hasPermission(text)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.RANK_PERMS.DOESNT_HAVE_PERMISSION)
                            .replace("<perm>", text)
                            .replace("<rank>", rank.getDisplayName()));
        }
        rank.removePermission(text);
        this.plugin.getRankHandler().updateRank(rank, true);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.RANK_PERMS.REMOVED)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<perm>", text)));
    }

    @Command(name = "perm clear", description = "Clear permissions of rank")
    public void permClear(@Sender CommandSender sender, IRank rank) {
        rank.getPermissions().clear();
        this.plugin.getRankHandler().updateRank(rank, true);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.RANK_PERMS.CLEARED)
                                .replace("<rank>", rank.getDisplayName())));
    }

    @Command(name = "perm list", description = "View permissions of rank")
    public void permList(
            @Sender CommandSender sender, IRank rank, @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable(
                        "Permissions", rank.getDisplayName(), "/rank perm list " + rank.getName());
        ArrayList<Clickable> values = new ArrayList<>();
        for (IPermission permission : rank.getPermissions()) {
            Clickable clickable;
            (clickable = new Clickable())
                    .add(LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT));
            clickable.add(permission.getPermission() + " &7" + permission.getNiceScopes());
            values.add(clickable);
        }
        paginatedListClickable.display(sender, value, values);
    }

    @Command(name = "perm search", description = "Search for a permission")
    public void permSearch(
            @Sender CommandSender sender,
            @Named("permission") String text,
            @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable("Ranks", text, "/rank perm search " + text);
        ArrayList<Clickable> values = new ArrayList<>();
        Iterator<IRank> iterator2 =
                this.plugin.getRankHandler().getSortedRanksReversed().iterator();
        while (iterator2.hasNext()) {
            IRank rank;
            IPermission permission;
            if ((permission = (rank = (IRank) iterator2.next()).getPermission(text)) != null) {
                Clickable clickable;
                (clickable = new Clickable())
                        .add(LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT));
                clickable.add(
                        rank.getDisplayName()
                                + " &7- "
                                + text
                                + " &7"
                                + permission.getNiceScopes());
                values.add(clickable);
            }
        }
        paginatedListClickable.display(sender, value, values);
    }

    @Command(name = "inheritance add", description = "Adds rank as inheritance")
    public void inheritanceAdd(
            @Sender CommandSender sender, IRank rank, @Named("inheritance") IRank rank2) {
        if (rank.hasInheritance(rank2)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.RANK_INHERITANCE.ALREADY_HAS)
                            .replace("<inheritance>", rank2.getDisplayName())
                            .replace("<rank>", rank.getDisplayName()));
        }
        IRank rank3;
        if ((rank3 = rank.findDependencyLock(rank2)) == null) {
            rank.addInheritance(rank2);
            this.plugin.getRankHandler().updateRank(rank, true);
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.RANK_INHERITANCE.ADDED)
                                    .replace("<inheritance>", rank2.getDisplayName())
                                    .replace("<rank>", rank.getDisplayName())));
        } else {
            Iterator<String> iterator2 =
                    LocaleUtil.tr(sender, MessageConfig.RANK.RANK_INHERITANCE.LOCKED).iterator();
            while (iterator2.hasNext()) {
                String text =
                        ((String) iterator2.next())
                                .replace("<childRank>", rank2.getDisplayName())
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<causedRank>", rank3.getDisplayName());
                sender.sendMessage(Color.translate(text));
            }
        }
    }

    @Command(name = "inheritance delete")
    public void inheritanceDelete(
            @Sender CommandSender sender, IRank rank, @Named("inheritance") IRank rank2) {
        if (!rank.hasInheritance(rank2)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.RANK_INHERITANCE.DOESNT_HAVE)
                            .replace("<inheritance>", rank2.getDisplayName())
                            .replace("<rank>", rank.getDisplayName()));
        }
        rank.removeInheritance(rank2);
        this.plugin.getRankHandler().updateRank(rank, true);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.RANK_INHERITANCE.REMOVED)
                                .replace("<inheritance>", rank2.getDisplayName())
                                .replace("<rank>", rank.getDisplayName())));
    }

    @Command(name = "inheritance list", description = "View inheritance list of rank")
    public void inheritanceList(
            @Sender CommandSender sender, IRank rank, @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable(
                        "Inheritance", rank.getDisplayName(), "/rank child list " + rank.getName());
        ArrayList<Clickable> values = new ArrayList<>();
        for (UUID uUID : rank.getInheritance()) {
            Clickable clickable;
            (clickable = new Clickable())
                    .add(LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT));
            IRank rank2;
            if ((rank2 = this.plugin.getRankHandler().getRank(uUID)) != null) {
                clickable.add(Color.translate(rank2.getDisplayName()));
                values.add(clickable);
            }
        }
        paginatedListClickable.display(sender, value, values);
    }

    @Command(name = "visible", description = "Makes the rank visible")
    public void visible(
            @Sender CommandSender sender, IRank rank, @Optional @Named("visible") String text) {
        boolean matches = !rank.isVisible();
        if (text != null) {
            matches = Boolean.parseBoolean(text);
        }
        rank.setVisible(matches);
        this.plugin.getRankHandler().updateRank(rank);
        if (rank.isVisible()) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.RANK_STATE.VISIBLE_ON)
                                    .replace("<rank>", rank.getDisplayName())));
        } else {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.RANK_STATE.VISIBLE_OFF)
                                    .replace("<rank>", rank.getDisplayName())));
        }
    }

    @Command(name = "playerlist", description = "Changes the name tag prefix")
    public void playerlist(
            @Sender CommandSender sender,
            IRank rank,
            @Optional @Named("prefix") @Text String text) {
        if (text != null) {
            rank.setPlayerListPrefix(Color.translate(text));
        } else {
            rank.setPlayerListPrefix("");
        }
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.PLAYERLIST_PREFIX)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<prefix>", rank.getPlayerListPrefix())));
    }

    @Command(
            name = "playerlist-modern",
            description = "Changes the name tag prefix on modern versions")
    public void playerlistModern(
            @Sender CommandSender sender,
            IRank rank,
            @Optional @Named("prefix") @Text String text) {
        if (text != null) {
            rank.setPlayerListPrefixModern(Color.translate(text));
        } else {
            rank.setPlayerListPrefixModern("");
        }
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.PLAYERLIST_PREFIX)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<prefix>", rank.getPlayerListPrefixModern())));
    }

    @Command(name = "prefix", description = "Changes the rank prefix")
    public void prefix(
            @Sender CommandSender sender,
            IRank rank,
            @Optional @Named("prefix") @Text String text) {
        if (text != null) {
            rank.setPrefix(Color.translate(text));
        } else {
            rank.setPrefix("");
        }
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.PREFIX_UPDATED)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<prefix>", rank.getPrefix())));
    }

    @Command(name = "prefix-modern", description = "Changes the rank prefix for modern versions")
    public void prefixModern(
            @Sender CommandSender sender,
            IRank rank,
            @Optional @Named("prefix") @Text String text) {
        if (text != null) {
            rank.setPrefixModern(Color.translate(text));
        } else {
            rank.setPrefixModern("");
        }
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.PREFIX_UPDATED)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<prefix>", rank.getPrefixModern())));
    }

    @Command(name = "suffix", description = "Changes the rank suffix")
    public void suffix(
            @Sender CommandSender sender,
            IRank rank,
            @Optional @Named("suffix") @Text String text) {
        if (text != null) {
            rank.setSuffix(Color.translate(text));
        } else {
            rank.setSuffix("");
        }
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.SUFFIX_UPDATED)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<suffix>", Color.translate(rank.getSuffix()))));
    }

    @Command(name = "suffix-modern", description = "Changes the rank suffix on modern versions")
    public void suffixModern(
            @Sender CommandSender sender,
            IRank rank,
            @Optional @Named("suffix") @Text String text) {
        if (text != null) {
            rank.setSuffixModern(Color.translate(text));
        } else {
            rank.setSuffixModern("");
        }
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.SUFFIX_UPDATED)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<suffix>", Color.translate(rank.getSuffixModern()))));
    }

    @Command(
            name = "hover set",
            description = "Sets the rank's chat hover description. Use \\n for new lines.")
    public void hoverSet(
            @Sender CommandSender sender, IRank rank, @Named("description") @Text String text) {
        rank.setMetadata("hover-description", text);
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.RANK_HOVER.SET)
                                .replace("<rank>", rank.getDisplayName())));
    }

    @Command(
            name = "hover clear",
            description = "Clears the rank's chat hover (falls back to the global default)")
    public void hoverClear(@Sender CommandSender sender, IRank rank) {
        rank.removeMetadata("hover-description");
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.RANK_HOVER.CLEARED)
                                .replace("<rank>", rank.getDisplayName())));
    }

    @Command(
            name = "hover url",
            description = "Sets the URL opened when clicking the rank prefix. Omit to clear.")
    public void hoverUrl(
            @Sender CommandSender sender, IRank rank, @Optional @Named("url") String text) {
        if (text != null && !text.isEmpty()) {
            rank.setMetadata("hover-url", text);
            this.plugin.getRankHandler().updateRank(rank);
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.RANK_HOVER.URL_SET)
                                    .replace("<rank>", rank.getDisplayName())
                                    .replace("<url>", text)));
        } else {
            rank.removeMetadata("hover-url");
            this.plugin.getRankHandler().updateRank(rank);
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.RANK_HOVER.URL_CLEARED)
                                    .replace("<rank>", rank.getDisplayName())));
        }
    }

    @Command(
            name = "shadow",
            description =
                    "Sets the rank prefix's text shadow colour on 1.21.4+ clients. Omit to clear.")
    public void shadow(
            @Sender CommandSender sender, IRank rank, @Optional @Named("hex") String text) {
        if (text == null || text.isEmpty()) {
            rank.removeMetadata("shadow-color");
            this.plugin.getRankHandler().updateRank(rank);
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.SHADOW_CLEARED)
                                    .replace("<rank>", rank.getDisplayName())));
        } else {
            if (ChatHandler.parseShadowColor(text) == null) {
                throw new ExitMessage(
                        LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.SHADOW_INVALID)
                                .replace("<hex>", text));
            }
            rank.setMetadata("shadow-color", text);
            this.plugin.getRankHandler().updateRank(rank);
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.SHADOW_SET)
                                    .replace("<rank>", rank.getDisplayName())
                                    .replace("<hex>", text)));
        }
    }

    @Command(name = "links add", description = "Adds a pause menu server link to a rank")
    public void linksAdd(
            @Sender CommandSender sender,
            IRank rank,
            @Named("type") String text,
            @Named("url") String text2,
            @Optional @Named("label") @Text String text3) {
        String text4 = text.trim().toUpperCase();
        boolean matches;
        if (!(matches = "CUSTOM".equals(text4)) && !ScopeParser.strings.contains(text4)) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.LINK_INVALID_TYPE)
                            .replace("<type>", text)
                            .replace(
                                    "<types>",
                                    String.join("&c, &f", new TreeSet<>(ScopeParser.strings))));
        }
        if (!matches || text3 != null && !text3.trim().isEmpty()) {
            String text5;
            if (ScopeParser.create(
                            text5 =
                                    matches
                                            ? "CUSTOM|" + text3.trim() + "|" + text2.trim()
                                            : text4 + "|" + text2.trim())
                    == null) {
                throw new ExitMessage(
                        LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.LINK_NOT_STORED));
            }
            List<String> values;
            (values = this.findList(rank, "server-links")).add(text5);
            this.applyRank(rank, "server-links", values);
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.LINK_ADDED)
                                    .replace("<rank>", rank.getDisplayName())
                                    .replace("<amount>", String.valueOf(values.size()))));
        } else {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.LINK_NEEDS_LABEL)
                            .replace("<rank>", rank.getName()));
        }
    }

    @Command(
            name = "links remove",
            description = "Removes a server link from a rank by its number in the list")
    public void linksRemove(@Sender CommandSender sender, IRank rank, @Named("index") int value) {
        List<String> values = this.findList(rank, "server-links");
        if (value >= 1 && value <= values.size()) {
            String text = (String) values.remove(value - 1);
            this.applyRank(rank, "server-links", values);
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.LINK_REMOVED)
                                    .replace("<link>", text)
                                    .replace("<rank>", rank.getDisplayName())));
        } else {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.LINK_NOT_FOUND)
                            .replace("<index>", String.valueOf(value)));
        }
    }

    @Command(name = "links list", description = "Lists a rank's extra server links")
    public void linksList(
            @Sender CommandSender sender, IRank rank, @Default("1") @Named("page") int value) {
        this.applyCommandSender(
                sender,
                rank,
                "server-links",
                "Server Links",
                "/rank links list " + rank.getName(),
                value,
                result -> {
                    ScopeExpression scopeExpression;
                    return (scopeExpression = ScopeParser.create(result)) == null
                            ? "&c" + result + " &7(unreadable)"
                            : (scopeExpression.isEnabled()
                                            ? "&f" + scopeExpression.getTextString() + " &7(custom)"
                                            : "&f" + scopeExpression.getText())
                                    + " &8- &7"
                                    + scopeExpression.getText2();
                });
    }

    @Command(name = "links clear", description = "Removes every extra server link from a rank")
    public void linksClear(@Sender CommandSender sender, IRank rank) {
        rank.removeMetadata("server-links");
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.LINKS_CLEARED)
                                .replace("<rank>", rank.getDisplayName())));
    }

    private List<String> findList(IRank rank, String text) {
        String metadata = rank.getMetadata(text);
        ArrayList<String> values = new ArrayList<>();
        if (metadata != null && !metadata.trim().isEmpty()) {
            String[] texts;
            int value = (texts = metadata.split(";")).length;
            for (int index = 0; index < value; index++) {
                String text2;
                if (!(text2 = texts[index]).trim().isEmpty()) {
                    values.add(text2.trim());
                }
            }
            return values;
        } else {
            return values;
        }
    }

    private void applyRank(IRank rank, String text, List<String> values) {
        if (values.isEmpty()) {
            rank.removeMetadata(text);
        } else {
            rank.setMetadata(text, String.join(";", values));
        }
        this.plugin.getRankHandler().updateRank(rank);
    }

    private void applyCommandSender(
            CommandSender sender,
            IRank rank,
            String text,
            String text2,
            String text3,
            int value,
            Function<String, String> function) {
        List<String> values;
        if ((values = this.findList(rank, text)).isEmpty()) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.NOTHING_SET)
                            .replace("<rank>", rank.getDisplayName())
                            .replace("<type>", text2.toLowerCase()));
        }
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable(text2, rank.getDisplayName(), text3);
        ArrayList<Clickable> values2 = new ArrayList<>();
        for (int index = 0; index < values.size(); index++) {
            Clickable clickable;
            (clickable = new Clickable())
                    .add(LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT));
            clickable.add(
                    "&7"
                            + (index + 1)
                            + ". "
                            + (String) function.apply((String) values.get(index)));
            values2.add(clickable);
        }
        paginatedListClickable.display(sender, value, values2);
    }

    @Command(name = "hover view", description = "Preview the rank's chat hover")
    public void hoverView(@Sender CommandSender sender, IRank rank) {
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.RANK_HOVER.VIEW_HEADER)
                                .replace("<rank>", rank.getDisplayName())));
        Clickable clickable;
        (clickable = new Clickable())
                .addComponent(this.plugin.getChatHandler().buildRankHoverPreview(rank));
        clickable.sendToPlayer(sender);
    }

    @Command(name = "color", description = "Changes the rank color")
    public void color(@Sender CommandSender sender, IRank rank, @Named("color") String text) {
        rank.setColor(text);
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.COLOR_UPDATED)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<color>", text)));
    }

    @Command(name = "color-modern", description = "Changes the rank color for modern versions")
    public void colorModern(@Sender CommandSender sender, IRank rank, @Named("color") String text) {
        rank.setColorModern(text);
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.COLOR_UPDATED)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<color>", text)));
    }

    @Command(name = "chatcolor", description = "Changes the rank chatcolor")
    public void chatcolor(
            @Sender CommandSender sender, IRank rank, @Named("chatcolor") String text) {
        rank.setChatColor(text);
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.CHATCOLOR_UPDATED)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<color>", text)));
    }

    @Command(
            name = "chatcolor-modern",
            description = "Changes the rank chatcolor for modern versions")
    public void chatcolorModern(
            @Sender CommandSender sender, IRank rank, @Named("chatcolor") String text) {
        rank.setChatColorModern(text);
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.CHATCOLOR_UPDATED)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<color>", text)));
    }

    @Command(
            name = "display",
            aliases = "displayname",
            description = "Changes the display name of rank")
    public void display(
            @Sender CommandSender sender, IRank rank, @Named("displayname") @Text String text) {
        rank.setDisplayName(text);
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.DISPLAY_UPDATED)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<displayName>", Color.translate(text))));
    }

    @Command(
            name = "display-modern",
            aliases = "displayname-modern",
            description = "Changes the display name of rank for modern versions")
    public void displayModern(
            @Sender CommandSender sender, IRank rank, @Named("displayname") @Text String text) {
        rank.setDisplayNameModern(text);
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.DISPLAY_UPDATED)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<displayName>", Color.translate(text))));
    }

    @Command(name = "subscription", description = "Marks the rank as a subscription rank")
    public void subscription(
            @Sender CommandSender sender,
            IRank rank,
            @Optional @Named("subscription") String text) {
        boolean matches = !rank.isSubscription();
        if (text != null) {
            matches = Boolean.parseBoolean(text);
        }
        rank.setSubscription(matches);
        this.plugin.getRankHandler().updateRank(rank);
        if (rank.isSubscription()) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.RANK_STATE.SUB_ON)
                                    .replace("<rank>", rank.getDisplayName())));
        } else {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.RANK_STATE.SUB_OFF)
                                    .replace("<rank>", rank.getDisplayName())));
        }
    }

    @Command(name = "purchasable", description = "Toggles the purchasable state of rank")
    public void purchasable(
            @Sender CommandSender sender, IRank rank, @Optional @Named("purchasable") String text) {
        boolean matches = !rank.isPurchasable();
        if (text != null) {
            matches = Boolean.parseBoolean(text);
        }
        rank.setPurchasable(matches);
        this.plugin.getRankHandler().updateRank(rank);
        if (rank.isPurchasable()) {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.RANK_STATE.PURCHASABLE_ON)
                                    .replace("<rank>", rank.getDisplayName())));
        } else {
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.RANK.RANK_STATE.PURCHASABLE_OFF)
                                    .replace("<rank>", rank.getDisplayName())));
        }
    }

    @Command(name = "rename", description = "Renames the rank")
    public void rename(@Sender CommandSender sender, IRank rank, @Named("name") String text) {
        rank.setName(text);
        this.plugin.getRankHandler().updateRank(rank);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.NAME_UPDATED)
                                .replace("<rank>", rank.getDisplayName())
                                .replace("<name>", text)));
    }

    @Command(name = "list", description = "View all ranks available")
    public void list(@Sender CommandSender sender, @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable("Ranks", null, "/rank list");
        ArrayList<Clickable> values = new ArrayList<>();
        List<IRank> sortedRanksReversed = this.plugin.getRankHandler().getSortedRanksReversed();
        sortedRanksReversed.forEach(
                result -> {
                    String text =
                            MessageConfig.GENERAL
                                    .RANK_LIST_FORMAT
                                    .replace("<color>", result.getColor())
                                    .replace("<displayName>", result.getDisplayName())
                                    .replace("<name>", result.getName())
                                    .replace(
                                            "<prefix>",
                                            !result.getPrefix().isEmpty()
                                                    ? result.getPrefix()
                                                    : "&cNone")
                                    .replace(
                                            "<suffix>",
                                            !result.getSuffix().isEmpty()
                                                    ? result.getSuffix()
                                                    : "&cNone")
                                    .replace("<priority>", String.valueOf(result.getPriority()));
                    Clickable clickable;
                    (clickable = new Clickable())
                            .add(
                                    text,
                                    "&7Prefix: "
                                            + result.getPrefix()
                                            + "&7("
                                            + result.getPrefix().replace("&", "x")
                                            + "&7)\n&7Suffix: "
                                            + result.getSuffix()
                                            + " &7("
                                            + result.getSuffix().replace("&", "x")
                                            + "&7)\n&7Priority: &f"
                                            + result.getPriority()
                                            + "\n&7Staff: &f"
                                            + result.isStaff()
                                            + "\n&7&k&r\n&7&oClick for more information",
                                    "/rank info " + result.getName());
                    values.add(clickable);
                });
        paginatedListClickable.display(sender, value, values);
    }

    @Command(name = "info", description = "View info of a rank")
    public void info(@Sender CommandSender sender, IRank rank) {
        Iterator<String> iterator2 = LocaleUtil.tr(sender, MessageConfig.RANK.RANK_INFO).iterator();
        while (iterator2.hasNext()) {
            String text =
                    ((String) iterator2.next())
                            .replace("<rank>", rank.getDisplayNameLegacy())
                            .replace("<rankModern>", rank.getDisplayNameModern())
                            .replace("<name>", rank.getName())
                            .replace("<uuid>", rank.getUuid().toString())
                            .replace(
                                    "<prefix>",
                                    !rank.getPrefixLegacy().isEmpty() ? rank.getPrefix() : "None")
                            .replace(
                                    "<prefixModern>",
                                    !rank.getPrefixModern().isEmpty() ? rank.getPrefix() : "None")
                            .replace(
                                    "<suffix>",
                                    !rank.getSuffixLegacy().isEmpty() ? rank.getSuffix() : "None")
                            .replace(
                                    "<suffixModern>",
                                    !rank.getSuffixModern().isEmpty() ? rank.getSuffix() : "None")
                            .replace("<color>", rank.getColor() + "Color")
                            .replace("<colorModern>", rank.getColorModern() + "Color")
                            .replace("<visible>", rank.isVisible() ? "&ayes" : "&cno")
                            .replace("<playerList>", rank.getPlayerListPrefixLegacy())
                            .replace("<playerListPrefixModern>", rank.getPlayerListPrefixModern())
                            .replace("<staff>", rank.isStaff() ? "&ayes" : "&cno")
                            .replace("<default>", rank.isDefaultRank() ? "&ayes" : "&cno")
                            .replace("<subscription>", rank.isSubscription() ? "&ayes" : "&cno")
                            .replace("<priority>", String.valueOf(rank.getPriority()))
                            .replace("<price>", String.valueOf(rank.getPrice()))
                            .replace("<grantable>", rank.isGrantable() ? "&ayes" : "&cno")
                            .replace("<purchasable>", rank.isPurchasable() ? "&ayes" : "&cno")
                            .replace(
                                    "<scopes>",
                                    StringUtils.getStringFromList(rank.getNiceScopes()));
            sender.sendMessage(Color.translate(text));
        }
    }

    @Command(name = "tools clear-children", description = "Clear children of all ranks")
    public void toolsClearChildren(@Sender CommandSender sender) {
        Iterator<IRank> iterator2 = this.plugin.getRankHandler().getAvailableRanks().iterator();
        while (iterator2.hasNext()) {
            IRank rank;
            (rank = (IRank) iterator2.next()).getInheritance().clear();
            this.plugin.getRankHandler().updateRank(rank);
        }
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.RANK_INHERITANCE.CLEARED)));
    }

    @Command(name = "tools child", description = "Adds child to every rank")
    public void toolsChild(@Sender CommandSender sender, IRank rank) {
        Iterator<IRank> iterator2 = this.plugin.getRankHandler().getAvailableRanks().iterator();
        while (iterator2.hasNext()) {
            IRank rank2;
            if (!(rank2 = (IRank) iterator2.next()).getUuid().equals(rank.getUuid())) {
                rank2.addInheritance(rank);
                this.plugin.getRankHandler().updateRank(rank);
            }
        }
        sender.sendMessage(
                Color.translate(
                                LocaleUtil.tr(
                                        sender, MessageConfig.RANK.RANK_INHERITANCE.ADDED_TO_ALL))
                        .replace("<child>", rank.getDisplayName()));
    }

    @Command(name = "tools clone", description = "Clone a rank")
    public void toolsClone(@Sender CommandSender sender, IRank rank, @Named("name") String text) {
        if (this.plugin.getRankHandler().getRank(text) != null) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.ALREADY_EXISTS)
                            .replace("<rank>", text));
        }
        Rank rank2;
        (rank2 = new Rank(text)).setInheritance(rank.getInheritance());
        rank2.setColor(rank.getColor());
        rank2.setColorModern(rank.getColorModern());
        rank2.setDefaultRank(rank.isDefaultRank());
        rank2.setName(rank.getName());
        rank2.setDisplayName(rank.getDisplayName());
        rank2.setDisplayNameModern(rank.getDisplayNameModern());
        rank2.setScopes(rank.getScopes());
        rank2.setPermissions(rank.getPermissions());
        rank2.setPrice(rank.getPrice());
        rank2.setGrantable(rank.isGrantable());
        rank2.setPrefix(rank.getPrefix());
        rank2.setSuffix(rank.getSuffix());
        rank2.setPlayerListPrefix(rank.getPlayerListPrefix());
        rank2.setPrefixModern(rank.getPrefixModern());
        rank2.setSuffixModern(rank.getSuffixModern());
        rank2.setPlayerListPrefixModern(rank.getPlayerListPrefixModern());
        rank2.setStaff(rank.isStaff());
        rank2.setPurchasable(rank.isPurchasable());
        rank2.setSubscription(rank.isSubscription());
        rank2.setVisible(rank.isVisible());
        rank2.setPriority(rank.getPriority());
        this.plugin.getRankHandler().updateRank(rank2);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.CLONED)
                                .replace("<rank>", rank.getName())
                                .replace("<newRank>", rank2.getName())));
    }

    @Command(name = "tools child-from-start", description = "Adds child from start every rank")
    public void toolsChildFromStart(@Sender CommandSender sender) {
        IRank rank = null;
        for (IRank rank2 : this.plugin.getRankHandler().getSortedRanks()) {
            if (rank != null) {
                if (rank2.hasInheritance(rank)) {
                    continue;
                }
                rank2.addInheritance(rank);
                this.plugin.getRankHandler().updateRank(rank2);
            }
            rank = rank2;
        }
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.RANK.RANK_INHERITANCE.FROM_START)));
    }

    @Command(name = "tools players", description = "View all players that have this rank")
    public synchronized void toolsPlayers(
            @Sender CommandSender sender, IRank rank, @Default("1") @Named("page") int value) {
        if (this.enabled) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.SEARCH_IN_PROGRESS));
        }
        this.enabled = true;
        sender.sendMessage(
                Color.translate(LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.SEARCH_FETCHING)));
        CompletableFuture<List<IGrant>> completableFuture =
                this.plugin.getGrantHandler().getRepository().fetchAll(Filters.eq("active", true));
        completableFuture.whenCompleteAsync(
                (result, result2) -> {
                    long now = System.currentTimeMillis();
                    ArrayList values = new ArrayList();
                    Map<UUID, List<IGrant>> entries = new HashMap();
                    Iterator iterator2 = result.iterator();
                    while (iterator2.hasNext()) {
                        IGrant grant;
                        if ((grant = (IGrant) iterator2.next()).isActive()) {
                            entries.computeIfAbsent(grant.getTarget(), result3 -> new ArrayList<>())
                                    .add(grant);
                        }
                    }
                    ArrayList values2 = FastStreams.mapArrayList(entries.keySet(), UUID::toString);
                    for (IProfile profile :
                            this.plugin
                                    .getProfileHandler()
                                    .getRepository()
                                    .fetchAllSync(Filters.in("_id", values2))) {
                        List values3 = (List) entries.get(profile.getUuid());
                        boolean matches = false;
                        Iterator iterator3 = values3.iterator();
                        while (iterator3.hasNext()) {
                            if (((IGrant) iterator3.next()).getRank() == rank) {
                                matches = true;
                                break;
                            }
                        }
                        if (matches) {
                            values.add(profile.getName());
                        }
                    }
                    sender.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.SEARCH_TOOK)
                                            .replace(
                                                    "<time>",
                                                    String.valueOf(
                                                            System.currentTimeMillis() - now))));
                    PaginatedListClickable paginatedListClickable =
                            new PaginatedListClickable(
                                    "Users",
                                    rank.getDisplayName(),
                                    "/rank tools players " + rank.getName());
                    ArrayList<Clickable> values4 = new ArrayList<>();
                    for (String text : (Iterable<String>) ((Iterable) values)) {
                        values4.add(new Clickable(text));
                    }
                    paginatedListClickable.display(sender, value, values4);
                    this.enabled = false;
                });
    }

    @Command(
            name = "tools export",
            description = "Export ranks to ranks.json (Only for Phoenix!)",
            async = true)
    public void toolsExport(@Sender CommandSender sender) throws java.io.IOException {
        File file;
        if (!(file = new File("plugins/Phoenix/")).exists()) {
            file.mkdirs();
        }
        File file2;
        if (!(file2 = new File(file, "ranks.json")).exists()) {
            this.plugin.saveResource("ranks.json", false);
        }
        List<IRank> sortedRanks = this.plugin.getRankHandler().getSortedRanks();
        Files.write(this.plugin.getGson().toJson(sortedRanks), file2, Charsets.UTF_8);
        sender.sendMessage(
                Color.translate(LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.EXPORTED)));
    }

    @Command(name = "tools inheritance-graph", description = "View inheritance graph")
    public void toolsInheritanceGraph(@Sender CommandSender sender, @Optional IRank rank) {
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_HEADER_FORMAT)
                                .replace("<type>", "Inheritance Graph")
                                .replace("<page>", "1")
                                .replace("<pages>", "1")));
        if (rank != null) {
            sender.sendMessage(Color.translate(rank.getDisplayName()));
            this.sendMessage(sender, rank, new ArrayList<>(), 0);
        } else {
            for (IRank rank2 : this.plugin.getRankHandler().getSortedRanksReversed()) {
                sender.sendMessage(Color.translate(rank2.getDisplayName()));
                this.sendMessage(sender, rank2, new ArrayList<>(), 0);
            }
        }
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FOOTER_FORMAT)
                                .replace("<page>", "1")
                                .replace("<pages>", "1")
                                .replace(
                                        "<results>",
                                        ""
                                                + this.plugin
                                                        .getRankHandler()
                                                        .getAvailableRanks()
                                                        .size())));
    }

    public void sendMessage(CommandSender sender, IRank rank, List<String> values, int value) {
        if (!rank.getInheritance().isEmpty()) {
            for (int index = 0; index < rank.getInheritance().size(); index++) {
                IRank rank2;
                if ((rank2 = this.plugin.getRankHandler().getRank(rank.getInheritance().get(index)))
                        != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String text : values) {
                        stringBuilder.append(text);
                    }
                    boolean matches = index == rank.getInheritance().size() - 1;
                    String text2 = value < 1 ? "" : "&7 ";
                    String text3 = value > 1 ? "&7 " : "";
                    if (values.size() != value) {
                        stringBuilder.append(text3).append(" &7\u2503 ");
                        values.add(text3 + " &7\u2503 ");
                    }
                    if (matches) {
                        stringBuilder.append(text2).append(" &7\u2517\u2501");
                        values.add(text2 + "&7 &7 &7 ");
                    } else {
                        stringBuilder.append(text2).append(" &7\u2523\u2501");
                    }
                    sender.sendMessage(
                            Color.translate(stringBuilder + " " + rank2.getDisplayName()));
                    this.sendMessage(sender, rank2, new ArrayList<>(values), value + 1);
                }
            }
        }
    }

    @Command(
            name = "tools import",
            description = "Import ranks from ranks.json (Only from Phoenix!)",
            async = true)
    public void toolsImport(@Sender CommandSender sender) throws java.io.IOException {
        File file;
        if (!(file = new File("plugins/Phoenix/", "ranks.json")).exists()) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.FILE_MISSING));
        }
        List<IRank> values =
                (List)
                        this.plugin
                                .getGson()
                                .fromJson(Files.toString(file, Charsets.UTF_8), JsonTypes.typeType);
        IRankRepository rankRepository;
        (rankRepository = this.plugin.getRankHandler().getRepository()).getCache().clear();
        rankRepository.deleteAll();
        for (IRank rank : values) {
            this.plugin.getRankHandler().getRepository().saveSync(rank);
            this.plugin.getPacketHandler().sendSync(new RankUpdatePacket(rank, false));
        }
        this.plugin.getPacketHandler().send(new ServerCommandPacket("all", "stop", "Console"));
        sender.sendMessage(
                Color.translate(LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.IMPORTED)));
    }

    @Command(
            name = "tools import-preset",
            description = "Import preset ranks created by lightspark",
            async = true)
    public void toolsImportPreset(@Sender CommandSender sender) throws java.io.IOException {
        this.plugin.saveResource("ranks-preset.json", true);
        File file;
        if (!(file = new File("plugins/Phoenix/", "ranks-preset.json")).exists()) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.PRESET_FILE_MISSING));
        }
        List<IRank> values =
                (List)
                        this.plugin
                                .getGson()
                                .fromJson(Files.toString(file, Charsets.UTF_8), JsonTypes.typeType);
        Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
        while (iterator2.hasNext()) {
            ((Player) iterator2.next()).kickPlayer(Color.translate("&cImporting..."));
        }
        IRankRepository rankRepository = this.plugin.getRankHandler().getRepository();
        for (IRank rank : rankRepository.getCache()) {
            this.plugin.getPacketHandler().sendSync(new RankUpdatePacket(rank, true));
        }
        rankRepository.getCache().clear();
        rankRepository.deleteAll();
        for (IRank rank2 : values) {
            this.plugin.getRankHandler().getRepository().saveSync(rank2);
            this.plugin.getPacketHandler().sendSync(new RankUpdatePacket(rank2, false));
        }
        sender.sendMessage(
                Color.translate(LocaleUtil.tr(sender, MessageConfig.RANK.TOOLS.IMPORTED)));
    }

    public RankCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
