package xyz.refinedev.phoenix.config;

import xyz.refinedev.lib.config.ConfigHandler;
import xyz.refinedev.lib.config.StaticConfig;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class WebhookConfig extends StaticConfig {

    @Ignore public static WebhookConfig INSTANCE;

    public WebhookConfig(ConfigHandler configHandler) {
        super(new File("plugins/Phoenix/webhooks.yml"), configHandler);
        INSTANCE = this;
    }

    public static class GRANT {

        public static String WEBHOOK_URL = "";

        public static class EXECUTE_GRANT {

            public static String COLOR = "#bd0010";

            public static String TITLE = "Grant Executed for <player>";
        }

        public static class REVOKE_GRANT {

            public static String COLOR = "#bd0010";

            public static String TITLE = "Grant Revoked for <player>";
        }
    }

    public static class PUNISHMENT {

        public static String WEBHOOK_URL = "";

        public static class EXECUTE_PUNISHMENT {

            public static String COLOR = "#bd0010";

            public static String TITLE = "Punishment Executed for <player>";
        }

        public static class REVOKE_PUNISHMENT {

            public static String COLOR = "#bd0010";

            public static String TITLE = "Punishment Revoked for <player>";
        }
    }

    public static class REPORTS {

        public static String WEBHOOK_URL = "";

        public static class REPORT {

            public static String COLOR = "#bd0010";

            public static String TITLE = "Report \u00bb <player>";
        }

        public static class REPORT_RESOLVED {

            public static String COLOR = "#bd0010";

            public static String TITLE = "Report Resolved \u00bb <player>";
        }

        public static class REQUEST {

            public static String COLOR = "#bd0010";

            public static String TITLE = "Request \u00bb <player>";
        }
    }

    public static class SERVER {

        public static String WEBHOOK_URL = "";

        public static class SHUTDOWN {

            public static String COLOR = "#bd0010";

            public static String TITLE = "<server> has stopped";

            public static List<String> DESCRIPTION =
                    Arrays.asList("<server> (<region>) has just shut down");
        }

        public static class STARTUP {

            public static String COLOR = "#bd0010";

            public static String TITLE = "<server> has started";

            public static List<String> DESCRIPTION =
                    Arrays.asList("<server> (<region>) has just started up");
        }
    }

    public static class STAFF {

        public static String WEBHOOK_URL = "";

        public static class JOIN {

            public static String COLOR = "#bd0010";

            public static String TITLE = "<player> joined the network";

            public static List<String> DESCRIPTION =
                    Arrays.asList("<player> has joined <server> on <proxy>");
        }

        public static class LEAVE {

            public static String COLOR = "#bd0010";

            public static String TITLE = "<player> left the network";

            public static List<String> DESCRIPTION =
                    Arrays.asList("<player> has left from <server>");
        }

        public static class SWITCH {

            public static String COLOR = "#bd0010";

            public static String TITLE = "<player> switched servers";

            public static List<String> DESCRIPTION =
                    Arrays.asList("<player> has joined <to> from <from>");
        }
    }
}
