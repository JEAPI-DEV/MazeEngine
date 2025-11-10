package net.simplehardware;

import java.util.HashMap;
import java.util.Map;

public enum Mode {
    FLOOR,
    WALL,
    START,
    FINISH,
    SHEET,

    // Forms A–Z with associated labels
    FORM_A("A"),
    FORM_B("B"),
    FORM_C("C"),
    FORM_D("D"),
    FORM_E("E"),
    FORM_F("F"),
    FORM_G("G"),
    FORM_H("H"),
    FORM_I("I"),
    FORM_J("J"),
    FORM_K("K"),
    FORM_L("L"),
    FORM_M("M"),
    FORM_N("N"),
    FORM_O("O"),
    FORM_P("P"),
    FORM_Q("Q"),
    FORM_R("R"),
    FORM_S("S"),
    FORM_T("T"),
    FORM_U("U"),
    FORM_V("V"),
    FORM_W("W"),
    FORM_X("X"),
    FORM_Y("Y"),
    FORM_Z("Z");

    private final String label;

    Mode(String label) {
        this.label = label;
    }

    Mode() {
        this.label = null;
    }

    public String getLabel() {
        return label;
    }

    private static final Map<String, Mode> BY_LABEL = new HashMap<>();

    static {
        for (Mode mode : values()) {
            if (mode.label != null) {
                BY_LABEL.put(mode.label, mode);
            }
        }
    }

    public static Mode fromLabel(String label) {
        return BY_LABEL.get(label);
    }

    public static Mode fromChar(char ch) {
        return switch (ch) {
            case '#' -> WALL;
            case '@' -> START;
            case '!' -> FINISH;
            case 'S' -> SHEET;
            case '$' -> FORM_S;
            default -> {
                String label = String.valueOf(ch);
                Mode m = fromLabel(label);
                yield (m != null) ? m : FLOOR;
            }
        };
    }

}
