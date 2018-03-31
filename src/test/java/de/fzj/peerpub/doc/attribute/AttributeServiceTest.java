package de.fzj.peerpub.doc.attribute;

import name.falgout.jeffrey.testing.junit5.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Tag("fast")
public class AttributeServiceTest {
  
  @Mock AttributeRepository attributeRepository;
  
  AttributeService attributeService;
  
  @BeforeEach
  void setup() {
    this.attributeService = new AttributeService(attributeRepository);
  }
  
  @Test
  void getNameBasedMap() {
    // given
    List<Attribute> attributes = AttributeTest.generate(5);
    given(attributeRepository.findAll()).willReturn(attributes);
    
    // when
    Map<String, Attribute> map = attributeService.getNameBasedMap();
    
    // then
    assertEquals(attributes.size(), map.size());
    for(Attribute a : attributes) {
      assertTrue(map.containsKey(a.getName()));
      assertTrue(map.containsValue(a));
    }
  }
  
}
