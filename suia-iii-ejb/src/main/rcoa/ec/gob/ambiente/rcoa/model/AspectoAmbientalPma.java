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
@Table(name="environmental_aspect_pma", schema="coa_environmental_record")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "enas_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "enas_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "enas_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "enas_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "enas_user_update")) })
@NamedQueries({
	@NamedQuery(name=AspectoAmbientalPma.GET_ASPECTO_POR_PLAN, query="SELECT m FROM AspectoAmbientalPma m where m.estado = true and m.planManejoAmbientalPma.id = :planId")
})
public class AspectoAmbientalPma extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String GET_ASPECTO_POR_PLAN = "ec.gob.ambiente.rcoa.registroAmbiental.model.AspectoAmbientalPma.getAspectoPorPlan";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="enas_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="enas_descrption")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="enas_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name="enas_aspect_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name="enas_measured_type")
	private String tipoMedida;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mapr_id")
	private PlanManejoAmbientalPma planManejoAmbientalPma;
	
}