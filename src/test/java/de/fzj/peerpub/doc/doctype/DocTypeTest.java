package de.fzj.peerpub.doc.doctype;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import de.fzj.peerpub.doc.attribute.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import de.fzj.peerpub.utils.Random;

@Tag("fast")
public class DocTypeTest {
  DocType dt;

  @BeforeEach void setup() {
    this.dt = generate();
  }

  @Test
  @DisplayName("model.doc.DocType putAttribute() should fail if no default for mandatory attribute")
  void failNoDefaultForMandatory() {
    Attribute attr = AttributeTest.generate();
    assertThrows(IllegalArgumentException.class,
                 () -> {dt.putAttribute(attr, true, null);});
    assertThrows(IllegalArgumentException.class,
                 () -> {dt.putAttribute(attr, true, "");});
  }

  @Test
  void replaceSchemaEntriesForDups() {
    Attribute a = AttributeTest.generate();

    this.dt.putAttribute(a, true, "defaultA");
    assertTrue("defaultA".equals(this.dt.getDefaults().get(a.getName())));
    assertTrue(this.dt.getMandatory().get(a.getName()));

    // a is inserted as mandatory, now lets make it optional
    // and replace its default value
    this.dt.putAttribute(a, false, "test");
    assertTrue("test".equals(this.dt.getDefaults().get(a.getName())));
    assertFalse(this.dt.getMandatory().get(a.getName()));
  }

  /*
  @ParameterizedTest(name = "{index}: {0}")
  @Tag("exception-tests")
  @DisplayName("model.doc.DocType invalid name should throw an exception")
  @ValueSource(strings = { "awd2921", "##-aaa" })
  void invalidNameException(String s) {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      DocType.checkName(s);
    });
  }

  @ParameterizedTest(name = "{index}: {0}")
  @Tag("exception-tests")
  @DisplayName("model.doc.DocType valid name should return true")
  @ValueSource(strings = { "awd-as", "AAA", "AA_as" })
  void validNames(String s) {
    Assertions.assertTrue(DocType.checkName(s));
  }

  @Test
  @Tag("exception-tests")
  @DisplayName("model.doc.DocType setName() should throw an exception on system types")
  void setNameSystemTypeException() {
    DocType t = new DocType("test", false, true);
    Assertions.assertThrows(AssertionError.class, () -> {
      t.setName("tset");
    });
  }

  @Test
  @Tag("exception-tests")
  @DisplayName("model.doc.DocType setMultidoc() should throw an exception on system types")
  void setMultidocSystemTypeException() {
    DocType t = new DocType("test", false, true);
    Assertions.assertThrows(AssertionError.class, () -> {
      t.setMultidoc(true);
    });
  }
  */

  //TODO: test setName exception when system type
  //TODO: test setMultidoc exception when system type
  //TODO: test getAttributes with different inclusions of mandatory/optional attributes

  public static List<DocType> generate(int num) {
    // generate at least 2, at max 20 random attributes
    List<Attribute> attrs = AttributeTest.generate(Random.getInt(num%19)+2);

    List<DocType> dtl = new ArrayList<DocType>();
    for(int i = 0; i < num; i++) {
      // generate a random list of attributes to use for every doctype
      Collections.shuffle(attrs);
      List<Attribute> as = attrs.subList(0, Random.getInt(attrs.size()));

      // generate random values for each attribute
      Map<String,Boolean> man = new HashMap<String,Boolean>();
      Map<String,String> defs = new HashMap<String,String>();
      for(int j = 0; j < as.size(); j++) {
        man.put(as.get(j).getName(), Random.getBool());
        defs.put(as.get(j).getName(), Random.getString(10));
      }

      // generate the doctype
      DocType d = new DocType(Random.getString(6), Random.getBool(), Random.getBool(), as, man, defs);
      dtl.add(d);
    }
    return dtl;
  }
  public static DocType generate() {
    return generate(1).get(0);
  }
}
