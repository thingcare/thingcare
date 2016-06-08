package io.thingcare.modules.security.authority;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthorityRepository extends MongoRepository<Authority, String> {
}
