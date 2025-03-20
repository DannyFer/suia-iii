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
@Table(name = "criterion_variable", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "crva_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "crva_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "crva_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "crva_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "crva_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "crva_status = 'TRUE'")
public class VariableCriterio  extends EntidadAuditable{

	private static final long serialVersionUID = 6873959531147129554L;
	
	@Getter
	@Setter
	@Id
	@Column(name = "crva_id")	
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "crva_variable_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "crva_unit_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "crva_unit")
	private String unidad;
	
	@Getter
	@Setter
	@Column(name = "crva_unit_abbreviation")
	private String unidadAbreviado;
	
	@Getter
	@Setter
	@Column(name = "crva_observation_bd")
	private String observaionBase;
	
	
	@ManyToOne
	@JoinColumn(name = "lecr_id")
	@ForeignKey(name = "fk_lecr_id")
	@Getter
	@Setter
	private NivelMagnitud nivelMagnitud;
	
}
