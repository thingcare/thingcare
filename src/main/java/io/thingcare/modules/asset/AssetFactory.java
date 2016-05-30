package io.thingcare.modules.asset;

import org.springframework.stereotype.Component;

import io.thingcare.api.asset.model.AssetDto;

@Component
public class AssetFactory {
	public Asset create(AssetDto assetDto) {
		Asset asset = new Asset();
		asset.setName(assetDto.getName());
		return asset;
	}
}
