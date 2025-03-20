package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the feasibility_report_consultant database table.
 * 
 */
@Entity
@Table(name="feasibility_report_consultant", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "ferc_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "ferc_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "ferc_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ferc_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ferc_user_update")) })

@NamedQueries({
@NamedQuery(name="EquipoConsultorForestal.findAll", query="SELECT e FROM EquipoConsultorForestal e"),
@NamedQuery(name=EquipoConsultorForestal.GET_PRINCIPAL_POR_INFORME, query="SELECT e FROM EquipoConsultorForestal e where e.informeFactibilidad.id = :idInformeFactibilidad and e.tipoConsultor = 1 and e.estado = true order by id"),
@NamedQuery(name=EquipoConsultorForestal.GET_LISTA_EQUIPO_POR_INFORME, query="SELECT e FROM EquipoConsultorForestal e where e.informeFactibilidad.id = :idInformeFactibilidad and e.tipoConsultor = 2 and e.estado = true order by id")
})
public class EquipoConsultorForestal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_PRINCIPAL_POR_INFORME = PAQUETE + "EquipoConsultorForestal.getPrincipalPorInforme";
	public static final String GET_LISTA_EQUIPO_POR_INFORME = PAQUETE + "EquipoConsultorForestal.getListaEquipoPorInforme";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ferc_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="fere_id")
	private InformeFactibilidadForestal informeFactibilidad;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="cons_id")
	private ConsultorForestal consultor;

	@Getter
	@Setter
	@Column(name="ferc_email")
	private String correo;

	@Getter
	@Setter
	@Column(name="ferc_phone")
	private String telefono;

	@Getter
	@Setter
	@Column(name="ferc_type")
	private Integer tipoConsultor; //1 principal, 2 secundario

}