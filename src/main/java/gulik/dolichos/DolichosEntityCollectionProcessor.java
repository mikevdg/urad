package gulik.dolichos;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
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

import gulik.urad.Table;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/** I accept HTTP requests for collections of entities.  */
public class DolichosEntityCollectionProcessor extends EntityReader  implements EntityCollectionProcessor {
// TODO:  implements EntityCollectionProcessor, EntityProcessor,
//    PrimitiveProcessor, PrimitiveValueProcessor, ComplexProcessor
    private OData odata;
    private ServiceMetadata serviceMetadata;

    public DolichosEntityCollectionProcessor(List<Table> entitySets) {
        super(entitySets);
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
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();


        // 2nd: fetch the data from backend for this requested EntitySetName
        // it has to be delivered as EntitySet object
        EntityCollection entitySet = toEntityCollection(doQuery(toQuery(edmEntitySet, uriInfo)));

        // 3rd: create a serializer based on the requested format (json)
        ODataSerializer serializer = odata.createSerializer(responseFormat);

        // 4th: Now serialize the content: transform from the EntitySet object to InputStream
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();
        ContextURL contextUrl = ContextURL
                .with()
                .entitySet(edmEntitySet)
                .build();

        final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
        EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions
                .with()
                .id(id)
                .expand(uriInfo.getExpandOption()) // TODO - what does this do?
                .contextURL(contextUrl)
                .build();

        SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entitySet, opts);
        InputStream serializedContent = serializerResult.getContent();

        try { // TODO
            serializedContent.transferTo(System.out);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        response.setContent(serializedContent);
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }
}
