package ec.gob.ambiente.rcoa.digitalizacion.model;


import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;	


@Entity
@Table(name="details_intersection", schema = "coa_digitalization_linkage")
@NamedQueries({
@NamedQuery(name="DetalleInterseccionDigitalizacion.GETFINDALL", query="SELECT c FROM DetalleInterseccionDigitalizacion c")})

@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dein_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "dein_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "dein_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "dein_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "dein_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dein_status = 'TRUE'")

public class DetalleInterseccionDigitalizacion extends EntidadAuditable {
    private static final long serialVersionUID = 5016494380218175574L;    
    @Getter
    @Setter
    @Id    
    @SequenceGenerator(name = "DETALLEINTERSECCIONDIGITALIZACION_ID_GENERATOR", sequenceName = "details_intersection_dein_id_seq", schema = "coa_digitalization_linkage", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETALLEINTERSECCIONDIGITALIZACION_ID_GENERATOR")    
    @Column(name = "dein_id")
    private Integer id;
    @Getter
    @Setter
    @Column(name = "dein_geometry_name", length = 1000)
    private String nombreGeometria;
    @Getter
    @Setter
    @Column(name = "dein_geometry_id")
    private Integer idGeometria;    
    @Getter
    @Setter
    @Column(name = "dein_subsystem_layer", length = 1024)
    private String capaSubsistema;    
    @Getter
    @Setter
    @Column(name = "dein_cap")
    private String codigoUnicoCapa;
    
    @Getter
	@Setter
	@Column(name = "dein_code", length = 100)
	private String codigoConvenio;
    
	@ManyToOne
	@JoinColumn(name = "inpr_id")
	@ForeignKey(name = "fk_details_intersect_project_ipea_id_intersect_project_ipea_id")
	@Getter
	@Setter
	private InterseccionProyectoDigitalizacion interseccionProyectoDigitalizacion;
    	
}
