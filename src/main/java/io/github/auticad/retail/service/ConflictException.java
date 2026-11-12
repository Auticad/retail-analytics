package io.github.auticad.retail.service;

/** L'operazione viola una regola di business (codice duplicato, negozio con vendite...): l'API risponde 409. */
public class ConflictException extends RuntimeException {

    private final String field;

    public ConflictException(String field, String message) {
        super(message);
        this.field = field;
    }

    /** Campo del form a cui riferire l'errore; null se l'errore riguarda l'operazione nel suo insieme. */
    public String getField() {
        return field;
    }
}
