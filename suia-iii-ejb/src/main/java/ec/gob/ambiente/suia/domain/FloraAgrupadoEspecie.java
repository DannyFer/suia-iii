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
@Table(name = "flora_clustered_species", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "flcs_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "flcs_status = 'TRUE'")
@NamedQueries({
		@NamedQuery(name = "FloraAgrupadoEspecie.findAll", query = "SELECT c FROM FloraAgrupadoEspecie c where c.estado = true ")
 })
public class FloraAgrupadoEspecie extends EntidadAuditable implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5236062399325918257L;

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "FLORA_CLUSTERED_SPECIES_flcs_GENERATOR", sequenceName = "seq_flcs_id", initialValue = 1, schema = "suia_iii", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FLORA_CLUSTERED_SPECIES_flcs_GENERATOR")
	@Column(name = "flcs_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "puntoMuestreo", nullable = false, length = 255)
	private String puntoMuestreo;

	@Getter
	@Setter
	@Column(name = "flcs_family", nullable = false, length = 255)
	private String familia;

	@Getter
	@Setter
	@Column(name = "flcs_genere", nullable = false, length = 255)
	private String genero;

	@Getter
	@Setter
	@Column(name = "flcs_specie", nullable = false, length = 255)
	private String especie;

	@Getter
	@Setter
	@Column(name = "flcs_scientific_name", nullable = false, length = 255)
	private String nombreCientifico;

	@Getter
	@Setter
	@Column(name = "flcs_common_name", nullable = false, length = 255)
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
	@Column(name = "flcs_collection_number", nullable = true)
	private Double numeroColeccion;

	@Getter
	@Setter
	@Column(name = "flcs_tenure_center", nullable = true, length = 255)
	private String centroTenencia;

	@Getter
	@Setter
	@Column(name = "flcs_picture_description", nullable = true, length = 255)
	private String descripcionFoto;

	@Getter
	@Setter
	@Column(name = "flcs_picture_annex", nullable = true, length = 255)
	private String anexoFoto;

	@Getter
	@Setter
	@Column(name = "flcs_dap", nullable = true)
	private Double dap = 0.0;

	@Getter
	@Setter
	@Column(name = "flcs_total_height", nullable = true)
	private Double alturaTotal = 0.0;

	@Getter
	@Setter
	@Column(name = "flcs_commercial_height", nullable = true)
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