package de.fzj.peerpub.doc.attribute;

import java.util.List;
import java.util.Optional;

import de.fzj.peerpub.doc.attribute.Attribute;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AttributeRepository extends MongoRepository<Attribute, String> {

    public Optional<Attribute> findByName(String name);
    public List<Attribute> findByLabel(String label);
    public List<Attribute> findByKey(String key);

}
