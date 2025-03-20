package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.rcoa.inventarioForestal.model.HigherClassification;
import ec.gob.ambiente.rcoa.inventarioForestal.model.SpecieTaxa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the analysis_results_forest database table.
 * 
 */
@Entity
@Table(name="analysis_results_forest", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "anrf_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "anrf_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "anrf_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "anrf_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "anrf_user_update")) })

@NamedQueries({
@NamedQuery(name="AnalisisResultadoForestal.findAll", query="SELECT a FROM AnalisisResultadoForestal a"),
@NamedQuery(name=AnalisisResultadoForestal.GET_LISTA_POR_SITIO, query="SELECT a FROM AnalisisResultadoForestal a where a.idSitio = :idSitio and a.estado = true order by id")
})

public class AnalisisResultadoForestal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_LISTA_POR_SITIO = PAQUETE + "AnalisisResultadoForestal.getListaPorSitio";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="anrf_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="sasi_id")
	private Integer idSitio;

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
	@Column(name="anrf_common_name")
	private String nombreComun;

	@Getter
	@Setter
	@Column(name="anrf_frequency")
	private Integer frecuencia;

	@Getter
	@Setter
	@Column(name="anrf_area_basal")
	private Double areaBasal;	

	@Getter
	@Setter
	@Column(name="anrf_dnr")
	private Double valorDnr;

	@Getter
	@Setter
	@Column(name="anrf_dmr")
	private Double valorDmr;

	@Getter
	@Setter
	@Column(name="anrf_ivi")
	private Double valorIvi;
	
	@Getter
	@Setter
	@Column(name="anrf_value_pi")
	private Double valorPi; //valor de la frecuencia de la especie dividido para el total de individuos del sitio
	
	@Getter
	@Setter
	@Column(name="anrf_value_lnpi")
	private Double valorLnPi; //logaritmo natural del valor Pi
	
	@Getter
	@Setter
	@Column(name="anrf_value_pi_double")
	private Double valorPiCuadrado; //valor Pi elevado al cuadrado

}