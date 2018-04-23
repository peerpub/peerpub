package de.fzj.peerpub.doc.funding;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@ActiveProfiles("test")
@Tag("integration-embedded")
public class AwardRepositoryIT {

    @Autowired
    private AwardRepository awardRepository;

    @BeforeEach
    void emptyDatabase() {
      assertNotNull(awardRepository);
      awardRepository.deleteAll();
    }

    @Test
    void saveFindCompare() {
      Award test = AwardTest.generate();
      awardRepository.save(test);
      Optional<Award> find = awardRepository.findById(test.getId());
      assertTrue(find.isPresent());
      assertEquals(test, find.get());
    }
}
