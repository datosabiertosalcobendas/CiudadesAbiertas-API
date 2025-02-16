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

package org.ciudadesabiertas.alojamiento;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import javax.servlet.ServletContext;

import org.ciudadesAbiertas.rdfGeneratorZ.anotations.Rdf;
import org.ciudadesabiertas.config.WebConfig;
import org.ciudadesabiertas.dataset.controller.AlojamientoController;
import org.ciudadesabiertas.dataset.model.Alojamiento;
import org.ciudadesabiertas.dataset.utils.AlojamientoConstants;
import org.ciudadesabiertas.exception.DAOException;
import org.ciudadesabiertas.service.DatasetService;
import org.ciudadesabiertas.utils.TestUtils;
import org.ciudadesabiertas.utils.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;



/**
 * @author Juan Carlos Ballesteros (Localidata)
 * @author Carlos Martínez de la Casa (Localidata)
 * @author Oscar Corcho (UPM, Localidata)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { WebConfig.class })
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AlojamientoTest {
	
	private static final Logger log = LoggerFactory.getLogger(AlojamientoTest.class);
	
	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private DatasetService<Alojamiento> dService;
        
    private MockMvc mockMvc;
    
    @Before
    public void setup() throws Exception {

    	
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
       
    }

    @Test
    public void test01_Service() throws DAOException {
	
        final List<Alojamiento> users = dService.basicQuery(AlojamientoConstants.KEY,Alojamiento.class);
        
        assertTrue( users.size()>0 );


    }
    
    
    @Test
    public void test02_Controller() {
        ServletContext servletContext = wac.getServletContext();
         
        Assert.assertNotNull(servletContext);
        Assert.assertTrue(servletContext instanceof MockServletContext);
        Assert.assertNotNull(wac.getBean("alojamientoController"));
    }
    
    
    
    @Test    
    public void test03_List() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(AlojamientoController.LIST+".json")).andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    
    @Test    
    public void test04_Add() throws Exception {
    	String id ="TEST01_ALJ0001";
    	String obj = "{"    			
    		    +"\"id\" : \""+id+"\","
    		    +"\"title\" : \"Titulo de Pruebas\","
    		    +"\"categoria\" : \"PRUEBAS CATEGORIA\","
    		    +"\"description\" : \"DESCRIPCION PRUEBAS\","
    		    +"\"accesible\" : true,"
    		    +"\"tipoAccesibilidad\" : \"Variada\","
    		    +"\"municipioId\" : \"28079\","
    		    +"\"municipioTitle\" : \"Madrid\","
    		    +"\"streetAddress\" : \"Puerto del Milagro, 6\","
    		    +"\"postalCode\" : \"28018\","
    		    +"\"barrio\" : \"EMBAJADORES\","
    		    +"\"distrito\" : \"CENTRO\","
    		    +"\"xETRS89\" : 440396.51166,"
    		    +"\"yETRS89\" : 4474341.38044,"
    		    +"\"url\" : \"http://www.esmadrid.com/informacion-turistica/torre-san-jose\","
    		    +"\"email\" : \"reservations.atocha@coolrooms.com\","
    		    +"\"telephone\" : \"(+34) 91 088 77 80\","
    		    +"\"numHabitaciones\" : 110,"
    		    +"\"numCamas\" : 180,"
    		    +"\"modified\" : \"2019-03-28T00:00:00\","    	
    		    +"\"image\" : \"url de la imagen\""
        	  +"}";
    	    	    	
    	obj = new String (obj.getBytes(),"UTF-8");	
    	
    	 
        this.mockMvc.perform(MockMvcRequestBuilders.post(AlojamientoController.ADD)
        		.contentType(MediaType.APPLICATION_JSON)
    	        .content(obj))	
        	
        	.andExpect(MockMvcResultMatchers.status().isCreated());
    }
    
    @Test    
    public void test05_Add_NO_OK_400() throws Exception {
    	String agendaADD = "{"     	
    			+"}";
    	
    	 
        this.mockMvc.perform(MockMvcRequestBuilders.post(AlojamientoController.ADD)
        		.contentType(MediaType.APPLICATION_JSON)
    	        .content(agendaADD))	
        	
        	.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    
    @Test    
    public void test06_Add_NO_OK_409() throws Exception {
    	String id ="TEST01_ALJ0001";
    	String obj = "{"    			
    		    +"\"id\" : \""+id+"\","
    		    +"\"title\" : \"Titulo de Pruebas\","
    		    +"\"categoria\" : \"PRUEBAS CATEGORIA\","
    		    +"\"description\" : \"DESCRIPCION PRUEBAS\","
    		    +"\"accesible\" : true,"
    		    +"\"tipoAccesibilidad\" : \"Variada\","
    		    +"\"municipioId\" : \"28079\","
    		    +"\"municipioTitle\" : \"Madrid\","
    		    +"\"streetAddress\" : \"Puerto del Milagro, 6\","
    		    +"\"postalCode\" : \"28018\","
    		    +"\"barrio\" : \"EMBAJADORES\","
    		    +"\"xETRS89\" : 440396.51166,"
    		    +"\"yETRS89\" : 4474341.38044,"
    		    +"\"longitud\" : -3.65799129,"
    		    +"\"url\" : \"http://www.esmadrid.com/informacion-turistica/torre-san-jose\","
    		    +"\"email\" : \"reservations.atocha@coolrooms.com\","
    		    +"\"telephone\" : \"(+34) 91 088 77 80\","
    		    +"\"numHabitaciones\" : 110,"
    		    +"\"numCamas\" : 180,"
    		    +"\"modified\" : \"2019-03-28T00:00:00\","    	
    		    +"\"image\" : \"url de la imagen\""
        	  +"}";
    	
    	obj = new String (obj.getBytes(),"UTF-8");	
    	
    	 
        this.mockMvc.perform(MockMvcRequestBuilders.post(AlojamientoController.ADD)
        		.contentType(MediaType.APPLICATION_JSON)
    	        .content(obj))	
        	
        	.andExpect(MockMvcResultMatchers.status().isConflict());
    }
    
    @Test    
    public void test07_Update() throws Exception {
    	String id ="TEST01_ALJ0001";
    	String obj = "{"    			
    		    +"\"id\" : \""+id+"\","
    		    +"\"title\" : \"Titulo de Pruebas MODIF \","
    		    +"\"categoria\" : \"PRUEBAS CATEGORIA XXX\","
    		    +"\"description\" : \"DESCRIPCION PRUEBAS XXX\","
    		    +"\"accesible\" : true,"
    		    +"\"tipoAccesibilidad\" : \"Variada\","
    		    +"\"municipioId\" : \"28079\","
    		    +"\"municipioTitle\" : \"Madrid\","
    		    +"\"streetAddress\" : \"Puerto del Milagro, 6\","
    		    +"\"postalCode\" : \"28018\","
    		    +"\"barrio\" : \"EMBAJADORES\","
    		    +"\"distrito\" : \"CENTRO\","
    		    +"\"xETRS89\" : 440396.51166,"
    		    +"\"yETRS89\" : 4474341.38044,"
    		    +"\"url\" : \"http://www.esmadrid.com/informacion-turistica/torre-san-jose\","
    		    +"\"email\" : \"reservations.atocha@coolrooms.com\","
    		    +"\"telephone\" : \"(+34) 91 088 77 80\","
    		    +"\"numHabitaciones\" : 112,"
    		    +"\"numCamas\" : 182,"
    		    +"\"modified\" : \"2019-03-29T00:00:00\","    	
    		    +"\"image\" : \"url de la imagen MODUIF\""
        	  +"}";
    	
    	String agendaUPDATE = new String (obj.getBytes(),"UTF-8");	
    	
    	 
        this.mockMvc.perform(MockMvcRequestBuilders.put(AlojamientoController.ADD+"/"+id)
        		.contentType(MediaType.APPLICATION_JSON)
    	        .content(agendaUPDATE))	
            
	        .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test    
    public void test08_Update_NO_OK_400() throws Exception {
    	String id ="2Test01186";
    	String agendaUPDATE = "{"      	
    			+"}";
    	
    	 
        this.mockMvc.perform(MockMvcRequestBuilders.put(AlojamientoController.ADD+"/"+id)
        		.contentType(MediaType.APPLICATION_JSON)
    	        .content(agendaUPDATE))	
            
	        .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    
    @Test    
    public void test09_Update_NO_OK_404() throws Exception {    	
    	String id ="TEST01_NOOK_ALJ0001";
    	String obj = "{"    			
    		    +"\"id\" : \""+id+"\","
    		    +"\"title\" : \"Titulo de Pruebas\","
    		    +"\"categoria\" : \"PRUEBAS CATEGORIA\","
    		    +"\"description\" : \"DESCRIPCION PRUEBAS\","
    		    +"\"accesible\" : true,"
    		    +"\"tipoAccesibilidad\" : \"Variada\","
    		    +"\"municipioId\" : \"28079\","
    		    +"\"municipioTitle\" : \"Madrid\","
    		    +"\"streetAddress\" : \"Puerto del Milagro, 6\","
    		    +"\"postalCode\" : \"28018\","
    		    +"\"barrio\" : \"EMBAJADORES\","
    		    +"\"distrito\" : \"CENTRO\","
    		    +"\"xETRS89\" : 440396.51166,"
    		    +"\"yETRS89\" : 4474341.38044,"
    		    +"\"url\" : \"http://www.esmadrid.com/informacion-turistica/torre-san-jose\","
    		    +"\"email\" : \"reservations.atocha@coolrooms.com\","
    		    +"\"telephone\" : \"(+34) 91 088 77 80\","
    		    +"\"numHabitaciones\" : 110,"
    		    +"\"numCamas\" : 180,"
    		    +"\"modified\" : \"2019-03-28T00:00:00\","    	
    		    +"\"image\" : \"url de la imagen\""
        	  +"}";
    	
    	String agendaUPDATE = new String (obj.getBytes(),"UTF-8");	
    	
    	 
        this.mockMvc.perform(MockMvcRequestBuilders.put(AlojamientoController.ADD+"/"+id)
        		.contentType(MediaType.APPLICATION_JSON)
    	        .content(agendaUPDATE))	
            
	        .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    
   
    
    @Test    
    public void test10_Record() throws Exception {
    	String id ="TEST01_ALJ0001";
    	    	
        this.mockMvc.perform(MockMvcRequestBuilders.get(AlojamientoController.ADD+"/"+id+".json")
        		.contentType(MediaType.APPLICATION_JSON))	
            
	        .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test    
    public void test11_Record_HEAD() throws Exception {
    	String id ="TEST01_ALJ0001";
    	
        this.mockMvc.perform(MockMvcRequestBuilders.head(AlojamientoController.ADD+"/"+id+".json")
        		.contentType(MediaType.APPLICATION_JSON))
	        .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test    
    public void test12_Record_NO_OK_404() throws Exception {
    	String id ="TEST01_NOOK_PTI0001";
    	    	
        this.mockMvc.perform(MockMvcRequestBuilders.get(AlojamientoController.ADD+"/"+id+".json")
        		.contentType(MediaType.APPLICATION_JSON))	
            
	        .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    
    @Test    
    public void test13_Delete() throws Exception {
    	String id ="TEST01_ALJ0001";
    	    	
        this.mockMvc.perform(MockMvcRequestBuilders.delete(AlojamientoController.ADD+"/"+id)
        		.contentType(MediaType.APPLICATION_JSON))	
            
	        .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test    
    public void test14_Delete_NO_OK_404() throws Exception {
    	String id ="TEST01_NOOK_PTI0001";
    	    	
        this.mockMvc.perform(MockMvcRequestBuilders.delete(AlojamientoController.ADD+"/"+id)
        		.contentType(MediaType.APPLICATION_JSON))	
            
	        .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    
    @Test    
    public void test15_List_HEAD() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.head(AlojamientoController.LIST+".json")).andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test    
    public void test16_List_RDF() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.head(AlojamientoController.LIST+".rdf")).andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test    
    public void test16_List_HEAD_303() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.head(AlojamientoController.LIST)).andExpect(MockMvcResultMatchers.status().is(303));
    }
    
    @Test    
    public void test17_List_303() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(AlojamientoController.LIST)).andExpect(MockMvcResultMatchers.status().is(303));
    }

    @Test    
    public void test18_Post_Transform() throws Exception {        
    	String id ="TEST01_TRASFORM_ALJ0001";
    	String obj = "{"    			
    		    +"\"id\" : \""+id+"\","
    		    +"\"title\" : \"Titulo de Pruebas\","
    		    +"\"categoria\" : \"PRUEBAS CATEGORIA\","
    		    +"\"description\" : \"DESCRIPCION PRUEBAS\","
    		    +"\"accesible\" : true,"
    		    +"\"tipoAccesibilidad\" : \"Variada\","
    		    +"\"municipioId\" : \"28079\","
    		    +"\"municipioTitle\" : \"Madrid\","
    		    +"\"streetAddress\" : \"Puerto del Milagro, 6\","
    		    +"\"postalCode\" : \"28018\","
    		    +"\"barrio\" : \"EMBAJADORES\","
    		    +"\"distrito\" : \"CENTRO\","
    		    +"\"xETRS89\" : 440396.51166,"
    		    +"\"yETRS89\" : 4474341.38044,"
    		    +"\"url\" : \"http://www.esmadrid.com/informacion-turistica/torre-san-jose\","
    		    +"\"email\" : \"reservations.atocha@coolrooms.com\","
    		    +"\"telephone\" : \"(+34) 91 088 77 80\","
    		    +"\"numHabitaciones\" : 110,"
    		    +"\"numCamas\" : 180,"
    		    +"\"modified\" : \"2019-03-28T00:00:00\","    	
    		    +"\"image\" : \"url de la imagen\""
        	  +"}";
    	
    	String agendaTransform = new String (obj.getBytes(),"UTF-8");	
    	
    	 
    	this.mockMvc.perform(MockMvcRequestBuilders.post(AlojamientoController.TRANSFORM)
        		.contentType(MediaType.APPLICATION_JSON)
    	        .content(agendaTransform))	
        	
        	.andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test    
    public void test19_Post_Transform_NO_OK() throws Exception {
    	String id ="TEST01_TRASFORM_AGENDA0001";
    	String obj = "{"    			
    		    +"\"id\" : \""+id+"\","
    		   // +"\"title\" : \"Titulo de Pruebas MODIF\","
    		    +"\"categoria\" : \"XXPRUEBAS CATEGORIAXXX\","
    		    +"\"description\" : \"DESCRIPCION PRUEBAS MODIF\","
    		    +"\"accesible\" : true,"
    		    +"\"tipoAccesibilidad\" : \"Variada\","
    		    +"\"municipioId\" : \"28079\","
    		    +"\"municipioTitle\" : \"Madrid\","
    		    +"\"streetAddress\" : \"Puerto del Milagro, 6\","
    		    +"\"postalCode\" : \"28018\","
    		    +"\"barrio\" : \"EMBAJADORES\","
    		    +"\"distrito\" : \"CENTRO\","
    		    +"\"xETRS89\" : 440396.51166,"
    		    +"\"yETRS89\" : 4474341.38044,"
    		    +"\"web\" : \"http://www.esmadrid.com/informacion-turistica/torre-san-jose\","
    		    +"\"siglo\" : \"XX\","
    		    +"\"estiloArtistico\" : \"Renacentista\","
    		    +"\"costeMantenimiento\" : 19830.98,"
    		    +"\"ingresosObtenidos\" : 1260980.02,"
    		    +"\"afluenciaPublico\" : \"Jovenes y Adultos\","
    		    +"\"fechaDeclaracionBien\" : \"2019-03-28T00:00:00\","
    		    +"\"modoAcceso\" : \"Ascensores y Escaleras Mecánicas\","
    		    +"\"medioTransporte\" : \"Autobuses, cernanias y Metro\","
    		    +"\"notaHistorica\" : \"NOTA HISTORICA PRUEBAS MODIF\","
    		    +"\"openingHours\" : \"HORARIO DE APERTURA PRUEBAS MODIF\","
    		    +"\"image\" : \"\""
        	  +"}";
    	
    	String agendaTransform = new String (obj.getBytes(),"UTF-8");	
    	
    	 
    	this.mockMvc.perform(MockMvcRequestBuilders.post(AlojamientoController.TRANSFORM)
        		.contentType(MediaType.APPLICATION_JSON)
    	        .content(agendaTransform))	
        	
        	.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    
    @Test    
    public void test20_Database_and_vocabulary() throws Exception {
    	
    	Field[] declaredFields = (Alojamiento.class).getDeclaredFields();
    	
    	boolean checkFields=true;
    	
    	for (Field f:declaredFields)
    	{
    		Rdf annotation=f.getAnnotation(Rdf.class);
    		if (annotation!=null)
    		{        		        		
    			if (!Util.validatorFieldRDF(f.getName(), annotation.propiedad()))
    			{
    				log.info(f.getName()+" vs. "+annotation.propiedad() );
    				checkFields=false;
    			}
    		}
    	}
    	
    	assertTrue(checkFields);
    	
    }
    
    @Test    
    public void test21_List_Sort_200() throws Exception {
    	String sort="?sort=-id,title";
        this.mockMvc.perform(MockMvcRequestBuilders.get(AlojamientoController.LIST+".json"+sort)).andExpect(MockMvcResultMatchers.status().is(200));
    }
    
    @Test    
    public void test22_List_Sort_400() throws Exception {
    	String sort="?sort=-id,erroneo";
        this.mockMvc.perform(MockMvcRequestBuilders.get(AlojamientoController.LIST+".json"+sort)).andExpect(MockMvcResultMatchers.status().is(400));
    }
    
    @Test
	public void test23_Busqueda_geo() throws Exception
	{	
		String sort="?xETRS89=440929.42&yETRS89=4477579.19&meters=1000";
        this.mockMvc.perform(MockMvcRequestBuilders.get(AlojamientoController.GEO_LIST+".json"+sort)).andExpect(MockMvcResultMatchers.status().is(200));
	}
    
    @Test    
    public void test24_List_Distinct_200() throws Exception {
    	String sort="?field=id";
        this.mockMvc.perform(MockMvcRequestBuilders.get(AlojamientoController.SEARCH_DISTINCT+".json"+sort)).andExpect(MockMvcResultMatchers.status().is(200));
    }
    
    @Test
    public void test25_List_Formatos_200() throws Exception {    	    	
    	boolean checkAllFormats=TestUtils.checkFormatURIs(AlojamientoController.LIST, true, mockMvc);
    	assertTrue(checkAllFormats);    	    	
    }
    
    @Test
    public void test26_List_RDF_200() throws Exception {    	
    	String theURI = TestUtils.checkRDFURI(this.mockMvc,AlojamientoController.LIST);        
        this.mockMvc.perform(MockMvcRequestBuilders.get(theURI)).andExpect(MockMvcResultMatchers.status().is(200));    	    	
    }
    
    @Test
    public void test27_Record_Formatos_200() throws Exception {    	    	
    	boolean checkAllFormats=TestUtils.checkFormatURIs(AlojamientoController.LIST+"/"+"ALJ0001", true, mockMvc);
    	assertTrue(checkAllFormats);    	    	
    }
}
