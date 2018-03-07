package de.fzj.peerpub.model.doc;

import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@AllArgsConstructor
@EqualsAndHashCode(exclude={"id","jsonSchema","description"})
@Document(collection="MetadataAttributes")
public class Attribute {
  /**
   * The attributes unique ID, will be used as primary key.
   */
  @Id @Getter @NonNull private final String id;

  /**
   * Unique name for this attribute to have definite search results, etc.
   */
  @Indexed(unique = true) @NonNull @Getter @Setter private String name;
  /**
   * Display name for this attribute (printed as label).
   */
  @NonNull @Getter @Setter private String label;
  /**
   * The key value, under which the Attribute value will be added to the
   * composed metadata set.
   */
  @NonNull @Getter private String key;
  /**
   * A short informative text presented to the user as a helping hand what
   * this attribute is and what to insert.
   */
  @NonNull @Getter @Setter private String description;
  /**
   * A JSON schema that allows the generation of input masks and
   * validation of inserted data
   */
  @NonNull @Getter private String jsonschema;

  public void setJsonSchema(String schema) {
    //TODO: Validation of the provided schema + test
    this.jsonschema = schema;
  }

  //TODO: Future: add validation methods for data using the schema.
}
