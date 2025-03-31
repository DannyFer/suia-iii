package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the hazardous_chemicals_detail database table.
 * 
 */
@Entity
@Table(name="hazardous_chemicals_detail", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "hcde_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "hcde_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "hcde_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "hcde_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "hcde_user_update")) })
public class SustanciaQuimicaPeligrosaDetalle extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="hcde_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="dach_id")
	private SustanciaQuimicaPeligrosa sustanciaQuimica;

	@Getter
	@Setter
	@Column(name="hcde_percentage_chemical_mixture")
	private Integer porcentajeSustanciaEnMezcla;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="hach_id")
	private SustanciaQuimicaPeligrosaRetce sustanciaQuimicaPeligrosa;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id")
	private DetalleCatalogoGeneral catalogoEstado;
	
	@Getter	
	@Setter
	@Column(name="hcde_annual_amount")	
	private Double cantidadAnual;
	
	public SustanciaQuimicaPeligrosaDetalle() {
	}

}