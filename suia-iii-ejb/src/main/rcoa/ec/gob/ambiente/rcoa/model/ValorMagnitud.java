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
@Table(name = "variable_value", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "vava_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "vava_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "vava_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "vava_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "vava_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "vava_status = 'TRUE'")
public class ValorMagnitud extends EntidadAuditable{
	
	private static final long serialVersionUID = -2406205868897970664L;
	
	@Getter
	@Setter
	@Id
	@Column(name = "vava_id")	
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "crva_id")
	@ForeignKey(name = "fk_crva_id")
	@Getter
	@Setter
	private VariableCriterio criterioMagnitud;
	
	@Getter
	@Setter
	@Column(name = "vava_rank")
	private Integer valorVariable;
	
	@Getter
	@Setter
	@Column(name = "vava_rank_description")
	private String rango;
	
	@Getter
	@Setter
	@Column(name = "vava_value")
	private Integer valor;
	
	@Getter
	@Setter
	@Column(name = "vava_observation_bd")
	private String observacionBase;
	

}
