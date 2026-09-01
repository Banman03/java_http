package java_http.http;

public enum HttpMethod {
    GET("GET"), PUT("PUT"), POST("POST"), DELETE("DELETE"), HEAD("HEAD");

    private String method;
    
    private HttpMethod(String method) {
        this.method = method;
    }

    public String getHttpMethod() {
        return method;
    }
}