import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * This class represent the menu of four tesks to the client,
 * and send tasks and messages to the server
 * and get updates and results from the server
 */
public class Client {

    /**
     * This method get int input from user between range -> min to max
     * @param min type: int
     * @param max type: int
     * @param str type: String
     * @return int
     */
    private static int inputFromUserBetweenRange(int min, int max, String str){
        Scanner in = new Scanner(System.in);
        int num;

        do {
            System.out.print("Please enter "+ str +" between: " + min + " to: " + max + ": ");
            num = in.nextInt();
        } while (num < min || num >= max);

        return num;
    }

    /**
     * This method returns index according to the user request
     * @param matrix type: Matrix
     * @return Index
     */
    private static Index indexRequest(Matrix matrix) {
        int row, column, lenCol = matrix.getPrimitiveMatrix()[0].length, lenRow = matrix.getPrimitiveMatrix().length;
        row = inputFromUserBetweenRange(0, lenRow, "row");
        column = inputFromUserBetweenRange(0, lenCol, "col");

        return new Index(row , column);
    }

    /**
     * This method create matrix by the user
     * @return Matrix
     */
    public static Matrix createMatrix() {
        Scanner in = new Scanner(System.in);
        int row, column;
        row = inputFromUserBetweenRange(0, 100, "row");
        column = inputFromUserBetweenRange(0, 100, "col");
        int[][] matrix = new int[row][column];
        System.out.println("enter your matrix:");
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < column; c++) {
                matrix[r][c] = in.nextInt();
            }
        }
        return new Matrix(matrix);
    }

    /**
     * This method represent the menu of tasks
     */
    private static void menuOfTasks() {
        System.out.println("Please choose one task:");
        System.out.println("1 -> Find all Connected Components with value of 1");
        System.out.println("2 -> Find all shortest paths from source to destination");
        System.out.println("3 -> Submarine game: Find the number of normal submarines");
        System.out.println("4 -> Find all paths with minimum weight from source to destination");
        System.out.println("5 -> Exit");
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException ,ClassCastException{

        Scanner scanner = new Scanner(System.in);
        Socket socket = new Socket("127.0.0.1",8010);
        System.out.println("Socket created");

        ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

        Matrix matrix;
        boolean toExit = false;
        while(!toExit){
            menuOfTasks();
            String result = scanner.next();
            switch (result) {
                case "1" -> {
                    System.out.println("Start, task 1");
                    toServer.writeObject(result);
                    matrix = createMatrix();
                    System.out.println("From client, matrix: \n" + matrix);
                    toServer.writeObject(matrix);
                    List<HashSet<Index>> listOfSCCs = new ArrayList<>((List<HashSet<Index>>) fromServer.readObject());
                    System.out.println("From server, scc: " + listOfSCCs);
                    System.out.println("End, task 1");
                    scanner.nextLine();
                }

                case "2" -> {
                    System.out.println("Start, Task 2");
                    toServer.writeObject(result);
                    matrix = createMatrix();
                    System.out.println("From client, matrix: \n" + matrix);
                    toServer.writeObject(matrix);
                    Index startIndex = indexRequest(matrix);
                    System.out.println("From client, source node: " + startIndex);
                    toServer.writeObject(startIndex);
                    Index endIndex = indexRequest(matrix);
                    System.out.println("From client, destination node: " + endIndex);
                    toServer.writeObject(endIndex);
                    List<List<Index>> minPaths = new ArrayList<>((List<List<Index>>) fromServer.readObject());
                    System.out.println("From server, Shortest paths from source " + startIndex + " to destination " + endIndex + " are:\n" + minPaths);
                    System.out.println("End, task 2");
                    scanner.nextLine();
                }
                case "3" -> {
                    System.out.println("Start, Task 3");
                    toServer.writeObject(result);
                    matrix = createMatrix();
                    System.out.println("From client, matrix: \n" + matrix);
                    toServer.writeObject(matrix);
                    int amountOfSubmarine = (int) fromServer.readObject();
                    System.out.println("From Server, amount of submarine is: " + amountOfSubmarine);
                    System.out.println("End, task 3");
                    scanner.nextLine();
                }
                case "4" -> {
                    System.out.println("Start, Task 4");
                    toServer.writeObject(result);
                    matrix = createMatrix();
                    System.out.println("From client, matrix: \n" + matrix);
                    toServer.writeObject(matrix);
                    Index startIndex = indexRequest(matrix);
                    System.out.println("From client, source node: " + startIndex);
                    toServer.writeObject(startIndex);
                    Index endIndex = indexRequest(matrix);
                    System.out.println("From client, destination node: " + endIndex);
                    toServer.writeObject(endIndex);
                    LinkedList<List<Index>> minWeightList = new LinkedList<>((List<List<Index>>) fromServer.readObject());
                    System.out.println("From server, all paths with minimum weight from source " + startIndex + " to destination " + endIndex + " are:\n" + minWeightList);
                    System.out.println("End, task 4");
                    scanner.nextLine();
                }
                case "5" -> {
                    toExit = true;
                    toServer.writeObject(result);
                    fromServer.close();
                    toServer.close();
                    socket.close();
                    System.out.println("Client: Closed operational socket");
                }
            }
        }
    }
}
