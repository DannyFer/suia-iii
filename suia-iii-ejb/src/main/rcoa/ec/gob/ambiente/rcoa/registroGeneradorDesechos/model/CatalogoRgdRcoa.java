package ec.gob.ambiente.rcoa.registroGeneradorDesechos.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the waste_generator_catalog database table.
 * 
 */
@Entity
@Table(name = "waste_generator_catalog", schema = "coa_waste_generator_record")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "waca_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "waca_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "waca_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "waca_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "waca_user_update")) })
@NamedQuery(name = "CatalogoRgdRcoa.findAll", query = "SELECT c FROM CatalogoRgdRcoa c")
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "waca_status = 'TRUE'")
public class CatalogoRgdRcoa extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "waca_id")
	private Integer id;

	@Column(name = "waca_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name = "waca_description")
	private String descripcion;

	public CatalogoRgdRcoa() {
	}

}