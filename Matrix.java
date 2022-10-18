import java.io.Serializable;
import java.util.*;

/**
 * This class represents a Matrix
 */
public class Matrix implements Serializable {
    private final int[][] primitiveMatrix;

    //Constructor
    public Matrix(int[][] matrix){
        List<int[]> list = new ArrayList<>();
        for (int[] row : matrix) {
            int[] clone = row.clone();
            list.add(clone);
        }
        primitiveMatrix = list.toArray(new int[0][]);
    }

    /**
     * This method returns the string of the object
     * @return String
     */
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] row : primitiveMatrix) {
            stringBuilder.append(Arrays.toString(row));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * this method returns all the neighbors of specific index in the matrix, with diagonal
     * @param index type: Index
     * @return List<Index>
     */
    public List<Index> getNeighbors(final Index index) {
        List<Index> neighborsList = new ArrayList<>();
        int extracted = -1;
        try {
            extracted = primitiveMatrix[index.row+1][index.column];
            neighborsList.add(new Index(index.row + 1, index.column));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row][index.column+1];
            neighborsList.add(new Index(index.row, index.column + 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row-1][index.column];
            neighborsList.add(new Index(index.row - 1, index.column));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row][index.column-1];
            neighborsList.add(new Index(index.row, index.column - 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row+1][index.column+1];
            neighborsList.add(new Index(index.row + 1, index.column + 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row+1][index.column-1];
            neighborsList.add(new Index(index.row + 1, index.column - 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row-1][index.column+1];
            neighborsList.add(new Index(index.row - 1, index.column + 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row-1][index.column-1];
            neighborsList.add(new Index(index.row - 1, index.column - 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return neighborsList;
    }

    /**
     * This method returns the matrix value in specific index
     * @param index type: Index
     * @return int
     */
    public int getValue(final Index index){
        return primitiveMatrix[index.row][index.column];
    }

    public void printMatrix(){
        for (int[] row : primitiveMatrix) {
            String str = Arrays.toString(row);
            System.out.println(str);
        }
    }

    public final int[][] getPrimitiveMatrix() {
        return primitiveMatrix;
    }

    /**
     * This method finds and returns all indexes with a value of 1
     * @return List<Index>
     */
    public List<Index> getAllOnesInMatrix() {
        Index index;
        List<Index> listOfAllOnes = new ArrayList<>();

        for (int row = 0; row < primitiveMatrix.length; row++) {
            for (int col = 0; col < primitiveMatrix[row].length; col++) {
                if (primitiveMatrix[row][col] == 1) {
                    index = new Index(row, col);
                    listOfAllOnes.add(index);
                }
            }
        }
        return listOfAllOnes;
    }
}
