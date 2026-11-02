package xyz.refinedev.phoenix.utils;

public enum DefaultFontInfo {
    A('A', 5),
    defaultFontInfo('a', 5),
    B('B', 5),
    defaultFontInfoDefaultFontInfo('b', 5),
    C('C', 5),
    defaultFontInfo2('c', 5),
    D('D', 5),
    defaultFontInfo3('d', 5),
    E('E', 5),
    defaultFontInfo4('e', 5),
    F('F', 5),
    defaultFontInfo5('f', 4),
    G('G', 5),
    defaultFontInfo6('g', 5),
    H('H', 5),
    defaultFontInfo7('h', 5),
    defaultFontInfo8('I', 3),
    defaultFontInfo9('i', 1),
    J('J', 5),
    defaultFontInfo10('j', 5),
    K('K', 5),
    defaultFontInfo11('k', 4),
    L('L', 5),
    defaultFontInfo12('l', 1),
    M('M', 5),
    defaultFontInfo13('m', 5),
    N('N', 5),
    defaultFontInfo14('n', 5),
    O('O', 5),
    defaultFontInfo15('o', 5),
    P('P', 5),
    defaultFontInfo16('p', 5),
    Q('Q', 5),
    defaultFontInfo17('q', 5),
    R('R', 5),
    defaultFontInfo18('r', 5),
    S('S', 5),
    defaultFontInfo19('s', 5),
    T('T', 5),
    defaultFontInfo20('t', 4),
    U('U', 5),
    defaultFontInfo21('u', 5),
    V('V', 5),
    defaultFontInfo22('v', 5),
    W('W', 5),
    defaultFontInfo23('w', 5),
    X('X', 5),
    defaultFontInfo24('x', 5),
    Y('Y', 5),
    defaultFontInfo25('y', 5),
    Z('Z', 5),
    defaultFontInfo26('z', 5),
    NUM_1('1', 5),
    NUM_2('2', 5),
    NUM_3('3', 5),
    NUM_4('4', 5),
    NUM_5('5', 5),
    NUM_6('6', 5),
    NUM_7('7', 5),
    NUM_8('8', 5),
    NUM_9('9', 5),
    NUM_0('0', 5),
    EXCLAMATION_POINT('!', 1),
    AT_SYMBOL('@', 6),
    NUM_SIGN('#', 5),
    DOLLAR_SIGN('$', 5),
    PERCENT('%', 5),
    UP_ARROW('^', 5),
    AMPERSAND('&', 5),
    ASTERISK('*', 5),
    LEFT_PARENTHESIS('(', 4),
    RIGHT_PERENTHESIS(')', 4),
    MINUS('-', 5),
    UNDERSCORE('_', 5),
    PLUS_SIGN('+', 5),
    EQUALS_SIGN('=', 5),
    LEFT_CURL_BRACE('{', 4),
    RIGHT_CURL_BRACE('}', 4),
    LEFT_BRACKET('[', 3),
    RIGHT_BRACKET(']', 3),
    COLON(':', 1),
    SEMI_COLON(';', 1),
    DOUBLE_QUOTE('"', 3),
    SINGLE_QUOTE('\'', 1),
    LEFT_ARROW('<', 4),
    RIGHT_ARROW('>', 4),
    QUESTION_MARK('?', 5),
    SLASH('/', 5),
    BACK_SLASH('\\', 5),
    LINE('|', 1),
    TILDE('~', 5),
    TICK('`', 2),
    PERIOD('.', 1),
    COMMA(',', 1),
    SPACE(' ', 3),
    DEFAULT('a', 4);

    private final char character;

    private final int length;

    DefaultFontInfo(char character, int length) {
        this.character = character;
        this.length = length;
    }

    public static DefaultFontInfo getDefaultFontInfo(char value) {
        DefaultFontInfo[] defaultFontInfos;
        int value2 = (defaultFontInfos = values()).length;
        for (int index = 0; index < value2; index++) {
            DefaultFontInfo defaultFontInfo;
            if ((defaultFontInfo = defaultFontInfos[index]).getCharacter() == value) {
                return defaultFontInfo;
            }
        }
        return DEFAULT;
    }

    public int getBoldLength() {
        return this == SPACE ? this.getLength() : this.length + 1;
    }

    public char getCharacter() {
        return this.character;
    }

    public int getLength() {
        return this.length;
    }
}
