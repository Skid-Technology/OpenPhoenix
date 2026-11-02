package xyz.refinedev.phoenix.handler;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.WebhookConfig;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.utils.StringUtils;
import xyz.refinedev.phoenix.utils.webhook.DiscordWebhook;

import java.awt.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiscordHandler {

    private final Phoenix plugin;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public void applyGrant(IGrant grant) {
        if (!WebhookConfig.GRANT.WEBHOOK_URL.isEmpty()) {
            DiscordWebhook discordWebhook;
            (discordWebhook = new DiscordWebhook(WebhookConfig.GRANT.WEBHOOK_URL))
                    .addEmbed(
                            new DiscordWebhook.EmbedObject()
                                    .setColor(Color.decode(WebhookConfig.GRANT.EXECUTE_GRANT.COLOR))
                                    .setTitle(
                                            WebhookConfig.GRANT.EXECUTE_GRANT.TITLE.replace(
                                                    "<player>",
                                                    this.replaceFirst(
                                                            xyz.refinedev.phoenix.utils.Color
                                                                    .stripColor(
                                                                            grant
                                                                                    .getNiceTarget()))))
                                    .setUrl(
                                            "https://skins.mcstats.com/bust/"
                                                    + grant.getTarget().toString())
                                    .addField(
                                            "Target",
                                            this.replaceFirst(
                                                    xyz.refinedev.phoenix.utils.Color.stripColor(
                                                            grant.getNiceTarget())),
                                            true)
                                    .addField("Rank", grant.getRank().getName(), true)
                                    .addField("Duration", grant.getNiceDuration(), true)
                                    .addField(
                                            "Scopes",
                                            StringUtils.join(grant.getNiceActiveScopes()),
                                            true)
                                    .addField(
                                            "Issued By",
                                            this.replaceFirst(
                                                            xyz.refinedev.phoenix.utils.Color
                                                                    .stripColor(
                                                                            grant
                                                                                    .getNiceIssuedBy()))
                                                    .replaceFirst("_", "\\_"),
                                            true)
                                    .addField("Issued On", grant.getIssuedOn(), true)
                                    .addField("Issued Reason", grant.getReason(), true));
            DiscordWebhook discordWebhook2 = discordWebhook;
            this.executor.execute(
                    () -> {
                        try {
                            discordWebhook2.execute();
                        } catch (Exception exception) {
                            this.plugin
                                    .getLogger()
                                    .severe(
                                            "Failed to send webhook for grant. Maybe it has been rate-limited.");
                        }
                    });
        }
    }

    public void applyGrantByWEBHOOK_URL(IGrant grant) {
        if (!WebhookConfig.GRANT.WEBHOOK_URL.isEmpty()) {
            DiscordWebhook discordWebhook;
            (discordWebhook = new DiscordWebhook(WebhookConfig.GRANT.WEBHOOK_URL))
                    .addEmbed(
                            new DiscordWebhook.EmbedObject()
                                    .setColor(Color.decode(WebhookConfig.GRANT.REVOKE_GRANT.COLOR))
                                    .setTitle(
                                            WebhookConfig.GRANT.REVOKE_GRANT.TITLE.replace(
                                                    "<player>",
                                                    this.replaceFirst(
                                                            xyz.refinedev.phoenix.utils.Color
                                                                    .stripColor(
                                                                            grant
                                                                                    .getNiceTarget()))))
                                    .addField(
                                            "Target",
                                            this.replaceFirst(
                                                    xyz.refinedev.phoenix.utils.Color.stripColor(
                                                            grant.getNiceTarget())),
                                            true)
                                    .addField("Rank", grant.getRank().getName(), true)
                                    .addField("Duration", grant.getNiceDuration(), true)
                                    .addField(
                                            "Scopes",
                                            StringUtils.join(grant.getNiceActiveScopes()),
                                            true)
                                    .addField(
                                            "Issued By",
                                            this.replaceFirst(
                                                    xyz.refinedev.phoenix.utils.Color.stripColor(
                                                            grant.getNiceIssuedBy())),
                                            true)
                                    .addField("Issued On", grant.getIssuedOn(), true)
                                    .addField("Issued Reason", grant.getReason(), true)
                                    .addField(
                                            "Removed By",
                                            this.replaceFirst(
                                                    xyz.refinedev.phoenix.utils.Color.stripColor(
                                                            grant.getNiceRemovedBy())),
                                            true)
                                    .addField("Removed On", grant.getRemovedOn(), true)
                                    .addField("Removed Reason", grant.getRemovedReason(), true));
            DiscordWebhook discordWebhook2 = discordWebhook;
            this.executor.execute(
                    () -> {
                        try {
                            discordWebhook2.execute();
                        } catch (Exception exception) {
                            this.plugin
                                    .getLogger()
                                    .severe(
                                            "Failed to send webhook for a revoked grant. Maybe it has been rate-limited.");
                        }
                    });
        }
    }

    public void applyPunishment(IPunishment punishment) {
        if (!WebhookConfig.PUNISHMENT.WEBHOOK_URL.isEmpty()) {
            DiscordWebhook discordWebhook;
            (discordWebhook = new DiscordWebhook(WebhookConfig.PUNISHMENT.WEBHOOK_URL))
                    .addEmbed(
                            new DiscordWebhook.EmbedObject()
                                    .setColor(
                                            Color.decode(
                                                    WebhookConfig.PUNISHMENT
                                                            .EXECUTE_PUNISHMENT
                                                            .COLOR))
                                    .setTitle(
                                            WebhookConfig.PUNISHMENT.EXECUTE_PUNISHMENT.TITLE
                                                    .replace(
                                                            "<player>",
                                                            this.replaceFirst(
                                                                    xyz.refinedev.phoenix.utils
                                                                            .Color.stripColor(
                                                                            punishment
                                                                                    .getNiceTarget()))))
                                    .setUrl(
                                            "https://skins.mcstats.com/bust/"
                                                    + punishment.getTarget().toString())
                                    .addField(
                                            "Target",
                                            this.replaceFirst(
                                                    xyz.refinedev.phoenix.utils.Color.stripColor(
                                                            punishment.getNiceTarget())),
                                            true)
                                    .addField(
                                            "Type",
                                            punishment.getPunishmentType().getNiceName(),
                                            true)
                                    .addField("Duration", punishment.getNiceDuration(), true)
                                    .addField(
                                            "Issued By",
                                            this.replaceFirst(
                                                    xyz.refinedev.phoenix.utils.Color.stripColor(
                                                            punishment.getNiceIssuedBy())),
                                            true)
                                    .addField("Issued On", punishment.getIssuedOn(), true)
                                    .addField("Issued Reason", punishment.getReason(), true));
            DiscordWebhook discordWebhook2 = discordWebhook;
            this.executor.execute(
                    () -> {
                        try {
                            discordWebhook2.execute();
                        } catch (Exception exception) {
                            this.plugin
                                    .getLogger()
                                    .severe(
                                            "Failed to send webhook for punishment. Maybe it has been rate-limited.");
                        }
                    });
        }
    }

    public void applyPunishmentByWEBHOOK_URL(IPunishment punishment) {
        if (!WebhookConfig.PUNISHMENT.WEBHOOK_URL.isEmpty()) {
            DiscordWebhook discordWebhook =
                    new DiscordWebhook(WebhookConfig.PUNISHMENT.WEBHOOK_URL);
            DiscordWebhook.EmbedObject embedObject =
                    new DiscordWebhook.EmbedObject()
                            .setColor(
                                    Color.decode(WebhookConfig.PUNISHMENT.REVOKE_PUNISHMENT.COLOR))
                            .setTitle(
                                    WebhookConfig.PUNISHMENT.REVOKE_PUNISHMENT.TITLE.replace(
                                            "<player>",
                                            this.replaceFirst(
                                                    xyz.refinedev.phoenix.utils.Color.stripColor(
                                                            punishment.getNiceTarget()))))
                            .setUrl(
                                    "https://skins.mcstats.com/bust/"
                                            + punishment.getTarget().toString())
                            .addField(
                                    "Target",
                                    this.replaceFirst(
                                            xyz.refinedev.phoenix.utils.Color.stripColor(
                                                    punishment.getNiceTarget())),
                                    true)
                            .addField("Type", punishment.getPunishmentType().getNiceName(), true)
                            .addField("Duration", punishment.getNiceDuration(), true)
                            .addField(
                                    "Issued By",
                                    this.replaceFirst(
                                            xyz.refinedev.phoenix.utils.Color.stripColor(
                                                    punishment.getNiceIssuedBy())),
                                    true)
                            .addField("Issued On", punishment.getIssuedOn(), true)
                            .addField("Issued Reason", punishment.getReason(), true)
                            .addField(
                                    punishment.isVoided() ? "Voided By" : "Removed By",
                                    this.replaceFirst(
                                            xyz.refinedev.phoenix.utils.Color.stripColor(
                                                    punishment.getNiceRemovedBy())),
                                    true)
                            .addField(
                                    punishment.isVoided() ? "Voided On" : "Removed On",
                                    punishment.getRemovedOn(),
                                    true)
                            .addField(
                                    punishment.isVoided() ? "Voided Reason" : "Removed Reason",
                                    punishment.getRemovedReason(),
                                    true);
            if (punishment.isVoided() && punishment.getVoidReason() != null) {
                embedObject.addField("Voided As", punishment.getVoidReason().getNiceName(), true);
            }
            discordWebhook.addEmbed(embedObject);
            this.executor.execute(
                    () -> {
                        try {
                            discordWebhook.execute();
                        } catch (Exception exception) {
                            this.plugin
                                    .getLogger()
                                    .severe(
                                            "Failed to send webhook for a revoked punishment. Maybe it has been rate-limited.");
                        }
                    });
        }
    }

    public void applyString(String text, String text2) {
        if (!WebhookConfig.SERVER.WEBHOOK_URL.isEmpty()) {
            DiscordWebhook discordWebhook;
            (discordWebhook = new DiscordWebhook(WebhookConfig.SERVER.WEBHOOK_URL))
                    .addEmbed(
                            new DiscordWebhook.EmbedObject()
                                    .setColor(Color.decode(WebhookConfig.SERVER.STARTUP.COLOR))
                                    .setTitle(
                                            WebhookConfig.SERVER
                                                    .STARTUP
                                                    .TITLE
                                                    .replace("<server>", text)
                                                    .replace("<region>", text2))
                                    .setDescription(
                                            BukkitAPI.getKickMessage(
                                                            WebhookConfig.SERVER
                                                                    .STARTUP
                                                                    .DESCRIPTION)
                                                    .replace("<server>", text)
                                                    .replace("<region>", text2)));
            DiscordWebhook discordWebhook2 = discordWebhook;
            this.executor.execute(
                    () -> {
                        try {
                            discordWebhook2.execute();
                        } catch (Exception exception) {
                            this.plugin
                                    .getLogger()
                                    .severe(
                                            "Failed to send webhook for server startup. Maybe it has been rate-limited.");
                        }
                    });
        }
    }

    public void applyStringByWEBHOOK_URL(String text, String text2) {
        if (!WebhookConfig.SERVER.WEBHOOK_URL.isEmpty()) {
            DiscordWebhook discordWebhook;
            (discordWebhook = new DiscordWebhook(WebhookConfig.SERVER.WEBHOOK_URL))
                    .addEmbed(
                            new DiscordWebhook.EmbedObject()
                                    .setColor(Color.decode(WebhookConfig.SERVER.SHUTDOWN.COLOR))
                                    .setTitle(
                                            WebhookConfig.SERVER
                                                    .SHUTDOWN
                                                    .TITLE
                                                    .replace("<server>", text)
                                                    .replace("<region>", text2))
                                    .setDescription(
                                            BukkitAPI.getKickMessage(
                                                            WebhookConfig.SERVER
                                                                    .SHUTDOWN
                                                                    .DESCRIPTION)
                                                    .replace("<server>", text)
                                                    .replace("<region>", text2)));
            DiscordWebhook discordWebhook2 = discordWebhook;
            this.executor.execute(
                    () -> {
                        try {
                            discordWebhook2.execute();
                        } catch (Exception exception) {
                            this.plugin
                                    .getLogger()
                                    .severe(
                                            "Failed to send webhook for server shutdown. Maybe it has been rate-limited.");
                        }
                    });
        }
    }

    public void applyString(String text, String text2, String text3, String text4) {
        if (!WebhookConfig.STAFF.WEBHOOK_URL.isEmpty()) {
            DiscordWebhook discordWebhook;
            (discordWebhook = new DiscordWebhook(WebhookConfig.STAFF.WEBHOOK_URL))
                    .addEmbed(
                            new DiscordWebhook.EmbedObject()
                                    .setColor(Color.decode(WebhookConfig.STAFF.JOIN.COLOR))
                                    .setTitle(
                                            WebhookConfig.STAFF
                                                    .JOIN
                                                    .TITLE
                                                    .replace("<player>", this.replaceFirst(text2))
                                                    .replace("<server>", text3)
                                                    .replace("<proxy>", text4))
                                    .setUrl("https://skins.mcstats.com/bust/" + text)
                                    .setDescription(
                                            BukkitAPI.getKickMessage(
                                                            WebhookConfig.STAFF.JOIN.DESCRIPTION)
                                                    .replace("<player>", this.replaceFirst(text2))
                                                    .replace("<server>", text3)
                                                    .replace("<proxy>", text4)));
            DiscordWebhook discordWebhook2 = discordWebhook;
            this.executor.execute(
                    () -> {
                        try {
                            discordWebhook2.execute();
                        } catch (Exception exception) {
                            this.plugin
                                    .getLogger()
                                    .severe(
                                            "Failed to send webhook for grant. Maybe it has been rate-limited.");
                        }
                    });
        }
    }

    public void applyStringByWEBHOOK_URL(String text, String text2, String text3, String text4) {
        if (!WebhookConfig.STAFF.WEBHOOK_URL.isEmpty()) {
            DiscordWebhook discordWebhook;
            (discordWebhook = new DiscordWebhook(WebhookConfig.STAFF.WEBHOOK_URL))
                    .addEmbed(
                            new DiscordWebhook.EmbedObject()
                                    .setColor(Color.decode(WebhookConfig.STAFF.SWITCH.COLOR))
                                    .setTitle(
                                            WebhookConfig.STAFF
                                                    .SWITCH
                                                    .TITLE
                                                    .replace("<player>", this.replaceFirst(text2))
                                                    .replace("<from>", text4)
                                                    .replace("<to>", text3))
                                    .setUrl("https://skins.mcstats.com/bust/" + text)
                                    .setDescription(
                                            BukkitAPI.getKickMessage(
                                                            WebhookConfig.STAFF.SWITCH.DESCRIPTION)
                                                    .replace("<player>", this.replaceFirst(text2))
                                                    .replace("<from>", text4)
                                                    .replace("<to>", text3)));
            DiscordWebhook discordWebhook2 = discordWebhook;
            this.executor.execute(
                    () -> {
                        try {
                            discordWebhook2.execute();
                        } catch (Exception exception) {
                            this.plugin
                                    .getLogger()
                                    .severe(
                                            "Failed to send webhook for staff switch. Maybe it has been rate-limited.");
                        }
                    });
        }
    }

    public void applyString(String text, String text2, String text3) {
        if (!WebhookConfig.STAFF.WEBHOOK_URL.isEmpty()) {
            DiscordWebhook discordWebhook;
            (discordWebhook = new DiscordWebhook(WebhookConfig.STAFF.WEBHOOK_URL))
                    .addEmbed(
                            new DiscordWebhook.EmbedObject()
                                    .setColor(Color.decode(WebhookConfig.STAFF.LEAVE.COLOR))
                                    .setTitle(
                                            WebhookConfig.STAFF
                                                    .LEAVE
                                                    .TITLE
                                                    .replace("<player>", this.replaceFirst(text2))
                                                    .replace("<server>", text3))
                                    .setUrl("https://skins.mcstats.com/bust/" + text)
                                    .setDescription(
                                            BukkitAPI.getKickMessage(
                                                            WebhookConfig.STAFF.LEAVE.DESCRIPTION)
                                                    .replace("<player>", this.replaceFirst(text2))
                                                    .replace("<server>", text3)));
            DiscordWebhook discordWebhook2 = discordWebhook;
            this.executor.execute(
                    () -> {
                        try {
                            discordWebhook2.execute();
                        } catch (Exception exception) {
                            this.plugin
                                    .getLogger()
                                    .severe(
                                            "Failed to send webhook for staff leave. Maybe it has been rate-limited.");
                        }
                    });
        }
    }

    public String replaceFirst(String text) {
        return text.replaceFirst("_", "\\_");
    }

    public DiscordHandler(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
