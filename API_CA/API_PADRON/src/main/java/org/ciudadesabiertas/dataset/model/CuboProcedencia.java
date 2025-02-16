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

package org.ciudadesabiertas.dataset.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.ciudadesAbiertas.rdfGeneratorZ.anotations.Context;
import org.ciudadesAbiertas.rdfGeneratorZ.anotations.PathId;
import org.ciudadesAbiertas.rdfGeneratorZ.anotations.Rdf;
import org.ciudadesAbiertas.rdfGeneratorZ.anotations.RdfDinamico;
import org.ciudadesAbiertas.rdfGeneratorZ.anotations.RdfExternalURI;
import org.ciudadesabiertas.model.DataCubeModel;
import org.ciudadesabiertas.model.RDFModel;
import org.ciudadesabiertas.utils.Constants;
import org.ciudadesabiertas.utils.Util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * @author Juan Carlos Ballesteros (Localidata)
 * @author Carlos Martínez de la Casa (Localidata)
 * @author Oscar Corcho (UPM, Localidata)
 *
 */
@Entity
@ApiModel
@Table(name = "padron_procedencia")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder(alphabetic=false)
@JsonIgnoreProperties({Constants.IKEY})
@JacksonXmlRootElement(localName = Constants.RECORD)
@Rdf(contexto = Context.QB, propiedad = "Observation")
@PathId(value="/padron/datacube/procedencia/observation")
public class CuboProcedencia implements java.io.Serializable, RDFModel, DataCubeModel {
	
	@JsonIgnore
	private static final long serialVersionUID = -1504640833269124191L;

	@ApiModelProperty(hidden = true)
	@JsonIgnore
	private String ikey;	
	
	@ApiModelProperty(value = "Identificador de la observación. Ejemplo: obs1")
	@CsvBindByPosition(position=1)
	@CsvBindByName(column=Constants.IDENTIFICADOR, format=Constants.STRING_FORMAT)
	@Rdf(contexto = Context.DCT, propiedad = Constants.IDENTIFIER)
	private String id;	
	
	@ApiModelProperty(value = "Conjunto de datos de la observación. Ejemplo: poblacionPorProcedencia")
	@Transient
	@JsonIgnore
	@Rdf(contexto = Context.SKOS, propiedad = "notation")
	@RdfExternalURI(tipo=Context.QB_URI+"DataSet",inicioURI="/padron/datacube/", finURI="dataset", propiedad=Context.QB_URI+"dataset")
	private String dataset;
	
	@ApiModelProperty(value = "DSD de la observación. Ejemplo: poblacionPorProcedencia")
	@Transient
	@JsonIgnore
	@Rdf(contexto = Context.QB, propiedad = "structure")
	@RdfExternalURI(tipo=Context.QB_URI+"DataSet",inicioURI="/data-cube/data-structure-definition/", finURI="dsd", propiedad=Context.QB_URI+"structure")
	private String dsd;
	
	@ApiModelProperty(value = "El período de tiempo o punto en el tiempo al que se refiere la observación. Ejemplo: 2016")
	@CsvBindByPosition(position=2)		
	@CsvBindByName(column="refPeriod", format=Constants.STRING_FORMAT)
	@Rdf(contexto = Context.SDMXTDIMENSION , propiedad = "refPeriod")
	@RdfExternalURI(inicioURI="http://reference.data.gov.uk/id/year/", finURI="refPeriod", urifyLevel=1)
	private String refPeriod;		
	
	@ApiModelProperty(value = "Edad por grupos quinquenales de la observación. Ejemplo: 00-a-04")
	@CsvBindByPosition(position=3)
	@CsvBindByName(column="edad", format=Constants.STRING_FORMAT)
	@RdfDinamico(inicioURI="https://opendata.aragon.es/kos/iaest/edad-grupos-quinquenales/", finURI="edadGruposQuinquenales", propiedad=Context.IAESTDIMENSION_URI+"edad-grupos-quinquenales")
	private String edadGruposQuinquenales;
	
	@ApiModelProperty(value = "El nivel más alto de un programa educativo que la persona ha completado con éxito. Ejemplo: 43")
	@CsvBindByPosition(position=4)		
	@CsvBindByName(column="tipo_nivel_estudio", format=Constants.STRING_FORMAT)
	@Rdf(contexto = Context.SDMXTDIMENSION , propiedad = "educationLev")
	@RdfExternalURI(inicioURI="http://vocab.linkeddata.es/datosabiertos/kos/demografia/padron-municipal/tipo-nivel-estudio/", finURI="tipoNivelEstudio", urifyLevel=1)
	private String tipoNivelEstudio;	
		
	@ApiModelProperty(value = "Municipio de procedencia de la observación. Ejemplo: 15030")
	@CsvBindByPosition(position=5)		
	@CsvBindByName(column="municipio_procedencia", format=Constants.STRING_FORMAT)
	@Rdf(contexto = Context.ESPAD , propiedad = "municipio-procedencia")
	private String municipioProcedencia;
	
	@ApiModelProperty(value = "Provincia de procedencia de la observación. Ejemplo: a-coruna")
	@CsvBindByPosition(position=6)		
	@CsvBindByName(column="provincia_procedencia", format=Constants.STRING_FORMAT)
	@Rdf(contexto = Context.ESPAD , propiedad = "provincia-procedencia")
	private String provinciaProcedencia;
	
	@ApiModelProperty(value = "Identificador del país de la observación. Ejemplo: 724")
	@CsvBindByPosition(position=7)
	@CsvBindByName(column="pais", format=Constants.STRING_FORMAT)
	@Rdf(contexto = Context.ESADM, propiedad = "pais")
	@RdfExternalURI(inicioURI="/territorio/pais/", finURI="pais", urifyLevel=1)
	private String paisId;
	
	@ApiModelProperty(value = "Nombre del páis de la observación. Ejemplo: España")
	@CsvBindByPosition(position=8)	
	@CsvBindByName(column="paisTitle", format=Constants.STRING_FORMAT)	
	private String paisTitle;
	
	@ApiModelProperty(value = "Identificador de la autonomía de la observación. Ejemplo: 13")
	@CsvBindByPosition(position=9)
	@CsvBindByName(column="autonomia", format=Constants.STRING_FORMAT)
	@Rdf(contexto = Context.ESADM, propiedad = "autonomia")
	@RdfExternalURI(inicioURI="/territorio/autonomia/", finURI="autonomia", urifyLevel=1)
	private String autonomiaId;
	
	@ApiModelProperty(value = "Nombre de la autonomía de la observación. Ejemplo: Comunidad de Madrid")
	@CsvBindByPosition(position=10)	
	@CsvBindByName(column="autonomiaTitle", format=Constants.STRING_FORMAT)	
	private String autonomiaTitle;

	@ApiModelProperty(value = "Identificador del municipio de la observación. Ejemplo: 28006")
	@CsvBindByPosition(position=11)	
	@CsvBindByName(column="municipioId", format=Constants.STRING_FORMAT)
	@Rdf(contexto = Context.SDMXTDIMENSION, propiedad = "refArea")
	@RdfExternalURI(inicioURI="/territorio/municipio/", finURI="municipioId", urifyLevel=1)
	private String municipioId;
	
	@ApiModelProperty(value = "Nombre del municipio de la observación. Ejemplo: Alcobendas")
	@CsvBindByPosition(position=12)	
	@CsvBindByName(column="municipioTitle", format=Constants.STRING_FORMAT)	
	private String municipioTitle;

	@ApiModelProperty(value = "Identificador del distrito de la observación. Ejemplo: 2800601")
	@CsvBindByPosition(position=13)	
	@CsvBindByName(column="distritoId", format=Constants.STRING_FORMAT)	
	@Rdf(contexto = Context.SDMXTDIMENSION, propiedad = "refArea")
	@RdfExternalURI(inicioURI="/territorio/distrito/", finURI="distritoId", urifyLevel=1)
	private String distritoId;
	
	@ApiModelProperty(value = "Nombre del distrito de la observación. Ejemplo: Distrito 1")
	@CsvBindByPosition(position=14)	
	@CsvBindByName(column="distritoTitle", format=Constants.STRING_FORMAT)
	private String distritoTitle;
	
	@ApiModelProperty(value = "Identificador del barrio de la observación. Ejemplo: 28006011")
	@CsvBindByPosition(position=15)	
	@CsvBindByName(column="barrioId", format=Constants.STRING_FORMAT)
	@Rdf(contexto = Context.SDMXTDIMENSION, propiedad = "refArea")
	@RdfExternalURI(inicioURI="/territorio/barrio/", finURI="barrioId", urifyLevel=1)
	private String barrioId;
	
	@ApiModelProperty(value = "Nombre del barrio de la observación. Ejemplo: Barrio 1")
	@CsvBindByPosition(position=16)	
	@CsvBindByName(column="barrioTitle", format=Constants.STRING_FORMAT)
	private String barrioTitle;
	
	@ApiModelProperty(value = "Identificador de la sección censal de la observación. Ejemplo: 2800601020")
	@CsvBindByPosition(position=17)	
	@CsvBindByName(column="seccionCensalId", format=Constants.STRING_FORMAT)
	@Rdf(contexto = Context.SDMXTDIMENSION, propiedad = "refArea")
	@RdfExternalURI(inicioURI="/territorio/seccion-censal/", finURI="seccionCensalId", urifyLevel=1)
	private String seccionCensalId;
	
	@ApiModelProperty(value = "Nombre de la sección censal de la observación. Ejemplo: Sección Censal 20")
	@CsvBindByPosition(position=18)	
	@CsvBindByName(column="seccionCensalTitle", format=Constants.STRING_FORMAT)	
	private String seccionCensalTitle;	
	
	@ApiModelProperty(value = "Numero de personas de la observación. Ejemplo: 19076")
	@CsvBindByPosition(position=19)
	@CsvBindByName(column="numeroPersonas")
	@Rdf(contexto = Context.ESPADMEDIDA, propiedad="numero-personas", typeURI=Context.XSD_URI+"int")
	private Integer numeroPersonas;
	
	public void asignaCubo()
	{
		dataset="poblacionPorProcedencia";
	}
	
	public void asignaDSD()
	{
		dsd="poblacionPorProcedencia";
	}
	
	public CuboProcedencia()
	{
//		numeroPersonas=Constants.defaultNumberValue;
	}

	public CuboProcedencia(CuboProcedencia copia)
	{		
		this.ikey = copia.ikey;
		this.id = copia.id;
		this.paisId = copia.paisId;
		this.paisTitle = copia.paisTitle;
		this.autonomiaId = copia.autonomiaId;
		this.autonomiaTitle = copia.autonomiaTitle;
		this.municipioId = copia.municipioId;
		this.municipioTitle= copia.municipioTitle;
		this.distritoId= copia.distritoId;
		this.distritoTitle= copia.distritoTitle;
		this.barrioId= copia.barrioId;
		this.barrioTitle= copia.barrioTitle;
		this.seccionCensalId= copia.seccionCensalId;
		this.seccionCensalTitle= copia.seccionCensalTitle;
		this.refPeriod= copia.refPeriod;
		this.edadGruposQuinquenales= copia.edadGruposQuinquenales;
		this.municipioProcedencia=copia.municipioProcedencia;
		this.provinciaProcedencia=copia.provinciaProcedencia;
		this.edadGruposQuinquenales=copia.edadGruposQuinquenales;
		this.tipoNivelEstudio=copia.tipoNivelEstudio;
		this.numeroPersonas= copia.numeroPersonas;

	}

	
	public CuboProcedencia(CuboProcedencia copia, List<String> attributesToSet)
	{
		if (attributesToSet.contains(Constants.IKEY)) {
			this.ikey = copia.ikey;
		}	
		if (attributesToSet.contains(Constants.IDENTIFICADOR)) {
			this.id = copia.id;
		}
		if (attributesToSet.contains("edadGruposQuinquenales")) {
			this.edadGruposQuinquenales = copia.edadGruposQuinquenales;
		}
		
		if (attributesToSet.contains("paisId")) {
			this.paisId = copia.paisId;
		}
		
		if (attributesToSet.contains("paisTitle")) {
			this.paisTitle = copia.paisTitle;
		}
		
		if (attributesToSet.contains("autonomiaId")) {
			this.autonomiaId = copia.autonomiaId;
		}
		
		if (attributesToSet.contains("autonomiaTitle")) {
			this.autonomiaTitle = copia.autonomiaTitle;
		}
		
		if (attributesToSet.contains("distritoId")) {
			this.distritoId = copia.distritoId;
		}
		if (attributesToSet.contains("distritoTitle")) {
			this.distritoTitle = copia.distritoTitle;
		}
		if (attributesToSet.contains("municipioId")) {
			this.municipioId = copia.municipioId;
		}
		if (attributesToSet.contains("municipioTitle")) {
			this.municipioTitle = copia.municipioTitle;
		}	
		if (attributesToSet.contains("barrioId")) {
			this.barrioId = copia.barrioId;
		}
		if (attributesToSet.contains("barrioTitle")) {
			this.barrioTitle = copia.barrioTitle;
		}	
		if (attributesToSet.contains("seccionCensalId")) {
			this.seccionCensalId = copia.seccionCensalId;
		}
		if (attributesToSet.contains("seccionCensalTitle")) {
			this.seccionCensalTitle = copia.seccionCensalTitle;
		}	
		if (attributesToSet.contains("refPeriod")) {
			this.refPeriod = copia.refPeriod;
		}
		if (attributesToSet.contains("numeroPersonas")) {
			this.numeroPersonas = copia.numeroPersonas;
		}
		
		if (attributesToSet.contains("municipioProcedencia")) {
			this.municipioProcedencia=copia.municipioProcedencia;
		}
		
		if (attributesToSet.contains("provinciaProcedencia")) {
			this.provinciaProcedencia=copia.provinciaProcedencia;
		}
		
		if (attributesToSet.contains("edadGruposQuinquenales")) {
			this.edadGruposQuinquenales=copia.edadGruposQuinquenales;
		}
		
		
		if (attributesToSet.contains("tipoNivelEstudio")) {
			this.tipoNivelEstudio=copia.tipoNivelEstudio;
		}
		
		
			
		
	}




	@Id
	@Column(name = "ikey", unique = true, nullable = false, length = 50)
	public String getIkey()
	{
		return this.ikey;
	}

	public void setIkey(String ikey)
	{
		this.ikey = ikey;
	}

	@Column(name = "id", nullable = false, length = 50)
	public String getId()
	{
		return this.id;
	}

	public void setId(String id)
	{
		this.id = id;
	}
	
	@Column(name = "municipio_id", nullable = false, length = 50)
	public String getMunicipioId() {
		return this.municipioId;
	}

	public void setMunicipioId(String municipioId) {
		this.municipioId = municipioId;
	}

	@Column(name = "municipio_title", nullable = false, length = 400)
	public String getMunicipioTitle() {
		return this.municipioTitle;
	}

	public void setMunicipioTitle(String municipioTitle) {
		this.municipioTitle = municipioTitle;
	}

	@Column(name = "edad_grupos_quinquenales", nullable = false, length = 50)
	public String getEdadGruposQuinquenales() {
		return this.edadGruposQuinquenales;
	}

	public void setEdadGruposQuinquenales(String edadGruposQuinquenales) {
		this.edadGruposQuinquenales = edadGruposQuinquenales;
	}

	
	@Column(name = "pais_id", length = 50)
	public String getPaisId() {
		return paisId;
	}

	public void setPaisId(String paisId) {
		this.paisId = paisId;
	}

	@Column(name = "pais_title", length = 400)
	public String getPaisTitle() {
		return paisTitle;
	}

	public void setPaisTitle(String paisTitle) {
		this.paisTitle = paisTitle;
	}

	@Column(name = "autonomia_id", length = 50)
	public String getAutonomiaId() {
		return autonomiaId;
	}

	public void setAutonomiaId(String autonomiaId) {
		this.autonomiaId = autonomiaId;
	}
	
	@Column(name = "autonomia_title", length = 400)
	public String getAutonomiaTitle() {
		return autonomiaTitle;
	}

	public void setAutonomiaTitle(String autonomiaTitle) {
		this.autonomiaTitle = autonomiaTitle;
	}
	
	@Column(name = "distrito_id", length = 50)
	public String getDistritoId() {
		return this.distritoId;
	}

	public void setDistritoId(String distritoId) {
		this.distritoId = distritoId;
	}

	
	@Column(name = "distrito_title", length = 400)
	public String getDistritoTitle() {
		return this.distritoTitle;
	}

	public void setDistritoTitle(String distritoTitle) {
		this.distritoTitle = distritoTitle;
	}
	
	@Column(name = "barrio_id", length = 50)
	public String getBarrioId() {
		return this.barrioId;
	}

	public void setBarrioId(String barrioId) {
		this.barrioId = barrioId;
	}

	@Column(name = "barrio_title", length = 400)
	public String getBarrioTitle() {
		return this.barrioTitle;
	}

	public void setBarrioTitle(String barrioTitle) {
		this.barrioTitle = barrioTitle;
	}

	@Column(name = "ref_period", nullable = false, length = 50)
	public String getRefPeriod() {
		return this.refPeriod;
	}

	public void setRefPeriod(String refPeriod) {
		this.refPeriod = refPeriod;
	}
	
	@Column(name = "numero_personas", nullable = false)
	public Integer getNumeroPersonas() {
		return this.numeroPersonas;
	}

	public void setNumeroPersonas(Integer numeroPersonas) {
		this.numeroPersonas = numeroPersonas;
	}
	
	@Column(name = "municipio_procedencia", length = 50)
	public String getMunicipioProcedencia() {
		return this.municipioProcedencia;
	}

	public void setMunicipioProcedencia(String municipioProcedencia) {
		this.municipioProcedencia = municipioProcedencia;
	}
	
	@Column(name = "provincia_procedencia", length = 50)
	public String getProvinciaProcedencia() {
		return this.provinciaProcedencia;
	}

	public void setProvinciaProcedencia(String provinciaProcedencia) {
		this.provinciaProcedencia = provinciaProcedencia;
	}
	
	
	@Column(name = "seccion_censal_id", length = 50)
	public String getSeccionCensalId() {
		return this.seccionCensalId;
	}

	public void setSeccionCensalId(String seccionCensalId) {
		this.seccionCensalId = seccionCensalId;
	}

	@Column(name = "seccion_censal_title", length = 400)
	public String getSeccionCensalTitle() {
		return this.seccionCensalTitle;
	}

	public void setSeccionCensalTitle(String seccionCensalTitle) {
		this.seccionCensalTitle = seccionCensalTitle;
	}

	@Transient
	public String getDataset() {
		return dataset;
	}
	
	@Transient
	public void setDataset(String dataset) {
		 this.dataset=dataset;
	}
	
	
	@Transient
	public String getDsd() {
		return dsd;
	}
	
	@Transient
	public void setDsd(String dsd) {
		 this.dsd=dsd;
	}
	
	

	@Column(name = "tipo_nivel_estudio", nullable = false, length = 50)
	public String getTipoNivelEstudio() {
		return this.tipoNivelEstudio;
	}

	public void setTipoNivelEstudio(String tipoNivelEstudio) {
		this.tipoNivelEstudio = tipoNivelEstudio;
	}
	

	public Map<String,String> prefixes()
	{
		Map<String,String> prefixes=new HashMap<String,String>();				
		prefixes.put(Context.XSD, Context.XSD_URI);
		prefixes.put(Context.DCT, Context.DCT_URI);	
		//prefixes.put(Context.SCHEMA, Context.SCHEMA_URI);
		prefixes.put(Context.QB, Context.QB_URI);
		prefixes.put(Context.SKOS, Context.SKOS_URI);
		prefixes.put(Context.ESPAD, Context.ESPAD_URI);
		prefixes.put(Context.ESPADMEDIDA, Context.ESPADMEDIDA_URI);
		prefixes.put(Context.IAESTDIMENSION, Context.IAESTDIMENSION_URI);
		prefixes.put(Context.SDMXTDIMENSION, Context.SDMXDIMENSION_URI);
		
		return prefixes;
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> T cloneModel(T copia, List<String> listado) 
	{
		return (T) cloneClass((CuboProcedencia) copia, listado);
	}
	
	public CuboProcedencia cloneClass(CuboProcedencia copia, List<String> attributesToSet) {

		CuboProcedencia obj = new CuboProcedencia(copia,attributesToSet);		

		return obj;

	}

	@Override
	public List<String> validate()
	{
		List<String> result= new ArrayList<String>();
		
		//Validamos campos Obligatorios ver si es posible obtener esta información mediante anotaciones del modelo
		if (!Util.validValue(this.getId())) {
			result.add("Id is not valid [Id:"+this.getId()+"]");
		}
		
		
		if (!Util.validValue(this.getNumeroPersonas())) {
			result.add("NumeroPersonas is not valid [Id:"+this.getNumeroPersonas()+"]");
		}
		
		return result;
	}

	@Override
	public String toString() {
		return "CuboProcedencia [ikey=" + ikey + ", id=" + id + ", dataset=" + dataset + ", dsd=" + dsd + ", municipioId=" + municipioId + ", municipioTitle=" + municipioTitle + ", distritoId=" + distritoId
				+ ", distritoTitle=" + distritoTitle + ", barrioId=" + barrioId + ", barrioTitle=" + barrioTitle
				+ ", seccionCensalId=" + seccionCensalId + ", seccionCensalTitle=" + seccionCensalTitle + ", refPeriod="
				+ refPeriod + ", edadGruposQuinquenales=" + edadGruposQuinquenales + ", tipoNivelEstudio="
				+ tipoNivelEstudio + ", municipioProcedencia=" + municipioProcedencia + ", provinciaProcedencia="
				+ provinciaProcedencia + ", numeroPersonas=" + numeroPersonas + "]";
	}
	

	

	
	




}
