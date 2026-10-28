package com.athenea.ms_usuarios.dto.response;

import java.time.LocalDateTime;

public class RespuestaExitosa<T> {

    private LocalDateTime timestamp;
    private String status;
    private String code;
    private String message;
    private T data;

    public RespuestaExitosa() {
    }

    public RespuestaExitosa(LocalDateTime timestamp, String status, String code, String message, T data) {
        this.timestamp = timestamp;
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
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

    public T getData() {
        return data;
    }
}
