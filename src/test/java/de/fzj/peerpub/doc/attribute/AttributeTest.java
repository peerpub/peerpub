package de.fzj.peerpub.doc.attribute;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.List;
import java.util.ArrayList;
import de.fzj.peerpub.utils.Random;

import de.fzj.peerpub.doc.attribute.*;

@Tag("fast")
public class AttributeTest {

  @Test
  @DisplayName("model.doc.Attribute validation test")
  @Disabled("Future feature")
  void validationTest(TestInfo testInfo) {}

  public static List<Attribute> generate(int num) {
    ArrayList attrs = new ArrayList<Attribute>();
    for(int i = 0; i < num; i++) {
      String rnd = Random.getString(6);
      Attribute a = new Attribute(rnd,rnd.toLowerCase(),rnd.toUpperCase(),Random.getString(10),"{}");
      attrs.add(a);
    }
    return attrs;
  }
  public static Attribute generate() {
    return generate(1).get(0);
  }
}
