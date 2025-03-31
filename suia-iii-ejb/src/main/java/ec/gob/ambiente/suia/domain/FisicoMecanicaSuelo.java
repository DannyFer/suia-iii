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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "physical_mechanic_soils", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "pmso_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "pmso_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "pmso_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "pmso_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "pmso_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pmso_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = FisicoMecanicaSuelo.LISTAR_POR_ID_EIA, query = "SELECT f FROM FisicoMecanicaSuelo f WHERE f.estado=true AND f.eiaId = :eiaId") })
public class FisicoMecanicaSuelo extends EntidadAuditable {

	private static final long serialVersionUID = 1218304605831535129L;
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_ID_EIA = PAQUETE
			+ "FisicoMecanicaSuelo.listarPorIdEia";
	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "PHYSICAL_MECHANIC_SOILS_GENERATOR", initialValue = 1, schema = "suia_iii", sequenceName = "seq_pmso_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PHYSICAL_MECHANIC_SOILS_GENERATOR")
	@Column(name = "pmso_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "pmso_code")
	private String codigo;
	@Getter
	@Setter
	@Column(name = "pmso_humidity")
	private Double humedad;
	@Getter
	@Setter
	@Column(name = "pmso_liquid_limit")
	private Double limiteLiquido;
	@Getter
	@Setter
	@Column(name = "pmso_plastic_limit")
	private Double limitePlastico;
	@Getter
	@Setter
	@Column(name = "pmso_plasticity")
	private Double plasticidad;
	@Getter
	@Setter
	@Column(name = "pmso_clay")
	private Double arcilla;
	@Getter
	@Setter
	@Column(name = "pmso_silt")
	private Double limo;
	@Getter
	@Setter
	@Column(name = "pmso_sand")
	private Double arena;
	@Getter
	@Setter
	@Column(name = "pmso_gravel")
	private Double grava;
	@Getter
	@Setter
	@Column(name = "pmso_gravity")
	private Double gravedad;
	@Getter
	@Setter
	@Column(name="eia_id")
	private Integer eiaId;
	@Transient
	@Getter
	@Setter
	private boolean editar;
	
	@Transient
	@Getter
	@Setter
	private CoordenadaGeneral coordenadaGeneral;

}
