/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase entidad de las sustancias químicas peligrosas. </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 05/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "dangerous_chemistry_substances", schema = "suia_iii")
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dach_status = 'TRUE'")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dach_status")) })
public class SustanciaQuimicaPeligrosa extends EntidadBase {

	/**
	* 
	*/
	private static final long serialVersionUID = 6699207686437022843L;

	@Id
	@Column(name = "dach_id")
	@SequenceGenerator(name = "DANGEROUS_CHEMISTRY_SUBSTANCES_GENERATOR", sequenceName = "dangerous_chemistry_substances_dach_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DANGEROUS_CHEMISTRY_SUBSTANCES_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "dach_type_chemistry_substance")
	private String tipo;

	@Getter
	@Setter
	@Column(name = "dach_cas_code")
	private String numeroCas;

	@Getter
	@Setter
	@Column(name = "dach_onu_code")
	private String codigoOnu;

	@Getter
	@Setter
	@Column(name = "dach_description")
	private String descripcion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "dach_parent_id")
	@ForeignKey(name = "fk_dangerous_chemistry_substances_dach_parent_id_dangerous_chemistry_substances_dach_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wada_status = 'TRUE'")
	private SustanciaQuimicaPeligrosa sustanciaQuimicaPeligrosa;

	@Getter
	@Setter
	@OneToMany(mappedBy = "sustanciaQuimicaPeligrosa")
	private List<SustanciaQuimicaPeligrosa> sustanciaQuimicaPeligrosaHijos;

	@Override
	public String toString() {
		return this.descripcion;
	}

	public boolean isSustanciaFinal() {
		return sustanciaQuimicaPeligrosaHijos == null || sustanciaQuimicaPeligrosaHijos.isEmpty();
	}

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
	private Integer numeroNotificacion;
	
	@Getter
	@Setter
	@Transient
	private Date fechaCreacion;
	
	@Getter
	@Setter
	@Column(name = "dach_status_control")
	private Boolean controlSustancia;
	
	@Getter
	@Setter
	@Column(name = "dach_tariff_heading")
	private String partidaArancelaria;
}
