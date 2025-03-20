package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import javax.persistence.FetchType;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * Entidad para oficio de aprobación de requistos técnicos
 * 
 * @author Jonathan Guerrero
 * 
 */
@NamedQueries({ @javax.persistence.NamedQuery(name = OficioAproReqTec.OBTENER_OFICIO_POR_ART, query = "select o from OficioAproReqTec o where o.aprobacionRequisitosTecnicos.id = :apte_id and o.tipoDocumentoId = :doty_id and o.contadorBandejaTecnico=:ofart_time_in_technical"),
	@javax.persistence.NamedQuery(name = OficioAproReqTec.OBTENER_ULTIMO_OFICIO_POR_ART, query = "select o from OficioAproReqTec o where o.aprobacionRequisitosTecnicos.id = :apte_id and o.tipoDocumentoId = :doty_id order by contadorBandejaTecnico desc")})
@Entity
@Table(name = "office_art", schema = "suia_iii")
@AttributeOverrides({
		@javax.persistence.AttributeOverride(name = "estado", column = @Column(name = "ofart_status")),
		@javax.persistence.AttributeOverride(name = "fechaCreacion", column = @Column(name = "ofart_creation_date")),
		@javax.persistence.AttributeOverride(name = "fechaModificacion", column = @Column(name = "ofart_date_update")),
		@javax.persistence.AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ofart_creator_user")),
		@javax.persistence.AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ofart_user_update")) })
@Filter(name = "filterActive", condition = "ofart_status = 'TRUE'")
public class OficioAproReqTec extends EntidadAuditable {

	/**
     *
     */
	private static final long serialVersionUID = -3370611511388366542L;

	public static final String OBTENER_OFICIO_POR_ART = "ec.gob.ambiente.suia.domain.OficioAproReqTec.OficioAproReqTecPorART";
	public static final String OBTENER_ULTIMO_OFICIO_POR_ART = "ec.gob.ambiente.suia.domain.OficioAproReqTec.UltimoOficioAproReqTecPorART";
	
	public static final String OFICIO_APROBACION_REQUISITOS = "Oficio-AprobacionRequisitos-";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "office_art_id_generator", initialValue = 1, sequenceName = "seq_ofart_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "office_art_id_generator")
	@Column(name = "ofart_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_ofart_id_apte_id")
	@Filter(name = "filterActive", condition = "trart_status = 'TRUE'")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@Column(name = "doty_id")
	private Integer tipoDocumentoId;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "docu_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_ofart_id_documents_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoOficio;

	@Getter
	@Setter
	@Column(name = "docu_id", insertable = false, updatable = false)
	private Integer documentoOficioAprobacion;

	@Getter
	@Setter
	@Column(name = "ofart_report_number")
	private String numeroOficio;
	
	@Getter
	@Setter
	@Column(name = "ofart_report_number_old")
	private String numeroOficioAnterior;
	
	@Getter
	@Setter
	@Column(name = "ofart_date")
	private String fechaOficio;
	@Getter
	@Setter
	@Column(name = "ofart_procedure_type")
	private String tipoTramite;
	@Getter
	@Setter
	@Column(name = "ofart_proponent")
	private String proponente;
	@Getter
	@Setter
	@Column(name = "ofart_company")
	private String empresa;
	@Getter
	@Setter
	@Column(name = "ofart_position")
	private String cargo;
	@Getter
	@Setter
	@Column(name = "ofart_optionalText1")
	private String txtOpcional1;
	@Getter
	@Setter
	@Column(name = "ofart_procedure_number")
	private String numeroTramite;
	@Getter
	@Setter
	@Column(name = "ofart_procedure_date")
	private String fechaTramite;
	@Getter
	@Setter
	@Column(name = "ofart_project_name")
	private String nombreProyecto;
	@Getter
	@Setter
	@Column(name = "ofart_project_number")
	private String numeroProyecto;
	@Getter
	@Setter
	@Column(name = "ofart_mode")
	private String modalidad;
	@Getter
	@Setter
	@Column(name = "ofart_regulations")
	private String normativa;
	@Getter
	@Setter
	@Column(name = "ofart_optionalText2")
	private String txtOpcional2;
	@Getter
	@Setter
	@Column(name = "ofart_tech_rep_numb")
	private String numeroInfTecnico;
	@Getter
	@Setter
	@Column(name = "ofart_tech_rep_date")
	private String fechaInfTecnico;
	@Getter
	@Setter
	@Column(name = "ofart_procedure_type_txt")
	private String txtTipoTramite;
	@Getter
	@Setter
	@Column(name = "ofart_mode_txt")
	private String txtModalidad;
	@Getter
	@Setter
	@Column(name = "ofart_result_present_dispo_txt")
	private String txtDisposicionesPresentancionResultados;

	@Getter
	@Setter
	@Column(name = "ofart_corrections")
	private boolean existeCorrecciones;

	@Getter
	@Setter
	@Column(name = "ofart_type")
	private String tipoTratos;

	@Getter
	@Setter
	@Transient
	private String oficioRealPath;
	
	@Column(name = "ofart_time_in_technical")
	@Getter
	@Setter
	private Integer contadorBandejaTecnico;
	
	public String getNombreOficio(){
		return OFICIO_APROBACION_REQUISITOS+getNumeroOficio()+".pdf";
	}

}
