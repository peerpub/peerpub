package de.fzj.peerpub.doc.doctype;

import de.fzj.peerpub.doc.validator.Referable;
import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import de.fzj.peerpub.doc.attribute.Attribute;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represent a document type like article, report, etc in the database.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"})
@Document(collection = "DocTypes")
public class DocType {

  /**
   * A unique name.
   * Will be used as value for "type" within composed metadata documents.
   * BEWARE: the controller will apply restrictions on the content.
   */
  @Id @NonNull @Referable
  private String name;
  /**
   * System types will not be deletable from the UI.
   */
  @NonNull private Boolean system = false;
  /**
   * Not every type may be used with {@link doc.Collection} of multiple
   * {@link doc.Document}. Enable this to use it in this context.
   */
  @NonNull private Boolean multiDoc = false;
  
  /**
   * Map attributes by their name (=_id) and give them status mandatory or optional plus a default value.
   */
  @Setter(AccessLevel.NONE)
  @Referable
  @NonNull private Map<String, org.bson.Document> attributes = new HashMap<>();
  
  /**
   * Display name for showing this doc type in the UI. Could be a translatable
   * string "{xxx.xxx}", so we need to split name and displayName
   */
  @NotBlank private String displayName;
  
  static final String DEFAULT = "default";
  static final String MANDATORY = "mandatory";
  
  /**
   * Put an attribute to this document type. Replaces if already present.
   * @param aName The name of the attribute to put into the schema.
   * @param mandatory Indicate if this is a mandatory attribute (the user has to provide a value).
   * @param defaultValue Give a default value as a convinience for the user. May be null.
   */
  public void putAttribute(@NonNull String aName, @NonNull Boolean mandatory, String defaultValue) {
    // get the document for this attribute if present, else create one.
    org.bson.Document doc;
    if (attributes.containsKey(aName)) {
      doc = attributes.get(aName);
    } else {
      doc = new org.bson.Document();
    }
    
    // insert data
    doc.put(MANDATORY, mandatory);
    if (defaultValue != null && !defaultValue.isEmpty()) {
      doc.put(DEFAULT, defaultValue);
    }
    // add/overwrite the document
    attributes.put(aName, doc);
  }
  
  /**
   * Put an attribute to this document type. Replaces if already present.
   * @param a The attribute to put into the schema.
   * @param mandatory Indicate if this is a mandatory attribute (the user has to provide a value).
   * @param defaultValue Give a default value as a convinience for the user. May be null.
   */
  public void putAttribute(@NonNull Attribute a, @NonNull Boolean mandatory, String defaultValue) {
    putAttribute(a.getName(), mandatory, defaultValue);
  }
  
  /**
   * Simply delete the attribute key and associated settings. If not existent, this will stay silent.
   * @param aName
   */
  public void removeAttribute(@NonNull String aName) {
    attributes.remove(aName);
  }
  
  /**
   * Simply delete the attribute key and associated settings. If not existent, this will stay silent.
   * @param a
   */
  public void removeAttribute(@NonNull Attribute a) {
    removeAttribute(a.getName());
  }
  
  /**
   * Get all defined attributes with either status mandatory, optional or both.
   * @param mandatory Include mandatory attributes in the result?
   * @param optional Include optional attributes in the result?
   * @return A Set<String> with the names of the attributes. Another database lookup is needed to get real objects.
   */
  public Set<String> getAttributes(@NonNull Boolean mandatory, @NonNull Boolean optional) {
    Set<String> result = new HashSet<>();
    // iterate over the key set
    for (String aName : this.attributes.keySet()) {
      if (mandatory && isMandatory(aName)) {
        result.add(aName);
      } else {
        if (optional && isOptional(aName)) {
          result.add(aName);
        }
      }
    }
    return result;
  }
  
  /**
   * Get all defined attributes for a given document type.
   * @return A Set<String> with the names of the attributes. Another database lookup is needed to get real objects.
   */
  public Set<String> getAttributes() {
    return getAttributes(true, true);
  }
  
  /**
   * Sometime we need to get the real mapping. (Tests, etc)
   * Only package visibility for now.
   */
  Map<String, org.bson.Document> getAttributesMap() {
    return attributes;
  }
  
  /**
   * Return the mandatory status for an attribute.
   * @param a The name of the attribute we want the status from.
   * @return true = mandatory, false = optional
   * @throws IllegalArgumentException In case of anything is missing (status or attribute)
   */
  public Boolean isMandatory(@Referable String a) {
    if (this.attributes.containsKey(a)) {
      org.bson.Document d = this.attributes.get(a);
      if (d.containsKey(MANDATORY) && d.get(MANDATORY) != null) {
        return (Boolean) d.get(MANDATORY);
      } else {
        throw new IllegalArgumentException("The mandatory status has not been defined for attribute " + a
            + " on this document type.");
      }
    } else {
      throw new IllegalArgumentException("Attribute " + a + " is not defined for this document type.");
    }
  }
  
  /**
   * Return the mandatory status for an attribute.
   * @param a The attribute we want the status from.
   * @return true = mandatory, false = optional
   * @throws IllegalArgumentException In case of anything is missing (status or attribute)
   */
  public Boolean isMandatory(@NonNull Attribute a) {
    return isMandatory(a.getName());
  }
  
  /**
   * Return the optional status for an attribute.
   * @param a The attribute we want the status from.
   * @return true = optional, false = mandatory
   * @throws IllegalArgumentException In case of anything is missing (status or attribute)
   */
  public Boolean isOptional(@NonNull Attribute a) {
    return !isMandatory(a);
  }
  
  /**
   * Return the optional status for an attribute.
   * @param a The name of the attribute we want the status from.
   * @return true = optional, false = mandatory
   * @throws IllegalArgumentException In case of anything is missing (status or attribute)
   */
  public Boolean isOptional(@Referable String a) {
    return !isMandatory(a);
  }
  
  /**
   * Return the default value or an empty string for an attribute.
   * @param a The attribute we want the default value from.
   * @return Default value String or empty String.
   * @throws IllegalArgumentException In case if the attribute is not defined.
   */
  public String getDefault(@NonNull String a) {
    if (this.attributes.containsKey(a)) {
      org.bson.Document d = this.attributes.get(a);
      if (d.containsKey(DEFAULT) && d.get(DEFAULT) != null) {
        return (String) d.get(DEFAULT);
      } else {
        return "";
      }
    } else {
      throw new IllegalArgumentException("Attribute " + a + " is not defined for this document type.");
    }
  }
  
  /**
   * Return the default value or an empty string for an attribute.
   * @param a The attribute we want the default value from.
   * @return Default value String or empty String.
   * @throws IllegalArgumentException In case if the attribute is not defined.
   */
  public String getDefault(@NonNull Attribute a) {
    return getDefault(a.getName());
  }
  
  /**
   * equals(), extended Version. Normally we only want equals() to compare
   * the name, as anything else is not important. In some cases, we might want
   * a more verbose look, especially for testing.
   */
  public boolean equalsDeep(Object o) {
    if (this.equals(o)) {
      DocType oDT = (DocType) o;
      if (!oDT.getSystem().equals(this.getSystem())
          || !oDT.getMultiDoc().equals(this.getMultiDoc())) {
        return false;
      }
      if (!this.getAttributes().equals(oDT.getAttributes())) {
        return false;
      }
      if (!this.getAttributesMap().equals(oDT.getAttributesMap())) {
        return false;
      }
      if (!this.getDisplayName().equals(oDT.getDisplayName())) {
        return false;
      }
      return true;
    }
    return false;
  }
}
