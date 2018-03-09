package de.fzj.peerpub.model.doc;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.fzj.peerpub.model.doc.DocType;
import de.fzj.peerpub.model.doc.Attribute;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

@Tag("fast")
class DocTypeTest {
  DocType dt;
  Attribute a;

  @BeforeEach void setup() {
    this.a = new Attribute("a","a","A","An a desc...","{}");
    Attribute b = new Attribute("b","b","B","A b desc...","{}");

    Map<String,Boolean> man = new HashMap<String,Boolean>();
    man.put("a",true);
    man.put("b",false);

    Map<String,String> defs = new HashMap<String,String>();
    defs.put("a","defaultA");
    defs.put("b","defaultB");

    this.dt = new DocType("test",true,false,Arrays.asList(a,b),man,defs);
  }

  @Test
  @DisplayName("model.doc.DocType putAttribute() should fail if no default for mandatory attribute")
  void failNoDefaultForMandatory() {
    Attribute attr = new Attribute("c","c","C","A c is...","{}");
    assertThrows(IllegalArgumentException.class,
                            () -> {dt.putAttribute(attr, true, null);});
    assertThrows(IllegalArgumentException.class,
                            () -> {dt.putAttribute(attr, true, "");});
  }

  @Test
  void replaceSchemaEntriesForDups() {
    assertTrue("defaultA".equals(dt.getDefaults().get(this.a.getName())));
    assertTrue(dt.getMandatory().get(this.a.getName()));

    // a is inserted as mandatory, now lets make it optional
    // and replace its default value
    dt.putAttribute(this.a, false, "test");

    assertTrue("test".equals(dt.getDefaults().get(this.a.getName())));
    assertFalse(dt.getMandatory().get(this.a.getName()));
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
