package xyz.refinedev.phoenix.profile.disguise;

public interface ISkin {

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    String getName();

    String getValue();

    String getSignature();

    void setName(String text);

    void setValue(String text);

    void setSignature(String text);
}
