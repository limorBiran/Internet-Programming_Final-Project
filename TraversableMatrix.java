import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class implements the Traversable interface
 */
public class TraversableMatrix implements Traversable<Index> {

    protected final Matrix matrix;
    protected Index startIndex;
    protected Index destinationIndex;

    //Constructor
    public TraversableMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Index getStartIndex() {
        return startIndex;
    }

    public void setDestinationIndex(Index destinationIndex) {
        this.destinationIndex = destinationIndex;
    }

    @Override
    public int getValueN(Index someNode) {
        return matrix.getValue(someNode);
    }

    @Override
    public void setStartIndex(Index index) {
        startIndex = index;
    }

    /**
     * This method returns the string of the object
     * @return String
     */
    @Override
    public String toString() {
        return matrix.toString();
    }

    /**
     * This method returns Node<Index> of the startIndex
     * @return Node<Index>
     * @throws NullPointerException "The start index has not been initialized"
     */
    @Override
    public Node<Index> getSource() throws NullPointerException {
        if (startIndex == null) throw new NullPointerException("The start index has not been initialized");
        return new Node<>(startIndex);
    }

    /**
     * This method returns Node<Index> of the destinationIndex
     * @return Node<Index>
     * @throws NullPointerException "The destination index has not been initialized"
     */
    @Override
    public Node<Index> getDestination() throws NullPointerException {
        if (destinationIndex == null) throw new NullPointerException("The destination index has not been initialized");
        return new Node<>(destinationIndex);
    }

    /**
     * This method returns the reachable nodes -> with diagonal
     * @param node type: Node<Index>
     * @param isOne type: boolean
     * @return Collection<Node<Index>>
     */
    @Override
    public Collection<Node<Index>> getReachableNodes(Node<Index> node, boolean isOne) {
        List<Index> neighbors = matrix.getNeighbors(node.getData());
        return getAllOnesNodesInSpecificIndexList(neighbors, node, isOne);
    }

    /**
     * This method find all the node, in specific index list
     * @param indexList type: List<Index>
     * @param parentNode type: parentNode
     * @param isOne type: boolean
     * @return Collection<Node<Index>>
     */
    private Collection<Node<Index>> getAllOnesNodesInSpecificIndexList(List<Index> indexList, Node<Index> parentNode, boolean isOne){
        List<Node<Index>> reachableIndex = new ArrayList<>();

        for (Index index : indexList) {
            if (!isOne || matrix.getValue(index) == 1) {
                Node<Index> indexNode = new Node<>(index, parentNode);
                reachableIndex.add(indexNode);
            }
        }

        return reachableIndex;
    }
}
