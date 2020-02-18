package gulik.dolichos;

import gulik.urad.*;
import gulik.urad.annotations.GetEntities;
import gulik.urad.impl.Row;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.commons.api.ex.ODataException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private Stream<Method> entityMethods() {
        return Stream.of(endpoint.getDeclaredMethods())
                .filter(each -> each.isAnnotationPresent(GetEntities.class));
    }

    private String entityName(Method method) {
        return Stream.of(method.getAnnotationsByType(GetEntities.class))
                .findAny().get().value();
    }

    @Override
    public CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
        return entityMethods()
                .filter(each -> entityTypeName.equals(new FullQualifiedName("Todo.Namespace", entityName(each))))
                .map(this::entityTypeFromMethod)
                .findFirst().orElseThrow(() -> new ODataException("Unknown entityTypeName: "+entityTypeName));
    }

    private CsdlEntityType entityTypeFromMethod(Method me)  {
        return createEntityTypeFromTable(columnDefinitionsFromMethod(me));
    }

    private Table columnDefinitionsFromMethod(Method me) throws RuntimeException {
        Query q = Query.queryDefinition();
        try {
            return (Table) me.invoke(endpoint.getConstructor().newInstance(), q);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Could not get a Table from " + me.getName() + "(q)");
        }
    }

    private CsdlEntityType createEntityTypeFromTable(Table defs) {
        // Good luck debugging this funky code :-).
        return new CsdlEntityType()
                .setName(defs.getName())
                .setProperties(
                        defs.getColumns().stream()
                                .map(c ->
                                        new CsdlProperty()
                                                .setName(c.getName())
                                                .setType(edmTypeOf(c.getType())))
                                .collect(Collectors.toList()))
                .setKey(
                        defs.getPrimaryKey().stream()
                                .map(each -> new CsdlPropertyRef().setName(each.getName()))
                                .collect(Collectors.toList())
                );
    }

    private FullQualifiedName edmTypeOf(Type uradType) {
        switch(uradType) {
            case Integer:
                return EdmPrimitiveTypeKind.Int32.getFullQualifiedName();
            case String:
                return EdmPrimitiveTypeKind.String.getFullQualifiedName();
            case Float:
                return EdmPrimitiveTypeKind.Decimal.getFullQualifiedName(); // TODO: Decimal???
            default:
                throw new NotImplemented();
        }
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

        schema.setEntityTypes(
                Stream.of(endpoint.getDeclaredMethods())
                .filter(each -> each.isAnnotationPresent(GetEntities.class))
                .map(each -> entityTypeFromMethod(each))
                .collect(Collectors.toList()));

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
