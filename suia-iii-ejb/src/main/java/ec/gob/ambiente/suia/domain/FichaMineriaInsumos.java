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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "record_mining_inputs", schema = "suia_iii")
@NamedQueries({
		@NamedQuery(name = "FichaMineriaInsumos.findAll", query = "SELECT a FROM FichaMineriaInsumos a"),
		@NamedQuery(name = FichaMineriaInsumos.LISTAR_POR_FICHA, query = "SELECT a FROM FichaMineriaInsumos a WHERE a.estado = true AND a.idFichaMineria = :idMineria and idRegistroOriginal = null") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "rmin_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "rmin_status = 'TRUE'")
public class FichaMineriaInsumos extends EntidadBase {

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_FICHA = PAQUETE + "FichaMineriaInsumos.listarPorFicha";
	private static final long serialVersionUID = 2021076247329092617L;

	/**
     *
     */
	@Id
	@Getter
	@Setter
	@Column(name = "rmin_id")
	@SequenceGenerator(name = "RECORD_MINING_INPUTS_GENERATOR", sequenceName = "seq_rmin_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECORD_MINING_INPUTS_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "rmin_quantity")
	private Integer cantidadHijoInsumo;

	@Getter
	@Setter
	@Column(name = "rmiin_other_input")
	private String insumoOtro;

	@Getter
	@Setter
	@Column(name = "rmiin_other_son_input")
	private String hijoInsumoOtro;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gcph_input_id")
	@ForeignKey(name = "fk_record_mining_inputs_gcph_input_id_general_catalogs_physical_gcph_id")
	private CatalogoGeneralFisico catalogoInsumo;

	@Getter
	@Setter
	@Column(name = "gcph_input_id", insertable = false, updatable = false)
	private Integer idCatalogoInsumo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gcph_son_input_id")
	@ForeignKey(name = "fk_record_mining_inputs_gcph_son_input_id_general_catalogs_physical_gcph_id")
	private CatalogoGeneralFisico catalogoHijoInsumo;

	@Getter
	@Setter
	@Column(name = "gcph_son_input_id", insertable = false, updatable = false)
	private Integer idCatalogoHijoInsumo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mien_id")
	@ForeignKey(name = "fk_record_mining_inputs_mien_id_mining_enviromental_record_mien_id_fk")
	private FichaAmbientalMineria fichaAmbientalMineria;

	@Getter
	@Setter
	@Column(name = "mien_id", insertable = false, updatable = false)
	private Integer idFichaMineria;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "meun_id")
	@ForeignKey(name = "fk_record_mining_inputs_meun_id_measurement_units_meun_id")
	private UnidadMedida unidadMedida;

	@Getter
	@Setter
	@Transient
	private int indice;

	public FichaMineriaInsumos() {
	}

	public FichaMineriaInsumos(Integer id) {
		this.id = id;
	}
	
	//Cris F: aumento de campos para el historial	
	@Getter
	@Setter
	@Column(name = "rmin_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name = "rmin_historical_date")
	private Date fechaHistorico;
	
	@Getter
    @Setter
    @Transient
    private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;

}
