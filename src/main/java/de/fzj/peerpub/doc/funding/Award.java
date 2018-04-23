package de.fzj.peerpub.doc.funding;

import de.fzj.peerpub.doc.validator.Referable;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;

/**
 * Funding award POJO model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Awards")
public class Award {
  
  static final int MIN_SIZE = 2;
  static final int MAX_SIZE = 200;
  
  /**
   * Unique name for this funding award to have definite search results,
   * etc, used as _id for MongoDB
   */
  @Id
  @NonNull
  @Referable
  private String id;
  
  /**
   * Human readable name of the funder / agency / ...
   */
  @Size(min = MIN_SIZE, max = MAX_SIZE)
  private String funderName;
  /**
   * The number or the name assigned to the award/grant/program by the funder
   */
  @NonNull
  @Size(min = MIN_SIZE, max = MAX_SIZE)
  private String awardNumber;
  
  /**
   * A unique DOI from FundRef to identify this funding agency
   */
  @URL(protocol = "http", host = "dx.doi.org", regexp = ".*/10.13039/\\d+$")
  private String funderIdentifier;
  
}
