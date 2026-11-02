package xyz.refinedev.phoenix.utils.repository;

public class DataAccessException extends RuntimeException {

    public DataAccessException(String text, Throwable throwable) {
        super(text, throwable);
    }
}
