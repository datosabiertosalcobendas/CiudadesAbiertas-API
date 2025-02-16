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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ciudadesAbiertas.rdfGeneratorZ.anotations.Context;
import org.ciudadesAbiertas.rdfGeneratorZ.anotations.PathId;
import org.ciudadesAbiertas.rdfGeneratorZ.anotations.Rdf;
import org.ciudadesAbiertas.rdfGeneratorZ.anotations.RdfExternalURI;
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
import com.opencsv.bean.CsvDate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * @author Juan Carlos Ballesteros (Localidata)
 * @author Carlos Martínez de la Casa (Localidata)
 * @author Oscar Corcho (UPM, Localidata)
 * @author Hugo Lafuente (Localidata)
 */
@Entity
@ApiModel
@Table(name = "trafico_observacion")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder(alphabetic=false)
@JsonIgnoreProperties({Constants.IKEY})
@JacksonXmlRootElement(localName = Constants.RECORD)
@Rdf(contexto = Context.ESTRAF, propiedad = "ObservacionTrafico")
@PathId(value="/trafico/observacion")
public class TraficoObservacion  implements java.io.Serializable, RDFModel {
	
	@JsonIgnore
	private static final long serialVersionUID = -1504640833269124191L;	
	
	@ApiModelProperty(hidden = true)
	@JsonIgnore
	private String ikey;	
	
	@ApiModelProperty(value = "Referencia inequívoca al recurso dentro de un contexto dado. Ejemplo: TRAFOBS01")
	@CsvBindByPosition(position=1)
	@CsvBindByName(column=Constants.IDENTIFICADOR, format=Constants.STRING_FORMAT)
	@Rdf(contexto = Context.DCT, propiedad = Constants.IDENTIFIER)
	private String id;
	
	@ApiModelProperty(value = "Relación que enlaza la propiedad que se observa. Ejemplo: intensidad")
	@CsvBindByPosition(position=2)
	@CsvBindByName(column="observedProperty", format=Constants.STRING_FORMAT)
	@Rdf(contexto = Context.SOSA, propiedad = "observedProperty")
	@RdfExternalURI(inicioURI="/trafico/propiedad-medicion/",finURI="observedProperty", urifyLevel = 1)
	private String observedProperty;
	
	@ApiModelProperty(value = "Esta propiedad establece la fecha/hora de la observación. Ejemplo: 2020-04-01 12:45:00")
	@CsvBindByPosition(position=3)
	@CsvBindByName(column="resultTime")
	@CsvDate(Constants.DATE_TIME_FORMAT)
	@Rdf(contexto = Context.SOSA, propiedad = "resultTime" ,typeURI=Context.XSD_URI+"dateTime")
	private Date resultTime;	
	
	@ApiModelProperty(value = "Esta propiedad muestra el resultado de la observación. Ejemplo: 30.00")
	@CsvBindByPosition(position=4)
	@CsvBindByName(column="hasSimpleResult")
	@Rdf(contexto = Context.SOSA, propiedad = "hasSimpleResult")
	private BigDecimal hasSimpleResult;
	
	@ApiModelProperty(value = "Una relación entre una Observación y la entidad para la que se ha observado. Ejemplo: TRAFTRAM01")
	@CsvBindByPosition(position=4)
	@CsvBindByName(column="hasFeatureInterest")
	@Rdf(contexto = Context.SOSA, propiedad = "hasFeatureOfInterest")
	@RdfExternalURI(inicioURI="/trafico/tramo/",finURI="hasFeatureOfInterest", urifyLevel = 1)
	private String hasFeatureOfInterest;
		
	@ApiModelProperty(value = "Esta propiedad permite conocer si se ha producido una validación de la observación o no. Ejemplo: 0")
	@CsvBindByPosition(position=5)
	@CsvBindByName(column="validada")
	@Rdf(contexto = Context.ESTRAF, propiedad = "validada", typeURI=Context.XSD_URI+"boolean")
	private Boolean validada;
	
	@ApiModelProperty(value = "Esta propiedad establece el intervalo de tiempo de la observación. Ejemplo: TRAPROINT01")
	@CsvBindByPosition(position=6)
	@CsvBindByName(column="phenomenonTime")
	@Rdf(contexto = Context.TIME, propiedad = "phenomenonTime" )
	@RdfExternalURI(inicioURI="/trafico/proper-interval/",finURI="phenomenonTime", urifyLevel = 1)
	private String phenomenonTime;

	public TraficoObservacion()
	{
	}	
	
	public TraficoObservacion(TraficoObservacion copia) {
		super();
		this.ikey = copia.ikey;
		this.id = copia.id;
		this.observedProperty = copia.observedProperty;
		this.resultTime = copia.resultTime;
		this.hasSimpleResult = copia.hasSimpleResult;
		this.hasFeatureOfInterest = copia.hasFeatureOfInterest;
		this.validada = copia.validada;
		this.phenomenonTime = copia.phenomenonTime;
	}


	public TraficoObservacion(TraficoObservacion copia, List<String> attributesToSet)
	{
		if (attributesToSet.contains(Constants.IKEY)) {
			this.ikey = copia.ikey;
		}
		if (attributesToSet.contains(Constants.IDENTIFICADOR)) {
			this.id = copia.id;
		}
		if (attributesToSet.contains("observedProperty")) {
			this.observedProperty = copia.observedProperty;		
		}
		if (attributesToSet.contains("resultTime")) {
			this.resultTime = copia.resultTime;
		}
		if (attributesToSet.contains("hasSimpleResult")) {
			this.hasSimpleResult = copia.hasSimpleResult;
		}
		if (attributesToSet.contains("hasFeatureOfInterest")) {
			this.hasFeatureOfInterest = copia.hasFeatureOfInterest;
		}
		if (attributesToSet.contains("validada")) {
			this.validada = copia.validada;
		}
		if (attributesToSet.contains("phenomenonTime")) {
			this.phenomenonTime = copia.phenomenonTime;
		}
	}


	@Id
	@Column(name = "ikey", unique = true, nullable = false, length = 50)
	public String getIkey() {
		return this.ikey;
	}

	public void setIkey(String ikey) {
		this.ikey = ikey;
	}

	@Column(name = "id", unique = true, nullable = false, length = 50)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "observed_property", nullable = false, length = 100)
	public String getObservedProperty() {
		return this.observedProperty;
	}

	public void setObservedProperty(String observedProperty) {
		this.observedProperty = observedProperty;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "result_time", nullable = false, length = 19)
	public Date getResultTime() {
		return this.resultTime;
	}

	public void setResultTime(Date resultTime) {
		this.resultTime = resultTime;
	}

	@Column(name = "has_simple_result", nullable = false, precision = 12)
	public BigDecimal getHasSimpleResult() {
		return this.hasSimpleResult;
	}

	public void setHasSimpleResult(BigDecimal hasSimpleResult) {
		this.hasSimpleResult = hasSimpleResult;
	}
	
	@Column(name = "has_feature_interest", unique = true, nullable = false, length = 50)
	public String getHasFeatureOfInterest() {
		return hasFeatureOfInterest;
	}

	public void setHasFeatureOfInterest(String hasFeatureInterest) {
		this.hasFeatureOfInterest = hasFeatureInterest;
	}


	@Column(name = "validada")
	public Boolean getValidada() {
		return this.validada;
	}

	public void setValidada(Boolean validada) {
		this.validada = validada;
	}

	@Column(name = "phenomenon_time", length = 200)
	public String getPhenomenonTime() {
		return this.phenomenonTime;
	}

	public void setPhenomenonTime(String phenomenonTime) {
		this.phenomenonTime = phenomenonTime;
	}

	@Override
	public String toString() {
		return "TraficoObservacion [ikey=" + ikey + ", id=" + id + ", observedProperty=" + observedProperty
				+ ", resultTime=" + resultTime + ", hasSimpleResult=" + hasSimpleResult + ", hasFeatureOfInterest="
				+ hasFeatureOfInterest + ", validada=" + validada + ", phenomenonTime=" + phenomenonTime + "]";
	}

	public Map<String,String> prefixes()
	{
		Map<String,String> prefixes=new HashMap<String,String>();				
		prefixes.put(Context.XSD, Context.XSD_URI);
		prefixes.put(Context.DCT, Context.DCT_URI);			
		prefixes.put(Context.ESTRAF, Context.ESTRAF_URI);
		prefixes.put(Context.SOSA, Context.SOSA_URI);
		prefixes.put(Context.TIME, Context.TIME_URI);
		
		return prefixes;
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> T cloneModel(T copia, List<String> listado) 
	{
		return (T) cloneClass((TraficoObservacion) copia, listado);
	}
	
	public TraficoObservacion cloneClass(TraficoObservacion copia, List<String> attributesToSet) {

		TraficoObservacion obj = new TraficoObservacion(copia,attributesToSet);		

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
		
		return result;
	}
	


	
	




}

