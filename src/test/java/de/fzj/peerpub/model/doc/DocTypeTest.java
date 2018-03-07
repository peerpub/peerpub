package de.fzj.peerpub.model.doc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.fzj.peerpub.model.doc.DocType;
import de.fzj.peerpub.model.doc.Attribute;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

@Tag("fast")
class DocTypeTest {

  static DocType TESTTYPE;

  @BeforeAll static void setUp(TestInfo ti) {
    Attribute attr = new Attribute("12345","test","Test Label","test","A test is...","{}");
    Map<Attribute,Boolean> attribs = new HashMap<Attribute,Boolean>();
    attribs.put(attr,true);
    Map<Attribute,String> defs = new HashMap<Attribute,String>();
    defs.put(attr,"value");
    TESTTYPE = new DocType("123","test",true,true,attribs,defs);
  }

  @Test
  @DisplayName("model.doc.DocType addAttribute() should fail if no default for mandatory attribute")
  void failAddAttribute_NoDefaultForMandatory() {
    Attribute attr = new Attribute("123456","testA","Test Label","test","A test is...","{}");
    Assertions.assertThrows(IllegalArgumentException.class,
                            () -> {TESTTYPE.addAttribute(attr, true, null);});
    Assertions.assertThrows(IllegalArgumentException.class,
                            () -> {TESTTYPE.addAttribute(attr, true, "");});
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
}
