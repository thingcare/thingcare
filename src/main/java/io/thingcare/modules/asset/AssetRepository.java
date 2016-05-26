package io.thingcare.modules.asset;

import io.thingcare.modules.asset.Asset;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Asset entity.
 */
@SuppressWarnings("unused")
public interface AssetRepository extends MongoRepository<Asset,String> {

}
