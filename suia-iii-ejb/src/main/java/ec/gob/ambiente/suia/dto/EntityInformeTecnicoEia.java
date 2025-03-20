/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.dto;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author
 */
public class EntityInformeTecnicoEia {

	@Getter
	@Setter
	private String numeroInforme;
	@Getter
	@Setter
	private String ciudadInforme;
	@Getter
	@Setter
	private String fechaInforme;
	@Getter
	@Setter
	private String fichaTecnica;
	@Getter
	@Setter
	private String antecendentes;
	@Getter
	@Setter
	private String objetivos;
	@Getter
	@Setter
	private String caracteristicasProyecto;
	@Getter
	@Setter
	private String evaluacionTecnica;
	@Getter
	@Setter
	private String observacion;
	@Getter
	@Setter
	private String conclusionesRecomendaciones;
	@Getter
	@Setter
	private String otrasObligaciones;
	@Getter
	@Setter
	private String nombreTecnico;
	 /**
    * Nombre:SUIA
    * Descripción: Para obterner el áres responsable para el logo del documentos. 
    * ParametrosIngreso:
    * PArametrosSalida:
    * Fecha:16/08/2015
    */
	@Getter
	@Setter
	private String areaResponsable;
	/**
	  * FIN Para obterner el áres responsable para el logo del documentos.
	  */

}
