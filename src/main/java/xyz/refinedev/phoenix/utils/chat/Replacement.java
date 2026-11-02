package xyz.refinedev.phoenix.utils.chat;

import xyz.refinedev.phoenix.utils.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Replacement {

    private Map<Object, Object> replacements = new HashMap<>();

    private String message;

    public Replacement(String message) {
        this.message = message;
    }

    public Replacement add(Object result, Object result2) {
        this.replacements.put(result, result2);
        return this;
    }

    @Override
    public String toString() {
        if (this.message == null) {
            return "";
        }
        Set values = this.replacements.keySet();
        values.forEach(
                result ->
                        this.message =
                                this.message.replace(
                                        String.valueOf(result),
                                        String.valueOf(
                                                this.replacements.get(result) == null
                                                        ? ""
                                                        : this.replacements.get(result))));
        return Color.translate(this.message);
    }

    public String toString(boolean matches) {
        Set values = this.replacements.keySet();
        values.forEach(
                result ->
                        this.message =
                                this.message.replace(
                                        String.valueOf(result),
                                        String.valueOf(this.replacements.get(result))));
        return this.message;
    }

    public Map<Object, Object> getReplacements() {
        return this.replacements;
    }

    public String getMessage() {
        return this.message;
    }

    public void setReplacements(Map<Object, Object> entries) {
        this.replacements = entries;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
