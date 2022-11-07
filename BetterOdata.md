# A better OData protocol.

This file contains ideas and pain points that I come across when dealing with Odata. My end goal is to develop
another protocol that is better.

OData is a very complex protocol. On one hand, it has a huge amount of functionality. On the other, I imagine that
implementers are shying away from it, as it is a huge amount of work to implement.

Remove features:

* Choose one data format and stick with it. XML, JSON, YAML, ASN.1, whatever - pick only one. 

* Remove namespace and container names? One container only.

* Disallow getting entities by ID. Make a query for that. (maybe? But then you lose links to resources.)

* Disallow getting individual predicates. Make a select for that.

* Unify $select and $expand? Make all results a flat list using only $select.

* Edm.Binary, Edm.Stream, Edm.SBytes. Replace them with a URL link instead so that we get MIME types, download sizes, etc.

* Reduce all the numeric types to numbers with upper limit, lower limit, precision.

In general, if something can already be done, don't add another way of doing it.

New features:

* Mandate the order of parameters, columns, etc in a URL for efficient HTTP caching. Make $skip be multiples of 256 (?) and $top to be fixed at 256, again for efficient caching.

* Maybe change the encoding to ASN.1 PER over HTTP?
  - Maybe drop OData altogether and autogenerate OpenAPI with all the query parameters.
  - Tables and columns can be described by querying tables. Select * from tables;

* Implement encryption using a secret group key. This allows confidential data to be cached on a CDN.

* Add Templates. Before an INSERT, give the client a prepopulated template row for the user to populate. That prepopulated template should also have a new generated primary key ready to go. Effectively it's creating a row but keeping the transaction open so it can be rolled back if the user cancels.

* "Push updates" and subscriptions to tables for real-time multi-user stuff.

* Server-side column value verification. As the user types, ask the server whether the entered value is valid. Provide a descriptive reason to the user.

* Does Odata already support $select=path1/path2/path3 and $filter=(path1/path2/path3 eq 'foo')???

* Add transaction support... somehow. Maybe use something like a $transaction=XXX parameter. Maybe do something funky with 
  resources (tables) being immutable and getting new URIs if they change. 
  
* Batch updates only? Only support POST and GET.

* Batch updates are always ordered. 

* Batch updates can contain queries which set variables. --> Already exists?? Variables include User, current time, generated sequence results.

* As well as a primary key, mark a column in a table (or complex column) as the "display" one to show to the user when only showing a summary of that table (e.g. collapsed complex column, drop-down list).

* Change the column's name to a code indended for consumption by code.

* Add humanName, descriptions (tool tips) to columns. --> Annotations
  
* Maybe compress URLs. Perhaps do a funky URL encoding using one of the ASN.1 encodings and base64 it. The same 
  query on the same table version, built and encoded twice, would return the exact same URL. Give tables and columns
  special shortened codes.
  - Another idea: put the query in the body and use a 303 to a minified URL? Hmm.
  
* If compressed URLs are used, also provide some way of easily debugging them. 

* Replace formulas with a proper programming language. Maybe compile to WebAssembly and run on the browser. 
  - Formula columns
  - Triggers
  - Maybe even batch updates?
  - Or just ask the server to verify columns.

* Describe server limits
  - Max number of retrievable rows.
  - Any rate limiting?
  - Check the RESTier limits.

* Formalize error messages and exceptions so that they can be coded against and recovered from. RFC 7807.
  - "Query too difficult". 
  - "Row limit exceeded"
  - "Progress: n%"
  - "Timed out, partial data returned."
  etc.
