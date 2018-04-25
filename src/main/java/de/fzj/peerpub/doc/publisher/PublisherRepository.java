/*
 * Copyright (c) 2018 "Forschungszentrum Jülich GmbH". All rights reserved.
 *
 * This file is part of PeerPub, licensed under the GNU Affero General Public
 * License v3 or later. Refer to the included LICENSE file for full text of license.
 */

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
