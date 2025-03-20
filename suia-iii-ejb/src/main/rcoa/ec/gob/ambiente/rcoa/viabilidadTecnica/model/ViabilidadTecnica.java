package ec.gob.ambiente.rcoa.viabilidadTecnica.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the technical_viability database table.
 * 
 */
@Entity
@Table(name = "technical_viability", schema = "coa_viability_technical")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tevi_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tevi_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tevi_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tevi_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tevi_user_update")) })

@NamedQuery(name = "ViabilidadTecnica.findAll", query = "SELECT v FROM ViabilidadTecnica v ")

public class ViabilidadTecnica extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tevi_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "orga_id")
	private Organizacion organizacion;

	@Getter
	@Setter
	@Column(name = "tevi_ruc")
	private String ruc;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	private UbicacionesGeografica ubicacionGeografica;

	@Getter
	@Setter
	@Column(name = "tevi_trade_number")
	private String numeroOficio;

	@Getter
	@Setter
	@Column(name = "tevi_date")
	private Date fecha;

	@Getter
	@Setter
	@Column(name = "tevi_type")
	private Integer tipoViabilidad;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "geca_id")
	private CatalogoGeneralCoa catalogoGeneralFases;

	@Getter
	@Setter
	@Column(name = "tevi_observation")
	private String observacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "user_id")
	private Usuario usuario;

	@Getter
	@Setter
	@Column(name = "tevi_observation_bd")
	private String observacionBD;
	
	@Getter
	@Setter
	@Column(name = "tevi_social_reason")
	private String razonSocial;

	@Getter
	@Setter
	@Column(name = "tevi_sanitary_landfill")
	private Boolean esRellenoSanitario;

	public ViabilidadTecnica() {
	}

}