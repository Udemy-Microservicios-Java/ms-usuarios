package com.athenea.ms_usuarios.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class RespuestaError {

    private LocalDateTime timestamp;
    private String status;
    private String code;
    private String message;
    private String traceId;
    private List<String> details;

    public RespuestaError() {
    }

    public RespuestaError(LocalDateTime timestamp, String status, String code, String message,
                          String traceId, List<String> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.code = code;
        this.message = message;
        this.traceId = traceId;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getTraceId() {
        return traceId;
    }

    public List<String> getDetails() {
        return details;
    }
}
