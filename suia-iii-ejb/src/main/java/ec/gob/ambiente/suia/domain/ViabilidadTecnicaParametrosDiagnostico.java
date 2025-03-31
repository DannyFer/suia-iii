/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Rafael Richards - 25 Sept 2015
 */
@Entity
@Table(name = "technical_viability_diagnostic_parameters", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = ViabilidadTecnicaParametrosDiagnostico.OBTENER_POR_ID, query = "SELECT u FROM ViabilidadTecnicaParametrosDiagnostico u WHERE u.estado = true AND u.id = :id") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tvdp_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvdp_status = 'TRUE'")
public class ViabilidadTecnicaParametrosDiagnostico extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2799818724885428970L;
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String OBTENER_POR_ID = PAQUETE + "ViabilidadTecnicaParametrosDiagnostico.obtenerPorViabilidad";

	@Id
	@SequenceGenerator(name = "TECHNICAL_VIABILITY_DIAGN_PARAM_GENERATOR", sequenceName = "tvdp_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TECHNICAL_VIABILITY_DIAGN_PARAM_GENERATOR")
	@Getter
	@Setter
	@Column(name = "tvdp_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "tvdp_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "tvdp_group_code")
	private String codigoGrupo;

	public String toString() {
		return nombre;
	}

}
