```plantuml
@startuml
package doc.attribute {
  hide empty methods
  hide empty attribute

  class Attribute {
    -name: String
    -key: String
    -label: String
    -description: String
    -jsonSchema: String
  }

  AttributeRepository .> Attribute
  interface AttributeRepository <<MongoRepository>> {
    +findByName(name: String): Attribute
    +findByLabel(label: String): Attribute[0..*]
    +findByKey(key: String): Attribute[0..*]
  }

  Attribute <- AttributeService
  AttributeRepository <- AttributeService
  class AttributeService {
    +getNameBasedMap(): Map<String, Attribute>
  }

  Attribute <- AttributeAdminCtrl
  class AttributeAdminCtrl {
    +list()
    +addGetForm()
    +addPostForm()
    +editGetForm()
    +editPostForm()
    +delete()
  }
}
note top of doc.attribute
Scaffolding methods have been
intentionally left out.
end note
@enduml
```
