package gulik.dolichos;

import gulik.urad.*;
import gulik.urad.annotations.GetEntities;
import gulik.urad.value.Value;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class AnnotatedEntityReader {
    protected final Class<?> endpoint; // TODO: this should be an instance.

    public AnnotatedEntityReader(Class<?> endpoint) {
        this.endpoint = endpoint;
    }

    private Method getHandlerFor(String entityName) throws ODataApplicationException {
        return Stream.of(endpoint.getDeclaredMethods())
                .filter(eachMethod ->
                        Stream.of(eachMethod.getAnnotationsByType(GetEntities.class) )
                                .anyMatch(eachAnnotation -> entityName.equals(eachAnnotation.value())))
                .findAny().orElseThrow(() ->
                        new ODataApplicationException("Could not find method with the annotation @GetEntity("+entityName+")",
                                HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(),
                                Locale.ENGLISH));
    }

    protected Table doQuery(Query query) throws ODataApplicationException {
        Table table;
        try {
            table = (Table) getHandlerFor(query.getFrom()).invoke(endpoint.getConstructor().newInstance(), query);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            throw new ODataApplicationException("Failed to invoke hander.",
                    HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(),
                    Locale.ENGLISH,
                    e);
        }
        return table;
    }

    protected EntityCollection toEntityCollection(Table table) {
        EntityCollection result = new EntityCollection();
        List<Entity> entities = result.getEntities();
        for (Row each : table) {
            entities.add(toEntity(each, table));
        }
        return result;
    }

    protected Query toQuery(EdmEntitySet edmEntitySet, UriInfo uriInfo) {
        // TODO
        CountOption countOption = uriInfo.getCountOption();
        boolean isCount = null != countOption && countOption.getValue();
        if (isCount) {
            throw new NotImplemented();
        }

        SkipOption skipOption = uriInfo.getSkipOption();
        TopOption topOption = uriInfo.getTopOption();
        SelectOption selectOption = uriInfo.getSelectOption();
        return new Query()
                .from(edmEntitySet.getName());
    }

    /** Convert the given row to an Entity. The table is required for column definitions. */
    protected Entity toEntity(Row row, Table table) {
        /* This is a bit wasteful - we're creating objects to throw them away. A future version could make this more directly
        from the network.
         */
        Entity result = new Entity();
        for (Column eachColumn : table.getColumns()) {
            Value v = row.get(eachColumn.getPosition());
            Property p = new Property(null, eachColumn.getName(), ValueType.PRIMITIVE, v.value());
            result.addProperty(p);
        }
        // TODO: we assume there is only one primary key.
        Object primaryKey = row.get(table.getPrimaryKey().get(0).getPosition());
        result.setId(createId(table.getName(), primaryKey));
        return result;
    }

    private URI createId(String entitySetName, Object id) {
        try {
            return new URI(entitySetName + "(" + String.valueOf(id) + ")");
        } catch (URISyntaxException e) {
            throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
        }
    }
}