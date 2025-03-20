/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

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

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author christian
 */
@Table(name = "technical_viability_diagnostic_phases", schema = "suia_iii")
@Entity
@NamedQueries({ @NamedQuery(name = FaseViabilidadTecnicaDiagnostico.LISTAR_POR_ESTUDIO_DIAGNOSTICO, query = "SELECT f FROM FaseViabilidadTecnicaDiagnostico f WHERE f.estudioViabilidadTecnicaDiagnostico.id = :idDiagnostico ") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tvdp_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvdp_status = 'TRUE'")
public class FaseViabilidadTecnicaDiagnostico extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";

	public static final String LISTAR_POR_ESTUDIO_DIAGNOSTICO = PAQUETE
			+ "FaseViabilidadTecnicaDiagnostico.listarPorDiagnostico";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "FaseViabilidadTecnicaDiagnostico_generator", initialValue = 1, sequenceName = "seq_tvdp_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FaseViabilidadTecnicaDiagnostico_generator")
	@Column(name = "tvdp_id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "tvsd_id")
	@ForeignKey(name = "fk_technical_viability_diagnostic_phasestvsd_idtechnical_viabil")
	@Getter
	@Setter
	private EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico;

	@ManyToOne
	@JoinColumn(name = "tvsp_id")
	@ForeignKey(name = "fk_technical_viability_diagnostic_phasestvsp_idtechnical_viabil")
	@Getter
	@Setter
	private FaseViabilidadTecnica faseViabilidadTecnica;

}
