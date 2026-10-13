package bih.iths.sedina.orderservicewebshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<String> handleInsufficientStock(HttpClientErrorException.BadRequest exception) {
        String errorMessage = exception.getResponseBodyAsString();

        if (errorMessage.contains("Insufficient stock")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("There are not enough products in stock. "
                    + "Check the quantity in the shopping cart and try again.");
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Your order could not be created.");
    }
}
