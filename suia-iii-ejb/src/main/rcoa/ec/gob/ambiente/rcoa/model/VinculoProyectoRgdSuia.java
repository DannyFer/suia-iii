package ec.gob.ambiente.rcoa.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "bonding_coa_rgd", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "bocr_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "bocr_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "bocr_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "bocr_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "bocr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "bocr_status = 'TRUE'")
public class VinculoProyectoRgdSuia extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1645985721278202830L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "bocr_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="prco_id")
	private Integer idProyectoRcoa;

	@Getter
	@Setter
	@Column(name="hwge_id")
	private Integer idRgdSuia;
	
}
