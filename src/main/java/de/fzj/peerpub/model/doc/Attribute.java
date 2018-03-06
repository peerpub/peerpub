package de.fzj.peerpub.model.doc;

import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;
import lombok.ToString;
import lombok.AllArgsConstructor;

@ToString
@AllArgsConstructor
public class Attribute {
  @Getter @NonNull private final String id;

  @NonNull @Getter @Setter private String name;
  @NonNull @Getter @Setter private String key;
  @NonNull @Getter @Setter private String description;

  @NonNull @Getter private String jsonschema;
  public void setJsonSchema(String schema) {
    //TODO: Validation of the provided schema
    //TODO: Add test
    this.jsonschema = schema;
  }
}
