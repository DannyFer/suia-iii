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
@Table(name = "identification_contaminated_sites_eia", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = IdentificacionSitiosContaminadosEia.LISTAR_POR_EIA, query = "SELECT a FROM IdentificacionSitiosContaminadosEia a WHERE a.estado = true AND a.idEstudioImpactoAmbiental = :idEstudioImpactoAmbiental ORDER BY a.sitiosContaminados") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "idsc_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "idsc_status = 'TRUE'")
public class IdentificacionSitiosContaminadosEia extends EntidadBase {

	private static final long serialVersionUID = -4785151776873585044L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_EIA = PAQUETE + "IdentificacionSitiosContaminadosEia.listarPorEIA";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "IDENTIFICACION_SITIOS_CONTAMINADOS_EIA_ID_GENERATOR", initialValue = 1, sequenceName = "seq_idsc_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IDENTIFICACION_SITIOS_CONTAMINADOS_EIA_ID_GENERATOR")
	@Column(name = "idsc_id")
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "eist_id", referencedColumnName = "eist_id")
	@ForeignKey(name = "fk_identification_contaminated_sites_eia_eist_id_environmental_impact_studies_eist_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private EstudioImpactoAmbiental eistId;

	@Getter
	@Setter
	@Column(name = "eist_id", insertable = false, updatable = false)
	private Integer idEstudioImpactoAmbiental;

	@Getter
	@Setter
	@Column(name = "idsc_contaminated_sites", length = 500)
	private String sitiosContaminados;

	@Getter
	@Setter
	@Column(name = "idsc_pollution_sources", length = 500)
	private String fuentesContaminacion;

	@Getter
	@Setter
	@Column(name = "idsc_x")
	private Double x;

	@Getter
	@Setter
	@Column(name = "idsc_y")
	private Double y;

	@Getter
	@Setter
	@Transient
	private int indice;

	@Getter
	@Setter
	@Transient
	private boolean editar;

	public IdentificacionSitiosContaminadosEia() {
	}

	public IdentificacionSitiosContaminadosEia(Integer id) {
		this.id = id;
	}

}
