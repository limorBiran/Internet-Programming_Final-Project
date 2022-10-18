import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * This class Implements IHandler interface
 * There are four tasks executed - the results are passed to the client
 */
public class MatrixIHandler implements IHandler{

    @Override
    public void handle(InputStream fromClient, OutputStream toClient) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(fromClient);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(toClient);
        boolean doWork = true;
        Matrix matrix;
        while(doWork){
            switch (objectInputStream.readObject().toString()) {
                case "1" -> {
                    System.out.println("Start, Task 1");
                    matrix = (Matrix) objectInputStream.readObject();
                    List<HashSet<Index>> listOfSccs;
                    ThreadLocalDfsVisit threadLocalDfsVisit = new ThreadLocalDfsVisit();
                    listOfSccs = threadLocalDfsVisit.getAllSccParallel(matrix.getPrimitiveMatrix());
                    objectOutputStream.writeObject(listOfSccs);
                    System.out.println("End, Task 1");
                }
                case "2" -> {
                    System.out.println("Start, Task 2");
                    matrix = (Matrix) objectInputStream.readObject();
                    matrix.printMatrix();
                    Index source, destination;
                    source = (Index) objectInputStream.readObject();
                    System.out.println("From client - source index is: " + source);
                    destination = (Index) objectInputStream.readObject();
                    System.out.println("From client - destination index is: " + destination);
                    TraversableMatrix traversable = new TraversableMatrix(matrix);
                    traversable.setStartIndex(source);
                    traversable.setDestinationIndex(destination);
                    ThreadLocalBfsVisit threadLocalBfsVisit = new ThreadLocalBfsVisit();
                    List<List<Index>> minPaths;
                    minPaths = threadLocalBfsVisit.bfs(traversable, traversable.getSource(), traversable.getDestination());
                    objectOutputStream.writeObject(minPaths);
                    System.out.println("End, Task 2");
                }
                case "3" -> {
                    System.out.println("Start, Task 3");
                    matrix = (Matrix) objectInputStream.readObject();
                    List<HashSet<Index>> listOfSccs;
                    ThreadLocalDfsVisit<Index> threadLocalDfsVisit = new ThreadLocalDfsVisit<>();
                    listOfSccs = threadLocalDfsVisit.getAllSccParallel(matrix.getPrimitiveMatrix());
                    Submarine submarine = new Submarine();
                    int anountOfNormalSubmarine = submarine.submarineGame(listOfSccs, matrix.getPrimitiveMatrix());
                    objectOutputStream.writeObject(anountOfNormalSubmarine);
                    System.out.println("End, Task 3");
                }
                case "4" -> {
                    System.out.println("Start, Task 4");
                    matrix = (Matrix) objectInputStream.readObject();
                    matrix.printMatrix();
                    Index source, destination;
                    source = (Index) objectInputStream.readObject();
                    System.out.println("From client - source index is: " + source);
                    destination = (Index) objectInputStream.readObject();
                    System.out.println("From client - destination index is: " + destination);
                    TraversableMatrix traversable = new TraversableMatrix(matrix);
                    traversable.setStartIndex(source);
                    traversable.setDestinationIndex(destination);
                    ThreadLocalBellmanFord threadLocalBellmanFord = new ThreadLocalBellmanFord();
                    LinkedList<List<Index>> minimumWeightList;
                    minimumWeightList = threadLocalBellmanFord.bellmanFord(traversable, traversable.getSource(), traversable.getDestination());
                    System.out.println(minimumWeightList);
                    objectOutputStream.writeObject(minimumWeightList);
                    System.out.println("End, Task 4");
                }
                case "5" -> doWork = false;
            }
        }
    }
}
