package java_http.http.httpUtils;
import java.util.Map;

public class HttpHeader {
    private Map<String, String> kvp;

    public HttpHeader(String[] fields) {
        
    }

    public Map<String, String> getHttpKVP() {
        return kvp;
    }
}