package ec.gob.ambiente.suia.domain;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "radiations", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "radi_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "radi_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "radi_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "radi_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "radi_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "radi_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = Radiacion.LISTAR_POR_ID_EIA, query = "SELECT r FROM Radiacion r WHERE r.estado=true AND r.eiaId = :eiaId") })
public class Radiacion extends EntidadAuditable {

	private static final long serialVersionUID = 442307953615756135L;
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_ID_EIA = PAQUETE
			+ "Radiacion.listarPorIdEia";
	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "RADIATIONS_GENERATOR", initialValue = 1, schema = "suia_iii", sequenceName = "seq_radi_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RADIATIONS_GENERATOR")
	@Column(name = "radi_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "radi_code")
	private String codigo;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "radi_sampling_date")
	private Date fechaMuestreo;


	@Getter
	@Setter
	@Column(name = "nois_description")
	private String descripcion;

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
