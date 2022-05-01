package gulik.urad.queryables.collection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import gulik.urad.Query;
import gulik.urad.ResultSet;
import gulik.urad.Row;
import gulik.urad.exceptions.NotImplemented;
import gulik.urad.queryColumn.QueryColumn;
import gulik.urad.value.Value;

/**
 * I'm a wrapper around a collection.
 * TODO: dictionaries can be list elements and have natural column names.
 * TODO: Can a Map be a source?
 * TODO: The JPAQueryable will be similar to this - reuse code?
 */
public class CollectionResultSet extends ResultSet implements RowGenerator {
    private final Object[] source;

    public CollectionResultSet(Query query, Object[] source) {
        super(query);
        this.source = source;
        if (query.hasOrderBys()) {
            sort();
        }
    }

    @Override
    public Stream<Row> stream() {
        return Arrays.stream(source).map(each -> toRow(each));
    }

    @Override
    public Row insert(Row row) {
        throw new NotImplemented();
    }

    @Override
    public Row update(Value key, Row row) {
        throw new NotImplemented();
    }

    @Override
    public void delete(Value key) {
        throw new NotImplemented();
    }

    @Override
    public Iterator<Row> iterator() {
        return new CollectionIterator(this);
    }

    @Override
    public Iterator<Object> sourceIterator() {
        return Arrays.stream(source).iterator();
    }

    @Override
    public Row toRow(Object something) {
        int numColumns;
        if (this.query.getSelects().isEmpty()) {
            query.selectAll();
        }
        numColumns = this.query.getSelects().size();
        Row result = new Row(this, numColumns);
        for (QueryColumn each : this.query.getSelects()) {
            result.set(each.getColumnIndex(), getValue(something, each));
        }

        int i=0;
        for (QueryColumn each : this.query.getPrimaryKey()) {
            result.setPrimaryKey(i, getValue(something, each));
        }
        return result;

    }

    public Value getValue(Object something, QueryColumn column) {
        boolean found = false;

        // Is it a getter method?
        for (Method eachMethod : something.getClass().getDeclaredMethods()) {
            if (Modifier.isPublic(eachMethod.getModifiers())
                    && eachMethod.getName().startsWith("get")
                    && eachMethod.getParameterCount() == 0) {
                if (("get" + column.getName()).toLowerCase().equals(eachMethod.getName().toLowerCase())) {
                    // We do toLowerCase() because the column might be named "foo" and the getter is
                    // "getFoo()"
                    try {
                        Value v = Value.of(eachMethod.invoke(something));
                        return v;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        if (!found) {
            // Is it a public field?
            for (Field eachField : something.getClass().getDeclaredFields()) {
                if (Modifier.isPublic(eachField.getModifiers())) {
                    try {
                        Value v = Value.of(eachField.get(something));
                        return v;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        throw new IndexOutOfBoundsException(
                "Could not find field " + column + " in " + Objects.toString(something));
    }

    /** Return a sorted version of c, sorted by the orderBys in the Query q. */
    private void sort() {
        /**
         * Difficulty: we must sort by:
         * 1. A property which could be a direct member of c, or could be down a path in
         * c.
         * 2. More properties of c after that.
         */
        List<java.util.Comparator> comparators = query.getOrderBys().stream().map(each -> asComparator(each))
                .collect(Collectors.toList());
        // for each of the orderBys,
        for (int orderByIndex = 0; orderByIndex < query.getOrderBys().size(); orderByIndex++) {
            // Sort the whole list in fragments, where each fragment is where elements in
            // the previous sort were equal.
            int from = 0; // Start of each fragment.
            while (from < source.length) {
                int to; // End of each fragment.
                to = findEndOfFragment(source, from, comparators, orderByIndex);
                // Arrays.sort() sorts from 'from' inclusive, to 'to' exclusive. to must be one
                // more than the end.
                Arrays.sort(source, from, to + 1, comparators.get(orderByIndex));
                from = to + 1;
            }
        }
    }

    /** Create a comparator for sorting the named "column" in any given object. */
    private java.util.Comparator<Object> asComparator(QueryColumn orderBy) {
        return Comparator.comparing(something -> getValue(something, orderBy));
    }

    /**
     * When sorting a collection, we have several orderBy clauses. The collection is
     * first sorted by the
     * first orderBy, then the second, then the third, etc, except that after the
     * first orderBy, we only
     * sort the elements that were equal to each other in previous sorting
     * iterations.
     *
     * Here we try to find the end of a fragment of elements that are equal for all
     * comparators up to the
     * current one (comparators.get(orderByIndex)).
     */
    private int findEndOfFragment(Object[] c, int from, List<java.util.Comparator> comparators, int orderByIndex) {
        int to = from;
        Object currentElement = c[from];
        while (to < c.length - 1) {
            to++;
            Object nextElement = c[to];
            for (int i = 0; i < orderByIndex - 1; i++) { // For each relevant comparator (we don't care about
                                                         // >orderByIndex yet)
                java.util.Comparator currentComparator = comparators.get(i);
                if (currentComparator.compare(currentElement, nextElement) != 0) {
                    return to - 1;
                }
            }
        }
        return to;
    }
}
