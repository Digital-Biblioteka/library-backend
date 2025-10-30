package nsu.library.exception;

public class UserExistsException extends IllegalArgumentException{
    public UserExistsException() {
        super("Такой пользователь уже существует");
    }

    public UserExistsException(String message) {
        super(message);
    }

    public UserExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserExistsException(Throwable cause) {
        super(cause);
    }
}
