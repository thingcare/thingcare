package io.thingcare.modules.asset;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.thingcare.api.asset.model.AssetDto;

/**
 * Service Implementation for managing Asset.
 */
@Service
public class AssetService {

	private final Logger log = LoggerFactory.getLogger(AssetService.class);

	@Inject
	private AssetRepository repository;
	@Inject
	private AssetFactory factory;

	@Inject
	private AssetMapper assetMapper;

	/**
	 * Save a asset.
	 *
	 * @param assetDto
	 *            the entity to save
	 * @return the persisted entity
	 */
	public AssetDto save(AssetDto assetDto) {
		log.debug("Request to save Asset : {}", assetDto);
		Asset asset = factory.create(assetDto);
		asset = repository.save(asset);
		AssetDto result = assetMapper.asDto(asset);
		return result;
	}

	/**
	 * Get all the assets.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	public Page<Asset> findAll(Pageable pageable) {
		log.debug("Request to get all Assets");
		Page<Asset> result = repository.findAll(pageable);
		return result;
	}

	/**
	 * Get one asset by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	public AssetDto findOne(String id) {
		log.debug("Request to get Asset : {}", id);
		Asset asset = repository.findOne(id);
		AssetDto assetDto = assetMapper.asDto(asset);
		return assetDto;
	}

	/**
	 * Delete the asset by id.
	 *
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String id) {
		log.debug("Request to delete Asset : {}", id);
		repository.delete(id);
	}
}
