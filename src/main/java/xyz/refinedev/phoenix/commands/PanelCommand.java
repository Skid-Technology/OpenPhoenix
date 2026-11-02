package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.commands.api.annotations.RequireSecurity;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.editor.panel.PanelLinkCode;
import xyz.refinedev.phoenix.editor.panel.PanelLinkStatus;
import xyz.refinedev.phoenix.editor.panel.PanelSecret;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

@Help
@Register(name = "panel")
@Requires("core.command.panel")
public class PanelCommand {

    private static final String HTTPS_PHOENIX_REFINEDEV_ORG_LOGIN =
            "https://phoenix.refinedev.org/login";

    private final Phoenix plugin;

    @RequireSecurity
    @Command(name = "code", description = "Get your current web panel login code", async = true)
    @Requires("core.command.panel.code")
    public void code(@Sender Player player) {
        String text;
        if ((text = new PanelSecret(this.plugin).formatString(player.getUniqueId())) == null) {
            throw new ExitMessage("&cYour profile could not be loaded, so no code could be made.");
        }
        String text2 = PanelLinkCode.formatString(text, System.currentTimeMillis());
        long value = TimeUnit.MILLISECONDS.toSeconds(90000L);
        PanelLinkStatus panelLinkStatus = this.plugin.getPanelHandler().create();
        if (SettingsConfig.PANEL.CODE_IN_LINK && panelLinkStatus.isEnabled()) {
            String text3 =
                    "https://phoenix.refinedev.org/login?network="
                            + encode(panelLinkStatus.getTextString())
                            + "&name="
                            + encode(player.getName())
                            + "#code="
                            + text2;
            player.sendMessage(
                    Color.translate("&aYour panel sign-in, good for about " + value + " seconds:"));
            Clickable clickable;
            (clickable = new Clickable())
                    .addUrl(
                            "&e&l[Open the panel]",
                            "&aOpens the sign-in page with your code filled in.",
                            text3);
            clickable.add(" ");
            clickable.add(
                    "&7[Copy code]", "&7Puts the code in your chat box instead.", null, text2);
            clickable.sendToPlayer(player);
            player.sendMessage(Color.translate("&7Never share the link - it carries your code."));
        } else {
            player.sendMessage(
                    Color.translate("&aYour panel code, good for about " + value + " seconds:"));
            new Clickable("&e" + text2, "&aClick to copy.", "", text2).sendToPlayer(player);
            player.sendMessage(
                    Color.translate("&7Sign in with your username and this code. Run the command"));
            player.sendMessage(
                    Color.translate("&7again for a fresh one - never give it to anybody."));
        }
    }

    @Command(name = "link", description = "Where to sign in to the web panel", async = true)
    @Requires("core.command.panel.code")
    public void link(@Sender CommandSender sender) {
        PanelLinkStatus panelLinkStatus;
        if ((panelLinkStatus = this.plugin.getPanelHandler().create()).isEnabled()) {
            String textString = panelLinkStatus.getTextString();
            String text = "https://phoenix.refinedev.org/login?network=" + textString;
            sender.sendMessage(Color.translate("&aThe web panel:"));
            new Clickable("&e" + text, "&aClick to copy.", "", text).sendToPlayer(sender);
            sender.sendMessage(Color.translate("&7Network ID: &f" + textString));
            sender.sendMessage(
                    Color.translate("&7Sign in with that, your username, and a code from"));
            sender.sendMessage(Color.translate("&f/panel code&7."));
        } else if (!SettingsConfig.PANEL.ENABLED) {
            sender.sendMessage(Color.translate("&cThe panel is turned off on this server."));
            sender.sendMessage(
                    Color.translate(
                            "&cSet &fpanel.enabled&c to true in settings.yml and restart."));
        } else {
            sender.sendMessage(
                    Color.translate(
                            "&cNobody can sign in to the panel right now: &f"
                                    + ordinalByCounts(panelLinkStatus.getState())
                                    + "&c."));
            if (panelLinkStatus.getText() != null) {
                sender.sendMessage(Color.translate("&7Last error: &f" + panelLinkStatus.getText()));
            }
            sender.sendMessage(Color.translate("&7Run &f/panel status&7 for more."));
        }
    }

    @Command(name = "status", description = "Check the web panel link", async = true)
    @Requires("core.command.panel.status")
    public void status(@Sender CommandSender sender) {
        PanelLinkStatus panelLinkStatus = this.plugin.getPanelHandler().create();
        long now = System.currentTimeMillis();
        sender.sendMessage(
                Color.translate(
                        "&6Panel link: "
                                + ordinal(panelLinkStatus.getState())
                                + ordinalByCounts(panelLinkStatus.getState())
                                + " &7(for "
                                + DateUtils.formatTimeMillis(now - panelLinkStatus.getTime())
                                + ")"));
        if (panelLinkStatus.getState() == PanelLinkStatus.State.STOPPED) {
            sender.sendMessage(
                    Color.translate(
                            SettingsConfig.PANEL.ENABLED
                                    ? "&7It is turned on in settings.yml; restart the server to start it."
                                    : "&7Set &fpanel.enabled&7 to true in settings.yml and restart to use it."));
        } else {
            sender.sendMessage(
                    Color.translate(
                            "&7Network ID: &f"
                                    + (panelLinkStatus.getTextString() != null
                                            ? panelLinkStatus.getTextString()
                                            : "not assigned yet")));
            if (panelLinkStatus.getText() != null) {
                sender.sendMessage(
                        Color.translate(
                                "&7Last error &8("
                                        + DateUtils.formatTimeMillis(
                                                now - panelLinkStatus.getTimeTime())
                                        + " ago)&7: &f"
                                        + panelLinkStatus.getText()));
            }
            if (panelLinkStatus.getState() == PanelLinkStatus.State.OFFLINE) {
                String text =
                        panelLinkStatus.getTime2() > now
                                ? "in "
                                        + DateUtils.formatTimeMillis(
                                                panelLinkStatus.getTime2() - now)
                                : "within 15 seconds";
                sender.sendMessage(
                        Color.translate(
                                "&7Next attempt "
                                        + text
                                        + " &8("
                                        + panelLinkStatus.getCount()
                                        + " failed in a row)"));
            }
            if (panelLinkStatus.getCountCount() > 0) {
                sender.sendMessage(
                        Color.translate(
                                "&7Waiting on the backend: &f"
                                        + panelLinkStatus.getCountCount()
                                        + " request(s)"));
            }
        }
    }

    private static String encode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new IllegalStateException(unsupportedEncodingException);
        }
    }

    private static String ordinal(PanelLinkStatus.State state) {
        switch (state) {
            case UP:
                return "&a";
            case CONNECTING:
            case ANNOUNCING:
                return "&e";
            default:
                return "&c";
        }
    }

    private static String ordinalByCounts(PanelLinkStatus.State state) {
        switch (state) {
            case UP:
                return "linked";
            case CONNECTING:
                return "connecting";
            case ANNOUNCING:
                return "connected, waiting for the backend to accept this server";
            case NO_NETWORK_ID:
                return "linked, but the backend sent no Network ID";
            case REFUSED:
                return "refused by the backend";
            case OFFLINE:
                return "offline, retrying";
            case STOPPED:
            default:
                return "not running";
        }
    }

    @RequireSecurity
    @Command(name = "reset", description = "Invalidate somebody's panel codes", async = true)
    @Requires("core.command.panel.reset")
    public void reset(@Sender CommandSender sender, IProfile profile) {
        if (!new PanelSecret(this.plugin).matchesEnabled(profile.getUniqueId())) {
            throw new ExitMessage("&cThat profile could not be loaded.");
        }
        sender.sendMessage(
                Color.translate(
                        "&aEvery panel code for &e" + profile.getName() + " &ais now invalid."));
        this.sendMessage(sender, profile);
    }

    @RequireSecurity
    @Command(name = "logout", description = "End somebody's panel sessions", async = true)
    @Requires("core.command.panel.logout")
    public void logout(@Sender CommandSender sender, IProfile profile) {
        this.sendMessage(sender, profile);
    }

    private void sendMessage(CommandSender sender, IProfile profile) {
        boolean matches;
        try {
            matches =
                    this.plugin
                            .getPanelHandler()
                            .findCompletableFuture(profile.getUniqueId(), null)
                            .get(10L, TimeUnit.SECONDS);
        } catch (Exception exception) {
            matches = false;
        }
        if (matches) {
            sender.sendMessage(
                    Color.translate(
                            "&aEnded every panel session for &e" + profile.getName() + "&a."));
        } else {
            sender.sendMessage(
                    Color.translate("&cThe panel could not be reached, so their sessions are"));
            sender.sendMessage(
                    Color.translate("&cstill open. Run this again once the link is back."));
        }
    }

    public PanelCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
