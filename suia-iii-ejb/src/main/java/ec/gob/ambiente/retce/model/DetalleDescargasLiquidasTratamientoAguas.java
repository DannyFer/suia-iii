package ec.gob.ambiente.retce.model;

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

/**
 * The persistent class for the liquid_downloads_water_treatment database table.
 * 
 */
@Entity
@Table(name="liquid_downloads_water_treatment", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "ldwt_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "ldwt_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "ldwt_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ldwt_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ldwt_user_update")) })

public class DetalleDescargasLiquidasTratamientoAguas extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ldwt_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="ldde_id")
	private DetalleDescargasLiquidas detalleDescargasLiquidas;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="cwtr_id")
	private CatalogoTratamientoAguas catalogoTratamientoAguas;

	@Getter
	@Setter
	@Column(name="ldwt_other_description")
	private String otroTratamientoAgua;
	
}
