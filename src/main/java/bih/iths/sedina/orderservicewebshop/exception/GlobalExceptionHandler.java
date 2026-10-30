package bih.iths.sedina.orderservicewebshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Exception handler för HttpClientErrorException.BadRequest
    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<String> handleInsufficientStock(HttpClientErrorException.BadRequest exception) {
        String errorMessage = exception.getResponseBodyAsString();

        // Om felmeddelandet innehåller "Insufficient stock", returnera en konfliktstatus med ett specifikt meddelande
        if (errorMessage.contains("Insufficient stock")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("There are not enough products in stock. "
                    + "Check the quantity in the shopping cart and try again.");
        }

        // Annars, returnera en generell felmeddelande för order som inte kunde skapas
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Your order could not be created.");
    }
}
