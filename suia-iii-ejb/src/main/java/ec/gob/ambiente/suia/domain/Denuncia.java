package ec.gob.ambiente.suia.domain;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "complaints", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "comp_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "comp_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "comp_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "comp_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "comp_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "comp_status = 'TRUE'")
public class Denuncia extends EntidadAuditable {

	private static final long serialVersionUID = 7983423941952384571L;

	@Setter
	@Getter
	@SequenceGenerator(name = "COMPLAINT_GENERATOR", schema = "suia_iii", sequenceName = "seq_comp_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPLAINT_GENERATOR")
	@Column(name = "comp_id", unique = true, nullable = false)
	@Id
	private Integer id;

	@Setter
	@Getter
	@Column(name = "comp_date_event")
	@Temporal(TemporalType.DATE)
	private Date fechaEvento;

	@Setter
	@Getter
	@Column(name = "comp_date_record")
	@Temporal(TemporalType.DATE)
	private Date fechaRegistro;

	@Setter
	@Getter
	@Size(max = 255)
	@Column(name = "comp_place_event")
	private String lugarEvento;

	@Setter
	@Getter
	@Size(max = 255)
	@Column(name = "comp_reference")
	private String referencia;

	@Setter
	@Getter
	@Size(max = 255)
	@Column(name = "comp_description")
	private String descripcion;

	@Setter
	@Getter
	@Column(name = "comp_responsible")
	@Size(max = 255)
	@Pattern(regexp = "[a-z]*", message = "Debe ingresar solo texto")
	private String responsable;

	@Setter
	@Getter
	@Column(name = "comp_substance_involved")
	@Size(max = 255)
	private String sustanciaInvolucrada;

	@Setter
	@Getter
	@Column(name = "comp_actions")
	@Size(max = 255)
	private String medidasRealizadas;

	@Setter
	@Getter
	@Column(name = "comp_path_annexes")
	private String pathAnexos;

	@Setter
	@Getter
	@Column(name = "comp_valid")
	private Boolean denunciaValida;

	@Setter
	@Getter
	@ForeignKey(name = "fk_complaintscomp_id_geographical_locationsgelo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gelo_status = 'TRUE'")
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	private UbicacionesGeografica ubicacion;

	@Setter
	@Getter
	@ForeignKey(name = "fk_complaintscomp_id_complainantscomplainant_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "comp_status = 'TRUE'")
	@ManyToOne
	@JoinColumn(name = "complainant_id")
	private Denunciante denunciante;

}
