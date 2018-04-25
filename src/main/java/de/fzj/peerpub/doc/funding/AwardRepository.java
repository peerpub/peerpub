/*
 * Copyright (c) 2018 "Forschungszentrum Jülich GmbH". All rights reserved.
 *
 * This file is part of PeerPub, licensed under the GNU Affero General Public
 * License v3 or later. Refer to the included LICENSE file for full text of license.
 */

package de.fzj.peerpub.doc.funding;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * MongoDB DAO abstraction interface for Awards
 */
public interface AwardRepository extends MongoRepository<Award, String> {
  // This is intentionally left blank: currently it is unlikely to
  // have a lot of entries within the database (scope of PeerPub is
  // a single institute). Thus any complicated searches are
  // way to complicated, need testing, etc. Instead use a simple select
  // for now...
}
