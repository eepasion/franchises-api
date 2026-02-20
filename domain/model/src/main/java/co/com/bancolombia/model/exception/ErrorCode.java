package co.com.bancolombia.model.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    B400001("B400-001", "Invalid format: %s", 400),
    B404001("B404-001", "Franchise not found", 404);

    private final String code;
    private final String messageTemplate;
    private final int httpStatus;

    ErrorCode(String code, String messageTemplate, int httpStatus) {
        this.code = code;
        this.messageTemplate = messageTemplate;
        this.httpStatus = httpStatus;
    }

    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}