package de.fzj.peerpub.model.doc;

import lombok.NonNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import de.fzj.peerpub.model.doc.Attribute;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Represent a document type like article, report, etc in the database.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(of={"name"})
@Document(collection="DocTypes")
public class DocType {

  /**
   * A unique name.
   * Will be used as value for "type" within composed metadata documents.
   * BEWARE: the controller will apply restrictions on the content.
   */
  @Id @Indexed(unique = true) @NonNull private String name;
  /**
   * System types will not be deletable from the UI.
   */
  @NonNull private Boolean system = false;
  /**
   * Not every type may be used with {@link model.doc.Collection} of multiple
   * {@link model.doc.Document}.
   * Enable this to use it in this context.
   */
  @NonNull private Boolean multidoc = false;

  /**
   * A list of attributes. There needs to be information about the status (mandatory or
   * optional) plus default values.
   */
  @DBRef private List<Attribute> attributes = new ArrayList<Attribute>();
  private Map<String,Boolean> mandatory = new HashMap<String,Boolean>();
  private Map<String,String> defaults = new HashMap<String,String>();

  /**
   * Put an attribute to this Document Types schema. Replaces if already present.
   * You cannot add different options for the same attribute.
   * @param Attribute a The attribute to put into the schema.
   * @param Boolean mandatory Indicate if this is a mandatory attribute (the user has to provide a value).
   * @param String def Give a default value as a convinience for the user. May be null.
   */
  public void putAttribute(@NonNull Attribute a, @NonNull Boolean mandatory, String defaultValue) {
    if(mandatory && (defaultValue == null || defaultValue.isEmpty()))
      throw new IllegalArgumentException("Cannot add a mandatory attribute without a default value");
    if( ! this.attributes.contains(a))
      this.attributes.add(a);
    this.mandatory.put(a.getName(),mandatory);
    if(defaultValue != null && ! defaultValue.isEmpty())
      this.defaults.put(a.getName(),defaultValue);
  }
  //public Set<Attribute> getAttributes() {}
  //public Set<Attribute> getAttributes(Boolean incMand, Boolean incOpt) {}
  //public String getDefault(Attribute a) {}
  //public Boolean isMandatory(Attribute a) {}
  //public Boolean isOptional(Attribute a) {}
}
