package gulik.dolichos;

import gulik.urad.Column;
import gulik.urad.Query;
import gulik.urad.Row;
import gulik.urad.Table;
import gulik.urad.annotations.GetEntities;
import gulik.urad.value.Value;
import org.apache.olingo.commons.api.data.*;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

public class DolichosEntityCollectionProcessor implements EntityCollectionProcessor {
    private final Class<?> endpoint; // The class that contains your OData handlers.
    private OData odata;
    private ServiceMetadata serviceMetadata;

    public DolichosEntityCollectionProcessor(Class<?> endpoint) {
        this.endpoint = endpoint;
    }

    public void init(OData odata, ServiceMetadata serviceMetadata) {
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
    }

    public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
            throws ODataApplicationException, SerializerException {
        // I like how the OData tutorial says to use this code, but there's very
        // little of your own business logic in it.

        // 1st we have retrieve the requested EntitySet from the uriInfo object (representation of the parsed service URI)
        List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0); // in our example, the first segment is the EntitySet
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

        // 2nd: fetch the data from backend for this requested EntitySetName
        // it has to be delivered as EntitySet object
        EntityCollection entitySet = getData(edmEntitySet);

        // 3rd: create a serializer based on the requested format (json)
        ODataSerializer serializer = odata.createSerializer(responseFormat);

        // 4th: Now serialize the content: transform from the EntitySet object to InputStream
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();
        ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

        final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
        EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().id(id).contextURL(contextUrl).build();
        SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entitySet, opts);
        InputStream serializedContent = serializerResult.getContent();

        // Finally: configure the response object: set the body, headers and status code
        response.setContent(serializedContent);
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }

    private EntityCollection getData(EdmEntitySet edmEntitySet) throws ODataApplicationException {
        Method handler = null;
        for (Method eachMethod : endpoint.getDeclaredMethods()) {
            for (GetEntities eachAnnotation : eachMethod.getAnnotationsByType(GetEntities.class)) {
                if (edmEntitySet.getName().equals(eachAnnotation.value())) {
                    handler = eachMethod;
                    break;
                }
            }
        }
        if (null == handler) {
            throw new ODataApplicationException("Could not find a handler method for " + edmEntitySet.getName(),
                    HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(),
                    Locale.ENGLISH);
        }

        Query query = toQuery(edmEntitySet);
        Table table;
        try {
            table = (Table) handler.invoke(endpoint.getConstructor().newInstance(), query);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            throw new ODataApplicationException("Failed to invoke hander.",
                    HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(),
                    Locale.ENGLISH,
                    e);
        }

        EntityCollection result = new EntityCollection();
        List<Entity> entities = result.getEntities();
        for (Row each : table) {
            entities.add(toEntity(table, each));
        }
        return result;
    }

    private Query toQuery(EdmEntitySet edmEntitySet) {
        // TODO
        return new Query()
                .from(edmEntitySet.getName());
    }

    private Entity toEntity(Table table, Row row) {
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
