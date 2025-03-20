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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "technical_viability_report", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "tvre_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tvre_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tvre_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tvre_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tvre_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvre_status = 'TRUE'")
public class InformeViabilidadTecnica extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6318425276206201047L;

	public static final String PAQUETE = "ec.gob.ambiente.suia.domain.";

	@Id
	@SequenceGenerator(name = "technical_viability_report_id_generator", initialValue = 1, sequenceName = "seq_tvre_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "technical_viability_report_id_generator")
	@Column(name = "tvre_id")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "fk_report_viability_id_type_documentstydo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tydo_status = 'TRUE'")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@ManyToOne
	@JoinColumn(name = "tvst_id")
	@ForeignKey(name = "fk_report_viability_id_study_viability_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvst_status = 'TRUE'")
	@Getter
	@Setter
	private EstudioViabilidadTecnica estudioViabilidadTecnica;

	@Getter
	@Setter
	@Column(name = "tvst_id", updatable = false, insertable = false)
	private Integer estudioViabilidadTecnicaId;

	@Getter
	@Setter
	@Column(name = "doty_id", updatable = false, insertable = false)
	private Integer tipoDocumentoId;

	@Getter
	@Setter
	@Column(name = "tvre_number")
	private String numero;

	@Getter
	@Setter
	@Column(name = "tvre_html_report")
	private String htmlInforme;

	@Getter
	@Setter
	@Column(name = "tvre_html_template")
	private String htmlPlantilla;

	@Getter
	@Setter
	@Column(name = "tvre_title_study")
	private String tituloEstudio;

	@Getter
	@Setter
	@Column(name = "tvre_finance_by")
	private String financiadoPor;

	@Getter
	@Setter
	@Column(name = "tvre_responsable_technician")
	private String tecnicoResponsable;

	@Getter
	@Setter
	@Column(name = "tvre_report_date")
	@Temporal(TemporalType.DATE)
	private Date fechaInforme;

	@Getter
	@Setter
	@Column(name = "tvre_new_antecedent", length = 3000)
	private String nuevoAntecedente;

	@Getter
	@Setter
	@Column(name = "tvre_actual_store", length = 500)
	private String almacenamientoActual;

	@Getter
	@Setter
	@Column(name = "tvre_project_store", length = 500)
	private String almacenamientoProyecto;
	
	@Getter
	@Setter
	@Column(name = "tvre_actual_scanning", length = 500)
	private String barridoActual;
	
	@Getter
	@Setter
	@Column(name = "tvre_project_scanning", length = 500)
	private String barridoProyecto;
	
	@Getter
	@Setter
	@Column(name = "tvre_actual_collection", length = 500)
	private String recoleccionActual;
	
	@Getter
	@Setter
	@Column(name = "tvre_project_collection", length = 500)
	private String recoleccionProyecto;
	
	@Getter
	@Setter
	@Column(name = "tvre_actual_transfer", length = 500)
	private String transferenciaActual;
	
	@Getter
	@Setter
	@Column(name = "tvre_project_transfer", length = 500)
	private String transferenciaProyecto;
	
	@Getter
	@Setter
	@Column(name = "tvre_actual_disposal", length = 500)
	private String disposicionActual;
	
	@Getter
	@Setter
	@Column(name = "tvre_project_disposal", length = 500)
	private String disposicionProyecto;
	
	@Getter
	@Setter
	@Column(name = "tvre_actual_evacuation", length = 500)
	private String evacuacionActual;
	
	@Getter
	@Setter
	@Column(name = "tvre_project_evacuation", length = 500)
	private String evacuacionProyecto;
	
	@Getter
	@Setter
	@Column(name = "tvre_actual_leached", length = 500)
	private String lixiviadosActual;
	
	@Getter
	@Setter
	@Column(name = "tvre_project_leached", length = 500)
	private String lixiviadosProyecto;
	
	@Getter
	@Setter
	@Column(name = "tvre_actual_runoff", length = 500)
	private String escorrentiaActual;
	
	@Getter
	@Setter
	@Column(name = "tvre_project_runoff", length = 500)
	private String escorrentiaProyecto;
	
	@Getter
	@Setter
	@Column(name = "tvre_actual_residue", length = 500)
	private String residuosActual;
	
	@Getter
	@Setter
	@Column(name = "tvre_project_residue", length = 500)
	private String residuosProyecto;
	
	@Getter
	@Setter
	@Column(name = "tvre_actual_recyclable", length = 500)
	private String reciclablesActual;
	
	@Getter
	@Setter
	@Column(name = "tvre_project_recyclable", length = 500)
	private String reciclablesProyecto;
	
	@Getter
	@Setter
	@Column(name = "tvre_actual_sanitary", length = 500)
	private String sanitariosActual;
	
	@Getter
	@Setter
	@Column(name = "tvre_project_sanitary", length = 500)
	private String sanitariosProyecto;
	
	@Getter
	@Setter
	@Column(name = "tvre_actual_specials", length = 500)
	private String especialesActual;
	
	@Getter
	@Setter
	@Column(name = "tvre_project_specials", length = 500)
	private String especialesProyecto;
	
	@Getter
	@Setter
	@Column(name = "tvre_conclusion")
	private String conclusion;
	
	@Getter
	@Setter
	@Column(name = "tvre_recommendations", length = 500)
	private String recomendaciones;

}
