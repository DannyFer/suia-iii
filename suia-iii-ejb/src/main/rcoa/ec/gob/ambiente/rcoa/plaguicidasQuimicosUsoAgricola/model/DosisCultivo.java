package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name="culture_dose", schema = "chemical_pesticides")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "cudo_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "cudo_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "cudo_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cudo_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cudo_user_update")) })

@NamedQueries({
@NamedQuery(name="DosisCultivo.findAll", query="SELECT a FROM DosisCultivo a"),
@NamedQuery(name=DosisCultivo.GET_POR_ID_DETALLE, query="SELECT a FROM DosisCultivo a where a.idDetalleProyecto = :idDetalleProyecto and a.estado = true order by id")
})

public class DosisCultivo extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.";
	
	public static final String GET_POR_ID_DETALLE = PAQUETE + "DosisCultivo.getPorIdDetalle";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cudo_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="depe_id")
	private Integer idDetalleProyecto;

	@Getter
	@Setter
	@Column(name="cudo_dose_value")
	private Double dosis;

	@Getter
	@Setter
	@Column(name="cudo_unit_dose")
	private String unidad;

	@Getter
	@Setter
	@Column(name="cudo_is_physical")
	private Boolean registroFisico = false;

}