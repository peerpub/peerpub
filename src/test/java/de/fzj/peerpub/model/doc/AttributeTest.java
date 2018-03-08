package de.fzj.peerpub.model.doc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

@Tag("fast")
class AttributeTest {

  @Test
  @DisplayName("model.doc.Attribute validation test")
  @Disabled("Future feature")
  void validationTest(TestInfo testInfo) {}

  @Test
  void compareNameOnly() {
    Attribute a = new Attribute("a","a","A","An a desc...","{}");
    Attribute b = new Attribute("b","b","B","A b desc...","{}");
    Attribute c = new Attribute("a","c","C","A c desc...","{}");
    assertNotEquals(a,b);
    assertEquals(a,c);
  }
}
