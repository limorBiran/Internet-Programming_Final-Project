import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * This class represents a Thread-safe DFS algorithm
 * @param <T>
 */
public class ThreadLocalDfsVisit <T>{

    private final ThreadLocal<Stack<Node<T>>> threadLocalStack =
            ThreadLocal.withInitial(Stack::new);
    private final ThreadLocal<Set<Node<T>>> threadLocalSet =
            ThreadLocal.withInitial(HashSet::new);
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
            10, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * This method implements a DFS search
     * @param traversable type: Traversable<T>
     * @param listOfIndex type: listOfIndex
     * @return type: HashSet<HashSet<T>>
     */
    public HashSet<HashSet<T>> parallelDFS(Traversable<T> traversable, List<Index> listOfIndex) {
        HashSet<Future<HashSet<T>>> futureSetOfScc = new HashSet<>();
        HashSet<HashSet<T>> setOfScc = new HashSet<>();
        int size = listOfIndex.size();
        for (int i = 0; i < size; i++) {
            int finalI = i;
            Callable<HashSet<T>> callable = () -> {
                readWriteLock.writeLock().lock();
                traversable.setStartIndex(listOfIndex.get(finalI));
                HashSet<T> currentSCC = this.traverseFromSource(traversable);
                readWriteLock.writeLock().unlock();
                return currentSCC;
            };
            Future<HashSet<T>> futureCurrentSCC = threadPoolExecutor.submit(callable);
            futureSetOfScc.add(futureCurrentSCC);
        }
        futureSetOfScc.forEach(currScc -> {
            try {
                setOfScc.add(currScc.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        this.threadPoolExecutor.shutdown();
        return setOfScc;
    }

    /**
     * This method implements a push method to threadLocalStack
     * @param node type: Node<T>
     */
    private void threadLocalPush(Node<T> node) {
        this.threadLocalStack.get().push(node);
    }

    /**
     * This method implements a DFS search from the soruce node,
     * and returns a strong connected component.
     * @param traversable type: Traversable<T>
     * @return HashSet<T>
     */
    public HashSet<T> traverseFromSource(Traversable<T> traversable) {
        threadLocalPush(traversable.getSource());
        while (!threadLocalStack.get().isEmpty()) {
            Node<T> currentLastNode = threadLocalStack.get().pop();
            threadLocalSet.get().add(currentLastNode);
            Collection<Node<T>> reachableNodes = traversable.getReachableNodes(currentLastNode, true);
            reachableNodes.forEach(node -> {
                if (!threadLocalSet.get().contains(node) && !threadLocalStack.get().contains(node)) {
                    threadLocalPush(node);
                }
            });
        }
        HashSet<T> connectedComponent = new LinkedHashSet<>();
        threadLocalSet.get().forEach(node -> connectedComponent.add(node.getData()));

        threadLocalSet.get().clear();
        threadLocalStack.get().clear();
        return connectedComponent;
    }

    /**
     * This method finds all scc in this matrix in a parallel way.
     * @param source type: int[][]
     * @return List<HashSet<Index>>
     */
    public List<HashSet<Index>> getAllSccParallel(int[][] source) {
        List<Index> listIndexesOfOnes;
        HashSet<HashSet<Index>> setOfAllScc;
        Matrix matrix = new Matrix(source);
        matrix.printMatrix();

        TraversableMatrix traversableMatrix = new TraversableMatrix(matrix);
        listIndexesOfOnes = matrix.getAllOnesInMatrix();
        traversableMatrix.setStartIndex(traversableMatrix.getStartIndex());
        ThreadLocalDfsVisit<Index> threadLocalDfsVisit = new ThreadLocalDfsVisit<>();
        setOfAllScc = threadLocalDfsVisit.parallelDFS(traversableMatrix, listIndexesOfOnes);
        return setOfAllScc.stream().sorted(Comparator.comparingInt(HashSet::size))
                .collect(Collectors.toList());
    }
}
