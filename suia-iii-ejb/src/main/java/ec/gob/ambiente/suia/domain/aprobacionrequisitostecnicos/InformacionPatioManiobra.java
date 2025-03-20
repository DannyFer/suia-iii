package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "playground_maneuver_information", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "pmin_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "pmin_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "pmin_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "pmin_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "pmin_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pmin_status = 'TRUE'")
public class InformacionPatioManiobra extends EntidadAuditable {

	/**
	* 
	*/
	private static final long serialVersionUID = -9093561499577523378L;

	@Id
	@Column(name = "pmin_id")
	@SequenceGenerator(name = "PLAYGROUND_MANEUVER_GENERATOR", sequenceName = "seq_pmin_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PLAYGROUND_MANEUVER_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "pmin_street")
	@Getter
	@Setter
	private String calle;

	@Column(name = "pmin_number")
	@Getter
	@Setter
	private String numero;

	@Column(name = "pmin_landline")
	@Getter
	@Setter
	private String telefonoFijo;

	@Column(name = "pmin_celular")
	@Getter
	@Setter
	private String celular;

	@Column(name = "pmin_number_vehicle")
	@Getter
	@Setter
	private Integer numeroVehiculos = 0;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_playground_information_apte_id_approval_requirements_apte_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apte_status = 'TRUE'")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_playground_information_gelo_id_geographical_location_gelo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gelo_status = 'TRUE'")
	private UbicacionesGeografica ubicacionesGeografica;

	@Column(name = "pmin_observations")
	@Getter
	@Setter
	private String observaciones;

}
