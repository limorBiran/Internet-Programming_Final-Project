import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This interface defines the functionality required for a handle
 */
public interface IHandler {
    void handle(InputStream fromClient, OutputStream toClient)
            throws IOException, ClassNotFoundException;
}