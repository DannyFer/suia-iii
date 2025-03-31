package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the catalog database table.
 * 
 */
@Entity
@Table(name="catalog", schema = "coa_environmental_impact_study")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "cata_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "cata_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "cata_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cata_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cata_user_update")) })
@NamedQuery(name="CatalogoEsIA.findAll", query="SELECT c FROM CatalogoGeneralEsIA c")
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cata_status = 'TRUE'")
public class CatalogoGeneralEsIA extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cata_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="cata_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name="cata_name")
	private String descripcion;

	public CatalogoGeneralEsIA() {
	}

}