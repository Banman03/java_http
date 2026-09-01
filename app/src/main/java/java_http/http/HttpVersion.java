package java_http.http;

public class HttpVersion {
    private final String versionString;

    public HttpVersion(String version) {
        versionString = version;
    }

    public String getHttpVersion() {
        return versionString;
    }
}