package de.fzj.peerpub.doc.doctype;

import static org.junit.jupiter.api.Assertions.*;

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import de.fzj.peerpub.doc.attribute.*;

import java.util.*;

import de.fzj.peerpub.utils.Random;

@Tag("fast")
public class DocTypeTest {
 
  @Test
  @DisplayName("getAttributes() should return all attributes")
  void getAttributes() {
    // given
    List<Attribute> attrs = AttributeTest.generate(1);
    DocType dt = DocTypeTest.generate(Collections.emptyList());
  
    // (when)
    Set<String> attrSet = new HashSet<>();
    for (Attribute a : attrs) {
      attrSet.add(a.getName());
      dt.putAttribute(a, Random.getBool(), "");
    }
    
    // then
    // test if we can get all attributes correctly
    assertEquals(attrSet, dt.getAttributes());
  }
  
  @Test
  @DisplayName("getAttributes(mand, opt) should return attributes dependant on selection criteria")
  void getAttributesSelective() {
    // given
    DocType dt = DocTypeTest.generate(Collections.emptyList());
    Attribute aMand = AttributeTest.generate();
    Attribute aMandND = AttributeTest.generate();
    Attribute aOpt = AttributeTest.generate();
    Attribute aOptND = AttributeTest.generate();
  
    // when
    dt.putAttribute(aMand, true, "test");
    dt.putAttribute(aMandND, true, "");
    dt.putAttribute(aOpt, false, "test");
    dt.putAttribute(aOptND, false, null);
  
    // then
    // only mandatory
    assertEquals(dt.getAttributes(true, false), new HashSet(Arrays.asList(aMand.getName(), aMandND.getName())));
    // only optional
    assertEquals(dt.getAttributes(false, true), new HashSet(Arrays.asList(aOpt.getName(), aOptND.getName())));
    // both
    assertEquals(dt.getAttributes(true, true), new HashSet(Arrays.asList(aMand.getName(), aMandND.getName(), aOpt.getName(), aOptND.getName())));
  }
  
  @Test
  @DisplayName("isMandatory() and isOptional() should return correct status")
  void isMandatoryOptional() {
    // given
    DocType dt = DocTypeTest.generate(Collections.emptyList());
    Attribute aMand = AttributeTest.generate();
    Attribute aOpt = AttributeTest.generate();
    
    // when
    dt.putAttribute(aMand, true, "test");
    dt.putAttribute(aOpt, false, "test");
    
    // then
    assertTrue(dt.isMandatory(aMand));
    assertFalse(dt.isMandatory(aOpt));
  }
  
  @Test
  @DisplayName("isMandatory() throwing exceptions if no such attributes")
  void isMandatoryAttrNotFound() throws Exception {
    // given
    DocType dt = DocTypeTest.generate(Collections.emptyList());
    Attribute aMand = AttributeTest.generate();
    Attribute aOpt = AttributeTest.generate();
    dt.putAttribute(aMand, true, "test");
    dt.putAttribute(aOpt, false, "test");
  
    // when && then
    assertThrows(IllegalArgumentException.class, ()->{ dt.isMandatory(Random.getString(20)); });
  }
  
  @Test
  @DisplayName("getDefault() should return correct default values")
  void getDefault() {
    // given
    DocType dt = DocTypeTest.generate(Collections.emptyList());
    Attribute aMand = AttributeTest.generate();
    Attribute aMandND = AttributeTest.generate();
    Attribute aOpt = AttributeTest.generate();
    Attribute aOptND = AttributeTest.generate();
    
    // when
    dt.putAttribute(aMand, true, "test");
    dt.putAttribute(aMandND, true, "");
    dt.putAttribute(aOpt, false, "test");
    dt.putAttribute(aOptND, false, null);
    
    // then
    assertEquals("test", dt.getDefault(aMand));
    assertEquals("test", dt.getDefault(aOpt));
    assertEquals("", dt.getDefault(aMandND));
    assertEquals("", dt.getDefault(aOptND));
  }
  
  @Test
  @DisplayName("putAttribute() should silently replace attribute settings.")
  void replaceSchemaEntriesForDups() {
    // given
    Attribute a = AttributeTest.generate();
    DocType dt = DocTypeTest.generate();
    dt.putAttribute(a, true, "defaultA");
    
    // when:
    // a is inserted as mandatory, now lets make it optional
    // and replace its default value
    dt.putAttribute(a, false, "test");
    assertEquals("test", dt.getDefault(a));
    assertFalse(dt.isMandatory(a));
  }
  
  public static List<DocType> generate(int num, List<Attribute> attrs) {
    List<DocType> dtl = new ArrayList<>();
    for(int i = 0; i < num; i++) {
      Map<String, Document> attributes = new HashMap<>();
      
      if (attrs.size() > 0) {
        // generate a random list of attributes to use for every doctype
        Collections.shuffle(attrs);
        List<Attribute> as = attrs.subList(0, Random.getInt(attrs.size()));
  
        // generate random values for each attribute
        for (int j = 0; j < as.size(); j++) {
          Document d = new Document();
          if (Random.getBool())
            d.put("default", Random.getString(10));
          d.put("mandatory", Random.getBool());
          attributes.put(as.get(j).getName(), d);
        }
      }
      // generate the doctype
      DocType dt = new DocType(Random.getString(6),
                               Random.getBool(), Random.getBool(),
                               attributes, Random.getString(10));
      dtl.add(dt);
    }
    return dtl;
  }
  public static List<DocType> generate(int num) {
    // generate at least 2, at max 20 random attributes
    List<Attribute> attrs = AttributeTest.generate(Random.getInt(num%19)+2);
    return generate(num, attrs);
  }
  public static DocType generate(List<Attribute> attrs) {
    return generate(1, attrs).get(0);
  }
  public static DocType generate() {
    return generate(1).get(0);
  }
}
