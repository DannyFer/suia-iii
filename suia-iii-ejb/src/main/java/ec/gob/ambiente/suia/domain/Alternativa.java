/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * @author carlos.pupo
 */
@Entity
@Table(name = "alternatives", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "alte_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "alte_status = 'TRUE'")
public class Alternativa extends EntidadBase implements Cloneable {

	private static final long serialVersionUID = -4291139147364211455L;

	@Id
	@Column(name = "alte_id")
	@SequenceGenerator(name = "ALTERNATIVES_GENERATOR", initialValue = 1, sequenceName = "seq_alte_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ALTERNATIVES_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "alte_name")
	private String nombre;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "tcty_id")
	@ForeignKey(name = "fk_alternativesalte_id_tcty_id")
	private TipoCriterioTecnico tipoCriterioTecnico;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "esty_id")
	@ForeignKey(name = "fk_alternativesalte_id_esty_id")
	private TipoSistemaEcologico tipoSistemaEcologico;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "soty_id")
	@ForeignKey(name = "fk_alternativesalte_id_soty_id")
	private TipoSistemaSocioeconomico tipoSistemaSocioeconomico;

	@Getter
	@Setter
	@Column(name = "alte_characteristic")
	private String caracteristica;

	@Getter
	@Setter
	@Column(name = "alte_best_option")
	private boolean mejorOpcion;
	
	@Getter
	@Setter
	@Column(name = "alte_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "alte_notification_number")
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
	@Transient
	private Boolean mejorOpcionModificado;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "imac_id")
	@ForeignKey(name = "fk_alternativesalte_id_activitiesimac_id")
	private ActividadImplantacion actividadImplantacion;
	
	@Getter
	@Setter
	@Column(name = "alte_date_create")
	private Date fechaCreacion;

	@Override
	public String toString() {
		return nombre;
	}

	public Alternativa(Integer id) {
		super();
		this.id = id;
	}

	public Alternativa() {
		super();
	}
	
	@Override
	public Alternativa clone() throws CloneNotSupportedException {

		 Alternativa clone = (Alternativa)super.clone();
		 clone.setId(null);		 
		 return clone;
	}
}
