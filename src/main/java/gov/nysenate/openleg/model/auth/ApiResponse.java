package gov.nysenate.openleg.model.auth;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ApiResponse
{
    private ApiRequest baseRequest;
    private LocalDateTime responseDateTime;
    private int statusCode;
    private String contentType;
    private double processTime;

    /** --- Constructors --- */

    public ApiResponse(ApiRequest baseRequest, HttpServletResponse response, LocalDateTime responseDateTime) {
        this.baseRequest = baseRequest;
        this.responseDateTime = responseDateTime;
        this.processTime = ChronoUnit.MILLIS.between(baseRequest.getRequestTime(), responseDateTime);
        this.statusCode = response.getStatus();
        this.contentType = response.getContentType();
    }

    /** --- Basic Getters/Setters --- */

    public ApiRequest getBaseRequest() {
        return baseRequest;
    }

    public void setBaseRequest(ApiRequest baseRequest) {
        this.baseRequest = baseRequest;
    }

    public LocalDateTime getResponseDateTime() {
        return responseDateTime;
    }

    public void setResponseDateTime(LocalDateTime responseDateTime) {
        this.responseDateTime = responseDateTime;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public double getProcessTime() {
        return processTime;
    }

    public void setProcessTime(double processTime) {
        this.processTime = processTime;
    }
}