package de.fzj.peerpub.doc.doctype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import de.fzj.peerpub.doc.attribute.Attribute;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Represent a document type like article, report, etc in the database.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"})
@Document(collection = "DocTypes")
public class DocType {

  /**
   * A unique name.
   * Will be used as value for "type" within composed metadata documents.
   * BEWARE: the controller will apply restrictions on the content.
   */
  @Id @NonNull private String name;
  /**
   * System types will not be deletable from the UI.
   */
  @NonNull private Boolean system = false;
  /**
   * Not every type may be used with {@link doc.Collection} of multiple
   * {@link doc.Document}. Enable this to use it in this context.
   */
  @NonNull private Boolean multidoc = false;

  /**
   * A list of attributes for this document type
   */
  @DBRef private List<Attribute> attributes = new ArrayList<>();
  /**
   * Mandatory or optional status of the attributes
   */
  private Map<String, Boolean> mandatory = new HashMap<>();
  /**
   * (Optional) default values for attributes
   */
  private Map<String, String> defaults = new HashMap<>();

  /**
   * Put an attribute to this Document Types schema. Replaces if already present.
   * You cannot add different options for the same attribute.
   * @param a The attribute to put into the schema.
   * @param isMandatory Indicate if this is a mandatory attribute (the user has to provide a value).
   * @param defaultValue Give a default value as a convinience for the user. May be null.
   */
  public void putAttribute(@NonNull Attribute a, @NonNull Boolean isMandatory, String defaultValue) {
    if (isMandatory && (defaultValue == null || defaultValue.isEmpty())) {
      throw new IllegalArgumentException("Cannot add a mandatory attribute without a default value");
    }
    
    // add attribute if not already present
    if (!this.attributes.contains(a)) {
      this.attributes.add(a);
    }
    this.mandatory.put(a.getName(), isMandatory);
    
    // put default value if given
    if (defaultValue != null && !defaultValue.isEmpty()) {
      this.defaults.put(a.getName(), defaultValue);
    }
  }
  //public Set<Attribute> getAttributes() {}
  //public Set<Attribute> getAttributes(Boolean incMand, Boolean incOpt) {}
  //public String getDefault(Attribute a) {}
  //public Boolean isMandatory(Attribute a) {}
  //public Boolean isOptional(Attribute a) {}
}
