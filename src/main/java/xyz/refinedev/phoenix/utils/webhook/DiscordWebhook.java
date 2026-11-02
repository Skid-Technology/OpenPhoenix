package xyz.refinedev.phoenix.utils.webhook;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class DiscordWebhook {

    private final String url;

    private String content;

    private String username;

    private String avatarUrl;

    private boolean tts;

    private List<DiscordWebhook.EmbedObject> embeds = new ArrayList<>();

    public DiscordWebhook(String url) {
        this.url = url;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setTts(boolean tts) {
        this.tts = tts;
    }

    public void addEmbed(DiscordWebhook.EmbedObject embedObject) {
        this.embeds.add(embedObject);
    }

    public void execute() throws IOException {
        if (this.content == null && this.embeds.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }
        DiscordWebhook.JSONObject jSONObject;
        (jSONObject = new DiscordWebhook.JSONObject()).put("content", this.content);
        jSONObject.put("username", this.username);
        jSONObject.put("avatar_url", this.avatarUrl);
        jSONObject.put("tts", this.tts);
        if (!this.embeds.isEmpty()) {
            ArrayList<xyz.refinedev.phoenix.utils.webhook.DiscordWebhook.JSONObject> values =
                    new ArrayList<>();
            for (DiscordWebhook.EmbedObject embedObject : this.embeds) {
                DiscordWebhook.JSONObject jSONObject2;
                (jSONObject2 = new DiscordWebhook.JSONObject())
                        .put("title", embedObject.getTitle());
                jSONObject2.put("description", embedObject.getDescription());
                jSONObject2.put("url", embedObject.getUrl());
                if (embedObject.getColor() != null) {
                    Color color;
                    int value =
                            (((color = embedObject.getColor()).getRed() << 8) + color.getGreen()
                                            << 8)
                                    + color.getBlue();
                    jSONObject2.put("color", value);
                }
                DiscordWebhook.EmbedObject.Footer footer = embedObject.getFooter();
                DiscordWebhook.EmbedObject.Image image = embedObject.getImage();
                DiscordWebhook.EmbedObject.Thumbnail thumbnail = embedObject.getThumbnail();
                DiscordWebhook.EmbedObject.Author author = embedObject.getAuthor();
                List<xyz.refinedev.phoenix.utils.webhook.DiscordWebhook.EmbedObject.Field> fields =
                        embedObject.getFields();
                if (footer != null) {
                    DiscordWebhook.JSONObject jSONObject3;
                    (jSONObject3 = new DiscordWebhook.JSONObject())
                            .put("text", DiscordWebhook.EmbedObject.Footer.access$100(footer));
                    jSONObject3.put(
                            "icon_url", DiscordWebhook.EmbedObject.Footer.access$200(footer));
                    jSONObject2.put("footer", jSONObject3);
                }
                if (image != null) {
                    DiscordWebhook.JSONObject jSONObject4;
                    (jSONObject4 = new DiscordWebhook.JSONObject())
                            .put("url", DiscordWebhook.EmbedObject.Image.access$300(image));
                    jSONObject2.put("image", jSONObject4);
                }
                if (thumbnail != null) {
                    DiscordWebhook.JSONObject jSONObject5;
                    (jSONObject5 = new DiscordWebhook.JSONObject())
                            .put("url", DiscordWebhook.EmbedObject.Thumbnail.access$400(thumbnail));
                    jSONObject2.put("thumbnail", jSONObject5);
                }
                if (author != null) {
                    DiscordWebhook.JSONObject jSONObject6;
                    (jSONObject6 = new DiscordWebhook.JSONObject())
                            .put("name", DiscordWebhook.EmbedObject.Author.access$500(author));
                    jSONObject6.put("url", DiscordWebhook.EmbedObject.Author.access$600(author));
                    jSONObject6.put(
                            "icon_url", DiscordWebhook.EmbedObject.Author.access$700(author));
                    jSONObject2.put("author", jSONObject6);
                }
                ArrayList<xyz.refinedev.phoenix.utils.webhook.DiscordWebhook.JSONObject> values2 =
                        new ArrayList<>();
                for (DiscordWebhook.EmbedObject.Field field : fields) {
                    DiscordWebhook.JSONObject jSONObject7;
                    (jSONObject7 = new DiscordWebhook.JSONObject())
                            .put("name", DiscordWebhook.EmbedObject.Field.access$800(field));
                    jSONObject7.put("value", DiscordWebhook.EmbedObject.Field.access$900(field));
                    jSONObject7.put("inline", DiscordWebhook.EmbedObject.Field.access$1000(field));
                    values2.add(jSONObject7);
                }
                jSONObject2.put("fields", values2.toArray());
                values.add(jSONObject2);
            }
            jSONObject.put("embeds", values.toArray());
        }
        HttpsURLConnection httpsURLConnection;
        (httpsURLConnection = (HttpsURLConnection) new URL(this.url).openConnection())
                .addRequestProperty("Content-Type", "application/json");
        httpsURLConnection.addRequestProperty("User-Agent", "Java-DiscordWebhook-BY-Gelox_");
        httpsURLConnection.setDoOutput(true);
        httpsURLConnection.setRequestMethod("POST");
        OutputStream outputStream;
        (outputStream = httpsURLConnection.getOutputStream())
                .write(jSONObject.toString().getBytes());
        outputStream.flush();
        outputStream.close();
        httpsURLConnection.getInputStream().close();
        httpsURLConnection.disconnect();
    }

    public static class EmbedObject {

        private String title;

        private String description;

        private String url;

        private Color color;

        private DiscordWebhook.EmbedObject.Footer footer;

        private DiscordWebhook.EmbedObject.Thumbnail thumbnail;

        private DiscordWebhook.EmbedObject.Image image;

        private DiscordWebhook.EmbedObject.Author author;

        private List<DiscordWebhook.EmbedObject.Field> fields = new ArrayList<>();

        public String getTitle() {
            return this.title;
        }

        public DiscordWebhook.EmbedObject setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getDescription() {
            return this.description;
        }

        public DiscordWebhook.EmbedObject setDescription(String description) {
            this.description = description;
            return this;
        }

        public String getUrl() {
            return this.url;
        }

        public DiscordWebhook.EmbedObject setUrl(String url) {
            this.url = url;
            return this;
        }

        public Color getColor() {
            return this.color;
        }

        public DiscordWebhook.EmbedObject setColor(Color color2) {
            this.color = color2;
            return this;
        }

        public DiscordWebhook.EmbedObject.Footer getFooter() {
            return this.footer;
        }

        public DiscordWebhook.EmbedObject.Thumbnail getThumbnail() {
            return this.thumbnail;
        }

        public DiscordWebhook.EmbedObject setThumbnail(String text) {
            this.thumbnail = new DiscordWebhook.EmbedObject.Thumbnail(text);
            return this;
        }

        public DiscordWebhook.EmbedObject.Image getImage() {
            return this.image;
        }

        public DiscordWebhook.EmbedObject setImage(String text) {
            this.image = new DiscordWebhook.EmbedObject.Image(text);
            return this;
        }

        public DiscordWebhook.EmbedObject.Author getAuthor() {
            return this.author;
        }

        public List<DiscordWebhook.EmbedObject.Field> getFields() {
            return this.fields;
        }

        public DiscordWebhook.EmbedObject setFooter(String text, String text2) {
            this.footer = new DiscordWebhook.EmbedObject.Footer(text, text2);
            return this;
        }

        public DiscordWebhook.EmbedObject setAuthor(String text, String text2, String text3) {
            this.author = new DiscordWebhook.EmbedObject.Author(text, text2, text3);
            return this;
        }

        public DiscordWebhook.EmbedObject addField(String text, String text2, boolean matches) {
            this.fields.add(new DiscordWebhook.EmbedObject.Field(text, text2, matches));
            return this;
        }

        class Author {

            private String name;

            private String url;

            private String iconUrl;

            private Author(String name, String url, String iconUrl) {
                this.name = name;
                this.url = url;
                this.iconUrl = iconUrl;
            }

            private String getName() {
                return this.name;
            }

            private String getUrl() {
                return this.url;
            }

            private String getIconUrl() {
                return this.iconUrl;
            }

            static String access$500(DiscordWebhook.EmbedObject.Author author) {
                return author.getName();
            }

            static String access$600(DiscordWebhook.EmbedObject.Author author) {
                return author.getUrl();
            }

            static String access$700(DiscordWebhook.EmbedObject.Author author) {
                return author.getIconUrl();
            }
        }

        class Field {

            private String name;

            private String value;

            private boolean inline;

            private Field(String name, String value, boolean inline) {
                this.name = name;
                this.value = value;
                this.inline = inline;
            }

            private String getName() {
                return this.name;
            }

            private String getValue() {
                return this.value;
            }

            private boolean isInline() {
                return this.inline;
            }

            static String access$800(DiscordWebhook.EmbedObject.Field field) {
                return field.getName();
            }

            static String access$900(DiscordWebhook.EmbedObject.Field field) {
                return field.getValue();
            }

            static boolean access$1000(DiscordWebhook.EmbedObject.Field field) {
                return field.isInline();
            }
        }

        class Footer {

            private String text;

            private String iconUrl;

            private Footer(String text, String iconUrl) {
                this.text = text;
                this.iconUrl = iconUrl;
            }

            private String getText() {
                return this.text;
            }

            private String getIconUrl() {
                return this.iconUrl;
            }

            static String access$100(DiscordWebhook.EmbedObject.Footer footer) {
                return footer.getText();
            }

            static String access$200(DiscordWebhook.EmbedObject.Footer footer) {
                return footer.getIconUrl();
            }
        }

        class Image {

            private String url;

            private Image(String url) {
                this.url = url;
            }

            private String getUrl() {
                return this.url;
            }

            static String access$300(DiscordWebhook.EmbedObject.Image image) {
                return image.getUrl();
            }
        }

        class Thumbnail {

            private String url;

            private Thumbnail(String url) {
                this.url = url;
            }

            private String getUrl() {
                return this.url;
            }

            static String access$400(DiscordWebhook.EmbedObject.Thumbnail thumbnail) {
                return thumbnail.getUrl();
            }
        }
    }

    class JSONObject {

        private final HashMap<String, Object> map = new HashMap<>();

        private JSONObject() {}

        void put(String text, Object result) {
            if (result != null) {
                this.map.put(text, result);
            }
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            Set values = this.map.entrySet();
            stringBuilder.append("{");
            int value = 0;
            Iterator iterator2 = values.iterator();
            while (iterator2.hasNext()) {
                Entry entry;
                Object value2 = (entry = (Entry) iterator2.next()).getValue();
                stringBuilder.append(this.quote((String) entry.getKey())).append(":");
                if (value2 instanceof String) {
                    stringBuilder.append(this.quote(String.valueOf(value2)));
                } else if (value2 instanceof Integer) {
                    stringBuilder.append(Integer.valueOf(String.valueOf(value2)));
                } else if (value2 instanceof Boolean) {
                    stringBuilder.append(value2);
                } else if (value2 instanceof DiscordWebhook.JSONObject) {
                    stringBuilder.append(value2.toString());
                } else if (value2.getClass().isArray()) {
                    stringBuilder.append("[");
                    int length = Array.getLength(value2);
                    for (int index = 0; index < length; index++) {
                        stringBuilder
                                .append(Array.get(value2, index).toString())
                                .append(index != length - 1 ? "," : "");
                    }
                    stringBuilder.append("]");
                }
                value++;
                stringBuilder.append(value == values.size() ? "}" : ",");
            }
            return stringBuilder.toString();
        }

        private String quote(String text) {
            return "\"" + text + "\"";
        }
    }
}
