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
import org.ciudadesabiertas.dataset.model.ConvRelFirmanteAyto;
import org.ciudadesabiertas.dataset.model.Convenio;
import org.ciudadesabiertas.dataset.model.ConvenioOrganization;
import org.ciudadesabiertas.dataset.utils.ConvRelFirmanteAytoResult;
import org.ciudadesabiertas.dataset.utils.ConvRelFirmanteAytoSearch;
import org.ciudadesabiertas.dataset.utils.ConvenioConstants;
import org.ciudadesabiertas.service.DatasetService;
import org.ciudadesabiertas.utils.Constants;
import org.ciudadesabiertas.utils.DistinctSearch;
import org.ciudadesabiertas.utils.ObjectResult;
import org.ciudadesabiertas.utils.RequestType;
import org.ciudadesabiertas.utils.ResultError;
import org.ciudadesabiertas.utils.SecurityURL;
import org.ciudadesabiertas.utils.SwaggerConstants;
import org.ciudadesabiertas.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
@Api(value="Convenio - Firmante Ayuntamiento",description = "Conjunto de operaciones relacionadas con el conjunto de datos Convenio - Firmante Ayuntamiento", tags= {"Convenio - Firmante Ayuntamiento"})
public class ConvRelFirmanteAytoController extends GenericController implements CiudadesAbiertasController 
{
	public static final String LIST = "/convenio/firmante-ayuntamiento";
	
	public static final String GEO_LIST = LIST+ "/geo";
	
	public static final String SEARCH_DISTINCT = LIST+"/distinct";
	
	public static final String RECORD = LIST+"/{id}";
	
	public static final String TRANSFORM = LIST+"/transform";
	
	public static final String ADD = LIST;
	public static final String UPDATE = LIST+"/{id}";
	public static final String DELETE = LIST+"/{id}";
	
	public static final String MODEL_VIEW_LIST = "convenio/firmanteAyuntamiento/list";
	public static final String MODEL_VIEW_ID = "convenio/firmanteAyuntamiento/id";
	
	private static List<RequestType> listRequestType = new ArrayList<RequestType>();
	
	private static String nameController = ConvRelFirmanteAytoController.class.getName();
	
	//Carga por defecto de las peticiones
	static {
		listRequestType.add(new RequestType("ConvRelFirmanteAyuntamiento_LIST", LIST, HttpMethod.GET,Constants.NO_AUTH));
		listRequestType.add(new RequestType("ConvRelFirmanteAyuntamiento_RECORD", RECORD, HttpMethod.GET,Constants.NO_AUTH));
		listRequestType.add(new RequestType("ConvRelFirmanteAyuntamiento_TRANSFORM", TRANSFORM, HttpMethod.POST,Constants.NO_AUTH));
		
		listRequestType.add(new RequestType("ConvRelFirmanteAyuntamiento_ADD", ADD, HttpMethod.POST,Constants.BASIC_AUTH));
		listRequestType.add(new RequestType("ConvRelFirmanteAyuntamiento_UPDATE", UPDATE, HttpMethod.PUT,Constants.BASIC_AUTH));
		listRequestType.add(new RequestType("ConvRelFirmanteAyuntamiento_DELETE", DELETE, HttpMethod.DELETE,Constants.BASIC_AUTH));
		
	}
	
	public static List<String> availableFields=Util.extractPropertiesFromBean(ConvRelFirmanteAyto.class);

	private static final Logger log = LoggerFactory.getLogger(ConvRelFirmanteAytoController.class);
		

	@Autowired
	protected DatasetService<ConvRelFirmanteAyto> service;
	
	@Autowired
	protected DatasetService<Convenio> serviceConvenio;
	
	@Autowired
	protected DatasetService<ConvenioOrganization> serviceOrg;
	
	@SuppressWarnings("unchecked")
	@ApiOperation(value = SwaggerConstants.BUSQUEDA_DISTINCT, notes = SwaggerConstants.DESCRIPCION_BUSQUEDA_DISTINCT, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_GROUPBY, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_DISTINCT,  response=ConvRelFirmanteAytoResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {SEARCH_DISTINCT, VERSION_1+SEARCH_DISTINCT}, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> distinctSearch(HttpServletRequest request, DistinctSearch search,															
			@RequestParam(value = Constants.PAGE, defaultValue = Constants.defaultPage
			+ "", required = false) 
			@ApiParam(value=SwaggerConstants.PARAM_PAGE) String page,
			@RequestParam(value = Constants.PAGESIZE, defaultValue = Constants.defaultGroupByPageSize
					+ "", required = false) 
			@ApiParam(value=SwaggerConstants.PARAM_PAGESIZE) String pageSize) {
		log.info("[distinctSearch][" + SEARCH_DISTINCT + "]");

		log.debug("[parmam][field:" + search.getField() + "] ");
		

		return distinctSearch(request, search, availableFields, page, pageSize,getKey(),NO_HAY_SRID, SEARCH_DISTINCT, new ConvRelFirmanteAyto(), new ObjectResult(),  service);

	}
	
	@ApiIgnore
	@ApiOperation(value = SwaggerConstants.LISTADO_Y_BUSQUEDA_HTML, notes = SwaggerConstants.DESCRIPCION_BUSQUEDA, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_HTML, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_O_LISTADO),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {LIST+Constants.EXT_HTML, VERSION_1+LIST+Constants.EXT_HTML}, method = RequestMethod.GET)	
	public ModelAndView listHTML(
			ModelAndView mv, HttpServletRequest request,ConvRelFirmanteAytoSearch search, 
			@RequestParam(value = Constants.RSQL_Q, defaultValue = "", required = false) String rsqlQ,
			@RequestParam(value = Constants.PAGE, defaultValue = "", required = false) String page,
			@RequestParam(value = Constants.PAGESIZE, defaultValue = "", required = false) String pageSize,
			@RequestParam(value = Constants.SORT, defaultValue = "", required = false) String sort) {
				
		log.info("[listHTML][" + LIST + ".html]");
		
		String params="?";
		if (Util.validValue(rsqlQ))	{
			params+="&q="+rsqlQ;
		}else if (search!=null){
			params+=search.toUrlParam();
		}		
		
		return listHTML(mv, request, NO_HAY_SRID, page, pageSize, sort, params, MODEL_VIEW_LIST);
	}
	
	
	@ApiIgnore
	@ApiOperation(value = SwaggerConstants.FICHA_HTML, notes = SwaggerConstants.DESCRIPCION_FICHA, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_HTML, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_FICHA,  response=ModelAndView.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {RECORD+Constants.EXT_HTML, VERSION_1+RECORD+Constants.EXT_HTML}, method = RequestMethod.GET)
	public ModelAndView recordHTML(ModelAndView mv, @PathVariable String id, HttpServletRequest request)
	{
		log.info("[recordHTML][" + RECORD + Constants.EXT_HTML + "]");
		
		return recordHTML(mv, request, NO_HAY_SRID, id, MODEL_VIEW_ID);
	}
	
	
	@SuppressWarnings({ "unchecked"})
	@ApiOperation(value = SwaggerConstants.LISTADO_Y_BUSQUEDA, notes = SwaggerConstants.DESCRIPCION_BUSQUEDA, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_FULL_WITHOUT_GEO, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_O_LISTADO,  response=ConvRelFirmanteAytoResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {LIST,  VERSION_1+LIST}, method = {RequestMethod.GET})	
	public @ResponseBody ResponseEntity<?> list(HttpServletRequest request, ConvRelFirmanteAytoSearch search, 
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
			@RequestHeader HttpHeaders headersRequest) {

		log.info("[list][" + LIST + "]");

		log.debug("[parmam] [page:" + page + "] [pageSize:" + pageSize + "] [fields:" + fields + "] [rsqlQ:" + rsqlQ + "] [sort:" + sort + "]");
		
		RSQLVisitor<CriteriaQuery<ConvRelFirmanteAyto>, EntityManager> visitor = new JpaCriteriaQueryVisitor<ConvRelFirmanteAyto>();
		
		ResponseEntity<ConvRelFirmanteAyto> list= list(request, search, fields, rsqlQ, page, pageSize, sort, NO_HAY_SRID, LIST,new ConvRelFirmanteAyto(), new ConvRelFirmanteAytoResult(), 
					 availableFields, getKey(), visitor,service);
		
		//return integraOrganigrama(list, request);
		return list;
	}


	
	

	@ApiOperation(value = SwaggerConstants.LISTADO_Y_BUSQUEDA, notes = SwaggerConstants.DESCRIPCION_BUSQUEDA_HEAD, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_NO_HTML_WITHOUT_GEO, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_O_LISTADO),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {LIST,  VERSION_1+LIST}, method = {RequestMethod.HEAD})	
	public @ResponseBody ResponseEntity<?> listHead(HttpServletRequest request, ConvRelFirmanteAytoSearch search, 
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
			@RequestHeader HttpHeaders headersRequest) {

		log.info("[listHead][" + LIST + "]");		
		return list(request, search, fields, rsqlQ, page, pageSize, sort, headersRequest);

	}
	
	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value={ADD,  VERSION_1+ADD}, method = RequestMethod.POST, consumes=SwaggerConstants.FORMATOS_ADD_REQUEST)
	@ApiOperation(value = SwaggerConstants.INSERCION, notes = SwaggerConstants.DESCRIPCION_INSERCION, produces = SwaggerConstants.FORMATOS_ADD_RESPONSE, consumes=SwaggerConstants.FORMATOS_ADD_REQUEST, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 201, message = SwaggerConstants.RESULTADO_DE_INSERCION,  response=ConvRelFirmanteAytoResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	public @ResponseBody ResponseEntity<?> add(			
			@ApiParam(required = true, name = Constants.OBJETO, value = SwaggerConstants.PARAM_CONVENIOSRELFIRAYTO_TEXT) 			
			@RequestBody ConvRelFirmanteAyto obj 
			)
	{

		log.info("[add][" + ADD + "]");

		log.debug("[parmam][dato:" + obj + "] ");		
		
		List<String> errores = checkClavesExternas(obj);
		
		return add(obj, nameController, ADD, service,getKey(),errores);

	}
	
	@SuppressWarnings({ "unchecked" })
	@ApiOperation(value = SwaggerConstants.MODIFICACION, notes = SwaggerConstants.DESCRIPCION_MODIFICACION, produces = SwaggerConstants.FORMATOS_ADD_RESPONSE, consumes=SwaggerConstants.FORMATOS_ADD_REQUEST, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 201, message = SwaggerConstants.RESULTADO_DE_MODIFICACION,  response=ConvRelFirmanteAytoResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value={UPDATE,  VERSION_1+UPDATE}, method = RequestMethod.PUT, consumes="application/json;charset=UTF-8")
	public @ResponseBody ResponseEntity<?> update(
			@ApiParam(required = true, name = Constants.IDENTIFICADOR, value = SwaggerConstants.PARAM_ID_TEXT) 
			@PathVariable(Constants.IDENTIFICADOR) String id, 
			@ApiParam(required = true, name = Constants.OBJETO, value = SwaggerConstants.PARAM_CONVENIOSRELFIRAYTO_TEXT) 
			@RequestBody ConvRelFirmanteAyto obj)
	{

		log.info("[update][" + UPDATE + "]");

		log.debug("[parmam][id:" + id + "] [dato:" + obj + "] ");	
		
		List<String> errores = checkClavesExternas(obj);
					
		return update(id, obj, nameController, UPDATE, service,getKey(),errores);
	}
	
	@SuppressWarnings({ "unchecked" })
	@ApiOperation(value = SwaggerConstants.DELETE, notes = SwaggerConstants.DESCRIPCION_DELETE, produces = SwaggerConstants.FORMATOS_ADD_RESPONSE, consumes=SwaggerConstants.FORMATOS_ADD_REQUEST, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_DELETE,  response=ConvRelFirmanteAytoResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value={DELETE,  VERSION_1+DELETE}, method = RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<?> delete(
			@ApiParam(required = true, name = Constants.IDENTIFICADOR, value = SwaggerConstants.PARAM_ID_TEXT) 
			@PathVariable(Constants.IDENTIFICADOR) String id)
	{

		log.info("[delete][" + DELETE + "]");

		log.debug("[parmam][id:" + id + "] ");			
		
		return delete(id, new ConvRelFirmanteAyto(), nameController, DELETE, service,getKey());
	}
		

	@SuppressWarnings({ "unchecked" })
	@ApiOperation(value = SwaggerConstants.FICHA, notes = SwaggerConstants.DESCRIPCION_FICHA, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_FULL_WITHOUT_GEO, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_FICHA,  response=ConvRelFirmanteAytoResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {RECORD,  VERSION_1+RECORD}, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> record(HttpServletRequest request, 
			@PathVariable 
			@ApiParam(required = true, value=SwaggerConstants.PARAM_ID+SwaggerConstants.PARAM_ID_CONVENIOSRELFIRAYTO) String id)
	{

		log.info("[record][" + RECORD + "]");

		log.debug("[parmam][id:" + id + "]");
				
		ResponseEntity<ConvRelFirmanteAyto> record= record(request, id, new ConvRelFirmanteAyto(),new ConvRelFirmanteAytoResult(), NO_HAY_SRID, nameController, RECORD, service,getKey());

		//return integraOrganigrama(record, request);
		return record;
	}
	
	@ApiOperation(value = SwaggerConstants.FICHA, notes = SwaggerConstants.DESCRIPCION_FICHA_HEAD, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_NO_HTML_WITHOUT_GEO, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_FICHA,  response=ConvRelFirmanteAytoResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {RECORD,  VERSION_1+RECORD}, method =  RequestMethod.HEAD)
	public @ResponseBody ResponseEntity<?> recordHead(HttpServletRequest request, 
			@PathVariable 
			@ApiParam(required = true, value=SwaggerConstants.PARAM_ID+SwaggerConstants.PARAM_ID_CONVENIOSRELFIRAYTO) String id)
	{

		log.info("[recordHead][" + RECORD + "]");
		return record(request, id);
		
	}

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value={TRANSFORM,  VERSION_1+TRANSFORM}, method = RequestMethod.POST, consumes=SwaggerConstants.FORMATOS_ADD_REQUEST)
	@ApiOperation(value = SwaggerConstants.TRANSFORMACION, notes = SwaggerConstants.DESCRIPCION_TRANSFORMACION, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_NO_HTML_WITHOUT_GEO, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_FICHA,  response=ConvRelFirmanteAytoResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	public @ResponseBody ResponseEntity<?> transform(
			@ApiParam(required = true, name = Constants.OBJETO, value = SwaggerConstants.PARAM_CONVENIOSRELFIRAYTO_TEXT) @RequestBody ConvRelFirmanteAyto obj) {

		log.info("[transform]");

		log.debug("[parmam][obj:" + obj + "]");

		return transform(obj, nameController, TRANSFORM);

	}


	@Override
	public ArrayList<SecurityURL> getURLsWithRoles()
	{		
		
		ArrayList<SecurityURL> urlRoles = new ArrayList<SecurityURL>();
		
		Properties properties = mConf.getDatabasesConfig().get(getKey());
				
		listRequestType = Util.getRequestType(properties,getKey(), listRequestType);
		
		for (RequestType rObj:listRequestType) {
			urlRoles.add(rObj.getSecurityURL());
		}		
		
		
		return urlRoles;
	}
		
	
	
		
	@Override
	public String getKey()
	{
		return ConvenioConstants.KEY;
	}

	@Override
	public String getListURI()
	{	
		return LIST;
	}

	/**
	 * ***********************************************************
	 * METODOS ESPECIFICOS PARA EL CONTROLADOR - NO GENERALIZABLES
	 * ***********************************************************
	 *  ||												||
	 *  \/												\/
	 */
	
//	@SuppressWarnings("unchecked")
//	private ResponseEntity<?> integraOrganigrama(ResponseEntity<ConvRelFirmanteAyto> list,  HttpServletRequest request) {
//
//		HttpStatus statusCode = list.getStatusCode();
//
//		if (statusCode.is2xxSuccessful()) {
//			boolean isSemantic = Util.isSemanticPetition(request);
//
//			if (isSemantic) {
//
//				Object body = list.getBody();
//
//				Result<ConvRelFirmanteAyto> result = ((Result<ConvRelFirmanteAyto>) body);
//
//				List<ConvRelFirmanteAyto> records = result.getRecords();
//
//				if (Util.isOrganigramaIntegration()) {
//					for (ConvRelFirmanteAyto agenda : records) {
//						// CMG Controlamos en la integración con equipamiento, que tenga valor equipamientoId
//						if ( Util.validValue(agenda.getOrganizationId() )) {
//							agenda.setOrganizationTitle(null);
//						}
//					}
//				} else {
//					for (ConvRelFirmanteAyto agenda : records) {
//						agenda.setOrganizationIdIsolated(agenda.getOrganizationId());
//						agenda.setOrganizationId(null);
//					}
//				}
//			}
//		}
//
//		return list;
//	}
	
	/**
	 * ***********************************************************
	 * METODOS ESPECIFICOS PARA EL CONTROLADOR - NO GENERALIZABLES
	 * ***********************************************************
	 *  ||												||
	 *  \/												\/
	 */
	
	private List<String> checkClavesExternas(ConvRelFirmanteAyto convFirmanteAyto)
	{
		List<String> errores=new ArrayList<String>();
		//Chequeamos los identificadores que apuntan a otras tablas					
		if (Util.validValue(convFirmanteAyto.getConvenioId()) && activeFK)
		{
		//1 CHECK
			Convenio findConvenioById = serviceConvenio.findById(getKey(),Convenio.class,convFirmanteAyto.getConvenioId());
			if (findConvenioById==null)
			{
				errores.add("Convenio ID not exists in entity: "+convFirmanteAyto.getConvenioId());
			}
		//2 CHECK
			ConvenioOrganization findOrganizationById = serviceOrg.findById(getKey(),ConvenioOrganization.class,convFirmanteAyto.getOrganizationId());
			if (findOrganizationById==null)
			{
				errores.add("Organization ID not exists in entity: "+convFirmanteAyto.getOrganizationId());
			}
		}
		
		
		return errores;
	}

}
