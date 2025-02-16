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

package org.ciudadesabiertas.dataset.utils;

import java.io.Serializable;
import java.math.BigDecimal;

import org.ciudadesabiertas.dataset.model.PuntoInteresTuristico;
import org.ciudadesabiertas.utils.DatasetSearch;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Juan Carlos Ballesteros (Localidata)
 * @author Carlos Martínez de la Casa (Localidata)
 * @author Oscar Corcho (UPM, Localidata)
 *
 */
public class PuntoInteresTuristicoSearch extends PuntoInteresTuristico implements  Serializable, DatasetSearch<PuntoInteresTuristico> {	
	

	
	@JsonIgnore
	private static final long serialVersionUID = -7261783860307676914L;

	@ApiModelProperty(hidden = true)
	private String portalIdIsolated;	
	

	@ApiModelProperty(hidden = true)
	private BigDecimal latitud;
	
	@ApiModelProperty(hidden = true)
	private BigDecimal longitud;
	
	
	@ApiModelProperty(hidden = true)
	private BigDecimal x;
	
	@ApiModelProperty(hidden = true)
	private BigDecimal y;

	@ApiModelProperty(hidden = true)
	private Double distance;
	
	@ApiModelProperty(value = "Coordenada X del lugar de interés turístico. Ejemplo: 440291.26773")
    private BigDecimal xETRS89;	
	
    @ApiModelProperty(value = "Coordenada Y del lugar de interés turístico. Ejemplo: 4474254.64478")
	private BigDecimal yETRS89;


	public BigDecimal getxETRS89()
	{
		return xETRS89;
	}


	public void setxETRS89(BigDecimal xETRS89)
	{	
		setX(xETRS89);
	}


	public BigDecimal getyETRS89()
	{
		return yETRS89;
	}


	public void setyETRS89(BigDecimal yETRS89)
	{		
		setY(yETRS89);
	}
}
