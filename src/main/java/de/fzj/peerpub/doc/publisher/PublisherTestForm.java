package de.fzj.peerpub.doc.publisher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublisherTestForm {
  private String name;
  private List<String> supports;
  private Map<String, Map<String, org.bson.Document>> map;
  
  public static final String DEFAULT = "default";
  public static final String MANDATORY = "mandatory";
}