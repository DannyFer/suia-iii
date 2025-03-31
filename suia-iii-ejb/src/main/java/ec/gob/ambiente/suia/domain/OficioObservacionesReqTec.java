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

/**
 * Entidad para oficio de observaciones de requistos técnicos
 * 
 * @author Jonathan Guerrero
 * 
 */
@NamedQueries({ @javax.persistence.NamedQuery(name = OficioObservacionesReqTec.OBTENER_OFICIO_POR_ORT, query = "select o from OficioObservacionesReqTec o where o.aprobacionRequisitosTecnicos.id = :apte_id and o.tipoDocumentoId = :doty_id") })
@Entity
@Table(name = "office_ort", schema = "suia_iii")
@AttributeOverrides({
		@javax.persistence.AttributeOverride(name = "estado", column = @Column(name = "ofort_status")),
		@javax.persistence.AttributeOverride(name = "fechaCreacion", column = @Column(name = "ofort_creation_date")),
		@javax.persistence.AttributeOverride(name = "fechaModificacion", column = @Column(name = "ofort_date_update")),
		@javax.persistence.AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ofort_creator_user")),
		@javax.persistence.AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ofort_user_update")) })
@Filter(name = "filterActive", condition = "ofort_status = 'TRUE'")
public class OficioObservacionesReqTec extends EntidadAuditable {

	/**
	* 
	*/
	private static final long serialVersionUID = -3370611511388366542L;

	public static final String OBTENER_OFICIO_POR_ORT = "ec.gob.ambiente.suia.domain.OficioAproReqTec.OficioObservacionesReqTecPorORT";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "office_ort_id_generator", initialValue = 1, sequenceName = "seq_ofort_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "office_ort_id_generator")
	@Column(name = "ofort_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_ofort_id_apte_id")
	@Filter(name = "filterActive", condition = "trort_status = 'TRUE'")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@Column(name = "doty_id")
	private Integer tipoDocumentoId;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_office_ort_docu_id_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoOficioObservacion;

	@Getter
	@Setter
	@Column(name = "ofort_report_number")
	private String numeroOficio;
	@Getter
	@Setter
	@Column(name = "ofort_date")
	private String fechaOficio;
	@Getter
	@Setter
	@Column(name = "ofort_procedure_type")
	private String tipoTramite;
	@Getter
	@Setter
	@Column(name = "ofort_proponent")
	private String proponente;
	@Getter
	@Setter
	@Column(name = "ofort_company")
	private String empresa;
	@Getter
	@Setter
	@Column(name = "ofort_position")
	private String cargo;
	@Getter
	@Setter
	@Column(name = "ofort_optionalText1")
	private String txtOpcional1;
	@Getter
	@Setter
	@Column(name = "ofort_procedure_number")
	private String numeroTramite;
	@Getter
	@Setter
	@Column(name = "ofort_procedure_date")
	private String fechaTramite;
	@Getter
	@Setter
	@Column(name = "ofort_project_name")
	private String nombreProyecto;
	@Getter
	@Setter
	@Column(name = "ofort_project_number")
	private String numeroProyecto;
	@Getter
	@Setter
	@Column(name = "ofort_mode")
	private String modalidad;
	@Getter
	@Setter
	@Column(name = "ofort_regulations")
	private String normativa;
	@Getter
	@Setter
	@Column(name = "ofort_optionalText2")
	private String txtOpcional2;
	@Getter
	@Setter
	@Column(name = "ofort_tech_rep_numb")
	private String numeroInfTecnico;
	@Getter
	@Setter
	@Column(name = "ofort_tech_rep_date")
	private String fechaInfTecnico;
	@Getter
	@Setter
	@Column(name = "ofort_observations")
	private String observaciones;
	@Getter
	@Setter
	@Column(name = "ofort_additional_txt")
	private String txtAdicional;
}