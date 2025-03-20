package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

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
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.inventarioForestal.model.HigherClassification;
import ec.gob.ambiente.rcoa.inventarioForestal.model.SpecieTaxa;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

/**
 * The persistent class for the species_report_forest database table.
 * 
 */
@Entity
@Table(name="species_report_forest", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "sprf_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "sprf_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "sprf_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "sprf_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "sprf_user_update")) })

@NamedQueries({
@NamedQuery(name="EspeciesInformeForestal.findAll", query="SELECT e FROM EspeciesInformeForestal e"),
@NamedQuery(name=EspeciesInformeForestal.GET_LISTA_ESPECIES_POR_SITIO, query="SELECT e FROM EspeciesInformeForestal e where e.idSitio = :idSitio and e.estado = true order by id")
})

public class EspeciesInformeForestal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_LISTA_ESPECIES_POR_SITIO = PAQUETE + "EspeciesInformeForestal.getListaEspeciesPorSitio";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="sprf_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="sasi_id")
	private Integer idSitio;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="hicl_id_family")
	private HigherClassification familiaEspecie;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="hicl_id_genre")
	private HigherClassification generoEspecie;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="spta_id")
	private SpecieTaxa especie;

	@Getter
	@Setter
	@Column(name="sprf_frequency")
	private Integer frecuencia;

	@ManyToOne
	@JoinColumn(name = "geca_id_uicn")	
	@Getter
	@Setter
	private CatalogoGeneralCoa estadoConservacion;

	@ManyToOne
	@JoinColumn(name = "geca_id_cites")	
	@Getter
	@Setter
	private CatalogoGeneralCoa estadoCites;

	@Getter
	@Setter
	@Column(name="sprf_conditional_use")
	private Boolean aprovechamientoCondicionado;

	@Getter
	@Setter
	@Column(name="sprf_endemic_species")
	private Boolean especieEndemica;

	@Getter
	@Setter
	@Column(name="sprf_common_name")
	private String nombreComun;

	@Getter
	@Setter
	@Column(name="sprf_dap")
	private Double alturaDap;

	@Getter
	@Setter
	@Column(name="sprf_ht")
	private Double alturaTotal;

	@Getter
	@Setter
	@Column(name="sprf_hc")
	private Double alturaComercial;

	@Getter
	@Setter
	@Column(name="sprf_ab")
	private Double areaBasal;	

	@Getter
	@Setter
	@Column(name="sprf_vt")
	private Double volumenTotal;

	@Getter
	@Setter
	@Column(name="sprf_vc")
	private Double volumenComercial;

	@Getter
	@Setter
	@Column(name="sprf_taxonomy")
	private Integer nivelTaxonomia; //1 = Género, 2 = Especie

	@Getter
	@Setter
	@Transient
	private SitioMuestral sitioMuestral;

}