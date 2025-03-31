package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the photographs_forest_report database table.
 * 
 */
@Entity
@Table(name = "photographs_forest_report", schema = "coa_viability")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "phfr_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "phfr_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "phfr_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "phfr_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "phfr_user_update")) })

@NamedQueries({
		@NamedQuery(name = "FotografiaInformeForestal.findAll", query = "SELECT f FROM FotografiaInformeForestal f"),
		@NamedQuery(name = FotografiaInformeForestal.GET_POR_INFORME_TIPO, query = "SELECT f FROM FotografiaInformeForestal f where f.idInformeInspeccion = :idInforme and f.tipoFoto = :tipo and f.estado = true") })
public class FotografiaInformeForestal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";

	public static final String GET_POR_INFORME_TIPO = PAQUETE + "FotografiaInformeForestal.getPorInformeTipo";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "phfr_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "inrf_id")
	private Integer idInformeInspeccion;

	@Getter
	@Setter
	@Column(name = "phfr_document_link")
	private String enlaceDocumento;

	@Getter
	@Setter
	@Column(name = "phfr_fiel_inspection")
	private String descripcion;

	@Getter
	@Setter
	@Column(name = "phfr_photo_date")
	private Date fechaFotografia;

	@Getter
	@Setter
	@Column(name = "phfr_type")
	private Integer tipoFoto; // 1 Estado de cobertura vegetal, 2 tipo de ecosistema, 3 area de implantacion

	@Getter
	@Setter
	@Transient
	private String url;

	@Getter
	@Setter
	@Transient
	private String nombre;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "dovi_id")
	@ForeignKey(name = "dovi_id")
	private DocumentoViabilidad docImagen;
}