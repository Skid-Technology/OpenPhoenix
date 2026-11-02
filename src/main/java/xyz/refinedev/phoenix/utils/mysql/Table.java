package xyz.refinedev.phoenix.utils.mysql;

import java.util.StringJoiner;

public interface Table {

    String getName();

    default String getTableAttributes() {
        StringJoiner stringJoiner = new StringJoiner(",");
        Attribute[] attributes;
        int value = (attributes = this.getAttributes()).length;
        for (int index = 0; index < value; index++) {
            Attribute attribute = attributes[index];
            stringJoiner.add(attribute.toDefinition());
        }
        return stringJoiner.toString();
    }

    Attribute[] getAttributes();
}
