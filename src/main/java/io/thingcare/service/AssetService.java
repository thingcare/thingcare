package io.thingcare.service;

import io.thingcare.domain.Asset;
import io.thingcare.repository.AssetRepository;
import io.thingcare.web.rest.dto.AssetDTO;
import io.thingcare.web.rest.mapper.AssetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Asset.
 */
@Service
public class AssetService {

    private final Logger log = LoggerFactory.getLogger(AssetService.class);
    
    @Inject
    private AssetRepository assetRepository;
    
    @Inject
    private AssetMapper assetMapper;
    
    /**
     * Save a asset.
     * 
     * @param assetDTO the entity to save
     * @return the persisted entity
     */
    public AssetDTO save(AssetDTO assetDTO) {
        log.debug("Request to save Asset : {}", assetDTO);
        Asset asset = assetMapper.assetDTOToAsset(assetDTO);
        asset = assetRepository.save(asset);
        AssetDTO result = assetMapper.assetToAssetDTO(asset);
        return result;
    }

    /**
     *  Get all the assets.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    public Page<Asset> findAll(Pageable pageable) {
        log.debug("Request to get all Assets");
        Page<Asset> result = assetRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one asset by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    public AssetDTO findOne(String id) {
        log.debug("Request to get Asset : {}", id);
        Asset asset = assetRepository.findOne(id);
        AssetDTO assetDTO = assetMapper.assetToAssetDTO(asset);
        return assetDTO;
    }

    /**
     *  Delete the  asset by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete Asset : {}", id);
        assetRepository.delete(id);
    }
}
