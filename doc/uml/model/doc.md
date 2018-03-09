```plantuml
@startuml
package model.user {
  class PPUser
  hide members
}
package model.multidoc {
  hide members

  class DocCollection
}
package model.doc {
  hide empty methods

  PPUser --- Document
  Attribute - Document
  DocCollection -- Document
  class Document {
    -id: String
    -type: DocType
    -title: String
    -keywords: String[0..*]
    -metadata: Map<Attribute,String>
    -committer: PPUser
    -reviewer: PPUser
    -director: PPUser
    -authors: DocAuthor
    -dates: Map<String,Date>
    -versions: DocVersion
    -releases: DocRelease
    -funding: Set<FundAward>

    +getDateCommitted(): Date
    +getDateDeadline(): Date
    +getDateStarted(): Date
    +getDateReviewed(): Date
    +getDateApproved(): Date
    +getDateDone(): Date
    +getDateLastMod(): Date
  }

  DocType - Document
  Attribute - DocType
  class DocType {
    -name: String
    -system: Boolean
    -multidoc: Boolean
    -attributes: Attribute[0..*]
    -mandatory: Map<String,Boolean>
    -defaults: Map<String,String>

    +putAttributes(a: Attribute, mandatory: Boolean, defaultValue: String)
    +getAttributes(): Set<Attribute>
    +getAttributes(Boolean incMand, Boolean incOpt): Set<Attribute>
    +getDefault(a: Attribute): String
    +isMandatory(a: Attribute): Boolean
    +isOptional(a: Attribute): Boolean
    (...)
  }

  class Attribute {
    -name: String
    -label: String
    -key: String
    -description: String
    -jsonschema: String
  }

  PPUser - DocAuthor
  DocAuthor "1..*" --o "1" Document
  DocCollection "1" -o "1..*" DocAuthor
  class DocAuthor {
    -user: PPUser
    -name: String
    -role: String
    -affiliation: String
  }

  Document "1" o-- "1..*" DocRelease
  Attribute -- DocRelease
  class DocRelease {
    -id: String
    -publisher: Publisher
    -metadata: Map<Attribute,String>
    -started: Date
    -published: Date
    -done: Date
    -status: RelStatus
    -pubdbentry: URL
    -uri: URI
  }
  DocRelease -- RelStatus
  enum RelStatus {
    SUBMITTED
    UNDER_REVIEW
    ACCEPTED
    REJECTED
  }

  DocVersion "1..*" --o "1" Document
  class DocVersion {
    -committed: Date
    -name: String
    -location: URL
    -type: MIMEType
    -tag: String
  }

  Comment - PPUser
  Document -- Comment
  class Comment {
    -id: String
    -document: Document
    -posted: Date
    -severity: CommentSeverity
    -author: PPUser
    -msg: String
    -files: CommentFile[0..*]
  }
  Comment - CommentFile
  class CommentFile {
    -type: MIMEType
    -location: URL
    -name: String
  }
  Comment -- CommentSeverity
  enum CommentSeverity {
    FINAL_REJECTION
    MAJOR_CHANGE
    MINOR_CHANGE
    NONE
  }

  Publisher - DocRelease
  DocType --o Publisher
  Attribute -- Publisher
  class Publisher {
    -id: String
    -name: String
    -reviewing: Boolean
    -system: Boolean
    -aliases: String[0..*]
    -supports: Set<DocType>
    -attributes: Map<DocType,Map<Attribute,Boolean>>
    -defaults: Map<DocType,Map<Attribute,String>>

    +getSupported(): Set<DocType>
    +getAttributes(DocType d): Set<Attribute>
    +getAttributes(DocType d, Boolean incMand, Boolean incOpt): Set<Attribute>
    +getDefault(DocType d, Attribute a): String
    +isMandatory(DocType d, Attribute a): Boolean
    +isOptional(DocType d, Attribute a): Boolean
    (...)
  }

  Document - FundAward
  class FundAward {
    -id: String
    -name: String
    -number: String
    -funder: String
    -identifier: String
  }
}
note top of model.doc
Scaffolding methods have been
intentionally left out.
end note
@enduml
```
