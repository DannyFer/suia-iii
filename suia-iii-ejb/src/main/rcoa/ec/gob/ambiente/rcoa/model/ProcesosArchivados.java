package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name="process_archived", schema="coa_mae")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "prar_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prar_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prar_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prar_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prar_user_update")) })

@NamedQueries({
@NamedQuery(name="ProcesosArchivados.findAll", query="SELECT p FROM ProcesosArchivados p"),
@NamedQuery(name=ProcesosArchivados.GET_POR_PROYECTO, query="SELECT p FROM ProcesosArchivados p where p.codigoProyecto = :codigoProyecto and p.estado = true order by id desc") })

public class ProcesosArchivados  extends EntidadAuditable implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.model.";

	public static final String GET_POR_PROYECTO = PAQUETE + "ProcesosArchivados.getPorProyecto";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="prar_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="prar_code")
	private String codigoProyecto;

	@Getter
	@Setter
	@Column(name = "prar_desciption")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "prar_suspended")
	private Boolean esArchivado;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prar_code", referencedColumnName = "prco_cua", insertable = false, updatable = false)
    private ProyectoLicenciaCoa proyectoLicenciaCoa;

}