import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class represents a Thread-safe BFS algorithm
 * @param <T>
 */
public class ThreadLocalBfsVisit<T>{

    protected final ThreadLocal<Queue<List<Node<T>>>> threadLocalQueue =
            ThreadLocal.withInitial(LinkedList::new);
    public ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
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
            Collection<Node<T>> reachableNodes = traversable.getReachableNodes(lastNodeInCurrentPath, true);
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
     * This method finds the shortest paths in a parallel way
     * @param traversable represent a graph
     * @param source represent start index
     * @param destination represent final/ destination index
     * @return List<List < Node < T>>> - all the shortest paths between source node to destination
     */
    public List<List<Node<T>>> bfs(Traversable<T> traversable, Node<T> source, Node<T> destination) {
        AtomicInteger minSize = new AtomicInteger();
        AtomicInteger sumOfSizePath = new AtomicInteger();
        minSize.set(Integer.MAX_VALUE);
        List<Future<List<Node<T>>>> listOfMinimumPaths = new ArrayList<>();
        List<List<Node<T>>> listOfAllPaths = getPaths(traversable,source,destination);
        LinkedList<List<Node<T>>> listOfMinimumPathsWithoutNull = new LinkedList<>();
        List<List<Node<T>>> listAllTheMostMinimumSizePaths = new ArrayList<>();

        for (List<Node<T>> path: listOfAllPaths) {
            Callable<List<Node<T>>> callable = () -> {
                readWriteLock.writeLock().lock();
                sumOfSizePath.set(path.size());

                if(sumOfSizePath.get() <= minSize.get()) {
                    minSize.set(sumOfSizePath.get());
                    readWriteLock.writeLock().unlock();
                    return path;
                }
                else {
                    readWriteLock.writeLock().unlock();
                    return null;
                }
            };
            Future<List<Node<T>>> futureCurrentPath = threadPoolExecutor.submit(callable);
            listOfMinimumPaths.add(futureCurrentPath);
        }

        listOfMinimumPaths.forEach(futureCurrPath -> {
            try {
                if(futureCurrPath.get() != null) {
                    listOfMinimumPathsWithoutNull.add(futureCurrPath.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        int sumSizeOfPath;
        for (List<Node<T>> currentPath : listOfMinimumPathsWithoutNull) {
            sumSizeOfPath = currentPath.size();
            if (sumSizeOfPath == minSize.get()) {
                listAllTheMostMinimumSizePaths.add(currentPath);
            }
        }

        this.threadPoolExecutor.shutdown();
        return listAllTheMostMinimumSizePaths;
    }

}