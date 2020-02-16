package gulik.dolichos;

import gulik.urad.annotations.GetEntities;
import gulik.urad.impl.Row;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.commons.api.ex.ODataException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class UradEdmProvider extends CsdlAbstractEdmProvider {
    /*

(TODO)
$metadata currently looks like:

<edmx:Edmx Version="4.0" xmlns:edmx="http://docs.oasis-open.org/odata/ns/edmx">
    <edmx:DataServices>
        <Schema xmlns="http://docs.oasis-open.org/odata/ns/edm" Namespace="Todo.Namespace">
            <EntityType Name="TodoEntityName">
                <Key>
                    <PropertyRef Name="ID"/>
                </Key>
                <Property Name="ID" Type="Edm.Int32"/>
                <Property Name="Name" Type="Edm.String"/>
                <Property Name="Description" Type="Edm.String"/>
            </EntityType>
            <EntityContainer Name="TodoContainer">
                <EntitySet Name="TodoEntityName" EntityType="Todo.Namespace.TodoEntityName"/>
            </EntityContainer>
        </Schema>
    </edmx:DataServices>
</edmx:Edmx>

     */

    public Class<?> endpoint;

    public UradEdmProvider(Class<?> endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {
        // TODO: Copypasta example code.

        // this method is called for one of the EntityTypes that are configured in the Schema
        if(entityTypeName.equals(new FullQualifiedName("Todo.Namespace", "TodoEntityName"))){

            //create EntityType properties
            CsdlProperty id = new CsdlProperty().setName("ID").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
            CsdlProperty name = new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
            CsdlProperty  description = new CsdlProperty().setName("Description").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

            // create CsdlPropertyRef for Key element
            CsdlPropertyRef propertyRef = new CsdlPropertyRef();
            propertyRef.setName("ID");

            // configure EntityType
            CsdlEntityType entityType = new CsdlEntityType();
            entityType.setName("TodoEntityName");
            entityType.setProperties(Arrays.asList(id, name , description));
            entityType.setKey(Collections.singletonList(propertyRef));

            return entityType;
        }

        return null;
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {
        if (entityContainer.equals(new FullQualifiedName("Todo.Namespace", "TodoContainer"))) {
            for (Method eachMethod : endpoint.getDeclaredMethods()) {
                for (GetEntities eachAnnotation : eachMethod.getAnnotationsByType(GetEntities.class)) {
                    if (entitySetName.equals(eachAnnotation.value())) {
                        CsdlEntitySet entitySet = new CsdlEntitySet();
                        entitySet.setName(eachAnnotation.value());
                        entitySet.setType(new FullQualifiedName("Todo.Namespace", eachAnnotation.value()));
                        return entitySet;
                    }
                }
            }
        }
        throw new ODataException("Unknown entitySetName: "+entitySetName);
    }

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {

        // This method is invoked when displaying the Service Document at e.g. http://localhost:8080/DemoService/DemoService.svc
        if (entityContainerName == null || entityContainerName.equals(new FullQualifiedName("Todo.Namespace", "TodoContainer"))) {
            CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
            entityContainerInfo.setContainerName(new FullQualifiedName("Todo.Namespace", "TodoContainer"));
            return entityContainerInfo;
        }

        return null;
    }

    @Override
    public List<CsdlSchema> getSchemas() throws ODataException {

        // create Schema
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace("Todo.Namespace");

        // add EntityTypes
        List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
        entityTypes.add(getEntityType(new FullQualifiedName("Todo.Namespace", "TodoEntityName")));
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
            for (Method eachMethod : endpoint.getDeclaredMethods()) {
                for (GetEntities eachAnnotation : eachMethod.getAnnotationsByType(GetEntities.class)) {
                    entitySets.add(getEntitySet(new FullQualifiedName("Todo.Namespace", "TodoContainer"), eachAnnotation.value()));
                }
            }
            // create EntityContainer
            CsdlEntityContainer entityContainer = new CsdlEntityContainer();
            entityContainer.setName("TodoContainer");
            entityContainer.setEntitySets(entitySets);

            return entityContainer;
    }

}
