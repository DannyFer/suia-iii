package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model;

import java.io.Serializable;
import java.util.Date;

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
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name="pesticide_project", schema = "chemical_pesticides")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "chpe_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "chpe_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "chpe_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "chpe_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "chpe_user_update")) })

@NamedQueries({
@NamedQuery(name="ProyectoPlaguicidas.findAll", query="SELECT a FROM ProyectoPlaguicidas a"),
@NamedQuery(name=ProyectoPlaguicidas.GET_POR_CODIGO, query="SELECT a FROM ProyectoPlaguicidas a where a.codigoProyecto = :codigoProyecto and a.estado = true order by id"),
@NamedQuery(name=ProyectoPlaguicidas.GET_POR_ID_PRODUCTO, query="SELECT a FROM ProyectoPlaguicidas a where a.productoPqua.id = :idProducto and a.estado = true order by id")
})

public class ProyectoPlaguicidas  extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.";
	
	public static final String GET_POR_CODIGO = PAQUETE + "ProyectoPlaguicidas.getPorCodigo";
	public static final String GET_POR_ID_PRODUCTO = PAQUETE + "ProyectoPlaguicidas.getPorIdProducto";

	public static final int TRAMITE_APROBADO=1;
	public static final int TRAMITE_OBSERVADO=2;
	public static final int TRAMITE_ARCHIVADO=3;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="chpe_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="chpe_proyect_code")
	private String codigoProyecto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="prod_id")	
	private ProductoPqua productoPqua;

	@Getter
	@Setter
	@Column(name = "chpe_project_send_date")
	private Date fechaEnvioRevision;

	@Getter
	@Setter
	@Column(name = "chpe_final_result")
	private Integer resultadoRevision;

	@Getter
	@Setter
	@Column(name = "chpe_final_result_date")
	private Date fechaResultado;


}