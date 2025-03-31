package ec.gob.ambiente.rcoa.model;

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
 * The persistent class for the document_type_flow_not_visible database table.
 * 
 */
@Entity
@Table(name="document_type_flow_not_visible", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "dofl_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "dofl_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "dofl_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "dofl_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "dofl_user_update")) })
@NamedQueries({
@NamedQuery(name="DocumentoFlujoNoVisible.findAll", query="SELECT d FROM DocumentoFlujoNoVisible d"),
@NamedQuery(name=DocumentoFlujoNoVisible.GET_POR_FLUJO, query="SELECT d FROM DocumentoFlujoNoVisible d where d.idFlujo = :idFlujo and estado = true")
})
public class DocumentoFlujoNoVisible extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";

	public static final String GET_POR_FLUJO = PAQUETE + "DocumentoViabilidad.getPorFlujo";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="dofl_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="doty_id")
	private Integer idTipoDocumento;

	@Getter
	@Setter
	@Column(name="flow_id")
	private Integer idFlujo;

}