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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

/**
 * 
 * @author christian
 */
@Entity
@Table(name = "environmental_impact_studies_items_detail", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "eisd_status")) })
@NamedQueries({
		@NamedQuery(name = EiaOpcionesDetalle.LISTAR_POR_ESTUDIO_OPCION, query = "SELECT e FROM EiaOpcionesDetalle e, EiaOpciones e1  WHERE e.idEstudio = :idEstudio AND e.estado = true AND e.idOpciones = e1.id AND e1.numeroIdentificacion = :numeroIdentificacion"),
		@NamedQuery(name = EiaOpcionesDetalle.LISTAR_POR_ESTUDIO, query = "SELECT e FROM EiaOpcionesDetalle e WHERE e.idEstudio = :idEstudio AND e.estado = true") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eisd_status = 'TRUE'")
public class EiaOpcionesDetalle extends EntidadBase {

	private static final long serialVersionUID = 8723695871002006974L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_ESTUDIO_OPCION = PAQUETE + "EiaOpcionesDetalle.listarPorEstudioOpcion";
	public static final String LISTAR_POR_ESTUDIO = PAQUETE + "EiaOpcionesDetalle.listarPorEstudio";

	@Getter
	@Setter
	@Id
	@Column(name = "eisd_id")
	@SequenceGenerator(name = "ENVIROMENTAL_IMPACT_STUDIES_ITEMS_DETAIL_EISD_ID_GENERATOR", sequenceName = "seq_eisd_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIROMENTAL_IMPACT_STUDIES_ITEMS_DETAIL_EISD_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "eisi_id", referencedColumnName = "eisi_id")
	@ForeignKey(name = "fk_eia_items_detail_eisi_id_eia_items_eisi_id")
	private EiaOpciones eiaOpciones;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "eist_id", referencedColumnName = "eist_id")
	@ForeignKey(name = "fk_eia_items_detail_eist_id_environmental_impact_studies_eist_id")
	private EstudioImpactoAmbiental estudioImpactoAmbiental;

	@Getter
	@Setter
	@Column(name = "eist_id", insertable = false, updatable = false)
	private Integer idEstudio;

	@Getter
	@Setter
	@Column(name = "eisi_id", insertable = false, updatable = false)
	private Integer idOpciones;

}
