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

package org.ciudadesabiertas.dataset;

import static org.junit.Assert.assertTrue;

import org.ciudadesabiertas.config.WebConfig;
import org.ciudadesabiertas.dataset.controller.CuboEdadController;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { WebConfig.class })
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CuboEdadDataTest
{

	@Autowired
	private WebApplicationContext wac;	

	private MockMvc mockMvc;
	
	private String listURL=CuboEdadController.LIST;
	private String queryURL=CuboEdadController.QUERY;
	
	
	@Before
	public void setup() throws Exception
	{
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}


	@Test
	public void test_Busqueda_Id() throws Exception
	{
		String paramField="id";		
		String value = "obs1";
		JSONArray records = TestUtils.extractRecords(listURL, paramField, value, mockMvc);
		assertTrue(records.size() == 1);
	}

	
	@Test
	public void test_Busqueda_Sex() throws Exception
	{

		String [] paramField= {"sex","numeroPersonas"};
		String [] value = {"sex-F","37498"};		
		JSONArray records = TestUtils.extractRecords(listURL, paramField,  value, mockMvc);
		assertTrue(records.size() == 1);
	}
	
	@Test
	public void test_Busqueda_MunicipioId() throws Exception
	{		
		String [] paramField= {"municipioId","numeroPersonas"};
		String [] value = {"28006","37498"};		
		JSONArray records = TestUtils.extractRecords(listURL, paramField,  value, mockMvc);
		assertTrue(records.size() == 1);
	}
	
	@Test
	public void test_Busqueda_MunicipioTitle() throws Exception
	{
		String [] paramField= {"municipioTitle","numeroPersonas"};
		String [] value = {"Alcobendas","37498"};
		JSONArray records = TestUtils.extractRecords(listURL, paramField,  value, mockMvc);
		assertTrue(records.size() == 1);
	}
		
	@Test
	public void test_Busqueda_distritoId() throws Exception
	{
		String [] paramField= {"distritoId","numeroPersonas"};
		String [] value = {"2800601","37498"};
		JSONArray records = TestUtils.extractRecords(listURL, paramField,  value, mockMvc);
		assertTrue(records.size() == 1);
	}
	
	@Test
	public void test_Busqueda_distritoTitle() throws Exception
	{
		String [] paramField= {"distritoTitle","numeroPersonas"};
		String [] value = {"Distrito 1","37498"};
		JSONArray records = TestUtils.extractRecords(listURL, paramField,  value, mockMvc);
		assertTrue(records.size() == 1);
	}
	
	
	@Test
	public void test_Busqueda_barrioId() throws Exception
	{
		String [] paramField= {"barrioId","numeroPersonas"};
		String [] value = {"28006011","37498"};
		JSONArray records = TestUtils.extractRecords(listURL, paramField,  value, mockMvc);
		assertTrue(records.size() == 1);
	}
	
	@Test
	public void test_Busqueda_barrioTitle() throws Exception
	{
		String [] paramField= {"barrioTitle","numeroPersonas"};
		String [] value = {"Barrio 1","37498"};
		JSONArray records = TestUtils.extractRecords(listURL, paramField,  value, mockMvc);
		assertTrue(records.size() == 1);
	}
	
	@Test
	public void test_Busqueda_seccionCensalId() throws Exception
	{
		String [] paramField= {"seccionCensalId","numeroPersonas"};
		String [] value = {"2800601009","90073"};
		JSONArray records = TestUtils.extractRecords(listURL, paramField,  value, mockMvc);
		assertTrue(records.size() == 1);
	}
	
	@Test
	public void test_Busqueda_seccionCensalTitle() throws Exception
	{
		String [] paramField= {"seccionCensalTitle","numeroPersonas"};
		String [] value = {"Sección Censal 9","90073"};
		JSONArray records = TestUtils.extractRecords(listURL, paramField,  value, mockMvc);
		assertTrue(records.size() == 1);
	}
	
	@Test
	public void test_Busqueda_refPeriod() throws Exception
	{
		String [] paramField= {"refPeriod","numeroPersonas"};
		String [] value = {"2016","37498"};
		JSONArray records = TestUtils.extractRecords(listURL, paramField,  value, mockMvc);
		assertTrue(records.size() == 1);
	}
	
	@Test
	public void test_Busqueda_edad() throws Exception
	{
		String [] paramField= {"age","numeroPersonas"};
		String [] value = {"25","87232"};
		JSONArray records = TestUtils.extractRecords(listURL, paramField,  value, mockMvc);
		assertTrue(records.size() == 1);
	}
	
	@Test
	public void test_Busqueda_numeroPersonas() throws Exception
	{
		String paramField="numeroPersonas";
		String value = "37498";
		JSONArray records = TestUtils.extractRecords(listURL, paramField,  value.toUpperCase(), mockMvc);
		assertTrue(records.size() == 1);
	}

	
	
	 @Test    
    public void test_Query1Dimension() throws Exception 
	{   
    	String [] paramField= {"dimension","group","measure"};
		String [] value = {"refPeriod","AVG","numeroPersonas"};           
        JSONArray records = TestUtils.extractRecords(queryURL, paramField,  value, mockMvc);
		assertTrue(records.size() == 1);               
    }
	    
	    
    @Test    
    public void test_Query2Dimensions() throws Exception 
    {	
    	String [] paramField= {"dimension","group","measure"};
		String [] value = {"refPeriod,age","AVG","numeroPersonas"};   
		JSONArray records = TestUtils.extractRecords(queryURL, paramField,  value, mockMvc);
		assertTrue(records.size() == 12);
    }
	    
	    
    @Test    
    public void test_Query2DimensionsWhere() throws Exception
    {	
    	String [] paramField= {"dimension","group","measure","where"};
		String [] value = {"refPeriod,age","AVG","numeroPersonas","refPeriod=2016"};   
		JSONArray records = TestUtils.extractRecords(queryURL, paramField,  value, mockMvc);
		assertTrue(records.size() == 12);
    }
    
    @Test
   	public void test_Busqueda_AutonomiaPaisId() throws Exception
   	{		
   		String [] paramField= {"autonomiaId","paisId","numeroPersonas"};
   		String [] value = {"13","724","37498"};		
   		JSONArray records = TestUtils.extractRecords(listURL, paramField,  value, mockMvc);
   		assertTrue(records.size() == 1);
   		
   	
   	}
   	
   	@Test
   	public void test_Busqueda_AutonomiaPaisTitle() throws Exception
   	{
   		String [] paramField= {"autonomiaTitle","paisTitle","numeroPersonas"};
   		String [] value = {"Comunidad de Madrid","España","37498"};
   		JSONArray records = TestUtils.extractRecords(listURL, paramField,  value, mockMvc);
   		assertTrue(records.size() == 1);
   	}
   	
   	
   	@Test    
    public void test_Query3DimensionsPais() throws Exception 
    {	
    	String [] paramField= {"dimension","group","measure"};
		String [] value = {"refPeriod,age,paisTitle","AVG","numeroPersonas"};   
		JSONArray records = TestUtils.extractRecords(queryURL, paramField,  value, mockMvc);
		assertTrue(records.size() == 12);
    }

}
