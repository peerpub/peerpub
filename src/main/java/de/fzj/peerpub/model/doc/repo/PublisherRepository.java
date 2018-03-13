package de.fzj.peerpub.model.doc.repo;

import java.util.Set;

import de.fzj.peerpub.model.doc.Publisher;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;

public interface PublisherRepository extends MongoRepository<Publisher, String> {
  public Publisher findByName(String name);
  public Set<Publisher> findBySystem(Boolean system);
  public Set<Publisher> findByReviewing(Boolean reviewing);
  public Set<Publisher> findBySupports(String doctype);

  public Set<Publisher> findAllBy(TextCriteria criteria);
  @Query("{ $or: [ { displayName : { $regex: '^?0' } }, { aliases : { $regex: '^?0' } } ] }")
  public Set<Publisher> findByDisplayStartsWith(String search);
}
