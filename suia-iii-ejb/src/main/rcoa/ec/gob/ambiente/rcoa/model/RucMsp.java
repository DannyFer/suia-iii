package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the msp_ruc database table.
 * 
 */
@Entity
@Table(name="msp_ruc", schema="coa_mae")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "msru_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "msru_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "msru_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "msru_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "msru_user_update")) })
@NamedQuery(name="RucMsp.findAll", query="SELECT r FROM RucMsp r")
public class RucMsp extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="msru_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	private UbicacionesGeografica ubicacionesGeograficas;

	@Getter
	@Setter
	@Column(name="msru_approved_name")
	private String nombreHomologado;

	@Getter	
	@Setter
	@Column(name="msru_ruc")
	private String ruc;

	@Getter
	@Setter
	@Column(name="msru_ue_esigef")
	private String unidadEjecutoraEsigef;

	@Getter
	@Setter
	@Column(name="msru_zone")
	private String zona;
	public RucMsp() {
	}
}