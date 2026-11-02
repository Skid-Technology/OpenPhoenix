package xyz.refinedev.phoenix.commands.provider;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.actor.Actor;
import xyz.refinedev.lib.commands.actor.PlayerActor;
import xyz.refinedev.lib.commands.bukkit.actor.BukkitActor;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.CommandParameter;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.handler.IProfileHandler;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class ProfileProvider extends Provider<IProfile> {

    private final Phoenix plugin;

    private final IProfileHandler profileHandler;

    public ProfileProvider(Phoenix phoenix) {
        super(ProviderType.ARGUMENT, "player");
        this.plugin = phoenix;
        this.profileHandler = phoenix.getProfileHandler();
    }

    public IProfile provide(CommandExecution commandExecution, CommandArgument commandArgument) {
        String value = commandArgument.getValue();
        Actor actor = commandExecution.getActor();
        CommandParameter parameter = commandArgument.getParameter();
        boolean player = actor.isPlayer();
        if (BukkitAPI.isUuid(value)) {
            UUID uUID = UUID.fromString(value);
            IProfile profile;
            if ((profile = this.profileHandler.getProfile(uUID)) == null) {
                throw new ExitMessage(
                        LocaleUtil.tr(commandExecution, MessageConfig.ERROR.PROFILE_DOESNT_EXIST)
                                .replace("<player>", Color.escape(value)),
                        true);
            } else if (!this.isCoverdisguise(actor, parameter, profile, value, player)) {
                throw new ExitMessage(
                        LocaleUtil.tr(commandExecution, MessageConfig.ERROR.PLAYER_NOT_ONLINE)
                                .replace("<player>", Color.escape(value)),
                        true);
            } else {
                return profile;
            }
        } else {
            if (!BukkitAPI.isValidName(value.replace("*", "").replace(".", ""))) {
                throw new ExitMessage(
                        LocaleUtil.tr(commandExecution, MessageConfig.ERROR.PROFILE_DOESNT_EXIST)
                                .replace("<player>", Color.escape(value)),
                        true);
            }
            IProfile localProfileByName = this.profileHandler.getLocalProfileByName(value);
            String text;
            if ((parameter.hasOption("disguise") || parameter.hasOption("coverdisguise"))
                    && localProfileByName == null
                    && (text = this.plugin.getDisguiseHandler().getUuidByFakeName(value)) != null) {
                UUID uUID2 = UUID.fromString(text);
                localProfileByName = this.profileHandler.getProfile(uUID2);
            }
            if (localProfileByName == null || this.profileHandler.hasMultipleProfiles(value)) {
                List<IProfile> values;
                if ((values = this.profileHandler.getProfilesFromName(value)).size() > 1) {
                    if (parameter.hasOption("multiple")) {
                        actor.sendMessage(
                                LocaleUtil.tr(
                                                commandExecution,
                                                MessageConfig.ERROR
                                                        .MULTIPLE_PROFILES_WITH_SAME_NAME)
                                        .replace("<player>", Color.escape(value))
                                        .replace("<entries>", String.valueOf(values.size())));
                        String text2 =
                                StringUtils.niceBuilder(
                                        commandExecution.getArguments(), " ", " ", "");
                        for (int index = 0; index < values.size(); index++) {
                            IProfile profile2 = (IProfile) values.get(index);
                            String text3 =
                                    text2.replaceFirst(value, profile2.getUniqueId().toString());
                            this.profileHandler
                                    .getMultipleProfiles()
                                    .add(profile2.getName().toLowerCase());
                            CommandSender sender =
                                    ((BukkitActor) commandExecution.getActor()).getSender();
                            Clickable clickable;
                            (clickable = new Clickable())
                                    .add(
                                            MessageConfig.ERROR
                                                    .MULTIPLE_PROFILES_NAME
                                                    .replace(
                                                            "<uuid>",
                                                            profile2.getUniqueId().toString())
                                                    .replace("<name>", profile2.getName())
                                                    .replace("<number>", String.valueOf(index + 1)),
                                            "&aClick.",
                                            "/"
                                                    + commandExecution.getHandle().getFullName()
                                                    + " "
                                                    + text3);
                            clickable.sendToPlayer(sender);
                        }
                        throw new ExitMessage();
                    }
                    for (IProfile profile3 : values) {
                        if (this.plugin.getNetworkHandler().isOnline(profile3.getUuid(), false)) {
                            localProfileByName = profile3;
                            this.profileHandler
                                    .getRepository()
                                    .addToCache(
                                            localProfileByName.getUniqueId(), localProfileByName);
                            break;
                        }
                    }
                    if (localProfileByName == null) {
                        Stream<IProfile> stream2 = values.stream();
                        if ((localProfileByName =
                                        stream2.filter(result -> result.getName().equals(value))
                                                .findFirst()
                                                .orElse(null))
                                != null) {
                            this.profileHandler
                                    .getRepository()
                                    .addToCache(
                                            localProfileByName.getUniqueId(), localProfileByName);
                        }
                    }
                } else if (values.size() == 1) {
                    localProfileByName = (IProfile) values.get(0);
                    this.profileHandler
                            .getRepository()
                            .addToCache(localProfileByName.getUniqueId(), localProfileByName);
                }
            }
            if (localProfileByName != null) {
                if (!this.isCoverdisguise(actor, parameter, localProfileByName, value, player)) {
                    throw new ExitMessage(
                            LocaleUtil.tr(commandExecution, MessageConfig.ERROR.PLAYER_NOT_ONLINE)
                                    .replace("<player>", Color.escape(value)),
                            true);
                } else {
                    return localProfileByName;
                }
            } else if (parameter.hasOption("create") && SettingsConfig.SERVER.CREATE_PROFILE) {
                UUID uUID3;
                if ((uUID3 = this.plugin.getUuidCache().getUuid(value)) == null) {
                    throw new ExitMessage(
                            LocaleUtil.tr(
                                            commandExecution,
                                            MessageConfig.ERROR.PROFILE_DOESNT_EXIST)
                                    .replace("<player>", Color.escape(value)),
                            true);
                }
                IProfile profile4;
                (profile4 = this.profileHandler.createProfile(uUID3, value)).saveProfile();
                return profile4;
            } else {
                throw new ExitMessage(
                        LocaleUtil.tr(commandExecution, MessageConfig.ERROR.PROFILE_DOESNT_EXIST)
                                .replace("<player>", Color.escape(value)),
                        true);
            }
        }
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        Actor actor;
        Player player =
                (actor = commandSuggestion.getActor()).isPlayer()
                        ? ((BukkitActor) actor).getPlayer()
                        : null;
        return new ArrayList<>(
                this.plugin.getVisibilityHandler().getAllVisibleToNamesOnNetwork(player));
    }

    public boolean isCoverdisguise(
            PlayerActor playerActor,
            CommandParameter commandParameter,
            IProfile profile,
            String text,
            boolean matches) {
        if (!commandParameter.hasOption("coverdisguise") && !commandParameter.hasOption("online")) {
            return true;
        } else if (!this.plugin.getNetworkHandler().isOnline(profile.getUniqueId())) {
            return false;
        } else {
            return matches
                            && !this.plugin
                                    .getVisibilityHandler()
                                    .canSee(playerActor.getUniqueId(), profile.getUniqueId())
                    ? false
                    : !commandParameter.hasOption("coverdisguise")
                            || !profile.getDisguiseData().isDisguised()
                            || profile.getPlayerName().equalsIgnoreCase(text);
        }
    }

    public boolean isAsync() {
        return true;
    }
}
