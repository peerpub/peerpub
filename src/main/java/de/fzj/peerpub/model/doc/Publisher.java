package de.fzj.peerpub.model.doc;

import lombok.NonNull;
import lombok.Data;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
// do not import due to conflicting name with org.bson.Document
//import org.springframework.data.mongodb.core.mapping.Document;

import de.fzj.peerpub.model.doc.Attribute;
import de.fzj.peerpub.model.doc.DocType;

import org.bson.Document;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Represent a document type like article, report, etc in the database.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(of={"name"})
@org.springframework.data.mongodb.core.mapping.Document(collection="Publisher")
public class Publisher {
  /**
   * A unique name used as id
   */
  @Id @Indexed(unique = true) @NonNull private String name;

  /**
   * System publishers will not be deletable from the UI.
   */
  @Setter(AccessLevel.NONE)
  @NonNull private Boolean system = false;

  /**
   * Indicate whether this publisher needs an external review or not.
   */
  @NonNull private Boolean reviewing = false;

  /**
   * A list of supported document types, referenced by their name (_id), see
   * {@link model.doc.DocType}.
   * Not using @DBRef here, as this can only be indexed with a Compound Index.
   * To find all publishers that support some kind of doc type will be a very
   * common request, so a real index is preferrable here.
   */
  @Setter(AccessLevel.NONE)
  @Indexed @NonNull private List<String> supports;

  /**
   * Map supported document types by their String _id with attributes by their
   * _id and give them status mandatory or optional plus an default value.
   */
  @Setter(AccessLevel.NONE)
  @NonNull private Map<String,Map<String,Document>> attributes;

  /**
   * Used as value for displaying this publisher in the UI and as
   * "publisher" attribute value within composed metadata documents.
   */
  @TextIndexed @NonNull private String displayName;

  /**
   * List of aliases for this publisher. Index as TextIndexed to support
   * quick searches with parts
   */
  @Setter(AccessLevel.NONE)
  @TextIndexed private List<String> aliases;

  static final String DEFAULT = "default";
  static final String MANDATORY = "mandatory";

  /**
   * Add a supported document type to this publisher. Will only be added if
   * not yet present. Inits the schema on addition, too.
   * @param DocType dt A document type to add.
   */
  public void addSupDocType(@NonNull DocType dt) {
    if( ! supports.contains(dt.getName())) {
      supports.add(dt.getName());
      if( ! attributes.containsKey(dt.getName())) {
        Map<String,Document> schema = new HashMap<String,Document>();
        attributes.put(dt.getName(), schema);
      }
    }
  }

  /**
   * Remove the support for some document type. Destroys the schema, too.
   * @param DocType dt A currently supported document type to remove.
   */
  public void removeSupDocType(@NonNull DocType dt) {
    if(supports.contains(dt.getName()))
      supports.remove(dt.getName());
    if(attributes.containsKey(dt.getName()))
      attributes.remove(dt.getName());
  }

  /**
   * Check if the document type is supported by this publisher.
   * @param DocType dt A document type to check.
   * @return true or false
   */
  public Boolean isSupported(@NonNull DocType dt) {
    return supports.contains(dt.getName());
  }

  public void addAlias(@NonNull String alias) {
    if( ! this.aliases.contains(alias))
      this.aliases.add(alias);
  }
  public String removeAlias(@NonNull String alias) {
    if (this.aliases.remove(alias))
      return alias;
    else
      return null;
  }
  public void removeAllAlias() {
    this.aliases.clear();
  }

  private Boolean hasSchema(DocType dt) {
    return this.attributes.containsKey(dt.getName()) && null != this.attributes.get(dt.getName());
  }
  private Boolean hasDocument(Map<String,Document> schema, String a) {
    return schema.containsKey(a) && null != schema.get(a);
  }
  private Boolean hasDocument(Map<String,Document> schema, Attribute a) {
    return hasDocument(schema, a.getName());
  }

  /**
   * Put an attribute to this publisher schema. Replaces if already present.
   * For different document types you can add different options for the same attribute.
   * @param DocType dt A document type for which this attribute is added. Needs to be supported.
   * @param Attribute a The attribute to put into the schema.
   * @param Boolean mandatory Indicate if this is a mandatory attribute (the user has to provide a value).
   * @param String def Give a default value as a convinience for the user. May be null.
   */
  public void putAttribute(@NonNull DocType dt, @NonNull Attribute a, @NonNull Boolean mandatory, String defaultValue) {
    if(mandatory && (defaultValue == null || defaultValue.isEmpty()))
      throw new IllegalArgumentException("Cannot add a mandatory attribute without a default value");
    if( ! isSupported(dt))
      throw new IllegalArgumentException("Cannot add an attribute to an unsupported document type.");

    // get the schema map for this document type if it exists.
    // else create a new one.
    Map<String,Document> schema;
    if(hasSchema(dt))
      schema = this.attributes.get(dt.getName());
    else
      schema = new HashMap<String,Document>();

    // get the document for this attribute if present, else create one.
    Document doc;
    if(hasDocument(schema,a))
      doc = schema.get(a.getName());
    else
      doc = new Document();

    // insert data
    doc.put(MANDATORY,mandatory);
    if(defaultValue != null && ! defaultValue.isEmpty())
      doc.put(DEFAULT,defaultValue);
    // add/overwrite the document
    schema.put(a.getName(),doc);
    // add the schema only if it is not yet present for the doctype!
    // (overwritting otherwise)
    if(this.attributes.get(dt.getName()) == null)
      this.attributes.put(dt.getName(),schema);
  }

  public void removeAttribute(@NonNull DocType dt, @NonNull Attribute a) {
    if(hasSchema(dt)) {
      Map<String,Document> schema = this.attributes.get(dt.getName());
      // don't use hasDocument() here, as the doc could be null
      if(schema.containsKey(a.getName()))
        schema.remove(a.getName());
    }
  }

  /**
   * Get all defined attributes for a given document type.
   * @param DocType dt The document we are looking for
   * @param Boolean mandatory Include mandatory attributes in the result?
   * @param Boolean optional Include optional attributes in the result?
   * @return A List<String> with the names of the attributes. Another database lookup is needed to get real objects.
   * @throws IllegalArgumentException In case of anything is missing (status or attribute)
   */
  public Set<String> getAttributes(@NonNull DocType dt, @NonNull Boolean mandatory, @NonNull Boolean optional) {
    if(hasSchema(dt)) {
      Set<String> result = new HashSet<String>();
      // iterate over the key set
      for(String aName : this.attributes.get(dt.getName()).keySet()) {
        if(mandatory && isMandatory(dt, aName))
          result.add(aName);
        else if (optional && isOptional(dt, aName))
          result.add(aName);
      }
      return result;
    }
    else
      throw new IllegalArgumentException("No schema defined for this document type. Possible data corruption?");
  }

  /**
   * Get all defined attributes for a given document type.
   * @param DocType dt The document we are looking for
   * @return A List<String> with the names of the attributes. Another database lookup is needed to get real objects.
   */
  public Set<String> getAttributes(@NonNull DocType dt) {
    return getAttributes(dt,true,true);
  }

  /**
   * Return the mandatory status for an attribute.
   * @param DocType dt The document type
   * @param String a The name of the attribute we want the status from.
   * @return true = mandatory, false = optional
   * @throws IllegalArgumentException In case of anything is missing (status or attribute)
   */
  public Boolean isMandatory(@NonNull DocType dt, @NonNull String a) {
    if(hasSchema(dt)) {
      Map<String,Document> schema = this.attributes.get(dt.getName());
      if(hasDocument(schema,a)) {
        Document d = schema.get(a);
        if(d.containsKey(MANDATORY) && d.get(MANDATORY) != null)
          return ((Boolean)d.get(MANDATORY));
        else
          throw new IllegalArgumentException("The mandatory status has not been defined for attribute "+a+" on this document type.");
      } else
        throw new IllegalArgumentException("Attribute "+a+" is not defined for this document type.");
    } else
      throw new IllegalArgumentException("No schema defined for this document type. Possible data corruption?");
  }

  /**
   * Return the mandatory status for an attribute.
   * @param DocType dt The document type
   * @param Attribute a The attribute we want the status from.
   * @return true = mandatory, false = optional
   * @throws IllegalArgumentException In case of anything is missing (status or attribute)
   */
  public Boolean isMandatory(@NonNull DocType dt, @NonNull Attribute a) {
    return isMandatory(dt, a.getName());
  }

  /**
   * Return the optional status for an attribute.
   * @param DocType dt The document type
   * @param Attribute a The attribute we want the status from.
   * @return true = optional, false = mandatory
   * @throws IllegalArgumentException In case of anything is missing (status or attribute)
   */
  public Boolean isOptional(@NonNull DocType dt, @NonNull Attribute a) {
    return (!isMandatory(dt,a));
  }

  /**
   * Return the optional status for an attribute.
   * @param DocType dt The document type
   * @param String a The name of the attribute we want the status from.
   * @return true = optional, false = mandatory
   * @throws IllegalArgumentException In case of anything is missing (status or attribute)
   */
  public Boolean isOptional(@NonNull DocType dt, @NonNull String a) {
    return (!isMandatory(dt,a));
  }

  /**
   * Return the default value or an empty string for an attribute.
   * @param DocType dt The document type
   * @param Attribute a The attribute we want the default value from.
   * @return Default value String or empty String.
   * @throws IllegalArgumentException In case if the attribute is not defined.
   */
  public String getDefault(@NonNull DocType dt, @NonNull Attribute a) {
    if(hasSchema(dt)) {
      Map<String,Document> schema = this.attributes.get(dt.getName());
      if(hasDocument(schema,a)) {
        Document d = schema.get(a.getName());
        if(d.containsKey(DEFAULT) && d.get(DEFAULT) != null)
          return ((String)schema.get(a.getName()).get(DEFAULT));
        else
          return "";
      } else
        throw new IllegalArgumentException("Attribute "+a.getName()+" is not defined for this document type.");
    } else
      throw new IllegalArgumentException("No schema defined for this document type. Possible data corruption?");
  }
}
