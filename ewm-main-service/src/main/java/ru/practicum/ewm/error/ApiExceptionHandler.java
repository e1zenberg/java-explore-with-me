package ru.practicum.ewm.error;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.dto.ApiError;

@RestControllerAdvice
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(BadRequestException ex) {
        return ApiError.builder()
                .status("BAD_REQUEST")
                .reason("Некорректный запрос.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbidden(ForbiddenException ex) {
        return ApiError.builder()
                .status("FORBIDDEN")
                .reason("Доступ запрещён.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException ex) {
        return ApiError.builder()
                .status("NOT_FOUND")
                .reason("Объект не найден.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(Exception ex) {
        return ApiError.builder()
                .status("BAD_REQUEST")
                .reason("Некорректный запрос.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNoBody(HttpMessageNotReadableException ex) {
        return ApiError.builder()
                .status("BAD_REQUEST")
                .reason("Некорректный запрос.")
                .message("Тело запроса отсутствует или не читается.")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(DataIntegrityViolationException ex) {
        String msg = ex.getMostSpecificCause() == null
                ? ex.getMessage()
                : ex.getMostSpecificCause().getMessage();
        return ApiError.builder()
                .status("CONFLICT")
                .reason("Нарушение целостности данных.")
                .message(msg)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleOther(Exception ex) {
        return ApiError.builder()
                .status("INTERNAL_SERVER_ERROR")
                .reason("Внутренняя ошибка сервера.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
