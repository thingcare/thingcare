package io.thingcare.modules.asset;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.thingcare.api.asset.model.AssetDto;
import io.thingcare.api.web.util.HeaderUtil;
import io.thingcare.api.web.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class AssetResource {

	private final Logger log = LoggerFactory.getLogger(AssetResource.class);

	@Inject
	private AssetService assetService;

	@Inject
	private AssetMapper assetMapper;

	/**
	 * POST /assets : Create a new asset.
	 *
	 * @param assetDto
	 *            the assetDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new assetDTO, or with status 400 (Bad
	 *         Request) if the asset has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/assets", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AssetDto> createAsset(@Valid @RequestBody AssetDto assetDto) throws URISyntaxException {
		log.debug("REST request to save Asset : {}", assetDto);
		if (assetDto.getId() != null) {
			return ResponseEntity	.badRequest()
									.headers(HeaderUtil.createFailureAlert("asset", "idexists",
											"A new asset cannot already have an ID"))
									.body(null);
		}
		AssetDto result = assetService.save(assetDto);
		return ResponseEntity	.created(new URI("/api/assets/" + result.getId()))
								.headers(HeaderUtil.createEntityCreationAlert("asset", result	.getId()
																								.toString()))
								.body(result);
	}

	/**
	 * PUT /assets : Updates an existing asset.
	 *
	 * @param assetDto
	 *            the assetDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated assetDTO,
	 *         or with status 400 (Bad Request) if the assetDTO is not valid,
	 *         or with status 500 (Internal Server Error) if the assetDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/assets", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AssetDto> updateAsset(@Valid @RequestBody AssetDto assetDto) throws URISyntaxException {
		log.debug("REST request to update Asset : {}", assetDto);
		if (assetDto.getId() == null) {
			return createAsset(assetDto);
		}
		AssetDto result = assetService.save(assetDto);
		return ResponseEntity	.ok()
								.headers(HeaderUtil.createEntityUpdateAlert("asset", assetDto	.getId()
																								.toString()))
								.body(result);
	}

	/**
	 * GET /assets : get all the assets.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of assets in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/assets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<AssetDto>> getAllAssets(Pageable pageable) throws URISyntaxException {
		log.debug("REST request to get a page of Assets");
		Page<Asset> page = assetService.findAll(pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/assets");
		return new ResponseEntity<>(assetMapper.asDtos(page.getContent()), headers, HttpStatus.OK);
	}

	/**
	 * GET /assets/:id : get the "id" asset.
	 *
	 * @param id
	 *            the id of the assetDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the assetDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/assets/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AssetDto> getAsset(@PathVariable String id) {
		log.debug("REST request to get Asset : {}", id);
		AssetDto assetDto = assetService.findOne(id);
		return Optional	.ofNullable(assetDto)
						.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
						.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /assets/:id : delete the "id" asset.
	 *
	 * @param id
	 *            the id of the assetDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/assets/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteAsset(@PathVariable String id) {
		log.debug("REST request to delete Asset : {}", id);
		assetService.delete(id);
		return ResponseEntity	.ok()
								.headers(HeaderUtil.createEntityDeletionAlert("asset", id.toString()))
								.build();
	}

}
