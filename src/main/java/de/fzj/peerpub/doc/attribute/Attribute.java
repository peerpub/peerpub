package de.fzj.peerpub.doc.attribute;

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
   * Unique name for this attribute to have definite search results, etc.
   * As it is unique anyway, use it as the _id for MongoDB.
   */
  @Id @Indexed(unique = true) @NonNull private String name;
  /**
   * The key value, under which the Attribute value will be added to the
   * composed metadata set.
   * BEWARE: the controller will apply restrictions on the content.
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
