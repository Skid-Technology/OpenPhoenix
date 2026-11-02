package xyz.refinedev.phoenix.utils.auth.code.googleapi;

import java.util.List;

public interface ICredentialRepository {

    String getSecretKey(String text);

    void saveUserCredentials(String text, String text2, int value, List<Integer> values);
}
