package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="transport_management_companies", schema = "retce")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "tmco_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tmco_date_create")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tmco_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tmco_user_create")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tmco_user_update")) })

@NamedQueries({
	@NamedQuery(name="TransporteEmpresasGestoras.findAll", query="SELECT t FROM TransporteEmpresasGestoras t"),
	@NamedQuery(name = TransporteEmpresasGestoras.GET_POR_RGDRETCE, query = "SELECT t FROM TransporteEmpresasGestoras t WHERE t.idGeneradorRetce = :idRgdRetce and t.tipoActividad.id = :actividad and estado = true and idRegistroOriginal is null ORDER BY t.id desc"),
	@NamedQuery(name = TransporteEmpresasGestoras.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, query = "SELECT t FROM TransporteEmpresasGestoras t WHERE t.idRegistroOriginal = :idEmpresa and estado = true and numeroObservacion = :nroObservacion ORDER BY t.id desc"),
	@NamedQuery(name = TransporteEmpresasGestoras.GET_HISTORIAL_POR_ID, query = "SELECT t FROM TransporteEmpresasGestoras t WHERE t.idRegistroOriginal = :idEmpresa and estado = true ORDER BY t.id desc"),
	@NamedQuery(name = TransporteEmpresasGestoras.GET_HISTORIAL_POR_RGDRETCE, query = "SELECT t FROM TransporteEmpresasGestoras t WHERE t.idGeneradorRetce = :idRgdRetce and t.tipoActividad.id = :actividad and estado = true and idRegistroOriginal is not null ORDER BY t.id desc"),
	@NamedQuery(name = TransporteEmpresasGestoras.GET_HISTORIAL_EMPRESAS_ELIMINADAS_POR_RGDRETCE, 
	query = "SELECT t FROM TransporteEmpresasGestoras t WHERE t.idGeneradorRetce = :idRgdRetce and t.tipoActividad.id = :actividad and estado = true and idRegistroOriginal is not null and idRegistroOriginal in (SELECT a.id FROM TransporteEmpresasGestoras a where a.idGeneradorRetce = :idRgdRetce and t.tipoActividad.id = :actividad and a.estado = false and a.idRegistroOriginal is null ) ORDER BY t.id desc") })

public class TransporteEmpresasGestoras extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_POR_RGDRETCE = "ec.gob.ambiente.retce.model.TransporteEmpresasGestoras.getPorRgdRetce";
	public static final String GET_HISTORIAL_POR_ID_NRO_OBSERVACION = "ec.gob.ambiente.retce.model.TransporteEmpresasGestoras.getHistorialPorIdNroObservacion";
	public static final String GET_HISTORIAL_POR_ID = "ec.gob.ambiente.retce.model.TransporteEmpresasGestoras.getHistorialPorId";
	public static final String GET_HISTORIAL_POR_RGDRETCE = "ec.gob.ambiente.retce.model.TransporteEmpresasGestoras.getHistorialPorRgdRetce";
	public static final String GET_HISTORIAL_EMPRESAS_ELIMINADAS_POR_RGDRETCE = "ec.gob.ambiente.retce.model.TransporteEmpresasGestoras.getHistorialEmpresasEliminadasPorRgdRetce";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tmco_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_type_transport")
	private DetalleCatalogoGeneral tipoActividad;
	
	@Getter
	@Setter
	@Column(name="hwgr_id")
	private Integer idGeneradorRetce;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="id_management_company")
	private SedePrestadorServiciosDesechos empresaGestora;
	
	@Getter
	@Setter
	@Transient
	private Documento certificadoDestruccion;
	
	@Getter
	@Setter
	@Column(name="tmco_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="tmco_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="tmco_observation_number")
	private Integer numeroObservacion;
    
    @Getter
   	@Setter
   	@Column(name="tmco_other_company")
   	private String otraEmpresa;
    
    @Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
    
    @Getter
	@Setter
	@Transient
	private List<TransporteEmpresasGestoras> listaHistorial;
    
    @Getter
	@Setter
	@Transient
	private List<Documento> listaHistorialDocumentos;
    
    
    public boolean equalsObject(Object obj) {
  		if (obj == null)
  			return false;
  		TransporteEmpresasGestoras base = (TransporteEmpresasGestoras) obj;
  		if (this.getId() == null && base.getId() == null)
  			return super.equals(obj);
  		if (this.getId() == null || base.getId() == null)
  			return false;
  		
  		if (this.getId().equals(base.getId())
  				&& this.getEmpresaGestora().getId().equals(base.getEmpresaGestora().getId())
//  				&& ((this.getCertificadoDestruccion() == null && base.getCertificadoDestruccion() == null) || 
//  						(this.getCertificadoDestruccion() != null && base.getCertificadoDestruccion() != null && 
//  						this.getCertificadoDestruccion().getId().equals(base.getCertificadoDestruccion().getId())))
  			)
  			return true;
  		else
  			return false;
  	}
}
