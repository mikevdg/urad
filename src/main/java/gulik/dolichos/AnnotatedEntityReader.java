package gulik.dolichos;

import gulik.urad.*;
import gulik.urad.annotations.GetEntities;
import gulik.urad.value.Value;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.queryoption.*;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;

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
            throw new ODataApplicationException("Failed to invoke constructor "+endpoint.getName()+"(query)",
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
        Query result = new Query().from(edmEntitySet.getName());

        // TODO
        CountOption countOption = uriInfo.getCountOption();
        boolean isCount = null != countOption && countOption.getValue();
        if (isCount) {
            result.selectCount();
        } else {
            SelectOption selectOption = uriInfo.getSelectOption();
            if (null != selectOption) {
                for (SelectItem eachSelectItem : selectOption.getSelectItems()) {
                    // This API is confusing. TODO
                    //eachSelectItem.getResourcePath().?????
                    //result.select(...)
                }
            }
        }

        processOrderBy(uriInfo, result);

        SkipOption skipOption = uriInfo.getSkipOption();
        if (null!=skipOption) {
            result.skip(skipOption.getValue());
        }

        TopOption topOption = uriInfo.getTopOption();
        if (null!=topOption) {
            result.top(topOption.getValue());
        }

        return result;
    }

    private void processOrderBy(UriInfo uriInfo, Query result) {
        OrderByOption orderByOption = uriInfo.getOrderByOption();
        if (orderByOption != null) {
            List<OrderByItem> orderItemList = orderByOption.getOrders();
            for(OrderByItem each : orderItemList) {
                Expression expression = each.getExpression();
                if (expression instanceof Member) {
                    UriInfoResource resourcePath = ((Member) expression).getResourcePath();
                    UriResource uriResource = resourcePath.getUriResourceParts().get(0);
                    if (uriResource instanceof UriResourcePrimitiveProperty) {
                        EdmProperty edmProperty = ((UriResourcePrimitiveProperty) uriResource).getProperty();
                        final String sortPropertyName = edmProperty.getName();
                        result.orderBy(sortPropertyName);
                    }
                }
            }
        }
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
