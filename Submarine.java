import java.util.HashSet;
import java.util.List;

/**
 * This class represent submarine game according to the rules of the game
 */
public class Submarine {

    /**
     * this method count the normal submarine according to the rules of the game
     * @param setOfIntendedBeSubmarine type: List<HashSet<Index>>
     * @param primitiveMatrix type: int[][]
     * @return int -> amount of submarine
     */
    public int submarineGame(List<HashSet<Index>> setOfIntendedBeSubmarine, int[][] primitiveMatrix) {
        int amountOfSubmarine = setOfIntendedBeSubmarine.size(), minimumRow, minimumCol, maximumRow, maximumCol;
        for (HashSet<Index> intendedBeSubmarine : setOfIntendedBeSubmarine) {
            minimumRow = Integer.MAX_VALUE;
            minimumCol = Integer.MAX_VALUE;
            maximumRow = Integer.MIN_VALUE;
            maximumCol = Integer.MIN_VALUE;
            for (Index index : intendedBeSubmarine) {
                if (intendedBeSubmarine.size() == 1) {
                    amountOfSubmarine--;
                    continue;
                }
                minimumRow = Math.min(index.row, minimumRow);
                minimumCol = Math.min(index.column, minimumCol);
                maximumRow = Math.max(index.row, maximumRow);
                maximumCol = Math.max(index.column, maximumCol);
            }
            for (int row = minimumRow; row <= maximumRow; row++) {
                for (int col = minimumCol; col <= maximumCol; col++) {
                    if (primitiveMatrix[row][col] == 0) {
                        return 0;
                    }
                }
            }
        }
        return amountOfSubmarine;
    }
}