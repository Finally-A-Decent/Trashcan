package info.preva1l.trashcan.util;

import java.util.AbstractMap;
import java.util.Map;

/**
 * <p>
 *     A pair of data, can be used as a key value pair or to ship data around.
 * </p>
 *
 * Created on 25/06/2025
 *
 * @author Preva1l
 */
public class Tuple<L, R> {
    private final L left;
    private final R right;

    public Tuple(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Tuple<L, R> of(final L left, final R right) {
        return new Tuple<>(left, right);
    }

    /**
     * Converts this tuple to a map entry for use with maps.
     *
     * @return a map entry
     */
    public Map.Entry<L, R> asEntry() {
        return new AbstractMap.SimpleEntry<>(left, right);
    }

    public L first() {
        return left;
    }

    public R second() {
        return right;
    }

    public L left() {
        return left;
    }

    public R right() {
        return right;
    }

    // add these for kotlin field accessors

    public L getFirst() {
        return left;
    }

    public R getSecond() {
        return right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
}
