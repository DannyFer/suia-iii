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
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * Clase entidad para almacenar los informes tecnicos
 * 
 * @author Jonathan Guerrero
 * @version 1.0
 */
@Entity
@Table(name = "technical_record", catalog = "", schema = "suia_iii")
@NamedQuery(name = "InformeTecnico.findAll", query = "SELECT it FROM InformeTecnico it")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "tere_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tere_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tere_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tere_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tere_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tere_status = 'TRUE'")
public class InformeTecnico extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Getter
	@Setter
	@Column(name = "tere_id")
	@SequenceGenerator(name = "TECHNICAL_RECORD_GENERATOR", sequenceName = "SEQ_TECHNICAL_RECORD_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TECHNICAL_RECORD_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "tere_record")
	private String antecedente;

	@Getter
	@Setter
	@Column(name = "tere_goal")
	private String objetivo;

	@Getter
	@Setter
	@Column(name = "tere_technical_evaluation")
	private String evaluacionTecnica;

	@Getter
	@Setter
	@Column(name = "tere_observations")
	private String observacion;

	@Getter
	@Setter
	@Column(name = "tere_conclusions_recommendations")
	private String conclusionRecomendacion;

	@Getter
	@Setter
	@Column(name = "tere_corrections")
	private String correccion;
}
