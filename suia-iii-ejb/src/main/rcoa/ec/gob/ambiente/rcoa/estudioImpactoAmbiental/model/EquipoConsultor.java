package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.TipoComponenteParticipacion;
import ec.gob.ambiente.suia.domain.Consultor;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "team_consultant", schema = "coa_environmental_impact_study")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "teco_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "teco_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "teco_update_date")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "teco_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "teco_update_user")) })
@NamedQueries({
			@NamedQuery(name=EquipoConsultor.GET_EQUIPOCONSULTOR_POR_PROYECTO, query="SELECT m FROM EquipoConsultor m where m.estado = true and m.informacionProyectoEia.id = :proyectoEIAId"),
			@NamedQuery(name=EquipoConsultor.GET_EQUIPOCONSULTOR_POR_PROYECTO_ID_CONSULTOR, query="SELECT m FROM EquipoConsultor m where m.estado = true and m.informacionProyectoEia.id = :proyectoEIAId and m.consultor.id =:id")
		})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "teco_status = 'TRUE'")
public class EquipoConsultor extends EntidadAuditable {
	public static final String GET_EQUIPOCONSULTOR_POR_PROYECTO = "ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.EquipoConsultor.getEquipoConsultorEIAPorProyecto";
	public static final String GET_EQUIPOCONSULTOR_POR_PROYECTO_ID_CONSULTOR = "ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.EquipoConsultor.getEquipoConsultorEIAProyectoIdConsultor";

	private static final long serialVersionUID = 5200035293094691659L;
	  
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "teco_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "teco_consultant_name", length = 255)
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "teco_number_document_id", length = 255)
	private String documentoIdentificacion;
	
	@Getter
	@Setter
	@Column(name = "teco_mae_code", length = 255)
	private String codigoMae;
	
	@Getter
	@Setter
	@Column(name = "teco_vocational_training", length = 255)
	private String formacion;
	
	@Getter
	@Setter
	@Column(name = "teco_participation_description", length = 255)
	private String participacion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "tycp_id")
	@ForeignKey(name = "tycp_id")
	private TipoComponenteParticipacion tipoComponente;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prin_id")
	@ForeignKey(name = "prin_id")
	private InformacionProyectoEia informacionProyectoEia;
	
	@Getter
	@Setter
	@OneToOne
	@JoinColumn(name = "cons_id")
	@ForeignKey(name = "cons_id")
	private Consultor consultor;
	
	@Getter
	@Setter
	@Column(name = "teco_observation_bdd")
	private String descripcionBdd;

	@Getter
	@Setter
	@Transient
	private boolean seleccionado;

	@Getter
	@Setter
	@Transient
	private DocumentoEstudioImpacto adjuntoCertificado;
	
	@Getter
	@Setter
	@Transient
	private boolean documentoFirmado;
	
	@Getter
	@Setter
	@Transient
	private String nombreRepresentante;
	
	@Getter
	@Setter
	@Transient
	private String cedulaRepresentante;
	
	
}


