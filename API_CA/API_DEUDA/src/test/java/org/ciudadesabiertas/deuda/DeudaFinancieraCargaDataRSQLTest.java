/**
 * 
 */
package org.ciudadesabiertas.deuda;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.ciudadesabiertas.config.WebConfig;
import org.ciudadesabiertas.dataset.controller.DeudaFinancieraCargaController;
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
public class DeudaFinancieraCargaDataRSQLTest {

private static final Logger log = LoggerFactory.getLogger(DeudaFinancieraCargaDataRSQLTest.class);


@Autowired
private WebApplicationContext wac;

private MockMvc mockMvc;

private String listURL = DeudaFinancieraCargaController.LIST;

// Manually config for spring to use Parameterised
private TestContextManager testContextManager;

@Parameter(value = 0)
public String expression;

@Parameter(value = 1)
public Integer expected;

// Posibles valores que tomaran los parámetros anteriores
@Parameters(name = "{index}: test {0}")
public static Collection<Object[]> data() {
  Collection<Object[]> params = new ArrayList<>();

  params.add(new Object[] { "id=='2017-06-21-ES1234567893'", 1 });
  params.add(new Object[] { "date=='2017-06-21'", 1 });
  params.add(new Object[] { "importe==692332.00", 2 });
  params.add(new Object[] { "instrumentoFinanciacion=='ES1234567893'", 2 });
  params.add(new Object[] { "anio=='2017'", 2 });
  params.add(new Object[] { "gastoOPasivoFinanciero=='gasto financiero'", 2 });
  params.add(new Object[] { "description=='AMORT.VTO.21/12/17 PTM L/P MDEC-I.C.O.(20,77MM)%'", 1 });

  
  return params;
}

@Before
public void setup() throws Exception {
  this.testContextManager = new TestContextManager(getClass());
  this.testContextManager.prepareTestInstance(this);
  this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
}



@Test
public void test_evaluador_RSQL() throws Exception {
  
  JSONArray records = TestUtils.extractRecords(listURL, "q", expression, mockMvc);
  
  try
	{
	assertTrue(records.size() == expected);
	}
	catch (AssertionError e)
	{
	  log.error("Assertion error");
	  log.error("  Expression: "+expression);
	  log.error("  Expected: "+expected);
	  
	  throw new AssertionError("Incorrect value on Expresion "+expression+": "+records.size(), new Throwable("Expected: "+expected));
	}
}

}
