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
public class EntityFichaAmbientalDescripcionArea {

	@Getter
	@Setter
	private String clima;

	@Getter
	@Setter
	private String tiposSuelo;

	@Getter
	@Setter
	private String pendienteSuelo;

	@Getter
	@Setter
	private String condicionesDrenaje;

	@Getter
	@Setter
	private String deografia;

	@Getter
	@Setter
	private String abastecimientoAguaPoblacion;

	@Getter
	@Setter
	private String evacuacionAguasServidasPoblacion;

	@Getter
	@Setter
	private String electrificacion;

	@Getter
	@Setter
	private String vialidadAccesoPoblacion;

	@Getter
	@Setter
	private String organizacionSocial;

	@Getter
	@Setter
	private String componenteFlora;
	@Getter
	@Setter
	private String tablaComponenteFloraInicio;
	@Getter
	@Setter
	private String tablaComponenteFloraFin;

	@Getter
	@Setter
	private String tablaComponenteExtrasInicio;
	@Getter
	@Setter
	private String tablaComponenteExtrasFin;
	
	@Getter
	@Setter
	private String componenteFauna;
	@Getter
	@Setter
	private String tablaComponenteFaunaInicio;
	@Getter
	@Setter
	private String tablaComponenteFaunaFin;

	@Getter
	@Setter
	private String formacion;

	@Getter
	@Setter
	private String hegetal;

	@Getter
	@Setter
	private String habitat;

	@Getter
	@Setter
	private String tipoBosque;

	@Getter
	@Setter
	private String gradoIntervencionCobertura;

	@Getter
	@Setter
	private String aspectosEcologicos;

	@Getter
	@Setter
	private String pisoZoogeografico;

	@Getter
	@Setter
	private String gruposFaunisticos;

	@Getter
	@Setter
	private String sensibilidadPresentada;

	@Getter
	@Setter
	private String datosEcologicos;

}
