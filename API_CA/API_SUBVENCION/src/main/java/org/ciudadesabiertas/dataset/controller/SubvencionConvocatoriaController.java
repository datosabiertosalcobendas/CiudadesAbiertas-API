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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.servlet.http.HttpServletRequest;

import org.ciudadesabiertas.controller.CiudadesAbiertasController;
import org.ciudadesabiertas.controller.GenericController;
import org.ciudadesabiertas.dataset.model.SubvencionConvocatoria;
import org.ciudadesabiertas.dataset.model.SubvencionOrganization;
import org.ciudadesabiertas.dataset.utils.SubvencionConstants;
import org.ciudadesabiertas.dataset.utils.SubvencionConvocatoriaResult;
import org.ciudadesabiertas.dataset.utils.SubvencionConvocatoriaSearch;
import org.ciudadesabiertas.service.DatasetService;
import org.ciudadesabiertas.utils.Constants;
import org.ciudadesabiertas.utils.DistinctSearch;
import org.ciudadesabiertas.utils.GroupBySearch;
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
@Api(value="Convocatoria de subvención",description = "Conjunto de operaciones relacionadas con el conjunto de datos de convocatoria de subvención"+SwaggerConstants.VOCABULARIO_A_HREF+SubvencionConstants.subvencionVocabURL+SwaggerConstants.VOCABULARIO_A_HREF_END, tags= {"Subvención - Convocatoria"})
public class SubvencionConvocatoriaController extends GenericController implements CiudadesAbiertasController 
{
	public static final String LIST = "/subvencion/convocatoria";
	
	public static final String SEARCH_DISTINCT = LIST+"/distinct";
	
	public static final String RECORD = LIST+ "/{id}";
	
	public static final String TRANSFORM = LIST+"/transform";
	
	public static final String SEARCHGROUP = LIST+"/groupBy";
	
	public static final String ADD = LIST;
	public static final String UPDATE = RECORD;
	public static final String DELETE = RECORD;
	
	public static final String MODEL_VIEW_LIST = "subvencion/convocatoria/list";
	public static final String MODEL_VIEW_ID = "subvencion/convocatoria/id";
	
	private static List<RequestType> listRequestType = new ArrayList<RequestType>();
	
	private static String nameController = SubvencionConvocatoriaController.class.getName();
	
	//Carga por defecto de las peticiones
	static {
		listRequestType.add(new RequestType("Subvencion_Convocatoria_LIST", LIST, HttpMethod.GET,Constants.NO_AUTH));
		listRequestType.add(new RequestType("Subvencion_Convocatoria_RECORD", RECORD, HttpMethod.GET,Constants.NO_AUTH));
		listRequestType.add(new RequestType("Subvencion_Convocatoria_TRANSFORM", TRANSFORM, HttpMethod.POST,Constants.NO_AUTH));
		
		listRequestType.add(new RequestType("Subvencion_Convocatoria_ADD", ADD, HttpMethod.POST,Constants.BASIC_AUTH));
		listRequestType.add(new RequestType("Subvencion_Convocatoria_UPDATE", UPDATE, HttpMethod.PUT,Constants.BASIC_AUTH));
		listRequestType.add(new RequestType("Subvencion_Convocatoria_DELETE", DELETE, HttpMethod.DELETE,Constants.BASIC_AUTH));
		
	}
	
	public static List<String> availableFields=Util.extractPropertiesFromBean(SubvencionConvocatoria.class);

	private static final Logger log = LoggerFactory.getLogger(SubvencionConvocatoriaController.class);
		

	@Autowired
	protected DatasetService<SubvencionConvocatoria> service;
	
	@Autowired
	protected DatasetService<SubvencionOrganization> organizationService;
	
	@SuppressWarnings("unchecked")
	@ApiOperation(value = SwaggerConstants.LISTADO_Y_BUSQUEDA_AGRUPADA, notes = SwaggerConstants.DESCRIPCION_BUSQUEDA_AGRUPADA, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_GROUPBY, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_O_LISTADO_AGRUPADA,  response=SubvencionConvocatoriaResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {SEARCHGROUP, SubvencionConstants.VERSION_2+SEARCHGROUP}, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> groupBySearch(HttpServletRequest request, 
															GroupBySearch groupBySearch,
				@RequestParam(value = Constants.PAGE, defaultValue = Constants.defaultPage+"", required = false) 
					@ApiParam(value=SwaggerConstants.PARAM_PAGE) String page,
				@RequestParam(value = Constants.PAGESIZE, defaultValue = Constants.defaultGroupByPageSize+"", required = false) 
					@ApiParam(value=SwaggerConstants.PARAM_PAGESIZE) String pageSize)
	{

		log.info("[search][" + SEARCHGROUP + "]");

		log.debug("[parmam][search:" + groupBySearch + "] ");


		return groupBySearch(request, groupBySearch, page, pageSize,getKey(),NO_HAY_SRID, SEARCHGROUP, new SubvencionConvocatoria(), new ObjectResult(),  service);

	}
	


	
	@SuppressWarnings("unchecked")
	@ApiOperation(value = SwaggerConstants.BUSQUEDA_DISTINCT, notes = SwaggerConstants.DESCRIPCION_BUSQUEDA_DISTINCT, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_GROUPBY, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_DISTINCT,  response=ObjectResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {SEARCH_DISTINCT, SubvencionConstants.VERSION_2+SEARCH_DISTINCT}, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> distinctSearch(HttpServletRequest request, DistinctSearch search,															
															@RequestParam(value = Constants.PAGE, defaultValue = Constants.defaultPage+"", required = false) String page,
															@RequestParam(value = Constants.PAGESIZE, defaultValue = Constants.defaultGroupByPageSize+"", required = false) String pageSize)
	{

		log.info("[distinctSearch][" + SEARCH_DISTINCT + "]");

		log.debug("[parmam][field:" + search.getField() + "] ");
		

		return distinctSearch(request, search, availableFields, page, pageSize,getKey(),NO_HAY_SRID, SEARCH_DISTINCT, new SubvencionConvocatoria(), new ObjectResult(),  service);

	}
	
	@ApiIgnore
	@ApiOperation(value = SwaggerConstants.LISTADO_Y_BUSQUEDA_HTML, notes = SwaggerConstants.DESCRIPCION_BUSQUEDA, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_HTML, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_O_LISTADO),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {LIST+Constants.EXT_HTML, SubvencionConstants.VERSION_2+LIST+Constants.EXT_HTML}, method = RequestMethod.GET)	
	public ModelAndView listHTML(
			ModelAndView mv, HttpServletRequest request,SubvencionConvocatoriaSearch search, 
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
	@RequestMapping(value= {RECORD+Constants.EXT_HTML, SubvencionConstants.VERSION_2+RECORD+Constants.EXT_HTML}, method = RequestMethod.GET)
	public ModelAndView recordHTML(ModelAndView mv, @PathVariable String id, HttpServletRequest request)
	{
		log.info("[recordHTML][" + RECORD + Constants.EXT_HTML + "]");
		
		return recordHTML(mv, request, NO_HAY_SRID, id, MODEL_VIEW_ID);
	}
	
	
	@SuppressWarnings({ "unchecked"})
	@ApiOperation(value = SwaggerConstants.LISTADO_Y_BUSQUEDA, notes = SwaggerConstants.DESCRIPCION_BUSQUEDA, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_FULL, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_O_LISTADO,  response=SubvencionConvocatoriaResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {LIST,  SubvencionConstants.VERSION_2+LIST}, method = {RequestMethod.GET})	
	public @ResponseBody ResponseEntity<?> list(HttpServletRequest request, SubvencionConvocatoriaSearch search, 
			@RequestParam(value = Constants.FIELDS, defaultValue = "", required = false) String fields, 
			@RequestParam(value = Constants.RSQL_Q, defaultValue = "", required = false) String rsqlQ, 
			@RequestParam(value = Constants.PAGE, defaultValue = "1", required = false) String page, 
			@RequestParam(value = Constants.PAGESIZE, defaultValue = "", required = false) String pageSize,
			@RequestParam(value = Constants.SORT, defaultValue = Constants.IDENTIFICADOR, required = false) String sort,
						
			@RequestHeader HttpHeaders headersRequest)
	{

		log.info("[list][" + LIST + "]");

		log.debug("[parmam] [page:" + page + "] [pageSize:" + pageSize + "] [fields:" + fields + "] [rsqlQ:" + rsqlQ + "] [sort:" + sort + "]");
		
		RSQLVisitor<CriteriaQuery<SubvencionConvocatoria>, EntityManager> visitor = new JpaCriteriaQueryVisitor<SubvencionConvocatoria>();
		
		ResponseEntity<SubvencionConvocatoria> list =list(request, search, fields, rsqlQ, page, pageSize, sort, NO_HAY_SRID, LIST,new SubvencionConvocatoria(), new SubvencionConvocatoriaResult(), 
			 availableFields, getKey(), visitor,service);
		
		list=configureDataArrays(list, request);
		
		return integraAll(list, request);
		
	}


	
	

	@ApiOperation(value = SwaggerConstants.LISTADO_Y_BUSQUEDA, notes = SwaggerConstants.DESCRIPCION_BUSQUEDA_HEAD, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_NO_HTML, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_BUSQUEDA_O_LISTADO),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {LIST,  SubvencionConstants.VERSION_2+LIST}, method = {RequestMethod.HEAD})	
	public @ResponseBody ResponseEntity<?> listHead(HttpServletRequest request, SubvencionConvocatoriaSearch search, 
			@RequestParam(value = Constants.FIELDS, defaultValue = "", required = false) String fields, 
			@RequestParam(value = Constants.RSQL_Q, defaultValue = "", required = false) String rsqlQ, 
			@RequestParam(value = Constants.PAGE, defaultValue = "1", required = false) String page, 
			@RequestParam(value = Constants.PAGESIZE, defaultValue = "", required = false) String pageSize,
			@RequestParam(value = Constants.SORT, defaultValue = Constants.IDENTIFICADOR, required = false) String sort,
			@RequestHeader HttpHeaders headersRequest)
	{

		log.info("[listHead][" + LIST + "]");		
		return list(request, search, fields, rsqlQ, page, pageSize, sort, headersRequest);

	}
	
	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value={ADD,  SubvencionConstants.VERSION_2+ADD}, method = RequestMethod.POST, consumes=SwaggerConstants.FORMATOS_ADD_REQUEST)
	@ApiOperation(value = SwaggerConstants.INSERCION, notes = SwaggerConstants.DESCRIPCION_INSERCION, produces = SwaggerConstants.FORMATOS_ADD_RESPONSE, consumes=SwaggerConstants.FORMATOS_ADD_REQUEST, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 201, message = SwaggerConstants.RESULTADO_DE_INSERCION,  response=SubvencionConvocatoriaResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	public @ResponseBody ResponseEntity<?> add(			
			@ApiParam(required = true, name = Constants.OBJETO, value = SwaggerConstants.PARAM_SUBVENCION_TEXT) 			
			@RequestBody SubvencionConvocatoria obj 
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
	            @ApiResponse(code = 201, message = SwaggerConstants.RESULTADO_DE_MODIFICACION,  response=SubvencionConvocatoriaResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value={UPDATE,  SubvencionConstants.VERSION_2+UPDATE}, method = RequestMethod.PUT, consumes="application/json;charset=UTF-8")
	public @ResponseBody ResponseEntity<?> update(
			@ApiParam(required = true, name = Constants.IDENTIFICADOR, value = SwaggerConstants.PARAM_ID_TEXT) 
			@PathVariable(Constants.IDENTIFICADOR) String id, 
			@ApiParam(required = true, name = Constants.OBJETO, value = SwaggerConstants.PARAM_SUBVENCION_TEXT) 
			@RequestBody SubvencionConvocatoria obj)
	{
		log.info("[update][" + UPDATE + "]");
		log.debug("[parmam][id:" + id + "] [dato:" + obj + "] ");	
		
		List<String> errores = checkClavesExternas(obj);		
		return update(id, obj, nameController, UPDATE, service,getKey(),errores);
	}
	
	@SuppressWarnings({ "unchecked" })
	@ApiOperation(value = SwaggerConstants.DELETE, notes = SwaggerConstants.DESCRIPCION_DELETE, produces = SwaggerConstants.FORMATOS_ADD_RESPONSE, consumes=SwaggerConstants.FORMATOS_ADD_REQUEST, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_DELETE,  response=SubvencionConvocatoriaResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value={DELETE,  SubvencionConstants.VERSION_2+DELETE}, method = RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<?> delete(
			@ApiParam(required = true, name = Constants.IDENTIFICADOR, value = SwaggerConstants.PARAM_ID_TEXT) 
			@PathVariable(Constants.IDENTIFICADOR) String id)
	{

		log.info("[delete][" + DELETE + "]");

		log.debug("[parmam][id:" + id + "] ");			
		
		return delete(id, new SubvencionConvocatoria(), nameController, DELETE, service,getKey());
	}
		

	@SuppressWarnings({ "unchecked" })
	@ApiOperation(value = SwaggerConstants.FICHA, notes = SwaggerConstants.DESCRIPCION_FICHA, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_FULL, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_FICHA,  response=SubvencionConvocatoriaResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {RECORD,  SubvencionConstants.VERSION_2+RECORD}, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> record(HttpServletRequest request, @PathVariable String id)
	{

		log.info("[record][" + RECORD + "]");

		log.debug("[parmam][id:" + id + "]");
		
		ResponseEntity<SubvencionConvocatoria> record = record(request, id, new SubvencionConvocatoria(),new SubvencionConvocatoriaResult(), NO_HAY_SRID, nameController, RECORD, service,getKey());
	
		record=configureDataArrays(record, request);
		
		return integraAll(record, request);

	}
	
	@ApiOperation(value = SwaggerConstants.FICHA, notes = SwaggerConstants.DESCRIPCION_FICHA_HEAD, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_NO_HTML, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_FICHA,  response=SubvencionConvocatoriaResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 401, message = SwaggerConstants.NO_AUTORIZADO,  response=ResultError.class),
	            @ApiResponse(code = 409, message = SwaggerConstants.EL_RECURSO_YA_EXISTE,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	@RequestMapping(value= {RECORD,  SubvencionConstants.VERSION_2+RECORD}, method =  RequestMethod.HEAD)
	public @ResponseBody ResponseEntity<?> recordHead(HttpServletRequest request, @PathVariable String id)
	{

		log.info("[recordHead][" + RECORD + "]");
		return record(request, id);
		
	}

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value={TRANSFORM,  SubvencionConstants.VERSION_2+TRANSFORM}, method = RequestMethod.POST, consumes=SwaggerConstants.FORMATOS_ADD_REQUEST)
	@ApiOperation(value = SwaggerConstants.TRANSFORMACION, notes = SwaggerConstants.DESCRIPCION_TRANSFORMACION, produces = SwaggerConstants.FORMATOS_CONSULTA_RESPONSE_NO_HTML, authorizations = { @Authorization(value=Constants.APIKEY) })
	@ApiResponses({
	            @ApiResponse(code = 200, message = SwaggerConstants.RESULTADO_DE_FICHA,  response=SubvencionConvocatoriaResult.class),
	            @ApiResponse(code = 400, message = SwaggerConstants.PETICION_INCORRECTA,  response=ResultError.class),
	            @ApiResponse(code = 500, message = SwaggerConstants.ERROR_INTERNO,  response=ResultError.class)
	   })
	public @ResponseBody ResponseEntity<?> transform(
			@ApiParam(required = true, name = Constants.OBJETO, value = SwaggerConstants.PARAM_SUBVENCION_TEXT) @RequestBody SubvencionConvocatoria obj) {

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
		return SubvencionConstants.KEY;
	}

	@Override
	public String getListURI()
	{	
		return LIST;
	}

	/**
	 * Lógica para controlar si el convenio se gestiona por una Organizacion o por un distrito
	 * @param list
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ResponseEntity<?> gestionadoPor(ResponseEntity<SubvencionConvocatoria> list,  HttpServletRequest request) {

		HttpStatus statusCode = list.getStatusCode();

		if (statusCode.is2xxSuccessful()) {
			boolean isSemantic = Util.isSemanticPetition(request);

			if (isSemantic) {

				Object body = list.getBody();

				Result<SubvencionConvocatoria> result = ((Result<SubvencionConvocatoria>) body);

				List<SubvencionConvocatoria> records = result.getRecords();
				
				for (SubvencionConvocatoria subvencion : records) {
					// CMG Controlamos en la integración con subvencio, que tenga valor subvencioId
					if ( Util.validValue(subvencion.getGestionadoPorOrganization()) && subvencion.getGestionadoPorOrganization()) {
					  subvencion.setDistritoId(null);
					  subvencion.setDistritoTitle(null);

					}else if (Util.validValue(subvencion.getGestionadoPorDistrito()) && subvencion.getGestionadoPorDistrito()) {
					  subvencion.setOrganizationId(null);
					}else {
					  subvencion.setDistritoId(null);
					  subvencion.setDistritoTitle(null);
					  subvencion.setOrganizationId(null);
					}
			
				}
			}
		}

		return list;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private  ResponseEntity<SubvencionConvocatoria> integraConvenio(ResponseEntity<SubvencionConvocatoria> list,HttpServletRequest request){
		
		HttpStatus statusCode = list.getStatusCode();
		
		if (statusCode.is2xxSuccessful())
		{
			boolean isSemantic=Util.isSemanticPetition(request);				
			
			if (isSemantic) {
				
				Object body = list.getBody();
				
				Result<SubvencionConvocatoria> result=((Result<SubvencionConvocatoria>)body);
				
				List<SubvencionConvocatoria> records = result.getRecords();
				
				
				if (Util.isConvenioIntegration()) {
					for (SubvencionConvocatoria subvencion :records) {
						if (Util.validValue(subvencion.getInstrumentaId()))
						{
							subvencion.setInstrumentaTitle(null);
						}
						
					}
				}else {
					for (SubvencionConvocatoria subvencion :records) {		
						 subvencion.setInstrumentaIdIsolated(subvencion.getInstrumentaId());						
						 subvencion.setInstrumentaId(null);											
					}
				}
			}		
		}
	
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private ResponseEntity<SubvencionConvocatoria> configureDataArrays(ResponseEntity<SubvencionConvocatoria> list, HttpServletRequest request) {

		if (list != null) {		
			
			HttpStatus statusCode = list.getStatusCode();

			if (statusCode.is2xxSuccessful()) {
				
					boolean isCSV = Util.isCsvPetition(request);

					Object body = list.getBody();

					Result<SubvencionConvocatoria> result = ((Result<SubvencionConvocatoria>) body);

					List<SubvencionConvocatoria> records = result.getRecords();
					
					if (isCSV)
					{
						for (SubvencionConvocatoria subvencion : records) {							
							if ( Util.validValue(subvencion.getClasificacionEconomicaGastoSimple())) {
								subvencion.setClasificacionEconomicaGasto(Arrays.asList(subvencion.getClasificacionEconomicaGastoSimple()));
							}							
							if ( Util.validValue(subvencion.getClasificacionProgramaSimple())) {
								subvencion.setClasificacionPrograma(Arrays.asList(subvencion.getClasificacionProgramaSimple()));
							}
						}
					}
					else
					{
						for (SubvencionConvocatoria subvencion : records) {							
							if ( Util.validValue(subvencion.getClasificacionEconomicaGastoSimple())) {
								String[] split = subvencion.getClasificacionEconomicaGastoSimple().split(",");
								subvencion.setClasificacionEconomicaGasto(Arrays.asList(split));
							}							
							if ( Util.validValue(subvencion.getClasificacionProgramaSimple()) ) {
								String[] split = subvencion.getClasificacionProgramaSimple().split(",");
								subvencion.setClasificacionPrograma(Arrays.asList(split));
							}
						}
					}
				}
			}

			

		return list;
	}

	
	@SuppressWarnings("unchecked")
	private ResponseEntity<?> integraAll(ResponseEntity<SubvencionConvocatoria> list, HttpServletRequest request) {

		if (list != null) {
			
			list = (ResponseEntity<SubvencionConvocatoria>) gestionadoPor(list, request);
			
			list = (ResponseEntity<SubvencionConvocatoria>) integraConvenio(list, request);
			
			
		}

		return list;
	}

	
	private List<String> checkClavesExternas(SubvencionConvocatoria objCheck) {
		List<String> errores = new ArrayList<String>();
		// CHECK 1 SubvencionOrganization
		
		Set<String> organizationIds=new HashSet<String>();
		
		if (Util.validValue(objCheck.getAreaId()))
		{
		  organizationIds.add(objCheck.getAreaId());
		}
		if (Util.validValue(objCheck.getOrganizationId()))
		{
		  organizationIds.add(objCheck.getOrganizationId());
		}
		if (Util.validValue(objCheck.getServicioId()))
		{
		  organizationIds.add(objCheck.getServicioId());
		}
		if (Util.validValue(objCheck.getEntidadFinanciadoraId()))
		{
		  organizationIds.add(objCheck.getEntidadFinanciadoraId());
		}
						
		if (activeFK) {
		  	
		  	for (String organizationId:organizationIds)
		  	{
		  	  SubvencionOrganization findById = organizationService.findById(getKey(), SubvencionOrganization.class, organizationId);
		  	  if (findById == null) {
				errores.add("Organization not exists in entity: " + organizationId);
		  	  }
		  	}
		}
		
		return errores;
	}
}
