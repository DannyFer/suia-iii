package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name="type_observations", schema="chemical_pesticides")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tyob_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tyob_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tyob_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tyob_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tyob_user_update")) })
@NamedQuery(name="TipoObservacion.findAll", query="SELECT t FROM TipoObservacion t")

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tyob_status = 'TRUE'")
public class TipoObservacion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="tyob_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="tyob_name")
	private String nombre;

}