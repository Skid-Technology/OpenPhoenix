package xyz.refinedev.phoenix.utils.repository;

public class EntityVanishedException extends DataAccessException {

    public EntityVanishedException(String text) {
        super(text, null);
    }
}
