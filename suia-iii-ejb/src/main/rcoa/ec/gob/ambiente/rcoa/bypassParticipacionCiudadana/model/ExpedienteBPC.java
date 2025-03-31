package ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model;

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

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "expedient_bypass", schema = "coa_bypass_ppc")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "expe_status")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "expe_creator_user")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "expe_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "expe_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "expe_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "expe_status = 'TRUE'")
public class ExpedienteBPC extends EntidadAuditable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="expe_id")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="prco_id")
	private ProyectoLicenciaCoa proyectoLicenciaCoa;

	@Getter
	@Setter
	@Column(name="expe_physics_pronouncement")
	private Boolean pronunciamientoFisico;
	
	@Getter
	@Setter
	@Column(name="expe_approval_pronouncement")	
	private Boolean pronunciamientoAprobado;
	
	@Getter
	@Setter
	@Column(name="expe_physics_resolution")
	private Boolean tieneResolucionFisica;
	
	
	@Getter
	@Setter
	@Column(name="expe_payment")
	private Double pagoControlSeguimiento;

}
