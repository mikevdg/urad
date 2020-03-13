package gulik.dolichos;

import gulik.urad.exceptions.NotImplemented;
import gulik.urad.Query;
import gulik.urad.Table;
import gulik.urad.Type;
import gulik.urad.annotations.GetEntities;
import gulik.urad.annotations.ODataEndpoint;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.commons.api.ex.ODataException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UradEdmProvider extends CsdlAbstractEdmProvider {
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

    private String namespace() {
        return Stream.of(endpoint.getDeclaredAnnotationsByType(ODataEndpoint.class))
                .findAny().get().namespace();
    }

    private String container() {
        return Stream.of(endpoint.getDeclaredAnnotationsByType(ODataEndpoint.class))
                .findAny().get().container();
    }

    @Override
    public CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
        return entityMethods()
                .filter(each -> entityTypeName.equals(new FullQualifiedName(this.namespace()

                        , entityName(each))))
                .map(this::entityTypeFromMethod)
                .findFirst().orElseThrow(() -> new ODataException("Unknown entityTypeName: " + entityTypeName));
    }

    private CsdlEntityType entityTypeFromMethod(Method me)  {
        return createEntityTypeFromTable(columnDefinitionsFromMethod(me), entityName(me));
    }

    private Table columnDefinitionsFromMethod(Method me) throws RuntimeException {
        Query q = Query.queryDefinition();
        try {
            return (Table) me.invoke(endpoint.getConstructor().newInstance(), q);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Could not get a Table from " + me.getName() + "(q)", e);
        }
    }

    private CsdlEntityType createEntityTypeFromTable(Table defs, String name) {
        // Good luck debugging this funky code :-).
        return new CsdlEntityType()
                .setName(name)
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
        if (entityContainer.equals(new FullQualifiedName(this.namespace(), this.container()))) {
            for (Method eachMethod : endpoint.getDeclaredMethods()) {
                for (GetEntities eachAnnotation : eachMethod.getAnnotationsByType(GetEntities.class)) {
                    if (entitySetName.equals(eachAnnotation.value())) {
                        CsdlEntitySet entitySet = new CsdlEntitySet();
                        entitySet.setName(eachAnnotation.value());
                        entitySet.setType(new FullQualifiedName(this.namespace(), eachAnnotation.value()));
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
        if (entityContainerName == null || entityContainerName.equals(new FullQualifiedName(this.namespace(), this.container()))) {
            CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
            entityContainerInfo.setContainerName(new FullQualifiedName(this.namespace(), this.container()));
            return entityContainerInfo;
        }
        return null;
    }

    @Override
    public List<CsdlSchema> getSchemas() throws ODataException {
        // create Schema
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace(this.namespace());

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
                    entitySets.add(getEntitySet(new FullQualifiedName(this.namespace(), this.container()), eachAnnotation.value()));
                }
            }
            // create EntityContainer
            CsdlEntityContainer entityContainer = new CsdlEntityContainer();
        entityContainer.setName(this.container());
        entityContainer.setEntitySets(entitySets);

            return entityContainer;
    }
}
