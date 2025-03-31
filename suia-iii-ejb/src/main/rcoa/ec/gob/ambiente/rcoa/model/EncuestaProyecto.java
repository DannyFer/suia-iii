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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "operator_suggestions_detail", schema = "public")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "sude_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "sude_creation_date"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "sude_status = 'TRUE'")
@NamedQuery(name = "EncuestaProyecto.findAll", query = "SELECT o FROM EncuestaProyecto o")

public class EncuestaProyecto extends EntidadBase{

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "sude_id")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "user_id")
	private Usuario usuario;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prco_id")
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	@Transient
	private String codigoProyecto;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "opti_id")
	private OpcionesEncuesta opcionesEncuesta;
	
	@Getter
	@Setter
	@Column(name = "sude_value")
	private Boolean valor;
	
}
