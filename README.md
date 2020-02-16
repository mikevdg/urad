# What is this?

I am a library that makes it easier to create OData services in Java. I am built on Apache Olingo.

# Project status

It doesn't compile yet.

# Project structure

This project is work in progress. It contains several future projects in the same build package. These will be split up
when they work well enough to be separately maintained:

* dolichos - annotations and framework stuff for making an OData service. 
* urad - a query framework.
* demo - a quick web app hack to test my stuff. This will be discarded.
* (TODO) - Spring integration?? In particular, Spring security?
* (TODO) - OAuth integration??

To run me, point your browser at http://localhost:8080/ after you do:

```shell script
$ mvn jetty:run
```


# Dolichos

This library contains annotations and a Servlet implementation for creating OData services. 

Services can be defined by::

``` java
    @ODataEndpoint
    public class PersonController { // I'm not Spring. Don't get confused.
        
        // HTTP GET
        @GetEntities("Person")
        public Table getPersons(Query q) {
            // Insert pre-query business logic here.
            return new JPAQueryable(Person.class)
                .query(q);
            // Insert post-query business logic here.
        }

        // HTTP POST
        @CreateEntities("Person") // TODO - One or multiple entries in the table?
        public Table createPerson(Table person) {
            return new JPAQueryable(Person.class)
                .create(person);
        }

        // HTTP PUT
        @UpdateEntities("Person")
        public Table updatePerson(Table person) {
            return new JPAQueryable(Person.class)
                .update(person);
        }

        // HTTP DELETE
        @DeleteEntities("Person") 
        public void deletePerson(Table person) {
            return new JPAQueryable(Person.class)
                .delete(person);
        }
    }
```

This allows the implementer to:

* Include business logic before and after a query.
* Inspect the query before it runs, e.g. to disallow dangerously heavy queries. 
* Modify the query before it runs, e.g. to trim excessive column navigation.
* To add stuff to the result, e.g. from two or more queries.
* To create his own Queryable and Table classes for very custom behaviour.


# Urad

Urad is a Java library for managing queries on tabular data. I allow for SQL-esque queries to be performed on
standard Java lists, relational databases, via JPA bindings, or for fancier things such as OData or other
REST services.

I have the following interfaces, which should be reasonably straight-forward:

A Table is an iterable entity that has column metadata. When a client requests $metadata, this will get an empty 
query to fetch the metadata. It returns Row objects. Both Table and Row are interfaces that the user can implement
should the provided implementations not be satisfactory.

A Query is a manipulatable object containing the query parameters from the user. It basically contains a logical   
SQL SELECT statement.

The Queryable objects convert Queries to Tables. Tables need metadata that describe their columns and structure. 
Urad will provide at least a JPAQueryable that uses the JPA annotations to build up the metadata, and maybe
an SQLQueryable that works using JDBC's metadata mechanisms (?). Queryables can also be made by the user.

To use me::


```java
    // Say that we have a list of people. Person is probably annotated with JPA.
    List<Person> people = new ArrayList<>();
    people.add(new Person("Alice"), Gender.female, 33);
    people.add(new Person("Bob"), Gender.male, 18);

    // First make a query. This is a bit like jOOQ, but geared towards OData.
    Query q = new Query()
        .select("name")
        .select("age")
        .from("Person")
        .where(equal("name", "Bob" ))
        .orderBy("age") 
        .top(10) // windowing: get results 10 through 20.
        .skip(10);
        
    // Then we apply it to a Queryable. CollectionQueryable works with any Java collection.
    Queryable queryMe = new CollectionQueryable(people);
    Table result = queryMe.query(q);
    
    // Print out the columns
    for (Column eachColumn : result.columns()) {
        System.out.print(String.format("|.15s|", eachColumn.getTitle()));
    }
    System.out.println(); 
    
    // Now we can iterate over the results
    // This looks a bit funky. It's like this so that if the Queryable is based on SQL 
    // queries, it can close the ResultSet when done.
    result.iterate( (eachRow) -> {
        for (Value eachValue : eachRow.values()) {
            System.out.print(String.format("|.15s|", eachValue);
        }
        System.out.println();
    });
```

Note how the result is a Table of Rows. If we choose to "select" individual columns, we cannot return
POJOs.

There are no joins. Instead, we assume the columns on foreign keys are navigable using some metadata such as JPA 
bindings, other annotations or some schema mechanism. In particular, this framework assumes a 
set of "root" entities and navigable properties.

For example, say that Person was (keeping in mind that it might be data from SQL or REST)::

```java
    public class Person {
        String name,
        int age,
        Gender gender,
        List<Person> friends
    }

```
   
Then we could return a table containing two columns: a name, and all our friends ages, by::

```java
    Query q = new Query()
        .select("name")
        .select("friends/age") 
        .orderBy("friends/age");
    return new CollectionQueryable(people).query(q);
```

Columns are navigable using OData syntax with slashes between columns, e.g. "friends/name".

TODO: How to create, update, delete.
TODO: How to wrap a bulk update in a transaction? Or do transactions in general?

# Project goals

This is intended to be a component of a complete OData stack comprising:

* Chickpea - OData front-end              
* Runner - OData test framework
* Urad - Query framework
* Dolichos - OData annotations web service

TODO: A code generator to create a JPA-like metamodel for column names.
