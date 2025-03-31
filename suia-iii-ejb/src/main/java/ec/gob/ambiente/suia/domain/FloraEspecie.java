package ec.gob.ambiente.suia.domain;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the categories_catalog database table.
 * 
 */
@Entity
@Table(name = "flora_species", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "flsp_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "flsp_status = 'TRUE'")
@NamedQueries({
		@NamedQuery(name = "FloraEspecie.findAll", query = "SELECT c FROM FloraEspecie c where c.estado = true "),
		@NamedQuery(name = FloraEspecie.FIND_BY_PUNTO_MUESTREO, query = "SELECT c FROM FloraEspecie c where c.estado = true and c.puntosMuestreoId = :p_puntoId")
 })
public class FloraEspecie extends EntidadAuditable implements
		Serializable {

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String FIND_BY_PUNTO_MUESTREO = PAQUETE + "FloraEspecie.findByPuntoMuestreo";

	/**
	 * 
	 */
	private static final long serialVersionUID = 5236062399325918257L;

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "FLORA_SPECIES_FLSP_GENERATOR", sequenceName = "seq_flsp_id", initialValue = 1, schema = "suia_iii", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FLORA_SPECIES_FLSP_GENERATOR")
	@Column(name = "flsp_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "puntoMuestreo", nullable = false, length = 255)
	private String puntoMuestreo;

	@Getter
	@Setter
	@Column(name = "flsp_family", nullable = false, length = 255)
	private String familia;

	@Getter
	@Setter
	@Column(name = "flsp_genere", nullable = false, length = 255)
	private String genero;

	@Getter
	@Setter
	@Column(name = "flsp_specie", nullable = false, length = 255)
	private String especie;

	@Getter
	@Setter
	@Column(name = "flsp_scientific_name", nullable = false, length = 255)
	private String nombreCientifico;

	@Getter
	@Setter
	@Column(name = "flsp_common_name", nullable = false, length = 255)
	private String nombreComun;

	@Getter
	@Setter
	@Column(name = "geca_vegetation_type", nullable = false)
	private Integer tipoVegetacion;

	@Getter
	@Setter
	@Column(name = "geca_habit", nullable = false)
	private Integer habito;

	@Getter
	@Setter
	@Column(name = "geca_status_specie", nullable = false)
	private Integer estadoIndividuo;

	@Getter
	@Setter
	@Column(name = "geca_use", nullable = false)
	private Integer uso;

	@Getter
	@Setter
	@Column(name = "geca_origin", nullable = false)
	private Integer origen;

	@Getter
	@Setter
	@Column(name = "geca_international_uicn", nullable = false)
	private Integer uicnInternacional;

	@Getter
	@Setter
	@Column(name = "geca_identification_level", nullable = false)
	private Integer nivelIdentificacion;

	@Getter
	@Setter
	@Column(name = "geca_cities", nullable = false)
	private Integer cities;

	@Getter
	@Setter
	@Column(name = "geca_red_book", nullable = false)
	private Integer libroRojo;

	@Getter
	@Setter
	@Column(name = "flsp_collection_number", nullable = true)
	private Double numeroColeccion;

	@Getter
	@Setter
	@Column(name = "flsp_tenure_center", nullable = true, length = 255)
	private String centroTenencia;

	@Getter
	@Setter
	@Column(name = "flsp_picture_description", nullable = true, length = 255)
	private String descripcionFoto;

	@Getter
	@Setter
	@Column(name = "flsp_picture_annex", nullable = true, length = 255)
	private String anexoFoto;

	@Getter
	@Setter
	@Column(name = "flsp_dap", nullable = true)
	private Double dap = 0.0;

	@Getter
	@Setter
	@Column(name = "flsp_total_height", nullable = true)
	private Double alturaTotal = 0.0;

	@Getter
	@Setter
	@Column(name = "flsp_commercial_height", nullable = true)
	private Double alturaComercial = 0.0;
	
    @Getter
    @Setter
    @Column(name = "spfl_id")
    private Integer puntosMuestreoId;

    @Transient
    @Getter
    @Setter
    private byte[] fotoEspecie;
    
    @Transient
    @Getter
    @Setter
    private String contentType;
    
    @Transient
    @Getter
    @Setter
    private boolean archivoEditado;

}