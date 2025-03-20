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
 * @author frank torres
 */
public class EntityFichaAmbientalDescripcionProceso {

	@Getter
	@Setter
	private String actividades;

	@Getter
	@Setter
	private String herramientas;

	@Getter
	@Setter
	private String recursoAgua;

	@Getter
	@Setter
	private String insumos;

	@Getter
	@Setter
	private String tecnicasProceso;

	@Getter
	@Setter
	private String instalacionesProceso;

	@Getter
	@Setter
	private String plaguicidasProceso;

	@Getter
	@Setter
	private String fertiliantesProceso;

	@Getter
	@Setter
	private String desechosSanitarios;


	@Getter
	@Setter
	private String disposicionFinal;


	@Getter
	@Setter
	private String transporteDesechos;

}
