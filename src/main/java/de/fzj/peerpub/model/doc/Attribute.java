package de.fzj.peerpub.model.doc;

import lombok.NonNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
// only compare attributes with its unique name. the rest may be the same or different.
@EqualsAndHashCode(of={"name"})
@Document(collection="MetadataAttributes")
public class Attribute {
  /**
   * The attributes unique ID, will be used as primary key.
   */
  @Id private String id;

  /**
   * Unique name for this attribute to have definite search results, etc.
   */
  @Indexed(unique = true) @NonNull
  private String name;
  /**
   * The key value, under which the Attribute value will be added to the
   * composed metadata set.
   */
  @NonNull
  private String key;
  /**
   * Display name for this attribute (printed as label).
   */
  @NonNull
  private String label;
  /**
   * A short informative text presented to the user as a helping hand what
   * this attribute is and what to insert.
   */
  @NonNull
  private String description;
  /**
   * A JSON schema that allows the generation of input masks and
   * validation of inserted data
   */
  @NonNull
  private String jsonschema;
  public void setJsonSchema(String schema) {
    //TODO: Validation of the provided schema + test
    this.jsonschema = schema;
  }

  //TODO: Future: add validation methods for data using the schema.
}
