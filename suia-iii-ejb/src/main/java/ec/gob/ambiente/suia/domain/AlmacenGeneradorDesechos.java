package ec.gob.ambiente.suia.domain;

import java.util.ArrayList;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 08/06/2015]
 *          </p>
 */
@Entity
@Table(name = "hazardous_wastes_warehouses", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "hwwa_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwwa_status = 'TRUE'")
public class AlmacenGeneradorDesechos extends EntidadBase {

	private static final long serialVersionUID = 9131790264136846328L;

	@Getter
	@Setter
	@Id
	@Column(name = "hwwa_id")
	@SequenceGenerator(name = "HAZARDOUS_WASTES_HWWA_ID_GENERATOR", sequenceName = "seq_hwwa_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HAZARDOUS_WASTES_HWWA_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "hwwa_retention_pits")
	private double capacidadFosasRetencion;

	@Getter
	@Setter
	@Column(name = "hwwa_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name = "hwwa_environmental_permit_code")
	private String codigoPermisoAmbiental;

	@Getter
	@Setter
	@Column(name = "hwwa_fire_system")
	private String sistemaExtincionIncendios;

	@Getter
	@Setter
	@Column(name = "hwwa_environmental_permit_date")
	@Temporal(TemporalType.DATE)
	private Date fechaPermisoAmbiental;

	@Getter
	@Setter
	@Column(name = "hwwa_long")
	private double largo;

	@Getter
	@Setter
	@Column(name = "hwwa_width")
	private double ancho;

	@Getter
	@Setter
	@Column(name = "hwwa_height")
	private double altura;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "loty_id")
	@ForeignKey(name = "fk_hwwa_id_loty_id")
	private TipoLocal tipoLocal;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "vety_id")
	@ForeignKey(name = "fk_hwwa_id_vety_id")
	private TipoVentilacion tipoVentilacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "ilty_id")
	@ForeignKey(name = "fk_hwwa_id_ilty_id")
	private TipoIluminacion tipoIluminacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "bmty_id")
	@ForeignKey(name = "fk_hwwa_id_bmty_id")
	private TipoMaterialConstruccion tipoMaterialConstruccion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "epty_id")
	@ForeignKey(name = "fk_hwwa_id_epty_id")
	private TipoPermisoAmbiental tipoPermisoAmbiental;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_hazardous_wastes_warehouses_docu_id_documents_docu_id")
	private Documento permisoAlmacenamientoTmp;

	@Getter
	@Setter
	@OneToMany(mappedBy = "generadorDesechosAlmacen")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwsm_status = 'TRUE'")
	private List<GeneradorDesechosAlmacenMedidaSeguridad> generadorDesechosAlmacenMedidasSeguridad;

	@Getter
	@Setter
	@OneToMany(mappedBy = "almacenGeneradorDesechos")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwdw_status = 'TRUE'")
	private List<AlmacenGeneradorDesechoPeligroso> almacenGeneradorDesechoPeligrosos;

	@Setter
	@Transient
	private List<DesechoPeligroso> desechosPeligrosos;

	@Getter
	@Setter
	@Transient
	private boolean eliminardesecho;

	public List<DesechoPeligroso> getDesechosPeligrosos() {
		if (desechosPeligrosos == null)
			desechosPeligrosos = new ArrayList<DesechoPeligroso>();
		if (almacenGeneradorDesechoPeligrosos != null) {
			for (AlmacenGeneradorDesechoPeligroso desecho : almacenGeneradorDesechoPeligrosos) {
				if (desecho.getGeneradorDesechosDesechoPeligroso() != null
						&& desecho.getGeneradorDesechosDesechoPeligroso().getDesechoPeligroso() != null) {
					if (!desechosPeligrosos.contains(desecho.getGeneradorDesechosDesechoPeligroso()
							.getDesechoPeligroso())&& !eliminardesecho)
						desechosPeligrosos.add(desecho.getGeneradorDesechosDesechoPeligroso().getDesechoPeligroso());
				}
			}
		}
		return desechosPeligrosos;
	}

	@Setter
	@Transient
	private List<TipoMedidaSeguridad> medidasSeguridad;

	public List<TipoMedidaSeguridad> getMedidasSeguridad() {
		if (medidasSeguridad == null) {
			medidasSeguridad = new ArrayList<TipoMedidaSeguridad>();
			if (generadorDesechosAlmacenMedidasSeguridad != null) {
				for (GeneradorDesechosAlmacenMedidaSeguridad medida : generadorDesechosAlmacenMedidasSeguridad) {
					if (!medidasSeguridad.contains(medida.getTipoMedidaSeguridad()))
						medidasSeguridad.add(medida.getTipoMedidaSeguridad());
				}
			}
		}
		return medidasSeguridad;
	}

	public String getTextoMedidaSeguridaOpcionOtroSalvado() {
		if (textoMedidaSeguridaOpcionOtro != null && !textoMedidaSeguridaOpcionOtro.isEmpty())
			return textoMedidaSeguridaOpcionOtro;
		else {
			String textoMedidaSeguridaOpcionOtroSalvado = "";
			if (generadorDesechosAlmacenMedidasSeguridad != null) {
				for (GeneradorDesechosAlmacenMedidaSeguridad almacenMedida : generadorDesechosAlmacenMedidasSeguridad) {
					if (almacenMedida.getOtro() != null && !almacenMedida.getOtro().isEmpty()) {
						textoMedidaSeguridaOpcionOtroSalvado = almacenMedida.getOtro();
						return textoMedidaSeguridaOpcionOtroSalvado;
					}
				}
			}
		}
		return "";
	}

	@Getter
	@Setter
	@Transient
	private String textoMedidaSeguridaOpcionOtro;
}
