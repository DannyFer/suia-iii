/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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
@Table(name = "detail_fauna_species", schema = "suia_iii")
@NamedQueries({
		@NamedQuery(name = "DetalleFaunaEspecies.findAll", query = "SELECT d FROM DetalleFaunaEspecies d"),
		@NamedQuery(name = DetalleFaunaEspecies.LISTAR_POR_FAUNA_SUMA, query = "SELECT d FROM DetalleFaunaEspecies d WHERE d.estado = true AND d.idDetalleFaunaEspecies = :idDetalleFaunaEspecies") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dfsp_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dfsp_status = 'TRUE'")
public class DetalleFaunaEspecies extends EntidadBase {

	private static final long serialVersionUID = -7971468559413752050L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_FAUNA_SUMA = PAQUETE + "DetalleFaunaEspecies.listarPorFaunaSuma";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "DET_ES_ID_GENERATOR", initialValue = 1, sequenceName = "seq_dfsp_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DET_ES_ID_GENERATOR")
	@Basic(optional = false)
	@Column(name = "dfsp_id", nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "dfsp_specie", length = 100)
	private String especie;

	@Getter
	@Setter
	@Column(name = "dfsp_sample_points_id")
	private String puntosMuestreoIds;

	@Getter
	@Setter
	@Column(name = "dfsp_register_number")
	private Integer numeroRegistro;

	@Getter
	@Setter
	@Column(name = "dfsp_wealth", length = 50)
	private String riqueza;

	@Getter
	@Setter
	@Column(name = "dfsp_abundance", length = 50)
	private String abundancia;

	@Getter
	@Setter
	@Column(name = "dfsp_shannon", length = 50)
	private String shannon;

	@Getter
	@Setter
	@Column(name = "dfsp_simpson", length = 50)
	private String simpson;

	@Getter
	@Setter
	@Column(name = "dfsp_ept", length = 50)
	private String ept;

	@Getter
	@Setter
	@Column(name = "dfsp_bmwp_col", length = 50)
	private String bmwpCol;

	@Getter
	@Setter
	@Column(name = "dfsp_index", length = 50)
	private String indice;

	@Getter
	@Setter
	@Column(name = "dfsp_justification", length = 250)
	private String justificacion;

	@Getter
	@Setter
	@JoinColumn(name = "dfse_id", referencedColumnName = "dfse_id")
	@ManyToOne(fetch = FetchType.LAZY)
	@ForeignKey(name = "fk_detail_fauna_species_dsfs_id_detail_sum_fauna_species_dsfs_id")
	private DetalleFaunaSumaEspecies dfseId;

	@Getter
	@Setter
	@Column(name = "dfse_id", insertable = false, updatable = false)
	private Integer idDetalleFaunaEspecies;

	@Getter
	@Setter
	@Transient
	private String ordenConcatenado;
	@Getter
	@Setter
	@Transient
	private String familiaConcatenado;
	@Getter
	@Setter
	@Transient
	private String generoConcatenado;
	@Getter
	@Setter
	@Transient
	private String nombreComunConcatenado;
	@Getter
	@Setter
	@Transient
	private Integer numeroIndividuosConcatenado;
	@Getter
	@Setter
	@Transient
	private String criterioSensibilidadConcatenado;
	@Getter
	@Setter
	@Transient
	private String criterioConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbTipoRegistroConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbModoReproductivoConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbTipoVegetacionConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbSistemaHidrograficoConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbDistribucionColumnaAguaConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbUsoConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbEndemicaConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbCondicionesClimaticasConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbDistribucionVerticalConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbComportamientoSocialConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbGremioAlimenticioConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbPatronActividadConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbFaseLunarConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbSensibilidadConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbEspeciesMigratoriasConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbEspeciesBioindicadorasConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbUicnConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbCitesConcatenado;
	@Getter
	@Setter
	@Transient
	private String cmbLibroRojoConcatenado;

	public DetalleFaunaEspecies() {
		super();
	}

	public DetalleFaunaEspecies(Integer id) {
		super();
		this.id = id;
	}

}
