package ec.gob.ambiente.rcoa.model;

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
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "magnitude_criterion", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "macr_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "macr_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "macr_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "macr_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "macr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "macr_status = 'TRUE'")
public class CriterioMagnitud extends EntidadAuditable{
	
	private static final long serialVersionUID = -2406205868897970664L;
	
	@Getter
	@Setter
	@Id
	@Column(name = "macr_id")	
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "vava_id")
	@ForeignKey(name = "fk_vava_id")
	@Getter
	@Setter
	private ValorMagnitud valorMagnitud;
	
	@ManyToOne
	@JoinColumn(name = "prco_id")
	@ForeignKey(name = "fk_prco_id")
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	@Column(name = "crva_id")
	private Integer variableCriterio;
	
	@Getter
	@Setter
	@Column(name = "macr_rank")
	private Integer rango;
	
	@Getter
	@Setter
	@Column(name = "macr_rank_description")
	private String rangoDescripcion;
	
	@Getter
	@Setter
	@Column(name = "macr_value")
	private Integer valor;
	
	@Getter
	@Setter
	@Column(name = "macr_priority")
	private Boolean prioridad;
	
	@Getter
	@Setter
	@Column(name = "macr_order")
	private Integer orden;
	
	@Getter
	@Setter
	@Column(name = "macr_observation_bd")
	private String observacionBase;
	
	

}
