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
import org.ciudadesabiertas.dataset.model.DeudaFinancieraAmortizacion;
import org.ciudadesabiertas.dataset.model.DeudaFinancieraAnual;
import org.ciudadesabiertas.dataset.model.DeudaFinancieraCapitalVivo;
import org.ciudadesabiertas.dataset.model.DeudaFinancieraCarga;
import org.ciudadesabiertas.dataset.model.DeudaFinancieraEmision;
import org.ciudadesabiertas.dataset.model.DeudaFinancieraInstrumentoFinanciacion;
import org.ciudadesabiertas.dataset.model.DeudaFinancieraPrestamo;
import org.ciudadesabiertas.dataset.utils.DeudaConstants;
import org.ciudadesabiertas.dataset.utils.DeudaFinancieraAmortizacionSearch;
import org.ciudadesabiertas.dataset.utils.DeudaFinancieraAnualSearch;
import org.ciudadesabiertas.dataset.utils.DeudaFinancieraCapitalVivoSearch;
import org.ciudadesabiertas.dataset.utils.DeudaFinancieraCargaSearch;
import org.ciudadesabiertas.dataset.utils.DeudaFinancieraEmisionSearch;
import org.ciudadesabiertas.dataset.utils.DeudaFinancieraInstrumentoFinanciacionResult;
import org.ciudadesabiertas.dataset.utils.DeudaFinancieraInstrumentoFinanciacionSearch;
import org.ciudadesabiertas.dataset.utils.DeudaFinancieraPrestamoSearch;
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
@Api(value="Instrumento Financiación",description = "Conjunto de operaciones relacionadas con el conjunto de datos Deuda Financiera"+SwaggerConstants.VOCABULARIO_A_HREF+DeudaConstants.deudaFinancieraVocabURL+SwaggerConstants.VOCABULARIO_A_HREF_END, tags= {"Deuda Finaciera -  Instrumento Financiación"})
public class DeudaFinancieraInstrumentoFinanciacionController extends GenericController implements CiudadesAbiertasController 
{
	public static final String LIST = "/deuda-publica-financiera/instrumento-financiacion";
	
	public static final String SEARCH_DISTINCT = LIST+"/distinct";
	
	public static final String RECORD = LIST+"/{id}";
	
	public static final String TRANSFORM = LIST+"/transform";
	
	public static final String ADD = LIST;
	public static final String UPDATE = LIST+"/{id}";
	public static final String DELETE = LIST+"/{id}";
	
	public static final String MODEL_VIEW_LIST = "deuda/financiera/instrumento/list";
	public static final String MODEL_VIEW_ID = "deuda/financiera/instrumento/id";
	
	private static List<RequestType> listRequestType = new ArrayList<RequestType>();
	
	private static String nameController = DeudaFinancieraInstrumentoFinanciacionController.class.getName();
	
	
	
	//Carga por defecto de las peticiones
	static {
		listRequestType.add(new RequestType("DeudaFinancieraInstrumentoFinanciacion_LIST", LIST, HttpMethod.GET,Constants.NO_AUTH));
		listRequestType.add(new RequestType("DeudaFinancieraInstrumentoFinanciacion_RECORD", RECORD, HttpMethod.GET,Constants.NO_AUTH));
		listRequestType.add(new RequestType("DeudaFinancieraInstrumentoFinanciacion_TRANSFORM", TRANSFORM, HttpMethod.POST,Constants.NO_AUTH));
		
		listRequestType.add(new RequestType("DeudaFinancieraInstrumentoFinanciacion_ADD", ADD, HttpMethod.POST,Constants.BASIC_AUTH));
		listRequestType.add(new RequestType("DeudaFinancieraInstrumentoFinanciacion_UPDATE", UPDATE, HttpMethod.PUT,Constants.BASIC_AUTH));
		listRequestType.add(new RequestType("DeudaFinancieraInstrumentoFinanciacion_DELETE", DELETE, HttpMethod.DELETE,Constants.BASIC_AUTH));
		
	}
	
	public static List<String> availableFields=Util.extractPropertiesFromBean(DeudaFinancieraInstrumentoFinanciacion.class);

	private static final Logger log = LoggerFactory.getLogger(DeudaFinancieraInstrumentoFinanciacionController.class);
		

	@Autowired
	protected DatasetService<DeudaFinancieraInstrumentoFinanciacion> service;
	
	@Autowired
	protected DatasetService<DeudaFinancieraPrestamo> servicePrestamo;
	
	@Autowired
	protected DatasetService<DeudaFinancieraEmision> serviceEmision;
	
	@Autowired
	protected DatasetService<DeudaFinancieraAnual> serviceDeudaAnual;
	
	@Autowired
	protected DatasetService<DeudaFinancieraAmortizacion> serviceAmortizacion;
	
	@Autowired
	protected DatasetService<DeudaFinancieraCarga> serviceCarga;
	
	@Autowired
	protected DatasetService<DeudaFinancieraCapitalVivo> serviceCapitalVivo;
	
		
	@SuppressWarnings("unchecked")
	@ApiOperation(value = SwaggerConstants.BUSQUEDA_DISTINCT, notes = SwaggerConstants.DESCRIPCION_BUSQUEDA_DISTINCT, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_GROUPBY, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_DISTINCT,  response=ObjectResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {SEARCH_DISTINCT, VERSION_1+SEARCH_DISTINCT}, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> distinctSearch(HttpServletRequest request, DistinctSearch search,															
			@RequestParam(value = Constants.PAGE, defaultValue = Constants.defaultPage+"", required = false) 
				@ApiParam(value=SwaggerConstants.PARAM_PAGE) String page,
			@RequestParam(value = Constants.PAGESIZE, defaultValue = Constants.defaultGroupByPageSize+"", required = false) 
	    		@ApiParam(value=SwaggerConstants.PARAM_PAGESIZE) String pageSize)
	{

		log.info("[distinctSearch][" + SEARCH_DISTINCT + "]");

		log.debug("[parmam][field:" + search.getField() + "] ");
		

		return distinctSearch(request, search, availableFields, page, pageSize,getKey(),NO_HAY_SRID, SEARCH_DISTINCT, new DeudaFinancieraInstrumentoFinanciacion(), new ObjectResult(),  service);

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
			ModelAndView mv, HttpServletRequest request,DeudaFinancieraInstrumentoFinanciacionSearch search, 
			@RequestParam(value = Constants.RSQL_Q, defaultValue = "", required = false) String rsqlQ,
			@RequestParam(value = Constants.PAGE, defaultValue = "", required = false) String page, 
			@RequestParam(value = Constants.PAGESIZE, defaultValue ="", required = false) String pageSize, 
			@RequestParam(value = Constants.SORT, defaultValue = "", required = false) String sort)
	{
				
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
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_O_LISTADO,  response=DeudaFinancieraInstrumentoFinanciacionResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {LIST,  VERSION_1+LIST}, method = {RequestMethod.GET})	
	public @ResponseBody ResponseEntity<?> list(HttpServletRequest request, DeudaFinancieraInstrumentoFinanciacionSearch search, 
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
		@RequestHeader HttpHeaders headersRequest)
	{

		log.info("[list][" + LIST + "]");

		log.debug("[parmam] [page:" + page + "] [pageSize:" + pageSize + "] [fields:" + fields + "] [rsqlQ:" + rsqlQ + "] [sort:" + sort + "]");
		
		RSQLVisitor<CriteriaQuery<DeudaFinancieraInstrumentoFinanciacion>, EntityManager> visitor = new JpaCriteriaQueryVisitor<DeudaFinancieraInstrumentoFinanciacion>();
		ResponseEntity<DeudaFinancieraInstrumentoFinanciacion> list= list(request, search, fields, rsqlQ, page, pageSize, sort, NO_HAY_SRID, LIST,new DeudaFinancieraInstrumentoFinanciacion(), new DeudaFinancieraInstrumentoFinanciacionResult(), 
					 availableFields, getKey(), visitor,service);
		//return integraCallejero(list, request);
		
		return list;
	}


	
	

	@ApiOperation(value = SwaggerConstants.LISTADO_Y_BUSQUEDA, notes = SwaggerConstants.DESCRIPCION_BUSQUEDA_HEAD, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_FULL_WITHOUT_GEO, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_O_LISTADO),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {LIST,  VERSION_1+LIST}, method = {RequestMethod.HEAD})	
	public @ResponseBody ResponseEntity<?> listHead(HttpServletRequest request, DeudaFinancieraInstrumentoFinanciacionSearch search, 
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
	            @ApiResponse(code = 201, message = SwaggerConstants.RESULTADO_DE_INSERCION,  response=DeudaFinancieraInstrumentoFinanciacionResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	public @ResponseBody ResponseEntity<?> add(			
			@ApiParam(required = true, name = Constants.OBJETO, value = SwaggerConstants.PARAM_DEUDA_F_INSTRUMENTO_FINANCIACION_TEXT) 			
			@RequestBody DeudaFinancieraInstrumentoFinanciacion obj 
			)
	{

		log.info("[add][" + ADD + "]");

		log.debug("[parmam][dato:" + obj + "] ");		
		
		List<String> erroresFK = checkClavesExternas(null, obj.getPrestamo(), obj.getEmision(), obj.getDeudaAnual(), Constants.BASIC_OPERATION_ADD);
		
		return add(obj, nameController, ADD, service,getKey(),erroresFK);

	}
	
	@SuppressWarnings({ "unchecked" })
	@ApiOperation(value = SwaggerConstants.MODIFICACION, notes = SwaggerConstants.DESCRIPCION_MODIFICACION, produces = SwaggerConstants.FORMATOS_ADD_RESPONSE, consumes=SwaggerConstants.FORMATOS_ADD_REQUEST, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 201, message = SwaggerConstants.RESULTADO_DE_MODIFICACION,  response=DeudaFinancieraInstrumentoFinanciacionResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value={UPDATE,  VERSION_1+UPDATE}, method = RequestMethod.PUT, consumes="application/json;charset=UTF-8")
	public @ResponseBody ResponseEntity<?> update(
			@ApiParam(required = true, name = Constants.IDENTIFICADOR, value = SwaggerConstants.PARAM_ID_DEUDA_F_INSTRUMENTO_FINANCIACION) 
			@PathVariable(Constants.IDENTIFICADOR) String id, 
			@ApiParam(required = true, name = Constants.OBJETO, value = SwaggerConstants.PARAM_DEUDA_F_INSTRUMENTO_FINANCIACION_TEXT) 
			@RequestBody DeudaFinancieraInstrumentoFinanciacion obj)
	{

		log.info("[update][" + UPDATE + "]");

		log.debug("[parmam][id:" + id + "] [dato:" + obj + "] ");		
					
		List<String> erroresFK = checkClavesExternas(null, obj.getPrestamo(), obj.getEmision(), obj.getDeudaAnual(), Constants.BASIC_OPERATION_UPDATE);
		
		return update(id, obj, nameController, UPDATE, service,getKey(),erroresFK);
	}
	
	@SuppressWarnings({ "unchecked" })
	@ApiOperation(value = SwaggerConstants.DELETE, notes = SwaggerConstants.DESCRIPCION_DELETE, produces = SwaggerConstants.FORMATOS_ADD_RESPONSE, consumes=SwaggerConstants.FORMATOS_ADD_REQUEST, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_DELETE,  response=DeudaFinancieraInstrumentoFinanciacionResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value={DELETE,  VERSION_1+DELETE}, method = RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<?> delete(
			@ApiParam(required = true, name = Constants.IDENTIFICADOR, value = SwaggerConstants.PARAM_ID_DEUDA_F_INSTRUMENTO_FINANCIACION) 
			@PathVariable(Constants.IDENTIFICADOR) String id)
	{

		log.info("[delete][" + DELETE + "]");

		log.debug("[parmam][id:" + id + "] ");
		
		
		List<String> erroresFK = checkClavesExternas(id, null, null, null, Constants.BASIC_OPERATION_DELETE);
		
		return delete(id, new DeudaFinancieraInstrumentoFinanciacion(), nameController, DELETE, service,getKey(),erroresFK);
	}
		

	@SuppressWarnings({ "unchecked" })
	@ApiOperation(value = SwaggerConstants.FICHA, notes = SwaggerConstants.DESCRIPCION_FICHA, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_FULL_WITHOUT_GEO, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_FICHA,  response=DeudaFinancieraInstrumentoFinanciacionResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {RECORD,  VERSION_1+RECORD}, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> record(HttpServletRequest request, 
			@PathVariable 
			@ApiParam(required = true, value=SwaggerConstants.PARAM_ID+SwaggerConstants.PARAM_ID_DEUDA_F_INSTRUMENTO_FINANCIACION) String id)
	{

		log.info("[record][" + RECORD + "]");

		log.debug("[parmam][id:" + id + "]");
				
		ResponseEntity<DeudaFinancieraInstrumentoFinanciacion> record = record(request, id, new DeudaFinancieraInstrumentoFinanciacion(),new DeudaFinancieraInstrumentoFinanciacionResult(), NO_HAY_SRID, nameController, RECORD, service,getKey());
		
		return record;
	}
	
	@ApiOperation(value = SwaggerConstants.FICHA, notes = SwaggerConstants.DESCRIPCION_FICHA_HEAD, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_NO_HTML_WITHOUT_GEO, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_FICHA,  response=DeudaFinancieraInstrumentoFinanciacionResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {RECORD,  VERSION_1+RECORD}, method =  RequestMethod.HEAD)
	public @ResponseBody ResponseEntity<?> recordHead(HttpServletRequest request, 
			@PathVariable 
			@ApiParam(required = true, value=SwaggerConstants.PARAM_ID+SwaggerConstants.PARAM_ID_DEUDA_F_INSTRUMENTO_FINANCIACION) String id)
	{

		log.info("[recordHead][" + RECORD + "]");
		return record(request, id);
		
	}

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value={TRANSFORM,  VERSION_1+TRANSFORM}, method = RequestMethod.POST, consumes=SwaggerConstants.FORMATOS_ADD_REQUEST)
	@ApiOperation(value = SwaggerConstants.TRANSFORMACION, notes = SwaggerConstants.DESCRIPCION_TRANSFORMACION, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_NO_HTML_WITHOUT_GEO, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_FICHA,  response=DeudaFinancieraInstrumentoFinanciacionResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	public @ResponseBody ResponseEntity<?> transform(
			@ApiParam(required = true, name = Constants.OBJETO, value = SwaggerConstants.PARAM_DEUDA_F_INSTRUMENTO_FINANCIACION_TEXT) @RequestBody DeudaFinancieraInstrumentoFinanciacion obj) {

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
		return DeudaConstants.KEY;
	}

	@Override
	public String getListURI()
	{	
		return LIST;
	}

	
	private List<String> checkClavesExternas(String id, String prestamoId, String emisionId,String deudaAnualId, String operation) {
		
		List<String> errors=new ArrayList<String>();
		
		log.debug("checkClavesExternas] [id:" + id + "] [prestamoId:" + prestamoId + "] [emisionId:" + emisionId
				+ "] [deudaAnualId:" + deudaAnualId + "] [operation:" + operation + "]");

		if (activeFK == false)
		{
			return errors;
		}
		
		try
		{	
			if (operation.equals(Constants.BASIC_OPERATION_ADD)||(operation.equals(Constants.BASIC_OPERATION_UPDATE)))
			{
				// 1 FK PRESTAMO
				if (prestamoId!=null)
				{
					//1 FK DeudaFinancieraInstrumentoFinanciacion
					DeudaFinancieraPrestamoSearch fk1Search=new DeudaFinancieraPrestamoSearch();
					fk1Search.setId(prestamoId);
					long rowcount = servicePrestamo.rowcount(getKey(), DeudaFinancieraPrestamo.class, fk1Search);
					if (rowcount==0)			
					{
						errors.add("The DeudaFinancieraPrestamo ['"+prestamoId+"'] No Existe");		
					}
				}
				// 2 FK EMISION
				if (emisionId!=null)
				{
					//1 FK DeudaFinancieraInstrumentoFinanciacion
					DeudaFinancieraEmisionSearch fk2Search=new DeudaFinancieraEmisionSearch();
					fk2Search.setId(emisionId);
					long rowcount = serviceEmision.rowcount(getKey(), DeudaFinancieraEmision.class, fk2Search);
					if (rowcount==0)			
					{
						errors.add("The DeudaFinancieraEmision ['"+emisionId+"'] No Existe");		
					}
				}
				//3 FK DEUDA ANUAL
				if (deudaAnualId!=null)
				{
					//1 FK DeudaFinancieraInstrumentoFinanciacion
					DeudaFinancieraAnualSearch fk2Search=new DeudaFinancieraAnualSearch();
					fk2Search.setId(deudaAnualId);
					long rowcount = serviceDeudaAnual.rowcount(getKey(), DeudaFinancieraAnual.class, fk2Search);
					if (rowcount==0)			
					{
						errors.add("The DeudaFinancieraAnual ['"+deudaAnualId+"'] No Existe");		
					}
				}
			}			
			
			if (operation.equals(Constants.BASIC_OPERATION_DELETE) && id!=null) {
				
				//1 FK Amortizacion
				DeudaFinancieraAmortizacionSearch fk1Search=new DeudaFinancieraAmortizacionSearch();
				fk1Search.setInstrumentoFinanciacion(id);
				long rowcount = serviceAmortizacion.rowcount(getKey(), DeudaFinancieraAmortizacion.class, fk1Search);
				if (rowcount>0)			
				{
					errors.add("The InstrumentoFinanciacion '"+id+"' is used in "+rowcount+" DeudaFinancieraAmortizacion");		
				}
				
				//2 FK Carga
				DeudaFinancieraCargaSearch fk2Search=new DeudaFinancieraCargaSearch();
				fk2Search.setInstrumentoFinanciacion(id);
				long rowcount2 = serviceCarga.rowcount(getKey(), DeudaFinancieraCarga.class, fk2Search);
				if (rowcount2>0)			
				{
					errors.add("The InstrumentoFinanciacion '"+id+"' is used in "+rowcount2+" DeudaFinancieraCarga");		
				}
				
				//3 FK CapitalVivo
				DeudaFinancieraCapitalVivoSearch fk3Search=new DeudaFinancieraCapitalVivoSearch();
				fk3Search.setInstrumentoFinanciacion(id);
				long rowcount3 = serviceCapitalVivo.rowcount(getKey(), DeudaFinancieraCapitalVivo.class, fk3Search);
				if (rowcount3>0)			
				{
					errors.add("The InstrumentoFinanciacion '"+id+"' is used in "+rowcount3+" DeudaFinancieraCapitalVivo");		
				}
			}
			
		}
		catch (Exception e)
		{
			log.error("[checkClavesExternasRowCount]  [ERROR:" + e.getMessage() + "]");
			errors.add("[checkClavesExternasRowCount] [ERROR:" + e.getMessage() + "]");
		}
		
		return errors;
	}
	
}
