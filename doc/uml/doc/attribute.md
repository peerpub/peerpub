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
    -jsonschema: String
  }

  AttributeRepository .> Attribute
  interface AttributeRepository <<MongoRepository>> {
    +findByName(name: String): Attribute
    +findByLabel(label: String): Attribute[0..*]
    +findByKey(key: String): Attribute[0..*]
  }

  Attribute <- AttributeAdminCtrl
  class AttributeAdminCtrl {
    +list()
    +listJson()
    +addGetForm()
    +addPostForm()
    +updateGetForm()
    +updatePostForm()
    +delete()
  }
}
note top of doc.attribute
Scaffolding methods have been
intentionally left out.
end note
@enduml
```
