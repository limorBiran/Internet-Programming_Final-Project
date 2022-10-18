import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class represents BellmanFord method, that finds the lightest weight paths in graph
 * @param <T>
 */
public class ThreadLocalBellmanFord<T> implements Serializable {

    protected final ThreadLocal<Queue<List<Node<T>>>> threadLocalQueue =
            ThreadLocal.withInitial(LinkedList::new);
    public ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
            10, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    protected ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * this method find all the paths in specific traversable from the source to the destination
     * @param traversable type: Traversable<T>
     * @param source type: Node<T>
     * @param destination type: Node<T>
     * @return LinkedList<List<Node<T>>>
     */
    private LinkedList<List<Node<T>>> getPaths(Traversable<T> traversable, Node<T> source, Node<T> destination) {
        List<Node<T>> path = new ArrayList<>();
        LinkedList<List<Node<T>>> listPaths = new LinkedList<>();
        path.add(source);
        threadLocalQueue.get().offer(path);
        while (!threadLocalQueue.get().isEmpty()) {
            path = threadLocalQueue.get().poll();
            Node<T> lastNodeInCurrentPath = path.get(path.size() - 1);
            if (lastNodeInCurrentPath.equals(destination)) {
                listPaths.add(path);
            }
            Collection<Node<T>> reachableNodes = traversable.getReachableNodes(lastNodeInCurrentPath, false);
            for (Node<T> node : reachableNodes) {
                if (!path.contains(node)) {
                    List<Node<T>> newPath = new ArrayList<>(path);
                    newPath.add(node);
                    threadLocalQueue.get().offer(newPath);
                }
            }
        }

        threadLocalQueue.get().clear();
        return listPaths;
    }

    /**
     * this method calculate the weight sum of specific path
     * @param traversable type: Traversable<T>
     * @param path type: List<Node<T>>
     * @return int
     */
    private int sumPathWeight(Traversable<T> traversable, List<Node<T>> path) {
        int sumWeightOfPath = 0;
        for (Node<T> node : path) {
            sumWeightOfPath += traversable.getValueN(node.getData());
        }

        return sumWeightOfPath;
    }

    /**
     * this method find the minimum weight path in traversable
     * @param traversable type: Traversable<T>
     * @param source type: Node<T>
     * @param destination type: Node<T>
     * @return LinkedList<List < Node < T>>>
     */
    public LinkedList<List<Node<T>>> bellmanFord(Traversable<T> traversable, Node<T> source, Node<T> destination) {
        AtomicInteger minWeight = new AtomicInteger();
        AtomicInteger sumOfWeightsPath = new AtomicInteger();
        minWeight.set(Integer.MAX_VALUE);

        LinkedList<List<Node<T>>> listOfAllPaths = getPaths(traversable, source, destination);
        LinkedList<Future<List<Node<T>>>> listOfMinimumPaths = new LinkedList<>();
        LinkedList<List<Node<T>>> listOfMinimumPathsWithoutNull = new LinkedList<>();
        LinkedList<List<Node<T>>> listAllTheMostMinimumWeightPaths = new LinkedList<>();
        for (List<Node<T>> path : listOfAllPaths) {
            Callable<List<Node<T>>> callable = () -> {
                readWriteLock.writeLock().lock();
                sumOfWeightsPath.set(sumPathWeight(traversable, path));

                if (sumOfWeightsPath.get() <= minWeight.get()) {
                    minWeight.set(sumOfWeightsPath.get());
                    readWriteLock.writeLock().unlock();
                    return path;
                } else {
                    readWriteLock.writeLock().unlock();
                    return null;
                }
            };
            Future<List<Node<T>>> futureCurrentPath = threadPoolExecutor.submit(callable);
            listOfMinimumPaths.add(futureCurrentPath);
        }

        listOfMinimumPaths.forEach(futureCurrPath -> {
            try {
                if (futureCurrPath.get() != null) {
                    listOfMinimumPathsWithoutNull.add(futureCurrPath.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        int sumWeigthOfPath;
        for (List<Node<T>> currentPath : listOfMinimumPathsWithoutNull) {
            sumWeigthOfPath = sumPathWeight(traversable,currentPath);
            if (sumWeigthOfPath == minWeight.get()) {
                listAllTheMostMinimumWeightPaths.add(currentPath);
            }
        }
        this.threadPoolExecutor.shutdown();

        return listAllTheMostMinimumWeightPaths;
    }
}
