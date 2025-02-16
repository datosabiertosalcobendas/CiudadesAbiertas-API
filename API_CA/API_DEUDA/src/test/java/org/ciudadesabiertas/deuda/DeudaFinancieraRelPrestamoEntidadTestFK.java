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

package org.ciudadesabiertas.deuda;

import static org.junit.Assert.assertFalse;

import org.ciudadesabiertas.config.WebConfig;
import org.ciudadesabiertas.dataset.controller.DeudaFinancieraRelPrestamoEntidadController;
import org.ciudadesabiertas.utils.StartVariables;
import org.ciudadesabiertas.utils.Util;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
public class DeudaFinancieraRelPrestamoEntidadTestFK {
	
	private static boolean activeFK = StartVariables.activeFK;
	
	@Autowired
	private WebApplicationContext wac;
        
    private MockMvc mockMvc;
    
    @Before
    public void setup() throws Exception {    	
    	this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();    	
    }
      
    @Test
	public void test01_Add() throws Exception {

		if (activeFK == false) {
			assertFalse(activeFK);
		} else {

			String id ="RELPRES000X-OK-01";
	     	String item = " {\n"  
	    			+"	    \"id\": \""+id+"\",\n"
	    			+"      \"prestamoId\": \"ES1234567893\",\n"
	    			+"      \"entidadPrestamista\": \"ENTIDADPRUEBAS001\"\n"
	    			+"    }"; 	
	    	    	    	

			item = new String(item.getBytes(), "UTF-8");

			this.mockMvc.perform(MockMvcRequestBuilders.post(DeudaFinancieraRelPrestamoEntidadController.ADD)
					.contentType(MediaType.APPLICATION_JSON).content(item))
					.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

		}
	}

	@Test
	public void test02_Add_ERROR_FK_PRESTAMO() throws Exception {

		if (activeFK == false) {
			assertFalse(activeFK);
		} else {

			String id ="RELPRES000X-OK-02";
	     	String item = " {\n"  
	    			+"	    \"id\": \""+id+"\",\n"
	    			//Modificamos
	    			+"      \"prestamoId\": \"ES1234567893-KO\",\n"
	    			+"      \"entidadPrestamista\": \"ENTIDADPRUEBAS001\"\n"
	    			+"    }"; 	

			item = new String(item.getBytes(), "UTF-8");

			this.mockMvc.perform(MockMvcRequestBuilders.post(DeudaFinancieraRelPrestamoEntidadController.ADD)
					.contentType(MediaType.APPLICATION_JSON).content(item))
					.andExpect(MockMvcResultMatchers.status().isConflict());

		}
	}
	
	@Test
	public void test03_Add_ERROR_FK_ENTIDAD() throws Exception {

		if (activeFK == false) {
			assertFalse(activeFK);
		} else {

			String id ="RELPRES000X-OK-02";
	     	String item = " {\n"  
	    			+"	    \"id\": \""+id+"\",\n"
	    			+"      \"prestamoId\": \"ES1234567893\",\n"
	    			//Modificamos
	    			+"      \"entidadPrestamista\": \"ENTIDADPRUEBAS001-KO\"\n"
	    			+"    }"; 	

			item = new String(item.getBytes(), "UTF-8");

			this.mockMvc.perform(MockMvcRequestBuilders.post(DeudaFinancieraRelPrestamoEntidadController.ADD)
					.contentType(MediaType.APPLICATION_JSON).content(item))
					.andExpect(MockMvcResultMatchers.status().isConflict());

		}
	}

	@Test
	public void test04_Update() throws Exception {

		if (activeFK == false) {
			assertFalse(activeFK);
		} else {

			String id ="RELPRES000X-OK-01";
	     	String item = " {\n"  
	    			+"	    \"id\": \""+id+"\",\n"
	    			+"      \"prestamoId\": \"ES1234567893\",\n"
	    			+"      \"entidadPrestamista\": \"02-50-297-AO-005\"\n"
	    			+"    }"; 

			String itemUPDATE = new String(item.getBytes(), "UTF-8");

			id = Util.encodeURL(id);

			this.mockMvc
					.perform(MockMvcRequestBuilders.put(DeudaFinancieraRelPrestamoEntidadController.ADD + "/" + id)
							.contentType(MediaType.APPLICATION_JSON).content(itemUPDATE))

					.andExpect(MockMvcResultMatchers.status().isOk());

		}
	}

	@Test
	public void test05_Update_ERROR_FK_PRESTAMO() throws Exception {

		if (activeFK == false) {
			assertFalse(activeFK);
		} else {

			String id ="RELPRES000X-OK-01";
	     	String item = " {\n"  
	    			+"	    \"id\": \""+id+"\",\n"
	    			//Modificamos
	    			+"      \"prestamoId\": \"ES1234567893-KO\",\n"
	    			+"      \"entidadPrestamista\": \"ENTIDADPRUEBAS001\"\n"
	    			+"    }"; 

			String itemUPDATE = new String(item.getBytes(), "UTF-8");

			id = Util.encodeURL(id);

			this.mockMvc
					.perform(MockMvcRequestBuilders.put(DeudaFinancieraRelPrestamoEntidadController.ADD + "/" + id)
							.contentType(MediaType.APPLICATION_JSON).content(itemUPDATE))

					.andExpect(MockMvcResultMatchers.status().isConflict());

		}
	}

	@Test
	public void test06_Update_ERROR_FK_ENTIDAD() throws Exception {

		if (activeFK == false) {
			assertFalse(activeFK);
		} else {

			String id ="RELPRES000X-OK-01";
	     	String item = " {\n"  
	    			+"	    \"id\": \""+id+"\",\n"
	    			+"      \"prestamoId\": \"ES1234567893\",\n"
	    			//Modificamos
	    			+"      \"entidadPrestamista\": \"ENTIDADPRUEBAS001-KO\"\n"
	    			+"    }"; 

			String itemUPDATE = new String(item.getBytes(), "UTF-8");

			id = Util.encodeURL(id);

			this.mockMvc
					.perform(MockMvcRequestBuilders.put(DeudaFinancieraRelPrestamoEntidadController.ADD + "/" + id)
							.contentType(MediaType.APPLICATION_JSON).content(itemUPDATE))

					.andExpect(MockMvcResultMatchers.status().isConflict());

		}
	}
	
	

	@Test
	public void test99_Delete() throws Exception {
		if (activeFK == false) {
			assertFalse(activeFK);
		} else {

			String id ="RELPRES000X-OK-01";

			id = Util.encodeURL(id);

			this.mockMvc
					.perform(MockMvcRequestBuilders.delete(DeudaFinancieraRelPrestamoEntidadController.ADD + "/" + id)
							.contentType(MediaType.APPLICATION_JSON))

					.andExpect(MockMvcResultMatchers.status().isOk());

		}

	}
           
    
}
