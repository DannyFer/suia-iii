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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="environmental_subplan_record", schema="coa_environmental_record")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "ensr_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "ensr_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "ensr_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ensr_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ensr_user_update")) })
@NamedQueries({
	@NamedQuery(name=RegistroAmbientalSubPlanPma.GET_SUBPLANES_POR_PROYECTO, query="SELECT m FROM RegistroAmbientalSubPlanPma m where m.estado = true and m.registroAmbientalId= :registroAnbientalId ")
})
public class RegistroAmbientalSubPlanPma extends EntidadAuditable implements Serializable {
	public static final String GET_SUBPLANES_POR_PROYECTO = "ec.gob.ambiente.rcoa.model.RegistroAmbientalSubPlanPma.getSubPlanesPorProyecto";
	private static final long serialVersionUID = 1L;
	  
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ensr_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "enre_id")
	private Integer registroAmbientalId;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mapr_id")
	private PlanManejoAmbientalPma planManejoAmbientalPma;
}