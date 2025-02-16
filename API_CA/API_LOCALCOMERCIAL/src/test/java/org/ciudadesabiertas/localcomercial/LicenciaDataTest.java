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

package org.ciudadesabiertas.localcomercial;

import static org.junit.Assert.assertTrue;

import org.ciudadesabiertas.config.WebConfig;
import org.ciudadesabiertas.dataset.controller.LicenciaController;
import org.ciudadesabiertas.utils.TestUtils;
import org.json.simple.JSONArray;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
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
public class LicenciaDataTest
{

	@Autowired
	private WebApplicationContext wac;


	private String listURL=LicenciaController.LIST;

	private MockMvc mockMvc;


	@Before
	public void setup() throws Exception
	{
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}


	@Test
	public void test_Busqueda_Id() throws Exception
	{
		String paramField="id";		
		String value = "60000068-106-1993-02762";
		JSONArray records = TestUtils.extractRecords(listURL, paramField, value, mockMvc);
		assertTrue(records.size() == 1);		
	}
	
	@Test
	public void test_Busqueda_Identifier() throws Exception
	{
		String paramField="identifier";		
		String value = "60000068/106-1993-02762";
		
		JSONArray records = TestUtils.extractRecords(listURL, paramField, value, mockMvc);
		assertTrue(records.size() == 1);		
	}
	
	
	@Test
	public void test_Busqueda_tipo_asociadaA() throws Exception
	{
		String [] paramField= {"asociadaA"};
		String [] value = {"60000068"};
		long total = TestUtils.extractTotal(listURL, paramField, value, mockMvc);
		assertTrue(total== 1);			
	}
	
	@Test
	public void test_Busqueda_tipo_autorizaActividadEconomica() throws Exception
	{
		String [] paramField= {"autorizaActividadEconomica"};
		String [] value = {"52"};
		long total = TestUtils.extractTotal(listURL, paramField, value, mockMvc);
		assertTrue(total== 5);			
	}
	
	
	@Test
	public void test_Busqueda_tipo_estadoTramitacion() throws Exception
	{
		String [] paramField= {"estadoTramitacion"};
		String [] value = {"concedida"};
		long total = TestUtils.extractTotal(listURL, paramField, value, mockMvc);
		assertTrue(total== 603);			
	}
	
	
	@Test
	public void test_Busqueda_tipo_fechaAlta() throws Exception
	{
		String [] paramField= {"fechaAlta","estadoTramitacion"};
		String [] value = {"1900-01-01T00:00:00","concedida"};
		long total = TestUtils.extractTotal(listURL, paramField, value, mockMvc);
		assertTrue(total== 57);			
	}
	
	
	@Test
	public void test_Busqueda_tipo_fechaCese() throws Exception
	{
		String [] paramField= {"fechaCese"};
		String [] value = {"2040-02-16T00:00:00"};
		long total = TestUtils.extractTotal(listURL, paramField, value, mockMvc);
		assertTrue(total== 613);			
	}
	
	
	@Test
	public void test_Busqueda_tipo_fechaSolicitud() throws Exception
	{
		String [] paramField= {"fechaSolicitud","estadoTramitacion"};
		String [] value = {"2010-02-16T00:00:00","en tramite"};
		long total = TestUtils.extractTotal(listURL, paramField, value, mockMvc);
		assertTrue(total== 10);			
	}
	
	
	@Test
	public void test_Busqueda_tipo_seOtorgaA() throws Exception
	{
		String [] paramField= {"seOtorgaA","estadoTramitacion"};
		String [] value = {"Mario Gomez Gomez","en tramite"};
		long total = TestUtils.extractTotal(listURL, paramField, value, mockMvc);
		assertTrue(total== 10);			
	}
	
	
			
	@Test
	public void test_Head_MD5() throws Exception
	{

		String value = "060-1985-22975";	
		
		String paramField="id";		

		String md5_content = TestUtils.extractContentMD5(listURL, paramField, value, mockMvc);
		
		String md5_head = TestUtils.extractHeadMD5(listURL, paramField, value, mockMvc);

		assertTrue(md5_content.equals(md5_head));
	}
	
	@Test
	public void test_Busqueda_distinct() throws Exception
	{
		
		String paramField="field";
		
		String value = "fechaAlta";

		long total = TestUtils.extractTotalDistinct(LicenciaController.SEARCH_DISTINCT, paramField, value, mockMvc);

		assertTrue(total == 302);
	}
	


	
	
	
	
}
