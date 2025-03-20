/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.dto.EntityAdjunto;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

/**
 * 
 * @author christian
 */
@Entity
@Table(name = "determining_influence_area_eia", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = DeterminacionAreaInfluenciaEIA.LISTAR_POR_EIA, query = "SELECT a FROM DeterminacionAreaInfluenciaEIA a WHERE a.estado = true AND a.idEstudioImpactoAmbiental = :idEstudioImpactoAmbiental ORDER BY a.idCatalogo") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dain_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dain_status = 'TRUE'")
public class DeterminacionAreaInfluenciaEIA extends EntidadBase {

	private static final long serialVersionUID = 3764203166944708649L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_EIA = PAQUETE + "DeterminacionAreaInfluenciaEIA.listarPorEIA";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "DETERMINACION_AREA_INFLUENCIA_EIA_ID_GENERATOR", initialValue = 1, sequenceName = "seq_dain_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETERMINACION_AREA_INFLUENCIA_EIA_ID_GENERATOR")
	@Column(name = "dain_id")
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "eist_id", referencedColumnName = "eist_id")
	@ForeignKey(name = "fk_determining_influence_area_eist_id_environmental_impact_studies_eist_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private EstudioImpactoAmbiental eistId;

	@Getter
	@Setter
	@Column(name = "eist_id", insertable = false, updatable = false)
	private Integer idEstudioImpactoAmbiental;

	@Getter
	@Setter
	@JoinColumn(name = "geca_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_determining_influence_area_geca_id_general_catalog_geca_id")
	@ManyToOne(fetch = FetchType.EAGER)
	private CatalogoGeneral gecaId;

	@Getter
	@Setter
	@Column(name = "geca_id", insertable = false, updatable = false)
	private Integer idCatalogo;

	@Getter
	@Setter
	@Column(name = "dain_results", length = 500)
	private String resultados;

	@Getter
	@Setter
	@Transient
	private int indice;

	@Getter
	@Setter
	@Transient
	private EntityAdjunto entityAdjunto;

	public DeterminacionAreaInfluenciaEIA() {
	}

	public DeterminacionAreaInfluenciaEIA(Integer id) {
		this.id = id;
	}

}
