package de.fzj.peerpub.doc.doctype;

import de.fzj.peerpub.doc.attribute.Attribute;
import de.fzj.peerpub.doc.attribute.AttributeTest;
import de.fzj.peerpub.utils.Random;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
public class DocTypeFormTest {
 
  @Test
  @DisplayName("simple toForm().toType() should return original object")
  void simpleConvertToAndForm() {
    // given
    DocType dt = DocTypeTest.generate();
    // when
    DocType dtConv = DocTypeForm.toForm(dt).toType();
    // then
    assertTrue(dt.equalsDeep(dtConv));
  }
  
  @Test
  @DisplayName("more advanced toForm().toType() should return original object")
  void advConvertToAndForm() {
    // given
    List<Attribute> attrs = AttributeTest.generate(2);
    DocType dt = DocTypeTest.generate(attrs);
    // when
    DocType dtConv = DocTypeForm.toForm(dt).toType();
    // then
    assertTrue(dt.equalsDeep(dtConv));
  }
  
  @Test
  @DisplayName("Test on null in attribute list (deletion/...)")
  void testNullInAttributes() {
    // given
    List<Attribute> attrs = AttributeTest.generate(2);
    DocType dt = DocTypeTest.generate();
    dt.putAttribute(attrs.get(0), false, "");
    dt.putAttribute(attrs.get(1), false, "");
    
    DocTypeForm dtf = DocTypeForm.toForm(dt);
    dtf.getAttributes().add(1, null);
    dtf.getAttributes().add(null);
    
    // when
    DocType conv = dtf.toType();
    
    // then
    assertTrue(dt.equalsDeep(conv));
  }
  
  @Test
  @DisplayName("Test on null values within mandatory map as optional arguments in DocType")
  void testMandatoryNullAsOptionalAttributes() {
    // given
    List<Attribute> attrs = AttributeTest.generate(2);
    DocType dt = DocTypeTest.generate();
    dt.putAttribute(attrs.get(0), true, "");
    dt.putAttribute(attrs.get(1), false, "");
    
    DocTypeForm dtf = DocTypeForm.toForm(dt);
    dtf.getMandatory().put(attrs.get(0).getName(), null);
  
    // when
    DocType conv = dtf.toType();
  
    // then
    assertTrue(conv.isOptional(attrs.get(0)));
    assertFalse(dt.equalsDeep(conv));
  }
  
  @Test
  void formsAndTypesAreDistinct() {
    List<Attribute> attrs = AttributeTest.generate(2);
    DocType test = DocTypeTest.generate(attrs,2);
    
    // do an easy transform
    DocTypeForm testForm = DocTypeForm.toForm(test);
    testForm.setDisplayName("test");
    DocType altered = testForm.toType();
    assertFalse(altered.equalsDeep(test));
    
    // do a transformation on the maps
    testForm = DocTypeForm.toForm(test);
    testForm.getDefaults().put(attrs.get(0).getName(), "test");
    altered = testForm.toType();
    assertFalse(altered.equalsDeep(test));
  }
  
  /**
   * While using DocTypeForm generated from DocTypes in tests,
   * the mandatory status is true/false. When receiving the form
   * from the view, statis is true/null. Handle this.
   * @param dtf DocTypeForm needing correction for testing.
   * @return Corrected DocTypeForm
   */
  public static DocTypeForm adaptToViewValues(DocTypeForm dtf) {
    // manually correct the form:
    // 1. when sending to view, mandatory needs to be true/false, but
    // 2. getting from view values are true/null!
    for (Map.Entry<String, Boolean> b : dtf.getMandatory().entrySet()) {
      if (!b.getValue()) {
        b.setValue(null);
      }
    }
    return dtf;
  }
  
  /**
   * Clone DocTypeForm for testing purposes.
   * @param dtf DocTypeForm to be cloned.
   * @return Cloned DocTypeForm
   */
  public static DocTypeForm clone(DocTypeForm dtf) {
    return DocTypeFormTest.adaptToViewValues(DocTypeForm.toForm(dtf.toType()));
  }
}
