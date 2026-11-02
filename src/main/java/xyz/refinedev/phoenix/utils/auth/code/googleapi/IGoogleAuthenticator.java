package xyz.refinedev.phoenix.utils.auth.code.googleapi;

public interface IGoogleAuthenticator {

    GoogleAuthenticatorKey createCredentials();

    GoogleAuthenticatorKey createCredentials(String text);

    int getTotpPassword(String text);

    int getTotpPassword(String text, long value);

    int getTotpPasswordOfUser(String text);

    int getTotpPasswordOfUser(String text, long value);

    boolean authorize(String text, int value);

    boolean authorize(String text, int value, long value2);

    boolean authorizeUser(String text, int value);

    boolean authorizeUser(String text, int value, long value2);

    ICredentialRepository getCredentialRepository();

    void setCredentialRepository(ICredentialRepository credentialRepository);
}
