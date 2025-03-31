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
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "reassignment_project", schema = "suia_iii")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "repr_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "repr_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "repr_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "repr_creation_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "repr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "repr_status = 'TRUE'")
public class ReasignacionTareaProyectos extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
    @Setter
    @Id
    @Column(name = "repr_id")
	@SequenceGenerator(name = "REASISIGNMENT_PROJECTS", sequenceName = "seq_repr_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REASISIGNMENT_PROJECTS")
    private Integer id;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@ForeignKey(name = "user_id")
	@JoinColumn(name = "user_id")
	private Usuario usuario;
	
	@Getter
	@Setter
	@Column(name = "repr_reactivation_reason")
	private String motivoReasignacion;
	
	@Getter
	@Setter
	@Column(name = "rpr_code_reactivation")
	private String codigoReacctivacion;
	
	@Getter
	@Setter
	@Column(name = "repr_task_reassignment")
	private String tareaReasignada;
	
	@Getter
	@Setter
	@Column(name = "task_id")
	private Integer idTarea;
	
	@Getter
    @Setter
    @Column(name = "user_id_assignment")
    private Integer idUsuarioAsignado;
	
}
