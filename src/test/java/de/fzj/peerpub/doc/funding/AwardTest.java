package de.fzj.peerpub.doc.funding;

import de.fzj.peerpub.utils.Random;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

@Tag("fast")
public class AwardTest {
  public static List<Award> generate(int num) {
    ArrayList awards = new ArrayList<Award>();
    for(int i = 0; i < num; i++) {
      String id = Random.getString(8);
      String funderName = Random.getString(16);
      String awardNumber = Random.getString(16);
      String funderIdentifier = "http://dx.doi.org/10.13039/"+Random.getInt();
      Award a = new Award(id, funderName, awardNumber, funderIdentifier);
      awards.add(a);
    }
    return awards;
  }
  public static Award generate() {
    return generate(1).get(0);
  }
}
