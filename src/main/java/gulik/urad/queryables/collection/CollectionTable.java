package gulik.urad.queryables.collection;

import gulik.urad.*;
import gulik.urad.value.Value;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** I'm a wrapper around a collection.
 * TODO: dictionaries can be list elements and have natural column names.
 * TODO: Can a Map be a source?
 * TODO: The JPAQueryable will be similar to this - reuse code?
 * */
public class CollectionTable implements Table, RowGenerator {
    private final Object[] source;
    private final String name;
    private final Query query;
    private final List<Column> columns;
    private Integer count;

    public CollectionTable(String name, Object[] source, Query query) {
        this.name = name;
        this.source = source;
        this.query = query;
        this.columns = new ArrayList<>();
        deriveColumns();
        if (query.hasOrderBys()) {
            sort();
        }
    }

    /** Look at the source and set the columns using reflection. */
    private void deriveColumns() {
        Object exemplar = Arrays.stream(source).findAny().get();

        int i=0;
        // Is it a getter method?
        for (Method eachMethod : exemplar.getClass().getDeclaredMethods()) {
            if(Modifier.isPublic(eachMethod.getModifiers())
                    && eachMethod.getName().startsWith("get")
                    && eachMethod.getParameterCount()==0) {
                String name = eachMethod.getName().substring(3, eachMethod.getName().length());
                Column c = new gulik.urad.impl.Column()
                        .setName(name)
                        .setTitle(name)
                        .setType(toType(name, eachMethod.getReturnType()))
                        .setPosition(i);
                i++;
                this.columns.add(c);
            }
        }

        // Is it a public field?
        for (Field eachField : exemplar.getClass().getDeclaredFields()) {
            if (Modifier.isPublic(eachField.getModifiers())) {
                String name = eachField.getName();
                Column c = new gulik.urad.impl.Column()
                        .setName(name)
                        .setTitle(name)
                        .setType(toType(name, eachField.getType()))
                        .setPosition(i);
                i++;
                this.columns.add(c);
            }
        }
    }

    private Type toType(String columnName, Class<?> t) {
        if (String.class.equals(t)) {
            return Type.String;
        } else if (Integer.class.equals(t)) {
            return Type.Integer;
        } else if (Float.class.equals(t)) {
            return Type.Float;
        }
        throw new IndexOutOfBoundsException("Method getter get"+columnName+"() returns unmappable type: "+t.getName());
    }

    @Override
    public String getCode() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return name;
    }

    @Override
    public List<Column> getColumns() {
        return columns;
    }

    @Override
    public List<Column> getPrimaryKey() {
        return columns;
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
    public Iterator sourceIterator() {
        return Arrays.stream(source).iterator();
    }

    @Override
    public Row toRow(Object something) {
        Row result = new gulik.urad.impl.Row(columns.size());
        columns.stream().forEach(each -> result.set(each.getPosition(), getValue(something, each.getName())));
        return result;
    }

    public Value getValue(Object something, String columnName) {
        boolean found = false;

        // Is it a getter method?
        for (Method eachMethod : something.getClass().getDeclaredMethods()) {
            if(Modifier.isPublic(eachMethod.getModifiers())
                    && eachMethod.getName().startsWith("get")
                    && eachMethod.getParameterCount()==0) {
                if (("get" + columnName).equals(eachMethod.getName())) {
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

        throw new IndexOutOfBoundsException("Could not find field " + columnName + " in " + Objects.toString(something));
    }

    /** Return a sorted version of c, sorted by the orderBys in the Query q.*/
    private void sort() {
        /** Difficulty: we must sort by:
         * 1. A property which could be a direct member of c, or could be down a path in c.
         * 2. More properties of c after that.
         */
        List<java.util.Comparator> comparators = query.getOrderBys().stream().map(each ->
                asComparator(each))
                .collect(Collectors.toList());
        // for each of the orderBys,
        for (int orderByIndex = 0; orderByIndex<query.getOrderBys().size(); orderByIndex++) {
            // Sort the whole list in fragments, where each fragment is where elements in the previous sort were equal.
            int from=0; // Start of each fragment.
            while (from < source.length) {
                int to; // End of each fragment.
                to = findEndOfFragment(source, from, comparators, orderByIndex);
                Arrays.sort(source, from, to, comparators.get(orderByIndex));
                from = to+1;
            }
        }
    }

    /** Create a comparator for sorting the named "column" in any given object. */
    private java.util.Comparator asComparator(String orderBy) {
        return Comparator.comparing(something -> getValue(something, orderBy));
    }

    /** When sorting a collection, we have several orderBy clauses. The collection is first sorted by the
     * first orderBy, then the second, then the third, etc, except that after the first orderBy, we only
     * sort the elements that were equal to each other in previous sorting iterations.
     *
     * Here we try to find the end of a fragment of elements that are equal for all comparators up to the
     * current one (comparators.get(orderByIndex)).
     */
    private int findEndOfFragment(Object[] c, int from, List<java.util.Comparator> comparators, int orderByIndex) {
        int to = from;
        Object currentElement = c[from];
        while (to<c.length-1) {
            to++;
            Object nextElement = c[to];
            for (int i=0; i<orderByIndex-1; i++) { // For each relevant comparator (we don't care about >orderByIndex yet)
                java.util.Comparator currentComparator = comparators.get(i);
                if (currentComparator.compare(currentElement, nextElement) != 0) {
                    return to - 1;
                }
            }
        }
        return to;
    }

    @Override
    public boolean hasCount() {
        return null!=count;
    }

    @Override
    public Integer getCount() {
        return count;
    }
}
