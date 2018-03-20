package de.fzj.peerpub.doc.publisher;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import de.fzj.peerpub.utils.Random;
import de.fzj.peerpub.doc.doctype.*;
import de.fzj.peerpub.doc.attribute.*;

import org.bson.Document;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

@Tag("fast")
public class PublisherTest {
  Publisher pub;
  List<DocType> docTypeSupported;

  @BeforeEach void setup() {
    this.docTypeSupported = DocTypeTest.generate(4);
    this.pub = generate(docTypeSupported);
  }

  @Test
  @DisplayName("model.doc.Publisher putAttribute() should fail if no default for mandatory attribute")
  void failNoDefaultForMandatory() {
    DocType dt = docTypeSupported.get(0);
    Attribute pubAttr = AttributeTest.generate();
    assertThrows(IllegalArgumentException.class,
                 () -> {this.pub.putAttribute(dt, pubAttr, true, null);});
    assertThrows(IllegalArgumentException.class,
                 () -> {this.pub.putAttribute(dt, pubAttr, true, "");});
  }

  @Test
  @DisplayName("model.doc.Publisher putAttribute() should fail for unsupported document type")
  void failUnsupportedDocType() {
    DocType dt = DocTypeTest.generate();
    Attribute pubAttr = AttributeTest.generate();
    assertThrows(IllegalArgumentException.class,
                 () -> {this.pub.putAttribute(dt, pubAttr, false, null);});
  }

  @Test
  @DisplayName("model.doc.Publisher putAttribute() should silently replace attribute settings.")
  void replaceSchemaEntriesForDups() {
    Attribute a = AttributeTest.generate();
    DocType dt = docTypeSupported.get(0);

    this.pub.putAttribute(dt, a, true, "defaultA");
    assertTrue("defaultA".equals(this.pub.getAttributes().get(dt.getName()).get(a.getName()).get(Publisher.DEFAULT)));
    assertTrue(((Boolean)this.pub.getAttributes().get(dt.getName()).get(a.getName()).get(Publisher.MANDATORY)));

    // a is inserted as mandatory, now lets make it optional
    // and replace its default value
    this.pub.putAttribute(dt, a, false, "test");
    assertEquals("test", this.pub.getAttributes().get(dt.getName()).get(a.getName()).get(Publisher.DEFAULT));
    assertFalse(((Boolean)this.pub.getAttributes().get(dt.getName()).get(a.getName()).get(Publisher.MANDATORY)));
  }

  @Test
  @DisplayName("model.doc.Publisher getAttributes(doctype) should return all attributes")
  void getAttributes() {
    List<DocType> dts = DocTypeTest.generate(2);
    Publisher pubLocal = PublisherTest.generate(dts);

    // test if we can get all attributes correctly
    Set<String> pubAttrsDt1 = pubLocal.getAttributes(dts.get(0));
    assertEquals(pubAttrsDt1, pubLocal.getAttributes().get(dts.get(0).getName()).keySet());
}

@Test
@DisplayName("model.doc.Publisher getAttributes(doctype, mand, opt) should return attributes dependant on selection criteria")
void getAttributesSelective() {
    // generate a defined test data set first
    List<DocType> dts = DocTypeTest.generate(2);
    DocType dt = dts.get(0);
    Publisher pubLocal = PublisherTest.generate(dts);
    Attribute aMand = AttributeTest.generate();
    Attribute aOpt = AttributeTest.generate();
    Attribute aOptND = AttributeTest.generate();

    // remove and readd doctype + attributes
    pubLocal.removeSupDocType(dt);
    pubLocal.addSupDocType(dt);
    pubLocal.putAttribute(dt, aMand, true, "test");
    pubLocal.putAttribute(dt, aOpt, false, "test");
    pubLocal.putAttribute(dt, aOptND, false, null);

    // only mandatory
    assertEquals(pubLocal.getAttributes(dt, true, false), new HashSet(Arrays.asList(aMand.getName())));
    // only optional
    assertEquals(pubLocal.getAttributes(dt, false, true), new HashSet(Arrays.asList(aOpt.getName(), aOptND.getName())));
    // both
    assertEquals(pubLocal.getAttributes(dt, true, true), new HashSet(Arrays.asList(aMand.getName(), aOpt.getName(), aOptND.getName())));
  }

  @Test
  @DisplayName("model.doc.Publisher isMandatory() and isOptional() should return correct status")
  void isMandatoryOptional() {
    // generate a defined test data set first
    List<DocType> dts = DocTypeTest.generate(2);
    DocType dt = dts.get(0);
    Publisher pubLocal = PublisherTest.generate(dts);
    Attribute aMand = AttributeTest.generate();
    Attribute aOpt = AttributeTest.generate();

    pubLocal.putAttribute(dt, aMand, true, "test");
    pubLocal.putAttribute(dt, aOpt, false, "test");

    assertTrue(pubLocal.isMandatory(dt, aMand));
    assertFalse(pubLocal.isMandatory(dt, aOpt));
  }

  @Test
  @DisplayName("model.doc.Publisher getDefault() should return correct default values")
  void getDefault() {
    // generate a defined test data set first
    List<DocType> dts = DocTypeTest.generate(2);
    DocType dt = dts.get(0);
    Publisher pubLocal = PublisherTest.generate(dts);
    Attribute aMand = AttributeTest.generate();
    Attribute aOpt = AttributeTest.generate();
    Attribute aOptND = AttributeTest.generate();

    pubLocal.putAttribute(dt, aMand, true, "test");
    pubLocal.putAttribute(dt, aOpt, false, "test");
    pubLocal.putAttribute(dt, aOptND, false, null);

    assertEquals("test", pubLocal.getDefault(dt, aMand));
    assertEquals("test", pubLocal.getDefault(dt, aOpt));
    assertEquals("", pubLocal.getDefault(dt, aOptND));
  }

  @Test
  @DisplayName("model.doc.Publisher addAlias(),removeAlias(),removeAllAlias() testing")
  void aliasTesting() {
    List<DocType> dts = DocTypeTest.generate(2);
    Publisher pubLocal = PublisherTest.generate(dts);

    pubLocal.addAlias("test");
    assertTrue(pubLocal.getAliases().contains("test"));
    pubLocal.removeAlias("test");
    assertFalse(pubLocal.getAliases().contains("test"));
    pubLocal.removeAllAlias();
    assertEquals(0, pubLocal.getAliases().size());
    pubLocal.addAlias("test");
    pubLocal.addAlias("test2");
    assertEquals(2, pubLocal.getAliases().size());
    pubLocal.removeAllAlias();
    assertEquals(0, pubLocal.getAliases().size());
  }

  public static List<Publisher> generate(int num, List<DocType> dtList) {
    if(dtList == null || dtList.size() < 2)
      throw new IllegalArgumentException("Please provide a list of at least two document types.");

    // generate 5 all random attributes
    // those are the same for all publishers, so create them before.
    List<Attribute> attrs = AttributeTest.generate(5);

    List<Publisher> pubs = new ArrayList<Publisher>();
    for(int i = 0; i < num; i++) {
      // generate a random list of attributes to use for every publisher
      Collections.shuffle(attrs);
      // use 2 attributes out of the 5 generated
      List<Attribute> as = attrs.subList(0, 2);

      // generate a random list of document types to use for every publisher
      Collections.shuffle(dtList);
      // use max 2 attributes, but if there are less available, use this amount.
      List<DocType> dts = dtList.subList(0, 2);
      List<String> supported = new ArrayList<String>();
      for(DocType d : dts)
        supported.add(d.getName());

      Map<String,Map<String,Document>> attribs = new HashMap<String,Map<String,Document>>();
      for(String dt : supported) {
        // generate the schema map
        Map<String,Document> schema = new HashMap<String,Document>();

        for(Attribute a : as) {
          Document d = new Document();
          d.put("default",Random.getString(6));
          d.put("mandatory",Random.getBool());

          schema.put(a.getName(), d);
        }
        attribs.put(dt,schema);
      }

      String dspN = Random.getString(10);
      List<String> aliases = new ArrayList<String>(Arrays.asList(Random.getString(10)));

      // generate the doctype
      Publisher p = new Publisher(Random.getString(10), Random.getBool(), Random.getBool(), supported, attribs, dspN, aliases);
      pubs.add(p);
    }
    return pubs;
  }
  public static Publisher generate(List<DocType> dtList) {
    return generate(1, dtList).get(0);
  }
}
