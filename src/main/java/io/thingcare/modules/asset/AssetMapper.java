package io.thingcare.modules.asset;

import java.util.List;

import org.mapstruct.Mapper;

import io.thingcare.api.asset.AssetDto;

/**
 * Mapper for the entity Asset and its DTO AssetDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface AssetMapper {

	AssetDto asDto(Asset asset);

	List<AssetDto> asDtos(List<Asset> assets);

}
