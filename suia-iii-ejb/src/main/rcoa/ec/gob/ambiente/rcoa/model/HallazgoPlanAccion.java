package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name="action_plan_finding", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "apfi_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "apfi_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "apfi_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "apfi_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "apfi_user_update")) })

@NamedQueries({ 
@NamedQuery(name=HallazgoPlanAccion.GET_POR_PLAN, query="SELECT a FROM HallazgoPlanAccion a where a.planAccion.id = :idPlan and a.estado = true order by id desc")
})
public class HallazgoPlanAccion extends EntidadAuditable implements
		Serializable {
	private static final long serialVersionUID = 1L;

	public static Integer noConformidadMayor = 1;
	public static Integer noConformidadMenor = 2;
	public static Integer observacion = 3;

	public static final String GET_POR_PLAN = "ec.gob.ambiente.rcoa.model.HallazgoPlanAccion.getPorPlan";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "apfi_id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "prap_id")
	@ForeignKey(name = "fk_prap_id")
	@Getter
	@Setter
	private PlanAccion planAccion;

	@Getter
	@Setter
	@Column(name="apfi_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="apfi_qualification")
	private Integer calificacion; //1 = No conformidad Mayor: NC   2 = No conformidad Menor: NC   3 = Observación: O

	@Getter
	@Setter
	@Column(name="apfi_corrective_measures")
	private String medidasCorrectivas;

	@Getter
	@Setter
	@Column(name="apfi_schedule_start")
	private Date fechaInicio;

	@Getter
	@Setter
	@Column(name="apfi_schedule_end")
	private Date fechaFin;

	@Getter
	@Setter
	@Column(name="apfi_cost")
	private Double costo;

	@Getter
	@Setter
	@Column(name="apfi_indicators")
	private String indicadores;

	@Getter
	@Setter
	@OneToMany(mappedBy = "hallazgoPlanAccion")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apfd_status = 'TRUE'")
	@OrderBy("id ASC")
	private List<MedioVerificacionPlanAccion> listaMedios;
	
	@Getter
	@Setter
	@Transient
	private List<DocumentosCOA> listaMediosVerificacion, listaInstrumentos;

	@Transient
	private String docsMediosVerificacion;

	@Transient
	private String docsInstrumentos;
	
	public String getDocsMediosVerificacion() {
		String nombre = "";
		if(this.listaMediosVerificacion != null 
			&& this.listaMediosVerificacion.size() > 0) {
			for(DocumentosCOA documento : this.listaMediosVerificacion) {
				nombre = (nombre.equals("") ? documento.getNombreDocumento() : nombre + "<br />" + documento.getNombreDocumento());
			}
		}
		
		return nombre;
	}

	public String getDocsInstrumentos() {
		String nombre = " ";
		if(this.listaInstrumentos != null 
			&& this.listaInstrumentos.size() > 0) {
			for(DocumentosCOA documento : this.listaInstrumentos) {
				nombre = (nombre.equals(" ") ? documento.getNombreDocumento() : nombre + "<br />" + documento.getNombreDocumento());
			}
		}
		
		return nombre;
	}

}