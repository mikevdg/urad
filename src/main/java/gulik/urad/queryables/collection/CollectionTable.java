package gulik.urad.queryables.collection;

import gulik.urad.*;
import gulik.urad.value.Value;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

/** I'm a wrapper around a collection.
 * TODO: dictionaries can be list elements and have natural column names.
 * TODO: Can a Map be a source?
 * TODO: The JPAQueryable will be similar to this - reuse code?
 * */
public class CollectionTable implements Table, RowGenerator {
    private final Collection source;
    private final String name;
    private final Query query;
    private final List<Column> columns;

    public CollectionTable(String name, Collection source, Query query) {
        this.name = name;
        this.source = source;
        this.query = query;
        this.columns = new ArrayList<>();
        deriveColumns();
    }

    /** Look at the source and set the columns using reflection. */
    private void deriveColumns() {
        Object exemplar = source.stream().findAny().get();

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
        return source.stream().map(each -> toRow(each));
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
    public Collection<?> getSource() {
        return source;
    }

    @Override
    public Row toRow(Object something) {
        Row result = new gulik.urad.impl.Row(columns.size());
        for (Column each : columns) {
            boolean found = false;

            // Is it a getter method?
            for (Method eachMethod : something.getClass().getDeclaredMethods()) {
                if(Modifier.isPublic(eachMethod.getModifiers())
                        && eachMethod.getName().startsWith("get")
                        && eachMethod.getParameterCount()==0) {
                    if (("get" + each.getName()).equals(eachMethod.getName())) {
                        try {
                            Value v = Value.of(eachMethod.invoke(something));
                            result.set(each.getPosition(), v);
                            found = true;
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
                            result.set(each.getPosition(), v);
                            found = true;
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            if (!found) {
                throw new IndexOutOfBoundsException("Could not find field " + each.getName() + " in " + Objects.toString(something));
            }
        }
        return result;
    }
}
