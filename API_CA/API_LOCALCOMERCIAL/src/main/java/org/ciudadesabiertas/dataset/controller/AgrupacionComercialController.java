/**
 * Copyright 2019 Ayuntamiento de A Coruña, Ayuntamiento de Madrid, Ayuntamiento de Santiago de Compostela, Ayuntamiento de Zaragoza, Entidad Pública Empresarial Red.es
 * 
 * This file is part of the Open Cities API, developed within the "Ciudades Abiertas" project (https://ciudadesabiertas.es/).
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package org.ciudadesabiertas.dataset.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.servlet.http.HttpServletRequest;

import org.ciudadesabiertas.controller.CiudadesAbiertasController;
import org.ciudadesabiertas.controller.GenericController;
import org.ciudadesabiertas.dataset.model.AgrupacionComercial;
import org.ciudadesabiertas.dataset.model.LocalComercial;
import org.ciudadesabiertas.dataset.utils.AgrupacionComercialResult;
import org.ciudadesabiertas.dataset.utils.AgrupacionComercialSearch;
import org.ciudadesabiertas.dataset.utils.LocalComercialConstants;
import org.ciudadesabiertas.dataset.utils.LocalComercialSearch;
import org.ciudadesabiertas.service.DatasetService;
import org.ciudadesabiertas.utils.Constants;
import org.ciudadesabiertas.utils.DistinctSearch;
import org.ciudadesabiertas.utils.ExceptionUtil;
import org.ciudadesabiertas.utils.ObjectResult;
import org.ciudadesabiertas.utils.RequestType;
import org.ciudadesabiertas.utils.Result;
import org.ciudadesabiertas.utils.ResultError;
import org.ciudadesabiertas.utils.SecurityURL;
import org.ciudadesabiertas.utils.SwaggerConstants;
import org.ciudadesabiertas.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.github.tennaito.rsql.jpa.JpaCriteriaQueryVisitor;

import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Juan Carlos Ballesteros (Localidata)
 * @author Carlos Martínez de la Casa (Localidata)
 * @author Oscar Corcho (UPM, Localidata)
 *
 */
@SuppressWarnings("rawtypes")
@RestController
@Api(value = "AgrupacionComercial", description = "Conjunto de operaciones sobre agrupaciones comerciales del conjunto de datos Local Comercial"+SwaggerConstants.VOCABULARIO_A_HREF+LocalComercialConstants.agrupacionComercialVocabURL+SwaggerConstants.VOCABULARIO_A_HREF_END, tags = { "Local Comercial - Agrupación Comercial" })
public class AgrupacionComercialController extends GenericController implements CiudadesAbiertasController
{
	public static final String LIST = "/local-comercial/agrupacion-comercial";

	public static final String SEARCH_DISTINCT = LIST + "/distinct";

	public static final String RECORD = LIST+"/{id}";

	public static final String TRANSFORM = LIST+"/transform";

	public static final String ADD = LIST;
	public static final String UPDATE = RECORD;
	public static final String DELETE = RECORD;

	public static final String MODEL_VIEW_LIST = "localComercial/agrupacionComercialList";
	public static final String MODEL_VIEW_ID = "localComercial/agrupacionComercialId";

	private static List<RequestType> listRequestType = new ArrayList<RequestType>();

	// Carga por defecto de las peticiones
	static
	{
		listRequestType.add(new RequestType("AC_LIST", LIST, HttpMethod.GET, Constants.NO_AUTH));
		listRequestType.add(new RequestType("AC_RECORD", RECORD, HttpMethod.GET, Constants.NO_AUTH));
		listRequestType.add(new RequestType("AC_TRANSFORM", TRANSFORM, HttpMethod.POST, Constants.NO_AUTH));

		listRequestType.add(new RequestType("AC_ADD", ADD, HttpMethod.POST, Constants.BASIC_AUTH));
		listRequestType.add(new RequestType("AC_UPDATE", UPDATE, HttpMethod.PUT, Constants.BASIC_AUTH));
		listRequestType.add(new RequestType("AC_DELETE", DELETE, HttpMethod.DELETE, Constants.BASIC_AUTH));

	}

	public static List<String> availableFields = Util.extractPropertiesFromBean(AgrupacionComercial.class);

	private static final Logger log = LoggerFactory.getLogger(AgrupacionComercialController.class);

	@Autowired
	private DatasetService<AgrupacionComercial> service;

	@Autowired
	private DatasetService<LocalComercial> localComercialService;

	@SuppressWarnings("unchecked")
	@ApiOperation(value = SwaggerConstants.BUSQUEDA_DISTINCT, notes = SwaggerConstants.DESCRIPCION_BUSQUEDA_DISTINCT, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_GROUPBY, authorizations = { @Authorization(value = Constants.APIKEY) })
	@ApiResponses({ @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_DISTINCT, response = ObjectResult.class), @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA, response = ResultError.class),
			@ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO, response = ResultError.class), @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE, response = ResultError.class), @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO, response = ResultError.class) })
	@RequestMapping(value = { SEARCH_DISTINCT, VERSION_1 + SEARCH_DISTINCT }, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> distinctSearch(HttpServletRequest request, DistinctSearch search, 
			@RequestParam(value = Constants.PAGE, defaultValue = Constants.defaultPage + "", required = false) 
				@ApiParam(value=SwaggerConstants.PARAM_PAGE) String page,
			@RequestParam(value = Constants.PAGESIZE, defaultValue = Constants.defaultGroupByPageSize + "", required = false) 
				@ApiParam(value=SwaggerConstants.PARAM_PAGESIZE) String pageSize)
	{

		log.info("[distinctSearch][" + SEARCH_DISTINCT + "]");

		log.debug("[parmam][field:" + search.getField() + "] ");

		return distinctSearch(request, search, availableFields, page, pageSize, getKey(), NO_HAY_SRID, SEARCH_DISTINCT, new AgrupacionComercial(), new ObjectResult(), service);

	}

	@ApiIgnore
	@ApiOperation(value = SwaggerConstants.LISTADO_Y_BUSQUEDA_HTML, notes = SwaggerConstants.DESCRIPCION_BUSQUEDA, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_HTML, authorizations = { @Authorization(value = Constants.APIKEY) })
	@ApiResponses({ @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_O_LISTADO), @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA, response = ResultError.class), @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO, response = ResultError.class),
			@ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO, response = ResultError.class) })
	@RequestMapping(value = { LIST + Constants.EXT_HTML, VERSION_1 + LIST + Constants.EXT_HTML }, method = RequestMethod.GET)
	public ModelAndView listHTML(ModelAndView mv, HttpServletRequest request, AgrupacionComercialSearch search, @RequestParam(value = Constants.RSQL_Q, defaultValue = "", required = false) String rsqlQ, @RequestParam(value = Constants.PAGE, defaultValue = "", required = false) String page,
			@RequestParam(value = Constants.PAGESIZE, defaultValue = "", required = false) String pageSize, @RequestParam(value = Constants.SORT, defaultValue = "", required = false) String sort)
	{

		log.info("[listHTML][" + LIST + ".html]");

		String params = "?";
		if (Util.validValue(rsqlQ))
		{
			params += "&q=" + rsqlQ;
		} else if (search != null)
		{
			params += search.toUrlParam();
		}

		return listHTML(mv, request, NO_HAY_SRID, page, pageSize, sort, params, MODEL_VIEW_LIST);
	}

	@ApiIgnore
	@ApiOperation(value = SwaggerConstants.FICHA_HTML, notes = SwaggerConstants.DESCRIPCION_FICHA, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_HTML, authorizations = { @Authorization(value = Constants.APIKEY) })
	@ApiResponses({ @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_FICHA, response = ModelAndView.class), @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA, response = ResultError.class),
			@ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO, response = ResultError.class), @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO, response = ResultError.class) })
	@RequestMapping(value = { RECORD + Constants.EXT_HTML, VERSION_1 + RECORD + Constants.EXT_HTML }, method = RequestMethod.GET)
	public ModelAndView recordHTML(ModelAndView mv, @PathVariable String id, HttpServletRequest request)
	{
		log.info("[recordHTML][" + RECORD + Constants.EXT_HTML + "]");

		return recordHTML(mv, request, NO_HAY_SRID, id, MODEL_VIEW_ID);
	}

	@SuppressWarnings("unchecked")
	@ApiOperation(value = SwaggerConstants.LISTADO_Y_BUSQUEDA, notes = SwaggerConstants.DESCRIPCION_BUSQUEDA, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_FULL_WITHOUT_GEO, authorizations = { @Authorization(value = Constants.APIKEY) })
	@ApiResponses({ @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_O_LISTADO, response = AgrupacionComercialResult.class), @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA, response = ResultError.class),
			@ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO, response = ResultError.class), @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO, response = ResultError.class) })
	@RequestMapping(value = { LIST, VERSION_1 + LIST }, method = { RequestMethod.GET })
	public @ResponseBody ResponseEntity<?> list(HttpServletRequest request, AgrupacionComercialSearch search, 
			@RequestParam(value = Constants.FIELDS, defaultValue = "", required = false) 
				@ApiParam(value=SwaggerConstants.PARAM_FIELDS) String fields,
			@RequestParam(value = Constants.RSQL_Q, defaultValue = "", required = false) 
				@ApiParam(value=SwaggerConstants.PARAM_Q)  String rsqlQ,
			@RequestParam(value = Constants.PAGE, defaultValue = "1", required = false) 
				@ApiParam(value=SwaggerConstants.PARAM_PAGE) String page, 
			@RequestParam(value = Constants.PAGESIZE, defaultValue = "", required = false) 
				@ApiParam(value=SwaggerConstants.PARAM_PAGESIZE) String pageSize, 
			@RequestParam(value = Constants.SORT, defaultValue = Constants.IDENTIFICADOR, required = false) 
				@ApiParam(value=SwaggerConstants.PARAM_SORT) String sort,
			@RequestHeader HttpHeaders headersRequest)
	{

		log.info("[list][" + LIST + "]");

		log.debug("[parmam] [page:" + page + "] [pageSize:" + pageSize + "] [fields:" + fields + "] [rsqlQ:" + rsqlQ + "] [sort:" + sort + "]");

		RSQLVisitor<CriteriaQuery<AgrupacionComercial>, EntityManager> visitor = new JpaCriteriaQueryVisitor<AgrupacionComercial>();

		return list(request, search, fields, rsqlQ, page, pageSize, sort, NO_HAY_SRID, LIST, new AgrupacionComercial(), new AgrupacionComercialResult(), availableFields, getKey(), visitor, service);
	}

	@ApiOperation(value = SwaggerConstants.LISTADO_Y_BUSQUEDA, notes = SwaggerConstants.DESCRIPCION_BUSQUEDA_HEAD, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_NO_HTML_WITHOUT_GEO, authorizations = { @Authorization(value = Constants.APIKEY) })
	@ApiResponses({ @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_O_LISTADO), @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA, response = ResultError.class), @ApiResponse(code = 401, message = SwaggerConstants.PETICION_INCORRECTA, response = ResultError.class),
			@ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO, response = ResultError.class) })
	@RequestMapping(value = { LIST, VERSION_1 + LIST }, method = { RequestMethod.HEAD })
	public @ResponseBody ResponseEntity<?> listHead(HttpServletRequest request, AgrupacionComercialSearch search, 
			@RequestParam(value = Constants.FIELDS, defaultValue = "", required = false) 
				@ApiParam(value=SwaggerConstants.PARAM_FIELDS) String fields, 
			@RequestParam(value = Constants.RSQL_Q, defaultValue = "", required = false) 
				@ApiParam(value=SwaggerConstants.PARAM_Q) String rsqlQ,
			@RequestParam(value = Constants.PAGE, defaultValue = "1", required = false) 
				@ApiParam(value=SwaggerConstants.PARAM_PAGE) String page, 
			@RequestParam(value = Constants.PAGESIZE, defaultValue = "", required = false) 
				@ApiParam(value=SwaggerConstants.PARAM_PAGESIZE) String pageSize, 
			@RequestParam(value = Constants.SORT, defaultValue = Constants.IDENTIFICADOR, required = false) 
				@ApiParam(value=SwaggerConstants.PARAM_SORT) String sort,
			@RequestParam(value = Constants.SRID, defaultValue = Constants.SRID_DEFECTO, required = false) @ApiParam(value = SwaggerConstants.PARAM_SRID, allowableValues = Constants.SUPPORTED_SRIDS) String srId, @RequestHeader HttpHeaders headersRequest)
	{
		log.info("[listHead][" + LIST + "]");
		return list(request, search, fields, rsqlQ, page, pageSize, sort, headersRequest);

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { ADD, VERSION_1 + ADD }, method = RequestMethod.POST, consumes = SwaggerConstants.FORMATOS_ADD_REQUEST)
	@ApiOperation(value = SwaggerConstants.INSERCION, notes = SwaggerConstants.DESCRIPCION_INSERCION, produces = SwaggerConstants.FORMATOS_ADD_RESPONSE, consumes = SwaggerConstants.FORMATOS_ADD_REQUEST, authorizations = { @Authorization(value = Constants.APIKEY) })
	@ApiResponses({ @ApiResponse(code = 201, message = SwaggerConstants.RESULTADO_DE_INSERCION, response = AgrupacionComercialResult.class), @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA, response = ResultError.class),
			@ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO, response = ResultError.class), @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE, response = ResultError.class), @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO, response = ResultError.class) })
	public @ResponseBody ResponseEntity<?> add(@ApiParam(required = true, name = Constants.OBJETO, value = SwaggerConstants.PARAM_AGRUPACIONCOMERCIAL_TEXT) @RequestBody AgrupacionComercial obj)
	{

		log.info("[add][" + ADD + "]");

		log.debug("[parmam][dato:" + obj + "] ");

		return add(obj, AgrupacionComercialController.class.getName(), ADD, service, getKey());

	}

	@SuppressWarnings("unchecked")
	@ApiOperation(value = SwaggerConstants.MODIFICACION, notes = SwaggerConstants.DESCRIPCION_MODIFICACION, produces = SwaggerConstants.FORMATOS_ADD_RESPONSE, consumes = SwaggerConstants.FORMATOS_ADD_REQUEST, authorizations = { @Authorization(value = Constants.APIKEY) })
	@ApiResponses({ @ApiResponse(code = 201, message = SwaggerConstants.RESULTADO_DE_MODIFICACION, response = AgrupacionComercialResult.class), @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA, response = ResultError.class),
			@ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO, response = ResultError.class), @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE, response = ResultError.class), @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO, response = ResultError.class) })
	@RequestMapping(value = { UPDATE, VERSION_1 + UPDATE }, method = RequestMethod.PUT, consumes = "application/json;charset=UTF-8")
	public @ResponseBody ResponseEntity<?> update(@ApiParam(required = true, name = Constants.IDENTIFICADOR, value = SwaggerConstants.PARAM_ID_TEXT+SwaggerConstants.PARAM_ID_AGRUPACION_COMERCIAL) @PathVariable(Constants.IDENTIFICADOR) String id,
			@ApiParam(required = true, name = Constants.OBJETO, value = SwaggerConstants.PARAM_AGRUPACIONCOMERCIAL_TEXT) @RequestBody AgrupacionComercial obj)
	{

		log.info("[update][" + UPDATE + "]");

		log.debug("[parmam][id:" + id + "] [dato:" + obj + "] ");

		return update(id, obj, AgrupacionComercialController.class.getName(), UPDATE, service, getKey());
	}

	// TODO no se generaliza porque exite una comprobacion previa:long rowcount =
	// localComercialService.rowcount(getKey(),LocalComercial.class,localSearch);
	@ApiOperation(value = SwaggerConstants.DELETE, notes = SwaggerConstants.DESCRIPCION_DELETE, produces = SwaggerConstants.FORMATOS_ADD_RESPONSE, consumes = SwaggerConstants.FORMATOS_ADD_REQUEST, authorizations = { @Authorization(value = Constants.APIKEY) })
	@ApiResponses({ @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_DELETE, response = AgrupacionComercialResult.class), @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA, response = ResultError.class),
			@ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO, response = ResultError.class), @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE, response = ResultError.class), @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO, response = ResultError.class) })
	@RequestMapping(value = { DELETE, VERSION_1 + DELETE }, method = RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<?> delete(@ApiParam(required = true, name = Constants.IDENTIFICADOR, value = SwaggerConstants.PARAM_ID_TEXT+SwaggerConstants.PARAM_ID_AGRUPACION_COMERCIAL) @PathVariable(Constants.IDENTIFICADOR) String id)
	{

		log.info("[delete][" + DELETE + "]");

		log.debug("[parmam][id:" + id + "] ");

		ResponseEntity<?> responseEntity = new ResponseEntity<Object>(HttpStatus.OK);
		Result<Object> resultObj = new Result<Object>();
		ResultError errorApi = new ResultError();
		boolean error = false;

		if (Util.validValue(id))
		{

			try
			{
				AgrupacionComercial agrupacionComercial = service.findById(getKey(), AgrupacionComercial.class, id);

				if (agrupacionComercial == null)
				{
					// Fijamos ERROR
					error = true;
					errorApi.setStatus(404);
					errorApi.setClassName(AgrupacionComercialController.class.getName());
					errorApi.setMethod("delete");
					errorApi.setError("Error trying to delete. [id:" + id + "] Not Found in BBDD");

				} else
				{
					//CMG Control para ver si estan activas las FK
					long rowcount = 0;
					if (activeFK) {
						LocalComercialSearch localSearch = new LocalComercialSearch();
						localSearch.setAgrupacionComercial(agrupacionComercial.getId());
						rowcount = localComercialService.rowcount(getKey(), LocalComercial.class, localSearch);
					}

					if (rowcount == 0)
					{
						service.delete(getKey(), agrupacionComercial);
						List<Object> records = new ArrayList<Object>();
						records.add(agrupacionComercial);

						resultObj.setPage(1);
						resultObj.setPageSize(1);
						resultObj.setRecords(records);
						resultObj.setTotalRecords(1);
						resultObj.setStatus(200);
						responseEntity = new ResponseEntity<Object>(resultObj, HttpStatus.valueOf(resultObj.getStatus()));
					} else
					{
						error = true;
						errorApi.setStatus(409);
						errorApi.setClassName(AgrupacionComercialController.class.getName());
						errorApi.setMethod("delete");
						errorApi.setError("This AgrupacionComercial is used " + rowcount + " times in LocalComercial");
					}

				}

			} catch (Exception e)
			{
				responseEntity = ExceptionUtil.checkException(e);
			}
		} else
		{
			error = true;
			errorApi.setStatus(404);
			errorApi.setClassName(AgrupacionComercialController.class.getName());
			errorApi.setMethod("delete");
			errorApi.setError("id is not valid [id:" + id + "]");
		}

		// Con Errores
		if (error)
		{
			responseEntity = new ResponseEntity<Object>(errorApi, HttpStatus.valueOf(errorApi.getStatus()));
		}

		return responseEntity;
	}

	@SuppressWarnings("unchecked")
	@ApiOperation(value = SwaggerConstants.FICHA, notes = SwaggerConstants.DESCRIPCION_FICHA, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_FULL_WITHOUT_GEO, authorizations = { @Authorization(value = Constants.APIKEY) })
	@ApiResponses({ @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_FICHA, response = AgrupacionComercialResult.class), @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA, response = ResultError.class),
			@ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO, response = ResultError.class), @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE, response = ResultError.class), @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO, response = ResultError.class) })
	@RequestMapping(value = { RECORD, VERSION_1 + RECORD }, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> record(HttpServletRequest request, @PathVariable @ApiParam(required = true, value=SwaggerConstants.PARAM_ID+SwaggerConstants.PARAM_ID_AGRUPACION_COMERCIAL) String id)
	{

		log.info("[record][" + RECORD + "]");

		log.debug("[parmam][id:" + id + "]");

		return record(request, id, new AgrupacionComercial(),new AgrupacionComercialResult(), NO_HAY_SRID, AgrupacionComercialController.class.getName(), RECORD, service, getKey());

	}

	@ApiOperation(value = SwaggerConstants.FICHA, notes = SwaggerConstants.DESCRIPCION_FICHA_HEAD, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_NO_HTML_WITHOUT_GEO, authorizations = { @Authorization(value = Constants.APIKEY) })
	@ApiResponses({ @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_FICHA, response = AgrupacionComercialResult.class), @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA, response = ResultError.class),
			@ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO, response = ResultError.class), @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE, response = ResultError.class), @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO, response = ResultError.class) })
	@RequestMapping(value = { RECORD, VERSION_1 + RECORD }, method = RequestMethod.HEAD)
	public @ResponseBody ResponseEntity<?> recordHead(HttpServletRequest request, @PathVariable @ApiParam(required = true, value=SwaggerConstants.PARAM_ID+SwaggerConstants.PARAM_ID_AGRUPACION_COMERCIAL) String id)
	{

		log.info("[recordHead][" + RECORD + "]");
		return record(request, id);

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { TRANSFORM, VERSION_1 + TRANSFORM }, method = RequestMethod.POST, consumes = SwaggerConstants.FORMATOS_ADD_REQUEST)
	@ApiOperation(value = SwaggerConstants.TRANSFORMACION, notes = SwaggerConstants.DESCRIPCION_TRANSFORMACION, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_NO_HTML_WITHOUT_GEO, authorizations = { @Authorization(value = Constants.APIKEY) })
	@ApiResponses({ @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_FICHA, response = AgrupacionComercialResult.class), @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA, response = ResultError.class),
			@ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO, response = ResultError.class) })
	public @ResponseBody ResponseEntity<?> transform(@ApiParam(required = true, name = Constants.OBJETO, value = SwaggerConstants.PARAM_AGRUPACIONCOMERCIAL_TEXT) @RequestBody AgrupacionComercial obj)
	{

		log.info("[transform]");

		log.debug("[parmam][obj:" + obj + "]");

		return transform(obj, AgrupacionComercialController.class.getName(), TRANSFORM);

	}

	@Override
	public ArrayList<SecurityURL> getURLsWithRoles()
	{

		ArrayList<SecurityURL> urlRoles = new ArrayList<SecurityURL>();

		Properties properties = mConf.getDatabasesConfig().get(getKey());

		listRequestType = Util.getRequestType(properties, getKey(), listRequestType);

		for (RequestType rObj : listRequestType)
		{
			urlRoles.add(rObj.getSecurityURL());
		}

		return urlRoles;
	}

	@Override
	public String getKey()
	{
		return LocalComercialConstants.KEY;
	}

	@Override
	public String getListURI()
	{
		return LIST;
	}

}
