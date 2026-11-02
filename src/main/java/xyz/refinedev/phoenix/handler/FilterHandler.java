package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.packet.impl.server.FilterUpdatePacket;
import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.repository.IFilterRepository;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FilterHandler implements IFilterHandler {

    private final Pattern pattern =
            Pattern.compile(
                    "^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$");

    private final Pattern patternPattern =
            Pattern.compile(
                    "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

    private final Pattern pattern2 = Pattern.compile("\\p{Punct}|\\d");

    private final Map<String, Pattern> patternsByName = new ConcurrentHashMap<>();

    private final CommonPlatform platform;

    private final IFilterRepository repository;

    public FilterHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.repository = new RepositoryFactory(commonPlatform).filters();
        this.repository
                .fetchAll()
                .whenCompleteAsync(
                        (result, result2) ->
                                this.repository
                                        .getCache()
                                        .addAll((Collection<? extends IFilter>) result));
    }

    @Nullable
    @Override
    public IFilter getFilterFromMessage(String text) {
        String text2;
        String text3 = (text2 = text.trim()).toLowerCase();
        String text4 = this.formatString(text2);
        String text5 = text3.replace(" ", "");
        String text6 = text4.replace(" ", "");
        String[] texts = text3.split(" ", -1);
        String[] texts2 = text4.split(" ", -1);
        for (int index = 0; index < texts.length; index++) {
            String text7 = texts[index];
            String text8 = texts2[index];
            for (IFilter filter : this.repository.getCache()) {
                switch (filter.getFilterType()) {
                    case WORD:
                        if (filter.getFilter().equalsIgnoreCase(text8)
                                || filter.getFilter().equalsIgnoreCase(text6)) {
                            return filter;
                        }
                        break;
                    case REGEX:
                        try {
                            Pattern pattern;
                            if ((pattern =
                                                    this.patternsByName.computeIfAbsent(
                                                            filter.getFilter(), Pattern::compile))
                                            .matcher(text7)
                                            .matches()
                                    || pattern.matcher(text5).matches()) {
                                return filter;
                            }
                        } catch (PatternSyntaxException patternSyntaxException) {
                        }
                        break;
                    case CONTAINS:
                        if (text8.contains(filter.getFilter().toLowerCase())
                                || text6.contains(filter.getFilter().toLowerCase())) {
                            return filter;
                        }
                        break;
                    default:
                        return filter;
                }
            }
        }
        return null;
    }

    @Override
    public void updateFilter(IFilter filter) {
        this.repository.save(filter);
        this.platform.getPacketHandler().send(new FilterUpdatePacket(filter, false));
    }

    @Override
    public void removeFilter(IFilter filter) {
        this.repository.removeFromCache(filter);
        this.repository.delete(filter);
        this.platform.getPacketHandler().send(new FilterUpdatePacket(filter, true));
    }

    @Nullable
    @Override
    public IFilter getFilterByName(String text) {
        return this.repository.getFromCache(text).orElse(null);
    }

    @Override
    public boolean isFiltered(String text) {
        if (!GlobalConfig.FILTER.ENABLED) {
            return false;
        }
        if (this.getFilterFromMessage(text) != null) {
            this.applyString("rule");
            return true;
        }
        String[] texts;
        int value =
                (texts =
                                text.replace("(dot)", ".")
                                        .replace("(.)", ".")
                                        .replace("/./", ".")
                                        .replace("[dot]", ".")
                                        .trim()
                                        .split(" "))
                        .length;
        for (int index = 0; index < value; index++) {
            String text2 = texts[index];
            boolean matches2 = false;
            for (String text3 : GlobalConfig.FILTER.WHITELISTED_LINKS) {
                if (text2.toLowerCase().contains(text3)) {
                    matches2 = true;
                    break;
                }
            }
            if (!matches2) {
                if (this.patternPattern.matcher(text2).matches()) {
                    this.applyString("ip");
                    return true;
                }
                if (this.pattern.matcher(text2).matches()) {
                    this.applyString("url");
                    return true;
                }
            }
        }
        return false;
    }

    private void applyString(String text) {
        this.platform
                .getMetricsHandler()
                .metric("phoenix_chat_filter_hit")
                .findMetric("kind", text)
                .initialize();
    }

    @NotNull
    @Override
    public String sanitize(String text) {
        return this.formatString(text).trim();
    }

    @NotNull
    private String formatString(String text) {
        String text2 =
                text.toLowerCase()
                        .replace("3", "e")
                        .replace("1", "i")
                        .replace("!", "i")
                        .replace("@", "a")
                        .replace("7", "t")
                        .replace("0", "o")
                        .replace("5", "s")
                        .replace("8", "b");
        return this.pattern2.matcher(text2).replaceAll("");
    }

    @Override
    public IFilterRepository getRepository() {
        return this.repository;
    }
}
