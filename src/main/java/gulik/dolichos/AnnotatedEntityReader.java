package gulik.dolichos;

import gulik.urad.*;
import gulik.urad.annotations.GetEntities;
import gulik.urad.exceptions.NotImplemented;
import gulik.urad.value.Value;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.*;
import org.apache.olingo.commons.api.edm.*;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.*;
import org.apache.olingo.server.api.uri.queryoption.*;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
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

        if (table.hasCount()) {
            result.setCount(table.getCount());
            // If there is a count, we still return rows from the query.
        }

        List<Entity> entities = result.getEntities();
        for (Row each : table) {
            entities.add(toEntity(each, table));
        }

        /* When you have expanded entities:
        Link link = new Link();
        link.setTitle(navPropName);
        link.setInlineEntity(expandEntity);
        entity.getNavigationLinks().add(link);
         */

        return result;
    }

    protected Query toQuery(EdmEntitySet edmEntitySet, UriInfo uriInfo) throws ODataApplicationException {
        Query result = new Query().from(edmEntitySet.getName());

        // $count=true
        processCount(uriInfo, result);

        // $select
        processSelect(uriInfo, result);

        // $expand
        processExpand(edmEntitySet, uriInfo);

        // $filter
        processFilter(uriInfo);

        // $orderBy
        processOrderBy(uriInfo, result);

        // $skip
        processSkip(uriInfo, result);

        // $top
        processTop(uriInfo, result);

        return result;
    }

    private void processSelect(UriInfo uriInfo, Query result) throws ODataApplicationException {
        SelectOption selectOption = uriInfo.getSelectOption();
        if (null != selectOption) {
            for (SelectItem each : selectOption.getSelectItems()) {
                List<UriResource> r = each.getResourcePath().getUriResourceParts();
                if (r.size() > 1) {
                    throw new ODataApplicationException("Your $select is too fancy for my pathetic code.",
                            HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(),
                            Locale.ENGLISH);
                }
                result.select(r.get(0).getSegmentValue());
            }
        }
    }

    private void processCount(UriInfo uriInfo, Query result) {
        CountOption countOption = uriInfo.getCountOption();
        boolean isCount = null != countOption && countOption.getValue();
        if (isCount) {
            result.selectCount();
        }
    }

    private void processTop(UriInfo uriInfo, Query result) {
        TopOption topOption = uriInfo.getTopOption();
        if (null!=topOption) {
            result.top(topOption.getValue());
        }
    }

    private void processSkip(UriInfo uriInfo, Query result) {
        SkipOption skipOption = uriInfo.getSkipOption();
        if (null!=skipOption) {
            result.skip(skipOption.getValue());
        }
    }

    private void processFilter(UriInfo uriInfo) throws ODataApplicationException {
        FilterOption filterOption = uriInfo.getFilterOption();
        if (null!=filterOption) {
            // This isn't actually that bad. I'm going to steal this pattern.
            Expression filterExpression = filterOption.getExpression();
            FilterExpressionVisitor v = new FilterExpressionVisitor();
            try {
                filterExpression.accept(v);
            } catch (ExpressionVisitException e) {
                throw new ODataApplicationException("Failed to evaluate expression.",
                        HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(),
                        Locale.ENGLISH,
                        e);
            }
        }
    }

    private void processExpand(EdmEntitySet edmEntitySet, UriInfo uriInfo) {
        if (true) return; // Copypasta code below doesn't work yet.

        EdmNavigationProperty edmNavigationProperty=null;
        ExpandOption expandOption = uriInfo.getExpandOption();
        if (null!=expandOption) {
            ExpandItem expandItem = expandOption.getExpandItems().get(0);
            if (expandItem.isStar()) {
                List<EdmNavigationPropertyBinding> bindings = edmEntitySet.getNavigationPropertyBindings();
                // we know that there are navigation bindings
                // however normally in this case a check if navigation bindings exists is done
                if (!bindings.isEmpty()) {
                    // can in our case only be 'Category' or 'Products', so we can take the first
                    EdmNavigationPropertyBinding binding = bindings.get(0);
                    EdmElement property = edmEntitySet.getEntityType().getProperty(binding.getPath());
                    // we don't need to handle error cases, as it is done in the Olingo library
                    if (property instanceof EdmNavigationProperty) {
                        edmNavigationProperty = (EdmNavigationProperty) property;
                    }
                }
            } else {
                // can be 'Category' or 'Products', no path supported
                UriResource uriResource = expandItem.getResourcePath().getUriResourceParts().get(0);
                // we don't need to handle error cases, as it is done in the Olingo library
                if (uriResource instanceof UriResourceNavigation) {
                    edmNavigationProperty = ((UriResourceNavigation) uriResource).getProperty();
                }
            }
            if(edmNavigationProperty != null) {
                EdmEntityType expandEdmEntityType = edmNavigationProperty.getType();
                String navPropName = edmNavigationProperty.getName();
                // build the inline data
                Link link = new Link();
                link.setTitle(navPropName);
                link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
                link.setRel(Constants.NS_ASSOCIATION_LINK_REL + navPropName);

                if(edmNavigationProperty.isCollection()){ // in case of Categories(1)/$expand=Products
                    // fetch the data for the $expand (to-many navigation) from backend
                    // here we get the data for the expand
                    EntityCollection expandEntityCollection = null; // storage.getRelatedEntityCollection(entity, expandEdmEntityType);
                    link.setInlineEntitySet(expandEntityCollection);
                    link.setHref(expandEntityCollection.getId().toASCIIString());
                } else {  // in case of Products(1)?$expand=Category
                    // fetch the data for the $expand (to-one navigation) from backend
                    // here we get the data for the expand
                    Entity expandEntity = null; // storage.getRelatedEntity(entity, expandEdmEntityType);
                    link.setInlineEntity(expandEntity);
                    link.setHref(expandEntity.getId().toASCIIString());
                }

                // set the link - containing the expanded data - to the current entity
                Entity entity = null; // TODO.
                entity.getNavigationLinks().add(link);
            }
        }

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
