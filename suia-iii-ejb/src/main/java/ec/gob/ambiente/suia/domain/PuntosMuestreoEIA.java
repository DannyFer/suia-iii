/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author oscar campana
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Oscar Campana, Fecha: 13/08/2015]
 *          </p>
 */
@Entity
@Table(name = "sampling_points_eia", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = PuntosMuestreoEIA.LISTAR, query = "SELECT a FROM PuntosMuestreoEIA a WHERE a.estado = true and a.eia.id = :idEia and a.tipoMedioBiotico= :tipo") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "sapo_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "sapo_status = 'TRUE'")
public class PuntosMuestreoEIA extends EntidadBase {

	private static final long serialVersionUID = 273403518787987760L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR = PAQUETE + "PuntosMuestreoEIA.listar";

	@Id
	@SequenceGenerator(name = "SAPO_GENERATOR", schema = "suia_iii", sequenceName = "seq_sapo_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SAPO_GENERATOR")
	@Column(name = "sapo_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "eist_id", referencedColumnName = "eist_id")
	@ForeignKey(name = "fk_sapo_eist_id_env_impact_studies_eist_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private EstudioImpactoAmbiental eia;
	
	@Getter
	@Setter
	@Column(name = "sapo_point_name")
	private String nombrePunto;

	@Getter
	@Setter
	@OneToMany(mappedBy = "puntosMuestreo", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eslo_status = 'TRUE'")
	private List<RegistroEspeciesEIA> especies;

	@Getter
	@Setter
	@Column(name = "sapo_methodology")
	private String metodologia;

	@Getter
	@Setter
	@JoinColumn(name = "spme_id", referencedColumnName = "spme_id")
	@ForeignKey(name = "fk_sampling_points_methodology_spme_id_sampling_points_eia_spme_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private MetodologiaPuntoMuestreo metodologiaPuntoMuestreo;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "sapo_sampling_date")
	private Date fechaMuestreo;

	@Getter
	@Setter
	@Column(name = "sapo_altitude")
	private Integer altitude;

	@Getter
	@Setter
	@Column(name = "sapo_ept")
	private Double ept;

	@Getter
	@Setter
	@Column(name = "sapo_bmwp")
	private Double bmwp;

	@Getter
	@Setter
	@Column(name = "sapo_ipop")
	private Double indicePopOrgPalmer;

	@Getter
	@Setter
	@Column(name = "sapo_type")
	private String tipoMedioBiotico;

	@Getter
	@Setter
	@Column(name = "sapo_x1")
	private BigDecimal x1;

	@Getter
	@Setter
	@Column(name = "sapo_y1")
	private BigDecimal y1;

	@Getter
	@Setter
	@Column(name = "sapo_x2")
	private BigDecimal x2;

	@Getter
	@Setter
	@Column(name = "sapo_y2")
	private BigDecimal y2;

	@Getter
	@Setter
	@Column(name = "sapo_x3")
	private BigDecimal x3;

	@Getter
	@Setter
	@Column(name = "sapo_y3")
	private BigDecimal y3;

	@Getter
	@Setter
	@Column(name = "sapo_x4")
	private BigDecimal x4;

	@Getter
	@Setter
	@Column(name = "sapo_y4")
	private BigDecimal y4;

	//@Getter
	@Setter
	@Transient
	private Integer abundantes;

	public int getAbundantes(){
		if(this.abundantes == null){
			return 0;
		}
		return this.abundantes;
	}

	//@Getter
	@Setter
	@Transient
	private Integer comunes;

	public int getComunes(){
		if(this.comunes == null){
			return 0;
		}
		return this.comunes;
	}

	//@Getter
	@Setter
	@Transient
	private Integer pocoComunes;

	public int getPocoComunes(){
		if(this.pocoComunes == null){
			return 0;
		}
		return this.pocoComunes;
	}

	//@Getter
	@Setter
	@Transient
	private Integer raras;

	public int getRaras(){
		if(this.raras == null){
			return 0;
		}
		return this.raras;
	}

	@Getter
	@Setter
	@Transient
	private String punto;

	@Getter
	@Setter
	@Transient
	private int cantRegEspecies;

	public void initAbundancia() {
		this.abundantes = null;
		this.comunes = null;
		this.pocoComunes = null;
		this.raras = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.nombrePunto+"";
	}
}
