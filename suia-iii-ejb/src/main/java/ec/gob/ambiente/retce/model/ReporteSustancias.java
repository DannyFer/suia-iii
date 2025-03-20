package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the report_substances database table.
 * 
 */
@Entity
@Table(name="report_substances", schema = "retce")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "resu_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "resu_date_create")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "resu_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "resu_user_create")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "resu_user_update")) })

@NamedQueries({
	@NamedQuery(name="ReporteSustancias.findAll", query="SELECT c FROM ReporteSustancias c"),
	@NamedQuery(name = ReporteSustancias.GET_POR_ANIO_INFORMACION_PROYECTO, query = "SELECT t FROM ReporteSustancias t WHERE t.informacionProyecto.id = :idProyecto and t.anioDeclaracion = :anio and estado = true ORDER BY t.id desc"),
	@NamedQuery(name = ReporteSustancias.GET_POR_USUARIO, query = "SELECT t FROM ReporteSustancias t WHERE t.usuarioCreacion = :usuario and estado = true ORDER BY t.id desc"),
	@NamedQuery(name = ReporteSustancias.GET_POR_INFORMACION_PROYECTO, query = "SELECT t FROM ReporteSustancias t WHERE t.informacionProyecto.id = :idProyecto and estado = true ORDER BY t.id desc")
	})
public class ReporteSustancias extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_POR_ANIO_INFORMACION_PROYECTO = "ec.gob.ambiente.retce.model.ReporteSustancias.getPorAnioInformacionProyecto";
	public static final String GET_POR_USUARIO = "ec.gob.ambiente.retce.model.ReporteSustancias.getPorUsuario";
	public static final String GET_POR_INFORMACION_PROYECTO = "ec.gob.ambiente.retce.model.ReporteSustancias.getPorInformacionProyecto";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "resu_id")
	private Integer id;	

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "proj_id")
	private InformacionProyecto informacionProyecto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "resp_id")
	private TecnicoResponsable tecnicoResponsable;
	
	@Getter
	@Setter
	@Column(name="resu_code")
	private String codigoTramite;

	@Getter
	@Setter
	@Column(name = "resu_declaration_year")
	private Integer anioDeclaracion;
	
	@Getter
	@Setter
	@Column(name = "resu_date_processing")
	private Date fechaTramite;

	@Getter
	@Setter
	@Column(name = "resu_registration_finalized")
	private Boolean registroFinalizado;
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "reporteSustancias")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "sure_status = 'TRUE'")
	private List<SubstanciasRetce> listaSustanciasRetce;

}