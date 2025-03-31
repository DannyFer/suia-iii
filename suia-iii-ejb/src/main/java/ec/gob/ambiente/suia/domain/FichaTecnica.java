package ec.gob.ambiente.suia.domain;

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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "technical_sheets", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "tesh_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tesh_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tesh_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tesh_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tesh_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tesh_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = FichaTecnica.LISTAR_POR_ID_EIA, query = "SELECT f FROM FichaTecnica f WHERE f.estado=true AND f.eiaId = :eiaId") })
public class FichaTecnica extends EntidadAuditable {
	private static final long serialVersionUID = 4566036411599650555L;
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_ID_EIA = PAQUETE
			+ "FichaTecnica.listarPorIdEia";
	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "TECHNICAL_SHEETS_GENERATOR", initialValue = 1, schema = "suia_iii", sequenceName = "seq_tesh_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TECHNICAL_SHEETS_GENERATOR")
	@Column(name = "tesh_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "tesh_description")
	private String descripcion;
	@Getter
	@Setter
	@Column(name="eia_id")
	private Integer eiaId;
	@Transient
	@Getter
	@Setter
	private boolean editar;
    @Transient
    @Getter
    @Setter
    private String nombreArchivo;
    @Transient
    @Getter
    @Setter
    private byte[] contenidoArchivo; 
    @Transient
    @Getter
    @Setter
    private String tipoContenido;
    

}
