/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
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
import javax.persistence.Transient;

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
@Table(name = "project_phases_timetabled", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "prph_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prph_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prph_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prph_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prph_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prph_status = 'TRUE'")
public class CronogramaFasesProyectoEia extends EntidadAuditable implements Cloneable{

	/**
	*
	*/
	private static final long serialVersionUID = 8663216887186924551L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "PROJECT_PHASES_TIMETABLED_GENERATOR", sequenceName = "project_phases_timetabled_generator_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECT_PHASES_TIMETABLED_GENERATOR")
	@Column(name = "prph_id")
	private Integer id;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "prph_start_date")
	private Date fechaInicio;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "prph_end_date")
	private Date fechaFin;
	
	@Getter
	@Setter
	@Column(name = "prph_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "prph_notification_number")
	private Integer numeroNotificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "secp_id")
	@ForeignKey(name = "fk_project_phases_timetabled_secp_id_sectors_classifications_phases_secp_id")
	private CatalogoCategoriaFase catalogoCategoriaFase;

	@Getter
    @Setter
    @ManyToOne
	@JoinColumn(name = "eist_id")
	@ForeignKey(name = "fk_project_phases_timetabled_eist_id_envir_imp_stu_eist_id")
    private EstudioImpactoAmbiental estudioAmbiental;
	
	@Override
	public CronogramaFasesProyectoEia clone() throws CloneNotSupportedException {

		 CronogramaFasesProyectoEia clone = (CronogramaFasesProyectoEia)super.clone();
		 clone.setId(null);		 
		 return clone;
	}

}
