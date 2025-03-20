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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.inventarioForestal.model.HigherClassification;
import ec.gob.ambiente.rcoa.inventarioForestal.model.SpecieTaxa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

/**
 * The persistent class for the species_resource_forest database table.
 * 
 */
@Entity
@Table(name="species_resource_forest", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "srfo_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "srfo_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "srfo_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "srfo_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "srfo_user_update")) })

@NamedQueries({
@NamedQuery(name="EspeciesInspeccionForestal.findAll", query="SELECT e FROM EspeciesInspeccionForestal e"),
@NamedQuery(name=EspeciesInspeccionForestal.GET_LISTA_ESPECIES_POR_INFORME_TIPO, query="SELECT e FROM EspeciesInspeccionForestal e where e.idInforme = :idInforme and e.tipoRegistro = :tipo and e.estado = true order by id")
})

public class EspeciesInspeccionForestal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_LISTA_ESPECIES_POR_INFORME_TIPO = PAQUETE + "EspeciesInspeccionForestal.getListaEspeciesPorInformeTipo";

	public static Integer muestreoCenso = 1;
	public static Integer cualitativo = 2;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="srfo_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="inrf_id")
	private Integer idInforme;

	@Getter
	@Setter
	@Column(name="srfo_sampling_unit")
	private String nombreSitio;

	@Getter
	@Setter
	@Column(name="srfo_taxonomy")
	private Integer nivelTaxonomia;

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
	@Column(name="srfo_common_name")
	private String nombreComun;

	@Getter
	@Setter
	@Column(name="srfo_dap")
	private Double alturaDap;

	@Getter
	@Setter
	@Column(name="srfo_ht")
	private Double alturaTotal;

	@Getter
	@Setter
	@Column(name="srfo_hc")
	private Double alturaComercial;

	@Getter
	@Setter
	@Column(name="srfo_type")
	private Integer tipoRegistro;

}