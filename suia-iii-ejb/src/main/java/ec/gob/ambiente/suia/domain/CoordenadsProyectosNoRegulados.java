/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

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

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author christian
 */
@Table(name = "unregulated_projects_coordinates", schema = "suia_iii")
@Entity
@NamedQueries({ @NamedQuery(name = CoordenadsProyectosNoRegulados.LISTAR_POR_ESTUDIO_DIAGNOSTICO, query = "SELECT f FROM CoordenadsProyectosNoRegulados f WHERE f.id = :idDiagnostico ") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "unpc_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "unpc_status = 'TRUE'")
public class CoordenadsProyectosNoRegulados extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";

	public static final String LISTAR_POR_ESTUDIO_DIAGNOSTICO = PAQUETE
			+ "CoordenadsProyectosNoRegulados.listarPorDiagnostico";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "CoordenadsProyectosNoRegulados_generator", initialValue = 1, sequenceName = "seq_unpc_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CoordenadsProyectosNoRegulados_generator")
	@Column(name = "unpc_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "unpc_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name = "unpc_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name = "unpc_x")
	private Double valorX;

	@Getter
	@Setter
	@Column(name = "unpc_y")
	private Double valorY;

	@ManyToOne
	@JoinColumn(name = "unpl_id")
	@ForeignKey(name = "fk_unregulated_projects_coordinatesunep_iduepr_unep_id")
	@Getter
	@Setter
	private ProyectoAmbientalNoRegulado proyectoAmbientalNoRegulado;

}
