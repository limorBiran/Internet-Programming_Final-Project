import java.io.Serializable;
import java.util.Objects;

/**
 * This class wraps a concrete object
 * @param <T>
 */
public class Node<T> implements Serializable {
    private T data;
    private Node<T> parent;

    //Constructor
    public Node(){

    }

    //Constructor
    public Node(T data, Node<T> parent){
        this.data = data;
        this.parent = parent;
    }

    //Constructor
    public Node(T data){
        this(data,null);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node<T> getParent() {
        return parent;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }

    /**
     * this method compare between index and object, and return if they are equals
     * @param o type: Object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node<?> state1 = (Node<?>) o;
        return Objects.equals(data, state1.data);
    }

    /**
     * This method returns the hashcode of the current object
     * @return int
     */
    @Override
    public int hashCode() {
        return data != null ? data.hashCode():0;
    }

    /**
     * This method returns the string of the object
     * @return String
     */
    @Override
    public String toString() {
        return data.toString();
    }
}
