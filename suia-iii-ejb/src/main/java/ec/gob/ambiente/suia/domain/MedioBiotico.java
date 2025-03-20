/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
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

/**
 * 
 * @author ishmael
 */
@Entity
@Table(name = "biotic_environment", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "bien_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "bien_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "bien_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "bien_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "bien_user_update")) })
@NamedQueries({ @NamedQuery(name = MedioBiotico.BUSCAR_POR_EIA, query = "SELECT m FROM MedioBiotico m WHERE m.estudioImpactoAmbiental = :estudioImpactoAmbiental") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "bien_status = 'TRUE'")
public class MedioBiotico extends EntidadAuditable {

	private static final long serialVersionUID = -221408960788330143L;

	public static final String BUSCAR_POR_EIA = "ec.gob.ambiente.suia.domain.MedioBiotico.buscarPorEia";

	@Id
	@Basic(optional = false)
	@Getter
	@Setter
	@SequenceGenerator(name = "MEDIO_BIOTICO_ID_GENERATOR", initialValue = 1, sequenceName = "seq_bien_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEDIO_BIOTICO_ID_GENERATOR")
	@Column(name = "bien_id", nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "bien_ecosystems")
	private String ecosistemas;

	@Getter
	@Setter
	@Column(name = "bien_description")
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eist_id")
	@ForeignKey(name = "fk_biotic_environmenteist_id_faunaeist_id")
	@Getter
	@Setter
	private EstudioImpactoAmbiental estudioImpactoAmbiental;

}
