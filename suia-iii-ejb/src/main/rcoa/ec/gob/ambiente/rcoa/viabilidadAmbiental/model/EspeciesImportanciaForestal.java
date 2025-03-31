package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.rcoa.inventarioForestal.model.HigherClassification;
import ec.gob.ambiente.rcoa.inventarioForestal.model.SpecieTaxa;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the species_report_forest database table.
 * 
 */
@Entity
@Table(name="species_report_importance_forestal", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "srif_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "srif_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "srif_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "srif_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "srif_user_update")) })

@NamedQueries({
@NamedQuery(name="EspeciesImportanciaForestal.findAll", query="SELECT e FROM EspeciesImportanciaForestal e"),
@NamedQuery(name=EspeciesImportanciaForestal.GET_LISTA_POR_INFORME, query="SELECT e FROM EspeciesImportanciaForestal e where e.idInformeFactibilidad = :idInformeFactibilidad and e.estado = true order by id")
})

public class EspeciesImportanciaForestal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";

	public static final String GET_LISTA_POR_INFORME = PAQUETE + "EspeciesImportanciaForestal.getListaPorInforme";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="srif_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="fere_id")
	private Integer idInformeFactibilidad;

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
	@Transient
	private Integer frecuencia = 1; //siempre es uno, no se puede repetir la especie

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
	@Column(name="srif_conditional_use")
	private Boolean aprovechamientoCondicionado;

	@Getter
	@Setter
	@Column(name="srif_endemic_species")
	private Boolean especieEndemica;

	@Getter
	@Setter
	@Column(name="srif_taxonomy")
	private Integer nivelTaxonomia; //1 = Género, 2 = Especie
	
}