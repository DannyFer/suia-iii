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

import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

/**
 * The persistent class for the analysis_results_forest database table.
 * 
 */
@Entity
@Table(name="interpretation_indices_forest", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "inif_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "inif_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "inif_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "inif_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "inif_user_update")) })

@NamedQueries({
@NamedQuery(name="InterpretacionIndiceForestal.findAll", query="SELECT i FROM InterpretacionIndiceForestal i"),
@NamedQuery(name=InterpretacionIndiceForestal.GET_LISTA_POR_INFORME_TIPO_SITIO, query="SELECT i FROM InterpretacionIndiceForestal i where i.idInformeInspeccion = :idInforme and i.sitio.tipoSitio = :tipoSitio and i.estado = true order by id")
})

public class InterpretacionIndiceForestal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_LISTA_POR_INFORME_TIPO_SITIO = PAQUETE + "InterpretacionIndiceForestal.getListaPorInformeTipoSitio";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="inif_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "sasi_id")
	@ForeignKey(name = "sasi_id")
	private SitioMuestral sitio;

	@Getter
	@Setter
	@Column(name = "inrf_id")
	private Integer idInformeInspeccion;

	@Getter
	@Setter
	@Column(name="inif_shannon_int")
	private Integer interpretacionShannon;

	@Getter
	@Setter
	@Column(name="inif_simpson_int")
	private Integer interpretacionSimpson;

	@Getter
	@Setter
	@Column(name="inif_number_individuals")
	private Integer nroIndividuos;

}