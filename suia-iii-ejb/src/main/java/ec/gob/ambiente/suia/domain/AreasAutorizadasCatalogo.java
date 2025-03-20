package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "authorized_areas_catalog", schema = "suia_iii")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "arca_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "arca_date_creator")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "arca_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "arca_user_creator")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "arca_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "arca_status = 'TRUE'")
public class AreasAutorizadasCatalogo extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "AUTHORIZED_AREAS_CATALOG_GENERATOR", initialValue = 1, sequenceName = "seq_arca_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AUTHORIZED_AREAS_CATALOG_GENERATOR")
	@Column(name = "arca_id")
	private Integer id;
	
	@Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id")
    private Area area;
	
	@Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cacs_id")
    private CatalogoCategoriaSistema categoriaSistema;

	@Getter
    @Setter
    @Column(name = "arca_blocking")
    private Boolean actividadBloqueada;
	
	@Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "arca_area_responsible")
    private Area areaResponsable;
	
}
