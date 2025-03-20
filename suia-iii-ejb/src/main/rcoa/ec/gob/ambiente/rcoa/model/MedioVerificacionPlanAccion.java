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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


@Entity
@Table(name="action_plan_finding_description", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "apfd_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "apfd_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "apfd_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "apfd_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "apfd_user_update")) })

@NamedQueries({ 
@NamedQuery(name=MedioVerificacionPlanAccion.GET_POR_HALLAZGO, query="SELECT a FROM MedioVerificacionPlanAccion a where a.hallazgoPlanAccion.id = :idHallazgo and a.estado = true order by id desc")
})
public class MedioVerificacionPlanAccion extends EntidadAuditable implements
		Serializable {
	private static final long serialVersionUID = 1L;


	public static final String GET_POR_HALLAZGO = "ec.gob.ambiente.rcoa.model.MedioVerificacionPlanAccion.getPorHallazgo";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "apfd_id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "apfi_id")
	@ForeignKey(name = "fk_apfi_id")
	@Getter
	@Setter
	private HallazgoPlanAccion hallazgoPlanAccion;

	@Getter
	@Setter
	@Column(name="apfd_description")
	private String descripcion;

	@Getter
	@Setter
	@Transient
	private DocumentosCOA documentoAdjunto;


}