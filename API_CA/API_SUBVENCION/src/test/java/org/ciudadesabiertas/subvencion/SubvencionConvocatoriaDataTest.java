/**
 * 
 */
package org.ciudadesabiertas.subvencion;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import org.ciudadesabiertas.config.WebConfig;
import org.ciudadesabiertas.dataset.controller.SubvencionConvocatoriaController;
import org.ciudadesabiertas.utils.TestUtils;
import org.json.simple.JSONArray;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Juan Carlos Ballesteros (Localidata)
 * @author Carlos Martínez de la Casa (Localidata)
 * @author Hugo Lafuente Matesanz (Localidata)
 * @author Oscar Corcho (UPM, Localidata)
 *
 */
@RunWith(Parameterized.class)
@ContextConfiguration(classes = { WebConfig.class })
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SubvencionConvocatoriaDataTest {

private static final Logger log = LoggerFactory.getLogger(SubvencionConvocatoriaDataTest.class);


@Autowired
private WebApplicationContext wac;

private MockMvc mockMvc;

private String listURL = SubvencionConvocatoriaController.LIST;

// Manually config for spring to use Parameterised
private TestContextManager testContextManager;

@Parameter(value = 0)
public String paramName;

@Parameter(value = 1)
public String value;

@Parameter(value = 2)
public Integer expected;

// Posibles valores que tomaran los parámetros anteriores
@Parameters(name = "{index}: test {0}")
public static Collection<Object[]> data() {
  Collection<Object[]> params = new ArrayList<>();
  params.add(new Object[] { "id", "SUB1", 1 });
  params.add(new Object[] { "title","CONVOCATORIA PREMIOS ARGANZUELA XXXII EDICION PINTURA Y X EDICIÓN FOTOGRAFÍA", 1 });
  params.add(new Object[] { "basesReguladoras","https://www.bocm.es/boletin/CM_Orden_BOCM/2013/11/22/BOCM-20131122-34.PDF", 97 });
  params.add(new Object[] { "tipoInstrumento","PRÉSTAMOS", 93 });
  params.add(new Object[] { "nominativa","false", 231 });  
  params.add(new Object[] { "tipoProcedimiento","subvencion-directa", 95 });
  params.add(new Object[] { "objeto","Finalidad de la subvención SUB1", 1 });
  params.add(new Object[] { "importeTotalConcedido","116.39", 1 });
  params.add(new Object[] { "fechaAcuerdo","2018-07-20T00:00:00", 1 });
  params.add(new Object[] { "clasificacionPrograma","1522", 18 });
  params.add(new Object[] { "clasificacionEconomicaGasto","453", 19 });
  params.add(new Object[] { "instrumentaId","CONV001", 21 });
  params.add(new Object[] { "instrumentaTitle","CONVENIO DE SUBVENCIÓN ENTRE EL AYUNTAMIENTO Y LOS BENEFICIARIOS PARA EL DESARROLLO DEL PROGRAMA HIGHT", 10 });
  params.add(new Object[] { "tieneTematica","deporte", 27 });
  params.add(new Object[] { "gestionadoPorOrganization","false", 129 });
  params.add(new Object[] { "organizationId","A05003369", 74 });
  params.add(new Object[] { "gestionadoPorDistrito","true", 129 });
  params.add(new Object[] { "distritoId","28079602", 5 });  
  params.add(new Object[] { "distritoTitle","Arganzuela", 5 });
  params.add(new Object[] { "areaId","A05003355", 5 });
  params.add(new Object[] { "servicioId","A05003341", 129 });
  params.add(new Object[] { "entidadFinanciadoraId","A05003362", 13 });
  
  params.add(new Object[] { "importePresupuestado","129.32", 1 });
  params.add(new Object[] { "anioFiscal","2016", 194 });
  params.add(new Object[] { "gestionadoPorDistrito","false", 313 });
  params.add(new Object[] { "programaAsText","11", 1 });
  params.add(new Object[] { "gastoAsText","10000", 3 });
  params.add(new Object[] { "organizationId","A05003340", 123 });
  
  return params;
}

@Before
public void setup() throws Exception {
  this.testContextManager = new TestContextManager(getClass());
  this.testContextManager.prepareTestInstance(this);
  this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
}



@Test
public void test_evaluador_DATA() throws Exception {
  

	long total = TestUtils.extractTotal(listURL, paramName, value, mockMvc);

	
	
	try
	{
		assertTrue(total == expected);
	}
	catch (AssertionError e)
	{
	  log.error("Assertion error");
	  log.error("  Param: "+paramName);
	  log.error("  Value: "+value);
	  log.error("  Expected: "+expected);	  

//	  throw new AssertionError("Incorrect value on Param "+paramName+": "+records.size(), new Throwable("Expected: "+expected));
	  throw new AssertionError("Incorrect value on Param "+paramName+": "+total, new Throwable("Expected: "+expected));
	}
}

}
