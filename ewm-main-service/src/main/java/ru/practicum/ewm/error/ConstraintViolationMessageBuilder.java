package ru.practicum.ewm.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.Locale;

final class ConstraintViolationMessageBuilder {

    private ConstraintViolationMessageBuilder() {
    }

    static String buildMessage(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .findFirst()
                .map(ConstraintViolationMessageBuilder::toMessage)
                .orElse("Ошибка валидации входных данных");
    }

    private static String toMessage(ConstraintViolation<?> violation) {
        String field = extractFieldName(violation.getPropertyPath());
        Object invalidValue = violation.getInvalidValue();
        String value = String.valueOf(invalidValue);
        return String.format(Locale.ROOT, "Field: %s. Error: %s. Value: %s", field, violation.getMessage(), value);
    }

    private static String extractFieldName(Path propertyPath) {
        String field = "";
        for (Path.Node node : propertyPath) {
            String nodeName = node.getName();
            if (nodeName != null && !nodeName.isBlank()) {
                field = nodeName;
            }
        }
        return field;
    }
}
