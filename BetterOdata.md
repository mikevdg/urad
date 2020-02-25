# A better OData protocol.

* JSON only. No XML.
* Remove namespace and container names? One container only.
* Cannot get entities by ID. Make a query for that.
* Cannot get individual predicates. Make a select for that.
* In general, if something can already be done, don't add another way of doing it.
* Add transaction support.
* Add descriptions and codes to columns. --> Annotations
* Can get default values for a creatable entity. --> Annotations.
* Batch updates only. Only support POST and GET.
* Batch updates are always ordered. 
* Batch updates can contain queries which set variables. --> Already exists??
