/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Dec 20, 2014]
 *          </p>
 */
@NamedQueries({ @NamedQuery(name = CertificadoRegistroAmbiental.FIND_BY_PROJECT, query = "SELECT c FROM CertificadoRegistroAmbiental c WHERE c.proyecto.id = :idProyecto") })
@Entity
@Table(name = "environmental_certificates_registration", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "ence_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ence_status = 'TRUE'")
public class CertificadoRegistroAmbiental extends EntidadBase {

	private static final long serialVersionUID = -4750945805173900103L;

	public static final String FIND_BY_PROJECT = "ec.com.magmasoft.business.domain.CertificadoRegistroAmbiental.byProject";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "ence_id")
	private Integer id;

	@OneToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_environmental_certificatesence_id_projects_envpren_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;

	@OneToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_environmental_certificatesence_id_documentsdocu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	@Getter
	@Setter
	private Documento documento;
}
