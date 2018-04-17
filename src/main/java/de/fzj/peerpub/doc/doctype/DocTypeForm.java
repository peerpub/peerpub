package de.fzj.peerpub.doc.doctype;

import de.fzj.peerpub.doc.validator.Referable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * View model: basically a transformed class of
 * {@link DocType}, used due to Thymeleaf/Spring
 * restrictions for HTML form usage.
 * Details about the fields see {@link DocType}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocTypeForm {
  /**
   * See {@link DocType}
   */
  @Referable private String name;
  /**
   * See {@link DocType}
   */
  @NotNull @Size(min = MIN_DISPLAY, max = MAX_DISPLAY)
  private String displayName;
  /**
   * See {@link DocType}
   */
  @NotNull private Boolean system = false;
  /**
   * See {@link DocType}
   */
  @NotNull private Boolean multiDoc = false;
  /**
   * A LIST of attribute names.
   * DO NOT USE A SET HERE - this would not be bindable by Thymeleaf forms.
   */
  @UniqueElements @Referable private List<String> attributes = new ArrayList<>();
  /**
   * The mandatory or optional status of an attribute.
   * Used because we cannot use a map of maps in forms.
   */
  @Referable private Map<String, Boolean> mandatory;
  /**
   * Default values of an attribute.
   * Used because we cannot use a map of maps in forms.
   */
  @Referable private Map<String, String> defaults;
  
  static final int MIN_DISPLAY = 3;
  static final int MAX_DISPLAY = 50;
  
  /**
   * Convert from DocType to DocTypeForm
   */
  static DocTypeForm toForm(@NotNull DocType dt) {
    DocTypeForm dtf = new DocTypeForm();
    
    dtf.setName(dt.getName());
    dtf.setDisplayName(dt.getDisplayName());
    dtf.setSystem(dt.getSystem());
    dtf.setMultiDoc(dt.getMultiDoc());
    dtf.setAttributes(new ArrayList<>(dt.getAttributes()));
  
    Map<String, Boolean> mandatory = new HashMap<>();
    Map<String, String> defValues = new HashMap<>();
    for (String attr : dt.getAttributes()) {
      mandatory.put(attr, dt.isMandatory(attr));
      defValues.put(attr, dt.getDefault(attr));
    }
    dtf.setMandatory(mandatory);
    dtf.setDefaults(defValues);
    
    return dtf;
  }
  
  /**
   * Convert this DocTypeForm to DocType
   */
  DocType toType() {
    DocType dt = new DocType();
    
    dt.setName(this.name);
    dt.setDisplayName(this.displayName);
    dt.setSystem(this.system);
    dt.setMultiDoc(this.multiDoc);
    
    // convert attributes
    if (this.attributes != null) {
      for (String attr : this.attributes) {
        // skip null attributes (could be deleted or other reasons)
        if (attr != null) {
          Boolean mand = this.mandatory.get(attr);
          dt.putAttribute(attr,
              // mandatory will be null if not checked!
              mand != null && mand ? true : false,
              this.defaults.get(attr));
        }
      }
    }
    
    return dt;
  }
}
