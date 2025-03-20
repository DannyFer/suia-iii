package ec.gob.ambiente.rcoa.model;

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

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "permission_type_catalog_layers", schema = "coa_mae")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "pety_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "pety_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "pety_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "pety_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "pety_user_update")) })

@NamedQueries({
@NamedQuery(name="CatalogoCapaPermiso.findAll", query="SELECT c FROM CatalogoCapaPermiso c"),
@NamedQuery(name=CatalogoCapaPermiso.GET_POR_CIIU, query="SELECT c FROM CatalogoCapaPermiso c where c.catalogoCIUU.id = :idCiiu and c.estado = true order by id desc")
})

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pety_status = 'TRUE'")
public class CatalogoCapaPermiso extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.model.";
	public static final String GET_POR_CIIU = PAQUETE + "CatalogoCapaPermiso.getPorCiiu";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "pety_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "caci_id")
	@ForeignKey(name = "fk_caci_id")
	@Getter
	@Setter
	private CatalogoCIUU catalogoCIUU;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="laye_id")	
	private CapasCoa capa;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "cate_id")
	private Categoria tipoPermiso;

	@Getter
	@Setter
	@Column(name = "pety_entity")
	private Integer tipoAutoridadAmbiental;

	@Getter
	@Setter
	@Column(name = "pety_layer_hierarchy")
	private Integer jerarquiaCapa;

}
