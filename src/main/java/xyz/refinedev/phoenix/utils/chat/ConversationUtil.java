package xyz.refinedev.phoenix.utils.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.conversations.base.Conversation.Builder;
import xyz.refinedev.lib.conversations.base.question.Question;
import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.api.PhoenixDialog;
import xyz.refinedev.phoenix.dialog.impl.InputDialog;
import xyz.refinedev.phoenix.utils.Color;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class ConversationUtil {

    private static final String KEY = "input";

    private static final long DEFAULT_TIMEOUT_SECONDS = 30L;

    private ConversationUtil() {}

    public static void ask(
            @NotNull Phoenix phoenix,
            @NotNull Player player,
            @NotNull String text,
            @NotNull Consumer<String> consumer) {
        ask(phoenix, player, text, consumer, null);
    }

    public static void ask(
            @NotNull Phoenix phoenix,
            @NotNull Player player,
            @NotNull String text,
            @NotNull Consumer<String> consumer,
            Runnable runnable) {
        PhoenixDialog phoenixDialog;
        if (!SettingsConfig.MODERN.DIALOGS.INPUT
                || !((phoenixDialog = phoenix.getDialogHandler().findPhoenixDialog("input"))
                        instanceof InputDialog)
                || !((InputDialog) phoenixDialog)
                        .matchesEnabled(player, text, consumer, runnable)) {
            BaseComponent[] baseComponents = TextComponent.fromLegacyText(Color.translate(text));
            Question question =
                    Question.newBuilder()
                            .withIdentifier("input")
                            .withMessage(baseComponents)
                            .withTimeout(30L, TimeUnit.SECONDS)
                            .build();
            Builder builder =
                    phoenix.getConversationManager()
                            .newConversationBuilder(player)
                            .withTimeoutScheduler(phoenix.getTimeoutManager())
                            .withQuestion(question);
            builder.whenDone(
                            result -> {
                                Object result2;
                                if ((result2 = result.getInput("input")) != null) {
                                    String text2;
                                    if (!(text2 =
                                                    TextComponent.toPlainText(
                                                                    (BaseComponent[]) result2)
                                                            .trim())
                                            .isEmpty()) {
                                        if (!text2.equalsIgnoreCase("cancel")
                                                && !text2.equalsIgnoreCase("/no")) {
                                            consumer.accept(text2);
                                        } else {
                                            if (runnable != null) {
                                                runnable.run();
                                            }
                                        }
                                    }
                                }
                            })
                    .build()
                    .start();
        }
    }
}
