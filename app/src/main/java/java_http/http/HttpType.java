package java_http.http;

public enum HttpType {
    OZ("1.0"), OO("1.1"), TZ("2.0");
    private final String type;

    private HttpType(String type) {
        this.type = type;
    }
}