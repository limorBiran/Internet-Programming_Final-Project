import java.io.Serializable;
import java.util.Objects;

/**
 * This class represents location with row and column
 */
public class Index implements Serializable {
    int row, column;

    //Constructor
    public Index(final int row, final int column) {
        this.row=row;
        this.column=column;
    }

    /**
     * this method compare between index and object, and return if they are equals
     * @param o type: Object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index index = (Index) o;
        return row == index.row &&
                column == index.column;
    }

    /**
     * This method returns the hashcode of the current object
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    /**
     * This method returns the string of the object
     * @return String
     */
    @Override
    public String toString() {
        return "("+row +
                "," + column +
                ')';
    }
}
