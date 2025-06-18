package in.natchapol.deliveryfoodapi.exception;


import in.natchapol.deliveryfoodapi.io.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ErrorResponse handlerUserExist(UserAlreadyExistsException ex){
        return new ErrorResponse (ex.getMessage(),409);
    }
}
