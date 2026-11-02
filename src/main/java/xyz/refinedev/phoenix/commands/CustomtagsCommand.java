package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.menu.impl.tags.CustomTagsMenu;

import java.util.List;

@Register(name = "customtags", aliases = "customtag")
public class CustomtagsCommand {

    private final Phoenix plugin;

    @Command(name = "", async = true)
    public void execute(@Sender Player player) {
        List<xyz.refinedev.phoenix.profile.tag.ICustomTag> sortedTags =
                this.plugin.getCustomTagHandler().getSortedTags(player.getUniqueId());
        this.plugin.getMenuHandler().openMenuSync(new CustomTagsMenu(sortedTags), player);
    }

    public CustomtagsCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
