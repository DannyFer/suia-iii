/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase que representa a la tabla que contiene las fases del proyecto. </b>
 *
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 07/08/2015 $]
 *          </p>
 *
 */
@Entity
@Table(name = "eia_chemical_sustances", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "eich_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "eich_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "eich_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "eich_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "eich_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eich_status = 'TRUE'")
public class SustanciaQuimicaEia extends EntidadAuditable implements Cloneable{

	/**
	 *
	 */
	private static final long serialVersionUID = -5095064725231275969L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "EIA_CHEMICAL_SUSTANCES_GENERATOR", sequenceName = "eia_chemical_sustances_generator_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EIA_CHEMICAL_SUSTANCES_GENERATOR")
	@Column(name = "eich_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="dach_id")
	@ForeignKey(name = "fk_eia_chemical_sustances_eich_id_dan_chem_subs_dach_id")
	private SustanciaQuimicaPeligrosa sustanciaQuimicaPeligrosa;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="eist_id")
	@ForeignKey(name = "fk_eia_chemica_sustances_eist_id_enviroment_impact_studies")
	private EstudioImpactoAmbiental estudioAmbiental;
	
	@Getter
	@Setter
	@Column(name = "eich_historical_id")
	private Integer idHistorico;

	@Getter
	@Setter
	@Column(name = "eich_notification_number")
	private Integer numeroNotificacion;
	
	@Override
	public SustanciaQuimicaEia clone() throws CloneNotSupportedException {

		 SustanciaQuimicaEia clone = (SustanciaQuimicaEia)super.clone();
		 clone.setId(null);		 
		 return clone;
	}

}
