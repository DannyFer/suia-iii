package ec.gob.ambiente.suia.domain;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity que representa la tabla hazardous_wastes_generators_associated. </b>
 * 
 * @author Santiago Flores
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Santiago Flores, Fecha: 14/03/2016]
 *          </p>
 */
@Entity
@Table(name = "hazardous_wastes_generators_associated", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "hwga_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "hwga_user_date_creator")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "hwga_user_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "hwga_user_creator")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "hwga_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwga_status = 'TRUE'")
@NamedQueries({
		@NamedQuery(name = RegistroGeneradorDesechosAsociado.LISTAR_POR_CODIGO_DESECHO, query = "SELECT d FROM RegistroGeneradorDesechosAsociado d WHERE d.codigoDesecho = :codigoDesecho AND d.estado = true")
})

public class RegistroGeneradorDesechosAsociado extends EntidadAuditable {

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_CODIGO_DESECHO = PAQUETE + "RegistroGeneradorDesechosAsociado.listarPorCodigoDesecho";

	private static final long serialVersionUID = -1948131919746177457L;

	@Id
	@SequenceGenerator(name = "HWGA_HWGAID_GENERATOR", sequenceName="hazardous_wastes_generators_associated_hwga_id_seq", schema="suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HWGA_HWGAID_GENERATOR")
	@Column(name = "hwga_id")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "code_project")
	@Getter
	@Setter
	private String codigoProyecto;

	@Column(name = "code_enviroment_licensing")
	@Getter
	@Setter
	private String codigoPermisoAmbiental;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "hwga_date_enviroment_licensing")
	@Getter
	@Setter
	private Date fechaPermisoAmbiental;

	@Column(name = "hwga_docu_name")
	@Getter
	@Setter
	private String nombreDocumento;

	@Column(name = "hwga_docu_workspace", unique = true)
	@Getter
	@Setter
	private String idAlfresco;

	@Column(name = "hwga_request")
	@Getter
	@Setter
	private String codigoDesecho;

	@Column(name = "hwga_permission_suia")
	@Getter
	@Setter
	private boolean permisoSuia;
	
	@Column(name = "hwga_permission_ente")
	@Getter
	@Setter
	private boolean permisoEnte;
	
	@Column(name = "hwga_permission_fisical")
	@Getter
	@Setter
	private boolean permisoFisico;
	
}