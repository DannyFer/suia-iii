/*
 * Copyright (c) 2014 MAGMASOFT (Innovando tecnologia)
 * Todos los derechos reservados.
 * Este software es confidencial y debe usarlo de acorde con los tÃ©rminos de uso.
 */

package ec.gob.ambiente.suia.domain;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Type;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * Clase entidad para almacenar los informes tecnicos de tdr
 * 
 * @author Frank Torres
 * @version 1.0
 */
@Entity
@Table(name = "tdr_technical_record", catalog = "", schema = "suia_iii")
@NamedQueries({
		@NamedQuery(name = "TdrInformeTecnico.findAll", query = "SELECT t FROM TdrInformeTecnico t"),
		@NamedQuery(name = TdrInformeTecnico.FIND_BY_TDR, query = "SELECT t FROM TdrInformeTecnico t WHERE t.tdrEia.id = :idTdrEia") })
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "tdtr_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tdtr_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tdtr_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tdtr_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tdtr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tdtr_status = 'TRUE'")
public class TdrInformeTecnico extends EntidadAuditable {
	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.TdrInformeTecnico.findAll";
	public static final String FIND_BY_TDR = "ec.com.magmasoft.business.domain.TdrInformeTecnico.findByTdr";

	/**
	 * 
	 */
	private static final long serialVersionUID = -3485749007388686609L;

	@Id
	@Getter
	@Setter
	@Column(name = "tdtr_id")
	@SequenceGenerator(name = "TDRTECHNICAL_RECORD_GENERATOR", sequenceName = "SEQ_TDRTECHNICAL_RECORD_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TDRTECHNICAL_RECORD_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "tdtr_record")
	private String antecedente;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "tdtr_project_important_features")
	private String caracteristicasImportantesProyecto;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "tdtr_technical_evaluation")
	private String evaluacionTecnica;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "tdtr_environmental_component")
	private String componenteAmbinetal;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "tdtr_biotic_component")
	private String componenteBiotico;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "tdtr_social_component")
	private String componenteSocial;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "tdtr_cartographic_component")
	private String componenteCartografico;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "tdtr_national_forest_direction")
	private String direccionNacionalForestal;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "tdtr_national_biodiversity_direction")
	private String direccionNacionalBiodiversidad;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "tdtr_justice_ministry")
	private String ministrerioJusticia;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "tdtr_other")
	private String otros;

	@Getter
	@Setter
	@Type(type = "text")
	@Column(name = "tdtr_conclusions_recommendations")
	private String conclusionRecomendacion;

	@Getter
	@Setter
	@Column(name = "tdtr_type")
	private String tipo;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_tdr_technical_recordtdtr_id_usersuser_id")
	@Getter
	@Setter
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name = "tdel_id")
	@ForeignKey(name = "fk_tdr_technical_recordtdtr_id_tdr_eia_licensetdel_id")
	@Getter
	@Setter
	private TdrEiaLicencia tdrEia;

}
