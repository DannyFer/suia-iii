package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

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
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the operator_delegates_forest database table.
 * 
 */
@Entity
@Table(name="operator_delegates_forest", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "opdf_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "opdf_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "opdf_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "opdf_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "opdf_user_update")) })

@NamedQueries({
@NamedQuery(name="DelegadoOperadorForestal.findAll", query="SELECT d FROM DelegadoOperadorForestal d"),
@NamedQuery(name=DelegadoOperadorForestal.GET_POR_INFORME, query="SELECT d FROM DelegadoOperadorForestal d where d.idInformeInspeccion = :idInforme and d.estado = true") })
public class DelegadoOperadorForestal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";

	public static final String GET_POR_INFORME = PAQUETE + "DelegadoOperadorForestal.getPorInforme";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="opdf_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="inrf_id")
	private Integer idInformeInspeccion;

	@Getter
	@Setter
	@Column(name="opdf_identification")
	private String cedula;

	@Getter
	@Setter
	@Column(name="opdf_names")
	private String nombre;

	@Getter
	@Setter
	@Column(name="opdf_title")
	private String cargo;
}