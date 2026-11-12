package io.github.auticad.retail.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * La risorsa richiesta non esiste. Le API rispondono con un ProblemDetail 404 (ApiExceptionHandler);
 * per le pagine Thymeleaf basta @ResponseStatus, che porta alla pagina di errore 404.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
