# A better OData protocol.

This file contains ideas and pain points that I come across when dealing with Odata. My end goal is to develop
another protocol that is better.

OData is a very complex protocol. On one hand, it has a huge amount of functionality. On the other, I imagine that
implementers are shying away from it, as it is a huge amount of work to implement.

Remove features:

* Choose one data format and stick with it. XML, JSON, YAML, ASN.1, whatever - pick only one. 

* Remove namespace and container names? One container only.

* Cannot get entities by ID. Make a query for that.

* Cannot get individual predicates. Make a select for that.

* Unify $select and $expand?

* In general, if something can already be done, don't add another way of doing it.


New features:

* Maybe change the encoding to ASN.1 PER over HTTP? 

* Does Odata already support $select=path1/path2/path3 and $filter=(path1/path2/path3 eq 'foo')???

* Add transaction support. Maybe use something like a $transaction=XXX parameter. Maybe do something funky with 
  resources (tables) being immutable and getting new URIs if they change. 

* Add descriptions and codes to columns. --> Annotations

* Can get default values for a creatable entity. --> Annotations.

* Batch updates only? Only support POST and GET.

* Batch updates are always ordered. 

* Batch updates can contain queries which set variables. --> Already exists??

* Optimise for use of the browser's cache. A table's (or table segment's) URL should change if the table changes, and
  notifications for this should be sent to the browser somehow, possibly piggy-backing off other requests.
  
* Maybe compress URLs. Perhaps do a funky URL encoding using one of the ASN.1 encodings and base64 it. The same 
  query on the same table version, built and encoded twice, would return the exact same URL. Give tables and columns
  special shortened codes.
  
* If compressed URLs are used, also provide some way of easily debugging them. 
  