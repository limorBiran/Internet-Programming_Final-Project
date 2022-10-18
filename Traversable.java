import java.util.Collection;

/**
 * This interface defines the functionality required for a traversable graph
 * @param <T>
 */
public interface Traversable<T> {
    Node<T> getSource();

    Node<T> getDestination();

    Collection<Node<T>> getReachableNodes(Node<T> someNode, boolean isOne);

    int getValueN(T someNode);

    void setStartIndex(Index index);
}
