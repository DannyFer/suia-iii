package ec.gob.ambiente.rcoa.digitalizacion.model;


import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;	

import java.util.Date;

@Entity
@Table(name="intersections_project", schema = "coa_digitalization_linkage")
@NamedQueries({
@NamedQuery(name="InterseccionProyectoDigitalizacion.GETFINDALL", query="SELECT c FROM InterseccionProyectoDigitalizacion c")})

@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "inpr_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "inpr_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "inpr_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "inpr_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "inpr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "inpr_status = 'TRUE'")

public class InterseccionProyectoDigitalizacion extends EntidadAuditable {
    private static final long serialVersionUID = 5016494380218175574L;    
    @Getter
    @Setter
    @Id            
    @Column(name = "inpr_id")
    @SequenceGenerator(name = "INTERSECTIONS_PROJECT_DIGITALIZATION_ID_GENERATOR", sequenceName = "intersections_project_inpr_id_seq", schema = "coa_digitalization_linkage", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INTERSECTIONS_PROJECT_DIGITALIZATION_ID_GENERATOR")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "inpr_process_date")
    private Date fechaProceso;    
    @Getter
    @Setter
    @Column(name = "inpr_buffer_intersection")
    private Boolean interseca;
    @Getter
    @Setter
    @Column(name = "inpr_layer_description", length = 1000)
    private String descripcionCapa;    
    @Getter
    @Setter
    @Column(name = "inpr_update_number")
    private Integer nroActualizacion = 0;
	@ManyToOne
	@JoinColumn(name = "enaa_id")
	@ForeignKey(name = "fk_intersections_project_eaa_enaa_id_project_envirolm_licen_ena")
	@Getter
	@Setter
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativaAmbiental;
	@ManyToOne
	@JoinColumn(name = "laye_id")
	@ForeignKey(name = "fk_intersections_project_eaa_layer_id_layers_layer_id")
	@Getter
	@Setter
	private CapasCoa capa;
    @Getter
    @Setter
    @Column(name = "inpr_type_entry")
    private Integer tipoIngreso;
}
