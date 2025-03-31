/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoria2.bean;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.dto.EntityFichaGeneralReporte;

/**
 * 
 * @author Jonathan Guerrero
 */
public class ImpresionFichaGeneralBean {

	@Getter
	@Setter
	private EntityFichaGeneralReporte entityFichaGeneralReporte;
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbiental;
	@Getter
	@Setter
	private String plantillaHtml;
	@Getter
	@Setter
	private String direccion;
	@Getter
	@Setter
	private String telefono;
	@Getter
	@Setter
	private String fax;
	@Getter
	@Setter
	private String email;

	public void iniciarDatos() {
		setEntityFichaGeneralReporte(new EntityFichaGeneralReporte());
		setPlantillaHtml(null);
	}
}
