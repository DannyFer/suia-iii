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

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name="objective_group", schema="chemical_pesticides")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "obgr_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "obgr_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "obgr_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "obgr_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "obgr_user_update")) })

@NamedQueries({
@NamedQuery(name="GrupoObjetivo.findAll", query="SELECT g FROM GrupoObjetivo g"),
@NamedQuery(name=GrupoObjetivo.GET_ACTIVOS, query="SELECT g FROM GrupoObjetivo g WHERE g.estado = true order by descripcion")
})

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "obgr_status = 'TRUE'")
public class GrupoObjetivo extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.";
	
	public static final String GET_ACTIVOS = PAQUETE + "GrupoObjetivo.getActivos";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="obgr_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="obgr_description")
	private String descripcion;
	
}