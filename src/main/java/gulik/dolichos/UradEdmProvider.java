package gulik.dolichos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;

import gulik.urad.Table;
import gulik.urad.Type;
import gulik.urad.exceptions.NotImplemented;

public class UradEdmProvider extends CsdlAbstractEdmProvider {
    public List<Table> entitySets;

    public UradEdmProvider(List<Table> entitySets) {
        this.entitySets = entitySets;
    }

    private String namespace() {
        return "namespace";
    }

    private String container() {
        return "container";
    }

    @Override
    public CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
        Table t = entitySets
                .stream()
                .filter(each -> each.getName().equals(entityTypeName.getName()))
                .findFirst()
                .orElseThrow(() -> new ODataException("Unknown entityTypeName: " + entityTypeName));

        return createEntityTypeFrom(t);

    }

    /*
     * private CsdlEntityType entityTypeFromMethod(Method me) {
     * return createEntityTypeFromTable(columnDefinitionsFromMethod(me),
     * entityName(me));
     * }
     * 
     * private Table columnDefinitionsFromMethod(Method me) throws RuntimeException
     * {
     * Query q = Query.queryDefinition();
     * try {
     * return (Table) me.invoke(endpoint.getConstructor().newInstance(), q);
     * } catch (InstantiationException | IllegalAccessException |
     * InvocationTargetException | NoSuchMethodException e) {
     * throw new RuntimeException("Could not get a Table from " + me.getName() +
     * "(q)", e);
     * }
     * }
     */

    private CsdlEntityType createEntityTypeFrom(Table es) {
        // TODO: check for nulls on everything the user might provide.

        List<CsdlProperty> columns = es.getColumns().stream()
                .map(c -> new CsdlProperty()
                        .setName(c.getName())
                        .setType(edmTypeOf(c.getType())))
                .collect(Collectors.toList());

        List<CsdlPropertyRef> primaryKey = es.getColumns().stream()
                .filter(each -> each.isPrimaryKey())
                .map(each -> new CsdlPropertyRef()
                        .setName(each.getName()))
                .collect(Collectors.toList());

        return new CsdlEntityType()
                .setName(es.getName())
                .setProperties(columns)
                .setKey(primaryKey);
    }

    private FullQualifiedName edmTypeOf(Type uradType) {
        switch (uradType) {
            case Integer:
                return EdmPrimitiveTypeKind.Int32.getFullQualifiedName();
            case String:
                return EdmPrimitiveTypeKind.String.getFullQualifiedName();
            case Float:
                return EdmPrimitiveTypeKind.Decimal.getFullQualifiedName(); // TODO: Decimal???
            case Date:
                return EdmPrimitiveTypeKind.Date.getFullQualifiedName();
            case Boolean:
                return EdmPrimitiveTypeKind.Boolean.getFullQualifiedName();
            default:
                throw new NotImplemented();
        }
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {
        for (Table each : entitySets) {
            String name = each.getName();
            if (null==name) {
                throw new Fail(each.getClass().getName()+".getName() returned null.");
            }
            if (name.equals(entitySetName)) {
                CsdlEntitySet entitySet = new CsdlEntitySet();
                entitySet.setName(name);
                entitySet.setTitle("Human readable "+name); // TODO
                // I don't understand what the type is meant to be.
                entitySet.setType(new FullQualifiedName(this.namespace(), name));
                return entitySet;
            }
        }
        throw new Fail("Could not find entity set named " + entitySetName);
    }

    private class Fail extends ODataApplicationException {
        public Fail(String message) {
            super(message, HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
        }
    }

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {
        // This method is invoked when displaying the Service Document at e.g.
        // http://localhost:8080/DemoService/DemoService.svc
        if (entityContainerName == null
                || entityContainerName.equals(new FullQualifiedName(this.namespace(), this.container()))) {
            CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
            entityContainerInfo.setContainerName(new FullQualifiedName(this.namespace(), this.container()));
            return entityContainerInfo;
        }
        return null;
    }

    @Override
    public List<CsdlSchema> getSchemas() throws ODataException {
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace(this.namespace());

        schema.setEntityTypes(
                entitySets
                        .stream()
                        .map(each -> createEntityTypeFrom(each))
                        .collect(Collectors.toList()));

        schema.setEntityContainer(getEntityContainer());

        List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
        schemas.add(schema);

        return schemas;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() throws ODataException {
        List<CsdlEntitySet> ess = new ArrayList<CsdlEntitySet>();

        for (Table each : entitySets) {
            CsdlEntitySet c = getEntitySet(
                    new FullQualifiedName(this.namespace(), this.container()),
                    each.getName());
            ess.add(c);
        }

        return new CsdlEntityContainer()
                .setName(this.container())
                .setEntitySets(ess);
    }
}
