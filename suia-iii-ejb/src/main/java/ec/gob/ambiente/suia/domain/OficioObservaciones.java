package ec.gob.ambiente.suia.domain;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the categories_catalog database table.
 * 
 */
@Entity
@Table(name = "profession_observations", catalog = "", schema = "suia_iii")
@NamedQuery(name = "OficioObservaciones.findAll", query = "SELECT o FROM OficioObservaciones o")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "prob_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prob_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prob_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prob_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prob_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prob_status = 'TRUE'")
public class OficioObservaciones extends EntidadAuditable implements
		Serializable {
	private static final long serialVersionUID = 1L;
	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.OficioObservaciones.findAll";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "PROFESSION_OBSERVATIONS_PROB_ID_GENERATOR", sequenceName = "seq_prob_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROFESSION_OBSERVATIONS_PROB_ID_GENERATOR")
	@Column(name = "prob_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "prob_subject", length = 255)
	private String asunto;

	@Getter
	@Setter
	@Column(name = "prob_observation", columnDefinition = "TEXT")
	private String observacion;

	@Getter
	@Setter
	@Column(name = "prob_process_id")
	private Long proceso;

}