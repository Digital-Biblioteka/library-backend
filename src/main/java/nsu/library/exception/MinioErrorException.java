package nsu.library.exception;

import org.springframework.web.client.ResourceAccessException;

public class MinioErrorException extends ResourceAccessException {
    public MinioErrorException(String msg) {
        super(msg);
    }
}
