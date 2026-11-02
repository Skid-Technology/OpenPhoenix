package xyz.refinedev.phoenix.setup;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SetupGuidance {

    public static final Map<String, String> stringsByName =
            Collections.unmodifiableMap(
                    new LinkedHashMap<String, String>() {

                        {
                            this.put("core.staff", "Staff chat, alerts and staff-only settings");
                            this.put("core.admin", "Admin alerts and admin-only settings");
                            this.put("core.adminchat", "Use and read admin chat");
                            this.put("core.builderchat", "Use and read builder chat");
                            this.put("core.managementchat", "Use and read management chat");
                            this.put("core.chat.colorcodes", "Use color codes in chat");
                            this.put("core.message.colored", "Use color codes in private messages");
                            this.put(
                                    "core.globalchat.bypass",
                                    "Chat reaches players who hide or ignore it");
                            this.put("core.notes.view", "See a player's notes when they join");
                            this.put("core.lunar.staffmods", "Get Lunar staff mods on join");
                            this.put(
                                    "core.history.viewblacklists",
                                    "See blacklists in punishment history");
                            this.put(
                                    "core.history.viewaddedby",
                                    "See who issued and removed punishments");
                            this.put("core.media", "Shown in the media list");
                            this.put(
                                    "core.immune",
                                    "Protected from punishment by the immune system");
                            this.put("core.2fa", "Prompted to set up two-factor auth");
                            this.put("core.command.spawn", "Teleport yourself to spawn");
                            this.put("core.command.spawn.others", "Send another player to spawn");
                            this.put(
                                    "core.command.build.others",
                                    "Toggle build mode for another player");
                            this.put(
                                    "core.command.clear.others",
                                    "Clear another player's inventory");
                            this.put("core.command.feed.others", "Feed another player");
                            this.put("core.command.fly.others", "Toggle flight for another player");
                            this.put("core.command.heal.others", "Heal another player");
                            this.put(
                                    "core.command.gamemode.others",
                                    "Change another player's gamemode");
                            this.put(
                                    "core.command.enderchest.other",
                                    "Open another player's ender chest");
                            this.put(
                                    "core.command.modmode.others",
                                    "Toggle mod mode for another player");
                            this.put(
                                    "core.command.vanish.others",
                                    "Toggle vanish for another player");
                            this.put("core.command.disguise.name", "Pick the name to disguise as");
                            this.put(
                                    "core.disguise.real.bypass",
                                    "Disguise as a real player's name");
                            this.put("core.disguise.<rank>", "Disguise as that rank");
                            this.put("core.tags.*", "Use every tag");
                            this.put("core.tags.<name>", "Use that tag");
                            this.put("core.grant.*", "Grant every rank");
                            this.put("core.grant.<rank>", "Grant that rank");
                            this.put(
                                    "core.punish.<type>",
                                    "Use the punish ladder with that id, or ban-wave that punishment type");
                        }
                    });

    private static final String text = "This is a utility class and cannot be instantiated";

    private SetupGuidance() {
        throw new UnsupportedOperationException(text);
    }
}
