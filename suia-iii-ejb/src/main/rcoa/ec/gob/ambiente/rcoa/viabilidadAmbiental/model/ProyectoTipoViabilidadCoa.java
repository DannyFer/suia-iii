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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the project_viability_coa database table.
 * 
 */
@Entity
@Table(name="project_type_viability_coa", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "prtv_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prtv_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prtv_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prtv_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prtv_user_update")) })

@NamedQueries({
@NamedQuery(name="ProyectoTipoViabilidadCoa.findAll", query="SELECT v FROM ProyectoTipoViabilidadCoa v"),
@NamedQuery(name=ProyectoTipoViabilidadCoa.GET_POR_PROYECTO_TIPO, query="SELECT v FROM ProyectoTipoViabilidadCoa v where v.idProyectoLicencia = :proyecto and v.esViabilidadSnap = :EsSnap and v.estado = true") })
public class ProyectoTipoViabilidadCoa extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_POR_PROYECTO_TIPO = PAQUETE + "ProyectoTipoViabilidadCoa.getPorProyecto";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="prtv_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="prco_id")
	private Integer idProyectoLicencia;

	@Getter
	@Setter
	@Column(name="prtv_snap_viability")
	private Boolean esViabilidadSnap;

	
}