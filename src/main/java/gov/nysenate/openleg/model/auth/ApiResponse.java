package gov.nysenate.openleg.model.auth;

public class ApiResponse
{
    private ApiRequest baseRequest;

    private String responseTime;

    private int statusCode;

    private String contentType;

    private double processTime;

    public ApiResponse(ApiRequest baseRequest) {
        this.baseRequest = baseRequest;
    }


    /** Getters and Setters */
    public ApiRequest getBaseRequest() { return baseRequest; }
    public void setBaseRequest(ApiRequest baseRequest) { this.baseRequest = baseRequest; }

    public String getResponseTime() { return responseTime; }
    public void setResponseTime(String responseTime) { this.responseTime = responseTime; }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public double getProcessTime() { return processTime; }
    public void setProcessTime(double processTime) { this.processTime = processTime; }
}
