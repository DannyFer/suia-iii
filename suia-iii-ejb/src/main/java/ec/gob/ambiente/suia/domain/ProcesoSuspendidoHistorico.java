package ec.gob.ambiente.suia.domain;

import java.util.Date;

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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

/**
 * The persistent class for the process_suspended_historical database table.
 * 
 */

@Entity
@Table(name = "process_suspended_historical", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "psuh_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "psuh_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "psuh_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "psuh_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "psuh_user_update")) })
public class ProcesoSuspendidoHistorico extends EntidadAuditable {
	
	private static final long serialVersionUID = 8763271539009119070L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "psuh_id")	
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "psuh_code")
	private String codigo;//Codigo Proyecto
	
	@Getter
	@Setter
	@Column(name = "psuh_desciption")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "psuh_suspended")
	private Boolean suspendido;
	
	
	@Getter
	@Setter
	@Column(name = "psuh_type_project")
	private String tipoProyecto;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prsu_id")
	@ForeignKey(name = "psuh_fk_prsu")
	private ProcesoSuspendido procesoSuspendido;
	
	@Getter
	@Setter
	@Column(name = "psuh_reactivated_days")
	private Integer diasReactivados;
	
	@Getter
	@Setter
	@Column(name = "psuh_reactivated_date")
	private Date fechaActivacion;
}