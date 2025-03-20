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
@Table(name="types_aquatic_organisms", schema="chemical_pesticides")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tyaq_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tyaq_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tyaq_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tyaq_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tyaq_user_update")) })
@NamedQuery(name="TipoOrganismoAcuatico.findAll", query="SELECT t FROM TipoOrganismoAcuatico t")

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tyaq_status = 'TRUE'")
public class TipoOrganismoAcuatico extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int ID_PECES=1;
	public static final int ID_CRUSTACEOS=2;
	public static final int ID_ALGAS=3;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="tyaq_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="tyaq_name")
	private String nombre;

}