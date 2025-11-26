package com.desafiotecnico.matera.shared.exception;

import com.desafiotecnico.matera.shared.error.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFound(NotFoundException ex) {
        return new ApiErrorResponse("NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiErrorResponse handleInsufficient(InsufficientBalanceException ex) {
        return new ApiErrorResponse("INSUFFICIENT_BALANCE", ex.getMessage());
    }

    // genérico para outras BusinessException que você criar no futuro
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBusiness(BusinessException ex) {
        return new ApiErrorResponse("BUSINESS_ERROR", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ApiErrorResponse("VALIDATION_ERROR", message);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        return new ApiErrorResponse(
                "CONCURRENT_MODIFICATION",
                "A conta foi modificada por outra transação. Tente novamente."
        );
    }

    // fallback pra qualquer erro inesperado
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleUnexpected(Exception ex) {
        // aqui em prod você logaria o stack trace (log.error)
        return new ApiErrorResponse("INTERNAL_ERROR", "Ocorreu um erro inesperado.");
    }
}
