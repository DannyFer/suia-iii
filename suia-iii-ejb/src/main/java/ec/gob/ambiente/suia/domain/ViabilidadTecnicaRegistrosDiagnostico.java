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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Rafael Richards - 29 Sept 2015
 */
@Entity
@Table(name = "technical_viability_diagnostic_records", schema = "suia_iii")
@NamedQueries({
		@NamedQuery(name = ViabilidadTecnicaRegistrosDiagnostico.OBTENER_POR_ID, query = "SELECT u FROM ViabilidadTecnicaRegistrosDiagnostico u WHERE u.estado = true AND u.id = :id"),
		@NamedQuery(name = ViabilidadTecnicaRegistrosDiagnostico.OBTENER_POR_ID_VIABILIDAD, query = "SELECT u FROM ViabilidadTecnicaRegistrosDiagnostico u WHERE u.estado = true AND u.estudioViabilidadTecnicaDiagnostico = :idViabilidad AND u.viabilidadTecnicaParametrosDiagnostico = :idParametro") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tvdr_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvdr_status = 'TRUE'")
public class ViabilidadTecnicaRegistrosDiagnostico extends EntidadBase {

	private static final long serialVersionUID = 4959506501751965664L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";

	public static final String OBTENER_POR_ID = PAQUETE + "ViabilidadTecnicaRegistrosDiagnostico.obtenerPorID";
	public static final String OBTENER_POR_ID_VIABILIDAD = PAQUETE
			+ "ViabilidadTecnicaRegistrosDiagnostico.obtenerPorIdViabilidad";

	@Id
	@SequenceGenerator(name = "TECHNICAL_VIABILITY_DIAGN_REC_GENERATOR", sequenceName = "tvdr_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TECHNICAL_VIABILITY_DIAGN_REC_GENERATOR")
	@Getter
	@Setter
	@Column(name = "tvdr_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "tvdr_perforation_numbers")
	private Integer numeroPerforacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "tvdp_id")
	@ForeignKey(name = "fk_technical_viab_diag_records_tvdr_idtvdp_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvdp_status = 'TRUE'")
	private ViabilidadTecnicaParametrosDiagnostico viabilidadTecnicaParametrosDiagnostico;

	@Getter
	@Setter
	@Column(name = "tvdr_value")
	private Double valorElementoPerforacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "tvsd_id")
	@ForeignKey(name = "fk_technical_viab_study_diagnos_tvdr_idtvsd_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvdp_status = 'TRUE'")
	private EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico;

	@Getter
	@Setter
	@Column(name = "tvdf_sample_numbers")
	private Integer numeroMuestra;

}
