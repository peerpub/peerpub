package de.fzj.peerpub.doc.publisher;

import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;

/**
 * Publisher Data Access Interface
 */
public interface PublisherRepository extends MongoRepository<Publisher, String> {
  Publisher findByName(String name);
  Set<Publisher> findBySystem(Boolean system);
  Set<Publisher> findByReviewing(Boolean reviewing);
  Set<Publisher> findBySupports(String doctype);

  Set<Publisher> findAllBy(TextCriteria criteria);
  @Query("{ $or: [ { displayName : { $regex: '^?0' } }, { aliases : { $regex: '^?0' } } ] }")
  Set<Publisher> findByDisplayStartsWith(String search);
}
