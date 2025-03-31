package ec.gob.ambiente.rcoa.viabilidadTecnica.model;

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

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "organization_viability", schema = "coa_viability_technical")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "orvi_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "orvi_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "orvi_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "orvi_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "orvi_user_update")) })

@NamedQueries({
		@NamedQuery(name = "OrganizacionViabilidadTecnica.findAll", query = "SELECT v FROM OrganizacionViabilidadTecnica v "),
		@NamedQuery(name = OrganizacionViabilidadTecnica.GET_POR_ORGANIZACION_ACTIVIDAD, query = "SELECT v FROM OrganizacionViabilidadTecnica v WHERE v.tipoActividadPermitida = :tipoActividad and v.organizacion = :organizacion and v.estado = true order by 1") })

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "orvi_status = 'TRUE'")
public class OrganizacionViabilidadTecnica extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadTecnica.model.";
	
	public static final String GET_POR_ORGANIZACION_ACTIVIDAD = PAQUETE + "OrganizacionViabilidadTecnica.getPorOrganizacionActividad";

	public static final String TIPO_ENTIDAD_MINISTERIO = "1";
	public static final String TIPO_ENTIDAD_GOBIERNO_PROVINCIAL = "2";
	public static final String TIPO_ENTIDAD_GOBIERNO_MUNICIPAL = "3";
	public static final String TIPO_ENTIDAD_EMPRESA_PROVINCIAL = "4";
	public static final String TIPO_ENTIDAD_EMPRESA_MUNICIPAL = "5";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "orvi_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "orga_id")
	private Organizacion organizacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id_pro")	
	private CatalogoGeneralCoa tipoActividadPermitida;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id_ent")	
	private CatalogoGeneralCoa tipoEntidad;

}