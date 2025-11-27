package com.desafiotecnico.matera.shared.exception;

/**
 * Exceção base para erros de regra de negócio.
 * Pode ser usada diretamente ou estendida por exceções mais específicas.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
