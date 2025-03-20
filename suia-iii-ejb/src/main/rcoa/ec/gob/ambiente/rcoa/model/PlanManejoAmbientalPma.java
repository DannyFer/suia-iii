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
import ec.gob.ambiente.rcoa.model.FasesCoa;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name="management_plan_record_pma", schema="coa_environmental_record")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "mapr_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "mapr_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "mapr_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mapr_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mapr_user_update")) }
)
@NamedQueries({
    	@NamedQuery(name=PlanManejoAmbientalPma.GET_TODOS, query="SELECT m FROM PlanManejoAmbientalPma m where m.estado = true ")
    })
public class PlanManejoAmbientalPma extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String GET_TODOS = "ec.gob.ambiente.rcoa.model.PlanManejoAmbientalPma.getTodos";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="mapr_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="mapr_descrption")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="mapr_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name="mapr_cod_plan")
	private String codigoPlan;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "phas_id")
	private FasesCoa faseTipoPlan;
	
}
