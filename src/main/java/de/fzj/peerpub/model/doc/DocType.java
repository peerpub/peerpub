package de.fzj.peerpub.model.doc;

import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;
import lombok.ToString;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import de.fzj.peerpub.model.doc.Attribute;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

@ToString
@AllArgsConstructor
@Document(collection="DocTypes")
public class DocType {
  /**
   * The types unique ID, will be used as primary key.
   */
  @Id @Getter @NonNull private final String id;

  @Indexed(unique = true) @NonNull @Setter @Getter private String name;
  @NonNull @Setter @Getter private Boolean system = false;
  @NonNull @Setter @Getter private Boolean multidoc = false;

  //TODO: Change to Document instead of Map for easier storage in MongoDB
  @NonNull
  private Map<Attribute,Boolean> attributes = new HashMap<Attribute,Boolean>();
  @NonNull
  private Map<Attribute,String> defaults = new HashMap<Attribute,String>();

  /**
   * Add an attribute to this Document Type.
   * @param Boolean mandatory Indicate if this is a mandatory attribute (the user has to provide a value)
   * @param String def Give a default value, if the user is not able to add
   */
  public void addAttribute(Attribute a, Boolean mandatory, String def) {
    if(mandatory && (def == null || def.isEmpty()))
      throw new IllegalArgumentException("Cannot add a mandatory attribute without a default value");
    this.attributes.put(a,mandatory);
    if(def != null)
      this.defaults.put(a,def);
  }
  //public Set<Attribute> getAttributes() {}
  //public Set<Attribute> getAttributes(Boolean incMand, Boolean incOpt) {}
  //public String getDefault(Attribute a) {}
  //public Boolean isMandatory(Attribute a) {}
  //public Boolean isOptional(Attribute a) {}
}
