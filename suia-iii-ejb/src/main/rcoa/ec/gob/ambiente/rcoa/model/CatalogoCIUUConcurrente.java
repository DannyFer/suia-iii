package ec.gob.ambiente.rcoa.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "catalog_ciuu_concurrent", schema = "coa_mae")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "caco_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "caco_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "caco_creator_user")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "caco_date_update")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "caco_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "caco_status = 'TRUE'")

public class CatalogoCIUUConcurrente extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6725701739519396104L;
	
	@Getter
	@Setter
	@Id
	@Column(name = "caco_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "caco_city_name")	
	private String nombreUbicacion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "area_id")
	@ForeignKey(name = "fk_area_id")
	private Area areaResponsable;
	
	@ManyToOne
	@JoinColumn(name = "caci_id")
	@ForeignKey(name = "fk_caci_id")
	@Getter
	@Setter
	private CatalogoCIUU catalogoCIUU;
}
