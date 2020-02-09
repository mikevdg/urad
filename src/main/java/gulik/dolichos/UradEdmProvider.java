package gulik.dolichos;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.commons.api.ex.ODataException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UradEdmProvider extends CsdlAbstractEdmProvider {
    // Service Namespace
    public static final String NAMESPACE = "Todo.Namespace";

    // EDM Container
    public static final String CONTAINER_NAME = "Container";
    public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

    // Entity Types Names
    public static final String ET_ENTITY_NAME = "TodoEntityName";
    public static final FullQualifiedName ET_ENTITY_FQN = new FullQualifiedName(NAMESPACE, ET_ENTITY_NAME);


    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {
        // TODO: Copypasta example code.

        // this method is called for one of the EntityTypes that are configured in the Schema
        if(entityTypeName.equals(ET_ENTITY_FQN)){

            //create EntityType properties
            CsdlProperty id = new CsdlProperty().setName("ID").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
            CsdlProperty name = new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
            CsdlProperty  description = new CsdlProperty().setName("Description").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

            // create CsdlPropertyRef for Key element
            CsdlPropertyRef propertyRef = new CsdlPropertyRef();
            propertyRef.setName("ID");

            // configure EntityType
            CsdlEntityType entityType = new CsdlEntityType();
            entityType.setName(ET_ENTITY_NAME);
            entityType.setProperties(Arrays.asList(id, name , description));
            entityType.setKey(Collections.singletonList(propertyRef));

            return entityType;
        }

        return null;
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {

        if (entityContainer.equals(CONTAINER)) {
            if (entitySetName.equals(ET_ENTITY_NAME)) {
                CsdlEntitySet entitySet = new CsdlEntitySet();
                entitySet.setName(ET_ENTITY_NAME);
                entitySet.setType(ET_ENTITY_FQN);

                return entitySet;
            }
        }

        return null;
    }

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {

        // This method is invoked when displaying the Service Document at e.g. http://localhost:8080/DemoService/DemoService.svc
        if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
            CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
            entityContainerInfo.setContainerName(CONTAINER);
            return entityContainerInfo;
        }

        return null;
    }

    @Override
    public List<CsdlSchema> getSchemas() throws ODataException {

        // create Schema
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace(NAMESPACE);

        // add EntityTypes
        List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
        entityTypes.add(getEntityType(ET_ENTITY_FQN));
        schema.setEntityTypes(entityTypes);

        // add EntityContainer
        schema.setEntityContainer(getEntityContainer());

        // finally
        List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
        schemas.add(schema);

        return schemas;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() throws ODataException {

            // create EntitySets
            List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
            entitySets.add(getEntitySet(CONTAINER, ET_ENTITY_NAME));

            // create EntityContainer
            CsdlEntityContainer entityContainer = new CsdlEntityContainer();
            entityContainer.setName(CONTAINER_NAME);
            entityContainer.setEntitySets(entitySets);

            return entityContainer;
    }
}
