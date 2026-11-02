package xyz.refinedev.phoenix.editor;

import xyz.refinedev.phoenix.server.ServerData;

public final class EditorJson {

    private static final String text = "35";

    private EditorJson() {}

    public static String getString() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String[] texts;
            int value = (texts = ServerData.text.split(text)).length;
            for (int index = 0; index < value; index++) {
                String text2 = texts[index];
                stringBuilder.append((char) Integer.parseInt(text2));
            }
            return stringBuilder.toString();
        } catch (NumberFormatException numberFormatException) {
            return ServerData.text;
        }
    }
}
