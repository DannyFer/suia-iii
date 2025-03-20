/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.Collection;
import java.util.Date;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

/**
 * 
 * @author christian
 */
@Entity
@Table(name = "sampling_points", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "sapo_status")) })
@NamedQueries({
		@NamedQuery(name = "PuntosMuestreo.findAll", query = "SELECT s FROM PuntosMuestreo s"),
		@NamedQuery(name = PuntosMuestreo.LISTAR_POR_FAUNA, query = "SELECT s FROM PuntosMuestreo s WHERE s.idFauna = :idFauna AND s.estado = true"),
		@NamedQuery(name = PuntosMuestreo.LISTAR_POR_FAUNA_IDS, query = "SELECT s.id FROM PuntosMuestreo s WHERE s.idFauna = :idFauna AND s.estado = true")

})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "sapo_status = 'TRUE'")
public class PuntosMuestreo extends EntidadBase {

	private static final long serialVersionUID = 7107507783010058824L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_FAUNA = PAQUETE + "PuntosMuestreo.listarPorFauna";
	public static final String LISTAR_POR_FAUNA_IDS = PAQUETE + "PuntosMuestreo.listarPorFaunaIds";

	@Id
	@Basic(optional = false)
	@Getter
	@Setter
	@SequenceGenerator(name = "PUNTOS_MUESTREO_ID_GENERATOR", initialValue = 1, sequenceName = "seq_sapo_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PUNTOS_MUESTREO_ID_GENERATOR")
	@Column(name = "sapo_id", nullable = false)
	private Integer id;
	@Getter
	@Setter
	@Column(name = "sapo_code", length = 100)
	private String codigo;
	@Getter
	@Setter
	@Column(name = "sapo_sampling_date")
	@Temporal(TemporalType.DATE)
	private Date fechaMuestreo;

	@Getter
	@Setter
	@Column(name = "sapo_tab_index")
	private Integer indiceTab;

	@Getter
	@Setter
	@Column(name = "sapo_sampling_effort", length = 100)
	private String esfuerzoMuestreo;

	@Getter
	@Setter
	@Column(name = "sapo_body_water_features", length = 250)
	private String caracteristicasCuerpoAgua;

	@Getter
	@Setter
	@OneToMany(mappedBy = "puntosMuestreo", fetch = FetchType.LAZY)
	private Collection<DetalleFauna> detailFaunaCollection;

	@Getter
	@Setter
	@JoinColumn(name = "faun_id", referencedColumnName = "faun_id")
	@ForeignKey(name = "fk__sampling_points_faun_id_faun_faun_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Fauna fauna;

	@Getter
	@Setter
	@Column(name = "faun_id", insertable = false, updatable = false)
	private Integer idFauna;

	@Transient
	@Getter
	@Setter
	private CoordenadaGeneral coordenada;

	@Transient
	@Getter
	@Setter
	private CoordenadaGeneral coordenada1;

	@Getter
	@Setter
	@Transient
	private boolean editar;

	@Getter
	@Setter
	@Transient
	private int indice;

	public PuntosMuestreo() {
		super();
	}

	public PuntosMuestreo(Integer id) {
		super();
		this.id = id;
	}
}
