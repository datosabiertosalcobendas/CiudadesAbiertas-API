/**
 * 
 */
package org.ciudadesabiertas.empleo;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import org.ciudadesabiertas.config.WebConfig;
import org.ciudadesabiertas.dataset.controller.OfertaEmpleoController;
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
public class OfertaEmpleoDataTest {

private static final Logger log = LoggerFactory.getLogger(OfertaEmpleoDataTest.class);


@Autowired
private WebApplicationContext wac;

private MockMvc mockMvc;

private String listURL = OfertaEmpleoController.LIST;

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
  params.add(new Object[] { "id", "oferta001", 1 });
  params.add(new Object[] { "title", "Oferta Informatica", 1 });
  params.add(new Object[] { "description", "Oferta Informatica para el Ayuntamiento", 1 });
  params.add(new Object[] { "datePublished", "2020-11-24T11:00:00", 1 });
  params.add(new Object[] { "fechaAprobacion", "2020-11-24T12:00:00", 1 });
  params.add(new Object[] { "dateModified", "2020-11-17T12:00:00", 1 });
  params.add(new Object[] { "municipioId", "28079", 2 });
  params.add(new Object[] { "municipioTitle", "Madrid", 2 });
  params.add(new Object[] { "provinciaId", "28", 2 });
  params.add(new Object[] { "provinciaTitle", "Madrid", 2 });
  params.add(new Object[] { "autonomiaId", "13", 2 });
  params.add(new Object[] { "autonomiaTitle", "Comunidad de Madrid", 2 });
  params.add(new Object[] { "boletinOficialId", "boletin001", 1 }); 

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
  
	JSONArray records = TestUtils.extractRecords(listURL, paramName, value, mockMvc);
	try
	{
	assertTrue(records.size() == expected);
	}
	catch (AssertionError e)
	{
	  log.error("Assertion error");
	  log.error("  Param: "+paramName);
	  log.error("  Value: "+value);
	  log.error("  Expected: "+expected);	  

	  throw new AssertionError("Incorrect value on Param "+paramName+": "+records.size(), new Throwable("Expected: "+expected));
	}
}

}
