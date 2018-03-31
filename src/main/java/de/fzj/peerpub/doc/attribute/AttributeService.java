package de.fzj.peerpub.doc.attribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Metadata attribute DTO
 */
@Service
public class AttributeService {
  
  /**
   * Metadata attribute DAO
   */
  private AttributeRepository attributeRepository;
  
  /**
   * Constructor with explicit dependency
   */
  public AttributeService(@Autowired AttributeRepository attributeRepository) {
    this.attributeRepository = attributeRepository;
  }
  
  /**
   * Get a map of all attributes that associates attribute names (_id!) with their attribute objects.
   * @return map
   */
  public Map<String, Attribute> getNameBasedMap() {
    Map<String, Attribute> map = new HashMap<>();
    List<Attribute> attrs = attributeRepository.findAll();
    
    for (Attribute a : attrs) {
      map.put(a.getName(), a);
    }
    
    return map;
  }
}
