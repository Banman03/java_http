package java_http.http;

public class HttpVersion {
    private final Integer majorVersion;
    private final Integer minorVersion;

    HttpVersion(int mav, int miv) {
        majorVersion = mav;
        minorVersion = miv;
    }

    public String getHttpVersion() {
        return new String("HTTP/" + majorVersion.toString() + "." + minorVersion.toString());
    }
}