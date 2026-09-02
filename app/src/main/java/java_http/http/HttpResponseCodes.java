package java_http.http;

public enum HttpResponseCodes {
    CONTINUE(100), OK(200), CREATED(201), NO_CONTENT(204), MULTIPLE_CHOICES(300), MOVED_PERM(301), SEE_OTHER(303),
    NOT_MODIFIED(304), TEMP_REDIR(307), PERM_REDIR(308), BAD_REQUEST(400), UNAUTHORIZED(401), FORBIDDEN(403), NOT_FOUND(404),
    REQUEST_TIMEOUT(408), URI_TOO_LONG(414), INTERNAL_ERROR(500), NOT_IMPLEMENTED(501), BAD_GATEWAY(502), HTTP_VERSION_NOT_SUPPORTED(505),
    LOOP_DETECTED(508), NETWORK_AUTH_REQUIRED(511);

    private Integer responseCode;

    public Integer getResponseCode() {
        return responseCode;
    }

    private HttpResponseCodes(Integer code) {
        responseCode = code;
    }
}