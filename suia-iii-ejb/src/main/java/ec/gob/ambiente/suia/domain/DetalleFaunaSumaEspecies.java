/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.Collection;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

/**
 * 
 * @author christian
 */
@Entity
@Table(name = "detail_fauna_sum_species", schema = "suia_iii")
@NamedQueries({
		@NamedQuery(name = "DetailFaunaSumSpecies.findAll", query = "SELECT d FROM DetalleFaunaSumaEspecies d"),
		@NamedQuery(name = DetalleFaunaSumaEspecies.OBTENER_POR_FAUNA, query = "SELECT d FROM DetalleFaunaSumaEspecies d WHERE d.estado = true AND d.idFauna = :idFauna") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dfse_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dfse_status = 'TRUE'")
public class DetalleFaunaSumaEspecies extends EntidadBase {

	private static final long serialVersionUID = 3185434856961950864L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String OBTENER_POR_FAUNA = PAQUETE + "DetalleFaunaSumaEspecies.listarPorFauna";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "DET_ID_GENERATOR", initialValue = 1, sequenceName = "seq_dfse_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DET_ID_GENERATOR")
	@Column(name = "dfse_id", nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "dfse_sum_wealth", length = 50)
	private String sumaRiqueza;

	@Getter
	@Setter
	@Column(name = "dfse_sum_abundance", length = 50)
	private String sumaAbundancia;

	@Getter
	@Setter
	@Column(name = "dfse_sum_shannon", length = 50)
	private String sumaShannon;

	@Getter
	@Setter
	@Column(name = "dfse_sum_simpson", length = 50)
	private String sumaSimpson;

	@Getter
	@Setter
	@OneToMany(mappedBy = "dfseId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Collection<DetalleFaunaEspecies> detailFaunaSpeciesCollection;

	@Getter
	@Setter
	@JoinColumn(name = "faun_id", referencedColumnName = "faun_id")
	@ManyToOne(fetch = FetchType.LAZY)
	@ForeignKey(name = "fk_detail_fauna_suma_species_faun_id_fauna_faun_id")
	private Fauna faunId;

	@Getter
	@Setter
	@Column(name = "faun_id", insertable = false, updatable = false)
	private Integer idFauna;

	public DetalleFaunaSumaEspecies() {
		super();
	}

	public DetalleFaunaSumaEspecies(Integer id) {
		super();
		this.id = id;
	}
}
