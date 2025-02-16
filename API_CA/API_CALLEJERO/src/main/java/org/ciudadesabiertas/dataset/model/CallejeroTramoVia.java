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
@Table(name = "callejero_tramo_via")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder(alphabetic = false)
@JsonIgnoreProperties({ Constants.IKEY })
@JacksonXmlRootElement(localName = Constants.RECORD)
@Rdf(contexto = Context.ESCJR, propiedad = "TramoVia")
@PathId(value = "/callejero/tramo")
public class CallejeroTramoVia implements java.io.Serializable, RDFModel
{

	@JsonIgnore
	private static final long serialVersionUID = 1235116450110749613L;

	@ApiModelProperty(hidden = true)
	@JsonIgnore
	private String ikey;	
	
	@ApiModelProperty(value = "Identificador del tramo. Ejemplo: TRA000001")
	@CsvBindByPosition(position = 1)
	@CsvBindByName(column = Constants.IDENTIFICADOR, format = Constants.STRING_FORMAT)
	@Rdf(contexto = Context.DCT, propiedad = Constants.IDENTIFIER)
	private String id;
	
	@ApiModelProperty(value = "Nombre del tramo. Ejemplo: Tramo comprendido entre el número 1 hasta el 33 de la/del Calle Bravo Murillo")
	@CsvBindByPosition(position = 2)
	@CsvBindByName(column = "title", format = Constants.STRING_FORMAT)
	@Rdf(contexto = Context.DCT, propiedad = "title")
	private String title;

	@ApiModelProperty(value = "Validez del elemento en su ciclo de vida (versión). Puede ser histórico, vigente o provisional. Ejemplo: vigente")
	@CsvBindByPosition(position = 3)
	@CsvBindByName(column = "estado", format = Constants.STRING_FORMAT)
	@Rdf(contexto = Context.ESADM, propiedad = "estado")
	@RdfExternalURI(inicioURI= "http://vocab.linkeddata.es/datosabiertos/kos/sector-publico/territorio/tipoEstado/", finURI="estado")
	private String estado; 
	
	@ApiModelProperty(value = "Una junta administrativa rige los intereses peculiares de un pueblo que, en unión con otros, forma un municipio. En determinados municipios pueden existir subdivisiones de este concepto. Ejemplo: Chamberí")
	@CsvBindByPosition(position = 4)
	@CsvBindByName(column = "juntaAdministrativa", format = Constants.STRING_FORMAT)
	@Rdf(contexto = Context.ESADM, propiedad = "juntaAdministrativa")		
	private String juntaAdministrativa;
	
	@ApiModelProperty(value = "Número de la vía donde comienza el tramo. Un tramo puede tener uno o dos números iniciales. Puede indicar el par y el impar del tramo de la calle. Ejemplo: 1")
	@CsvBindByPosition(position = 5)
	@CsvBindByName(column = "numeroInicioTramo", format = Constants.STRING_FORMAT)
	@Rdf(contexto = Context.ESCJR, propiedad = "numeroInicioTramo")
	private String numeroInicioTramo;
	
	@ApiModelProperty(value = "Número de la vía donde finaliza el tramo. Un tramo puede tener uno o dos números finales. Puede indicar el par y el impar del tramo de la calle. Puede tener el valor \"FINAL\", para indicar que el tramo acaba al final de la calle. Ejemplo: 33")
	@CsvBindByPosition(position = 6)
	@CsvBindByName(column = "numeroFinTramo", format = Constants.STRING_FORMAT)
	@Rdf(contexto = Context.ESCJR, propiedad = "numeroFinTramo")
	private String numeroFinTramo;
	
	@ApiModelProperty(value = "Vía de comunicación construida para la circulación. En su definición según el modelo de direcciones de la Administración General del Estado, Incluye calles, carreteras de todo tipo, caminos, vías de agua, pantalanes, etc. Asimismo, incluye la pseudovía., es decir todo aquello que complementa o sustituye a la vía. En nuestro caso, este término se utiliza para hacer referencia a las vías urbanas. Ejemplo: 114600")
	@CsvBindByPosition(position = 7)
	@CsvBindByName(column = "via", format = Constants.STRING_FORMAT)
	@Rdf(contexto = Context.ESCJR, propiedad = "via")
	@RdfExternalURI(inicioURI= "/callejero/via/", finURI="via")	
	private String via;
	
	@ApiModelProperty(value = "Identificador del municipio del tramo. Ejemplo: 28079")
	@CsvBindByPosition(position = 8)
	@CsvBindByName(column = "municipioId", format = Constants.STRING_FORMAT)
	@Rdf(contexto = Context.ESADM, propiedad = "municipio")
	@RdfExternalURI(inicioURI="/territorio/municipio/",finURI="municipioId", urifyLevel = 1)
	private String municipioId;
	
	@ApiModelProperty(value = "Nombre del municipio del tramo. Ejemplo: Madrid")
	@CsvBindByPosition(position = 9)
	@CsvBindByName(column = "municipioTitle", format = Constants.STRING_FORMAT)
	private String municipioTitle;
	

	public CallejeroTramoVia()
	{
		super();
	}

	public CallejeroTramoVia(CallejeroTramoVia copia)
	{
		super();
		this.ikey = copia.ikey;
		this.via = copia.via;
		this.id = copia.id;
		this.title = copia.title;
		this.estado = copia.estado;
		this.municipioId = copia.municipioId;
		this.municipioTitle = copia.municipioTitle;
		this.juntaAdministrativa = copia.juntaAdministrativa;
		this.numeroInicioTramo = copia.numeroInicioTramo;
		this.numeroFinTramo = copia.numeroFinTramo;
	}



	public CallejeroTramoVia(CallejeroTramoVia copia, List<String> attributesToSet)
	{
		if (attributesToSet.contains(Constants.IKEY))
		{
			this.ikey = copia.ikey;
		}
		if (attributesToSet.contains(Constants.IDENTIFICADOR))
		{
			this.id = copia.id;
		}
		if (attributesToSet.contains("title"))
		{
			this.title = copia.title;
		}
		
		if (attributesToSet.contains("estado"))
		{
			this.estado = copia.estado;
		}
		
		if (attributesToSet.contains("via"))
		{
			this.via = copia.via;
		}
		
		if (attributesToSet.contains("municipioId"))
		{
			this.municipioId = copia.municipioId;
		}

		if (attributesToSet.contains("municipioTitle"))
		{
			this.municipioTitle = copia.municipioTitle;
		}
		
		if (attributesToSet.contains("juntaAdministrativa"))
		{
			this.juntaAdministrativa = copia.juntaAdministrativa;
		}

		if (attributesToSet.contains("numeroInicioTramo"))
		{
			this.numeroInicioTramo = copia.numeroInicioTramo;
		}

		if (attributesToSet.contains("numeroFinTramo"))
		{
			this.numeroFinTramo = copia.numeroFinTramo;
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

	
	@Column(name = "via", nullable = true, length = 50)
	public String getVia()
	{
		return via;
	}

	public void setVia(String via)
	{
		this.via = via;
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

	@Column(name = "title", nullable = false, length = 400)
	public String getTitle()
	{
		return this.title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	@Column(name = "estado", nullable = false, length = 10)
	public String getEstado()
	{
		return this.estado;
	}

	public void setEstado(String estado)
	{
		this.estado = estado;
	}

	@Column(name = "municipio_id", length = 10)
	public String getMunicipioId()
	{
		return this.municipioId;
	}

	public void setMunicipioId(String municipioId)
	{
		this.municipioId = municipioId;
	}

	@Column(name = "municipio_title", length = 200)
	public String getMunicipioTitle()
	{
		return this.municipioTitle;
	}

	public void setMunicipioTitle(String municipioTitle)
	{
		this.municipioTitle = municipioTitle;
	}

	@Column(name = "junta_administrativa", length = 200)
	public String getJuntaAdministrativa()
	{
		return this.juntaAdministrativa;
	}

	public void setJuntaAdministrativa(String juntaAdministrativa)
	{
		this.juntaAdministrativa = juntaAdministrativa;
	}

	@Column(name = "numero_inicio_tramo", nullable = false, length = 10)
	public String getNumeroInicioTramo()
	{
		return this.numeroInicioTramo;
	}

	public void setNumeroInicioTramo(String numeroInicioTramo)
	{
		this.numeroInicioTramo = numeroInicioTramo;
	}

	@Column(name = "numero_fin_tramo", nullable = false, length = 10)
	public String getNumeroFinTramo()
	{
		return this.numeroFinTramo;
	}

	public void setNumeroFinTramo(String numeroFinTramo)
	{
		this.numeroFinTramo = numeroFinTramo;
	}



	@SuppressWarnings("unchecked")
	@Override
	public <T> T cloneModel(T copia, List<String> listado) {		
		return  (T) cloneClass( (CallejeroTramoVia) copia,listado) ;
	}

	public CallejeroTramoVia cloneClass(CallejeroTramoVia copia, List<String> attributesToSet) {		
		CallejeroTramoVia obj = new CallejeroTramoVia(copia,attributesToSet);
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
								
		if (!Util.validValue(this.getTitle())) {
			result.add("Title is not valid [Title:"+this.getTitle()+"]");
		}
		
		if (!Util.validValue(this.getEstado())) {
			result.add("estado is not valid [estado:"+this.getEstado()+"]");
		}
		
		if (!Util.validValue(this.getVia())) {
			result.add("via is not valid [tipoVia:"+this.getVia()+"]");
		}
		
		if (!Util.validValue(this.getNumeroInicioTramo())) {
			result.add("numeroInicioTramo is not valid [numeroInicioTramo:"+this.getNumeroInicioTramo()+"]");
		}
		
		if (!Util.validValue(this.getNumeroFinTramo())) {
			result.add("numeroFinTramo is not valid [numeroFinTramo:"+this.getNumeroFinTramo()+"]");
		}
		
		return result;
	}



	@Override
	public Map<String, String> prefixes()
	{
		Map<String, String> prefixes = new HashMap<String, String>();		
		prefixes.put(Context.DCT, Context.DCT_URI);
		prefixes.put(Context.ESADM, Context.ESADM_URI);
		prefixes.put(Context.FOAF, Context.FOAF_URI);
		prefixes.put(Context.ESCJR, Context.ESCJR_URI);
		
		
		return prefixes;
	}



	@Override
	public String toString()
	{
		return "CallejeroTramoVia [ikey=" + ikey + ", via=" + via + ", id=" + id + ", title=" + title + ", estado=" + estado + ", municipioId=" + municipioId + ", municipioTitle=" + municipioTitle + ", juntaAdministrativa=" + juntaAdministrativa + ", numeroInicioTramo=" + numeroInicioTramo + ", numeroFinTramo="
				+ numeroFinTramo + "]";
	}
	
	

}
