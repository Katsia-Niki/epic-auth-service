package by.nikiforova.epic_auth_service.exception;

public class CredentialAlreadyExistsException extends RuntimeException {
    public CredentialAlreadyExistsException(String message) {
        super(message);
    }
}
