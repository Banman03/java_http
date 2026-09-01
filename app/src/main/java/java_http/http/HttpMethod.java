package java_http.http;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;

public enum HttpMethod {
    GET("GET"), PUT("PUT"), POST("POST"), DELETE("DELETE"), HEAD("HEAD");

    private final String method;

    private HttpMethod(String method) {
        this.method = method;
    }

    public String getHttpMethod() {
        return method;
    }

    private static final Map<String, HttpMethod> BY_METHOD = Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(http -> http.method.toUpperCase(), Function.identity()));
    
    public static Optional<HttpMethod> parseMethodSafe(String input) {
        if (input.isBlank()) return Optional.empty();
        return Optional.ofNullable(BY_METHOD.get(input.trim().toLowerCase()));
    }
}