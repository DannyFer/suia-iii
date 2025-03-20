package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.Periodicidad;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "container_washing_effluent_treatment", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "cwet_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "cwet_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "cwet_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cwet_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cwet_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cwet_status = 'TRUE'")
public class LavadoContenedoresYTratamientoEfluentes extends EntidadAuditable {

	/**
	* 
	*/
	private static final long serialVersionUID = -9093561499577523378L;

	@Id
	@Column(name = "cwet_id")
	@SequenceGenerator(name = "CONTAINER_WASHING_AND_EFFLUENT_TREATMENT_GENERATOR", sequenceName = "seq_cwet_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONTAINER_WASHING_AND_EFFLUENT_TREATMENT_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "cwet_wash_site")
	private String sitioLavado;

	@Getter
	@Setter
	@Column(name = "cwet_effluent_treatment_type")
	private String tipoTratamientoEfluentes;

	@Getter
	@Setter
	@Column(name = "cwet_download_place")
	private String lugarDescarga;

	@Getter
	@Setter
	@JoinColumn(name = "cwet_wash_frequency_id", referencedColumnName = "peri_id")
	@ForeignKey(name = "fk_wash_frequency_id_periodicityperi_id")
	@ManyToOne()
	private Periodicidad frecuenciaLavado;
	
	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_modality_treatment_apte_id_approval_requirements_apte_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apte_status = 'TRUE'")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

}
