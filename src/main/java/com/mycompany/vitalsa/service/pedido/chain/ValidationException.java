package com.mycompany.vitalsa.service.pedido.chain;

/**
 *
 * @author RRDev
*/

// Excepción a medida para cuando alguna validación pincha y tenemos que avisarle a la UI
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}