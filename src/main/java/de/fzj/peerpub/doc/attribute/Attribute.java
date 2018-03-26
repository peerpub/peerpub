package de.fzj.peerpub.doc.attribute;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// validation
import lombok.NonNull;
import javax.validation.constraints.NotBlank;
import de.fzj.peerpub.doc.validator.Referable;

/**
 * Metadata Attribute Data Transfer Object
 */
@Data
// only compare attributes with its unique name. the rest may be the same or different.
@EqualsAndHashCode(of = {"name"})
@NoArgsConstructor
@RequiredArgsConstructor
@Document(collection = "MetadataAttributes")
public class Attribute {
  /**
   * Unique name for this attribute to have definite search results, etc.
   * As it is unique anyway, use it as the _id for MongoDB.
   */
  @Id @NonNull @Referable
  private String name;
  /**
   * The key value, under which the Attribute value will be added to the
   * composed metadata set.
   * BEWARE: the controller will apply restrictions on the content.
   */
  @NonNull @Referable
  private String key;
  /**
   * Display name for this attribute (printed as label).
   */
  @NonNull @NotBlank
  private String label;
  /**
   * A short informative text presented to the user as a helping hand what
   * this attribute is and what to insert.
   */
  @NonNull @NotBlank
  private String description;
  /**
   * A JSON schema that allows the generation of input masks and
   * validation of inserted data
   */
  @NonNull @NotBlank
  private String jsonSchema = "{}";
  
  /**
   * Setter for JSON Schema, validating the schema when adding
   * TODO: MAYBE LEAVE THIS TO A CUSTOM ANNOTATION + VALIDATOR?
   * @param schema A JSON schema string
   */
  public void setJsonSchema(String schema) {
    this.jsonSchema = schema;
  }

  //TODO: Future: add validation methods for data using the schema.
}
