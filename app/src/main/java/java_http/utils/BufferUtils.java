package java_http.utils;
import java.io.IOException;
import java.io.InputStream;

public class BufferUtils {
    public static boolean isBufferEmpty(InputStream in) {
        try {
            return in.available() == 0;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return true;
    }
}