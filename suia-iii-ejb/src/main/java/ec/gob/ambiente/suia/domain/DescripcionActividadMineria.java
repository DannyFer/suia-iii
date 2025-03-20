package ec.gob.ambiente.suia.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@Entity
@Table(name = "mining_activity_description", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = DescripcionActividadMineria.LISTAR_POR_FICHA, query = "SELECT f FROM DescripcionActividadMineria f WHERE f.idFichaAmbiental = :idFicha AND f.estado = true and idRegistroOriginal = null") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "miad_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "miad_status = 'TRUE'")
public class DescripcionActividadMineria extends EntidadBase {

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_FICHA = PAQUETE + "DescripcionActividadMineria.listarPorFicha";
	/**
     *
     */
	private static final long serialVersionUID = 1154755965481020655L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "MINING_ACTIVITY_DESCRIPTION_MIAID_GENERATOR", sequenceName = "seq_mien_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MINING_ACTIVITY_DESCRIPTION_MIAID_GENERATOR")
	@Column(name = "miad_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "miad_number_people")
	private Integer numeroPersonasLaboran;
	@Getter
	@Setter
	@Column(name = "miad_investment_amount")
	private Double montoInversion;
	@Getter
	@Setter
	@Column(name = "miad_daily_volume_production", length = 50)
	private String volumenProduccionDiario;
	@Getter
	@Setter
	@Column(name = "miad_detailed_description_mining", length = 500)
	private String descripcionDetalladaActividadMinera;
	@Getter
	@Setter
	@Column(name = "miad_detailed_facilities", length = 500)
	private String descripcionDetalladaInstalaciones;
	@Getter
	@Setter
	@Column(name = "miad_detailed_machinery", length = 500)
	private String descripcionDetalladaMaquinaria;
	@Getter
	@Setter
	@Column(name = "miad_volumes_quantities_description", length = 500)
	private String descripcionCantidadesVolumenes;
	@Getter
	@Setter
	@Column(name = "miad_benefit_plant_name", length = 500)
	private String nombrePlantaBeneficio;
	@Getter
	@Setter
	@Column(name = "miad_has_environmental_license")
	private boolean tieneLicenciaAmbiental;
	@Getter
	@Setter
	@Column(name = "miad_observation_environmental_license_number", length = 500)
	private String numeroObservacionLicenciaAmbiental;
	@Getter
	@Setter
	@Column(name = "miad_grant_number", length = 500)
	private String numeroConcesion;

	@Getter
	@Setter
	@OneToOne
	@JoinColumn(name = "mien_id")
	@ForeignKey(name = "fk_mining_activity_description_mien_id_project_envirolment_licensing_mien_id")
	private FichaAmbientalMineria fichaAmbientalMineria;

	@Getter
	@Setter
	@Column(name = "mien_id", insertable = false, updatable = false)
	private Integer idFichaAmbiental;
	
	/**
	 * Cris F: aumento de campos para historial
	 */
	@Getter
	@Setter
	@Column(name = "miad_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name = "miad_historical_date")
	private Date fechaHistorico;
	

}
