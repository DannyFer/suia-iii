package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model;

import java.io.Serializable;

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
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name="crop_pesticide", schema = "chemical_pesticides")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "crpe_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "crpe_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "crpe_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "crpe_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "crpe_user_update")) })

@NamedQueries({
@NamedQuery(name="PlagaCultivo.findAll", query="SELECT a FROM PlagaCultivo a"),
@NamedQuery(name=PlagaCultivo.GET_POR_ID_DETALLE, query="SELECT a FROM PlagaCultivo a where a.idDetalleProyecto = :idDetalleProyecto and a.estado = true order by id")
})

public class PlagaCultivo extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.";
	
	public static final String GET_POR_ID_DETALLE = PAQUETE + "PlagaCultivo.getPorIdDetalle";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="crpe_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="depe_id")
	private Integer idDetalleProyecto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="pest_id")
	private Plaga plaga;

	@Getter
	@Setter
	@Column(name="crpe_common_name")
	private String nombreComunPlaga;

	@Getter
	@Setter
	@Column(name="crpe_is_physical")
	private Boolean registroFisico = false;

}