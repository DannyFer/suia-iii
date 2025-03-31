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
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name="management_plan_ciuu", schema="coa_environmental_record")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "mapc_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "mapc_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "mapc_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mapc_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mapc_user_update")) }
)
public class PlanManejoAmbientalActividad extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="mapc_id")
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "enmp_id")
	private MedidaVerificacionPma medidaVerificacionPma;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "enas_id")
	private AspectoAmbientalPma aspectoAmbientalPma;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "caci_id")
	private CatalogoCIUU catalogoCIUU;
	
}