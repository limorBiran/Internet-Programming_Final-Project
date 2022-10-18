import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class represent the TCP server Handle a request from the clients
 */
public class TcpServer {

    private final int port;
    private volatile boolean stopServer;
    private ThreadPoolExecutor threadPoolExecutor;
    private IHandler requestHandler;
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    //Constructor
    public TcpServer(int port) {
        this.port = port;
        stopServer = false;
        this.threadPoolExecutor = null;
        requestHandler = null;
    }

    /**
     * This method listen to incoming connections, accept if possible and handle clients
     * @param concreteHandler type: IHandler
     */
    public void supportClients(IHandler concreteHandler) {
        this.requestHandler = concreteHandler;

        new Thread(() -> {
            threadPoolExecutor = new ThreadPoolExecutor(3, 5, 10,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<>());
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while (!stopServer) {
                    Socket socket = serverSocket.accept();
                    Runnable runnable = () -> {
                        try {
                            requestHandler.handle(socket.getInputStream(),
                                    socket.getOutputStream());
                            socket.getInputStream().close();
                            socket.getOutputStream().close();
                            socket.close();
                        } catch (IOException | ClassNotFoundException ioException) {
                            System.err.println(ioException.getMessage());
                        }
                    };

                    threadPoolExecutor.execute(runnable);
                }
                serverSocket.close();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }).start();
    }

    /**
     * This method stop the server action
     */
    public void stop() {
        if (!stopServer) {
            try {
                readWriteLock.writeLock().lock();
                if (!stopServer) {
                    if (threadPoolExecutor != null)
                        threadPoolExecutor.shutdown();
                }
            } catch (SecurityException se) {
                se.printStackTrace();
            } finally {
                stopServer = true;
                readWriteLock.writeLock().unlock();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting the server");

        TcpServer matrixServer = new TcpServer(8010);
        matrixServer.supportClients(new MatrixIHandler());
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Stopping the server");
        matrixServer.stop();
    }
}
