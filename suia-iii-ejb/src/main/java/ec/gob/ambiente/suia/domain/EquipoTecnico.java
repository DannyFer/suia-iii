package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the technical_team database table.
 * 
 */
@Entity
@Table(name = "technical_team", schema = "suia_iii")
@NamedQueries({
		@NamedQuery(name = "EquipoTecnico.findAll", query = "SELECT t FROM EquipoTecnico t"),
		@NamedQuery(name = EquipoTecnico.FIND_BY_TDR, query = "SELECT e FROM EquipoTecnico e WHERE e.tdrEiaLicencia.id = :idTdr") })
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "tete_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tete_date_creation")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tete_date_modification")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tete_user_creation")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tete_user_modification")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tete_status = 'TRUE'")
public class EquipoTecnico extends EntidadAuditable {
	private static final long serialVersionUID = 1L;
	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.EquipoTecnico.findAll";
	public static final String FIND_BY_TDR = "ec.com.magmasoft.business.domain.EquipoTecnico.findByTdr";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "TECHNICAL_TEAM_TETEID_GENERATOR", initialValue = 1, sequenceName = "seq_tete_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TECHNICAL_TEAM_TETEID_GENERATOR")
	@Column(name = "tete_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "tete_speciality")
	private String especialidad;

	@Getter
	@Setter
	@Column(name = "tete_task_develop")
	private String tareasDesarrollar;

	// // bi-directional many-to-one association to TdrAuditLicense
	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "tdal_id")
	// private TdrAuditLicense tdrEiaLicencia;

	// bi-directional many-to-one association to TdrEiaLicense
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@ForeignKey(name = "fk_technical_teamtdel_id_tdr_eia_licensetdel_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	@JoinColumn(name = "tdel_id")
	private TdrEiaLicencia tdrEiaLicencia;

	// // bi-directional many-to-one association to TermsReference
	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "tere_id")
	// private TermsReference termsReference;

}