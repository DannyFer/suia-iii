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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the general_catalog database table.
 * 
 */
@Entity
@Table(name="unique_manifest_treatment", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "umt_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "umt_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "umt_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "umt_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "umt_user_update")) })
public class ManifiestoUnicoTratamiento extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="umt_id")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="unm_id")
	private ManifiestoUnico manifiestoUnico;
	
	@Getter
	@Setter
	@JoinColumn(name="umt_residue_generator")
	private Boolean generaResiduo;
	
	@Getter
	@Setter
	@Transient
	private String opcionGeneraResiduo;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_residue")
	private DetalleCatalogoGeneral detalleCatalogoGeneral;
	

}
