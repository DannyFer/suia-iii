package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.WordUtils;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


/**
 * The persistent class for the product_registration database table.
 * 
 */
@Entity
@Table(name="product_registration", schema="chemical_pesticides")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prre_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prre_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prre_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prre_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prre_user_update")) })

@NamedQueries({
@NamedQuery(name="RegistroProducto.findAll", query="SELECT r FROM RegistroProducto r"),
@NamedQuery(name=RegistroProducto.GET_REGISTROS, query="SELECT r FROM RegistroProducto r where r.estado = true order by mesIngreso desc"),
@NamedQuery(name=RegistroProducto.GET_POR_MES_INGRESO, query="SELECT r FROM RegistroProducto r where r.mesIngreso = :fechaMesIngreso and r.estado = true")
})

public class RegistroProducto extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.";
	
	public static final String GET_REGISTROS = PAQUETE + "RegistroProducto.getRegistros";
	public static final String GET_POR_MES_INGRESO = PAQUETE + "RegistroProducto.getPorMesIngreso";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="prre_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="prre_date_agrocalidad_trade")
	private Date fechaIngresoOficioAgrocalidad;

	@Getter
	@Setter
	@Column(name="prre_income_month")
	private Date mesIngreso;

	@Getter
	@Setter
	@Column(name="prre_number_trade_agrocalidad")
	private String numeroOficioAgrocalidad;
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "registroProducto")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prod_status = 'TRUE'")
	@OrderBy("id ASC")
	private List<ProductoPqua> listaDetalleProductos;

	@Getter
	@Setter
	@Transient
	private DocumentoPqua documentoAgrocalidad;

	public String getInfoMesingreso() {
		if(this.mesIngreso != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(this.mesIngreso);
			
			int month = cal.get(Calendar.MONTH) + 1;
			int year = cal.get(Calendar.YEAR);

			return month + "-" + year;
		} else {
			return "-";
		}
	}
	
	public String getMesIngresoSeleccion() {
		if (this.mesIngreso != null) {
			
			SimpleDateFormat simpleformat = new SimpleDateFormat("MMMM", new Locale("es")) ;
			
			String strMonth = simpleformat.format(mesIngreso);
			
			return WordUtils.capitalize(strMonth);
		} else {
			return null;
		}
	}

	public String getAnioIngresoSeleccion() {
		if (this.mesIngreso != null) {
			SimpleDateFormat simpleformat = new SimpleDateFormat("YYYY");
			String strMonth = simpleformat.format(mesIngreso);
			
			return strMonth;
		} else {
			return null;
		}
	}
	
	public Boolean getHabilitarCarga() {
		if (this.mesIngreso != null) {
			if(mesIngreso.compareTo(new Date()) >= 0) {
				return false;
			}
		} else {
			return false;
		}
		
		return true;
	}

}