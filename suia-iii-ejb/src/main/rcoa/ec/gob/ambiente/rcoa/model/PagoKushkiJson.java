package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;


/**
 * The persistent class for the payment_json database table.
 * 
 */
@Entity
@Table(name="payment_json", schema = "coa_mae")
@NamedQueries({
	@NamedQuery(name = PagoKushkiJson.findAll, query="SELECT p FROM PagoKushkiJson p"),
	@NamedQuery(name = PagoKushkiJson.LISTAR_POR_TRAMITE, query = "SELECT p FROM PagoKushkiJson p where p.pajsTransactionstatus ='APPROVAL' and p.pajsMerchantname =:tramite and p.estado= true")})
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "pajs_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "pajs_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "pajs_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "pajs_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "pajs_user_update")) })
public class PagoKushkiJson extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.rcoa.model.PagoKushkiJson";
	public static final String findAll = PAQUETE_CLASE + "listarTodos";
	public static final String LISTAR_POR_TRAMITE = PAQUETE_CLASE + "listarPorTramite";

	@Setter
	@Getter
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
	@Column(name="pajs_id",unique=true, nullable = false)
	private Integer Id_PagoJson;
	@Setter
	@Getter
	@Column(name="observation_bd")
	private String observationBd;
	@Setter
	@Getter
	@Column(name="pajs_acquirerbank")
	private String pajsAcquirerbank;
	@Setter
	@Getter
	@Column(name="pajs_address1")
	private String pajsAddress1;
	@Setter
	@Getter
	@Column(name="pajs_address2")
	private String pajsAddress2;
	@Setter
	@Getter
	@Column(name="pajs_approvalcode")
	private String pajsApprovalcode;
	@Setter
	@Getter
	@Column(name="pajs_approvedtransactionamount")
	private String pajsApprovedtransactionamount;
	@Setter
	@Getter
	@Column(name="pajs_bank")
	private String pajsBank;
	@Setter
	@Getter
	@Column(name="pajs_bincard")
	private String pajsBincard;
	@Setter
	@Getter
	@Column(name="pajs_cardcountry")
	private String pajsCardcountry;
	@Setter
	@Getter
	@Column(name="pajs_cardholdername")
	private String pajsCardholdername;
	@Setter
	@Getter
	@Column(name="pajs_cardtype")
	private String pajsCardtype;
	@Setter
	@Getter
	@Column(name="pajs_country")
	private String pajsCountry;
	@Setter
	@Getter
	@Column(name="pajs_created")
	private String pajsCreated;
	@Setter
	@Getter
	@Column(name="pajs_currencycode")
	private String pajsCurrencycode;
	@Setter
	@Getter
	@Column(name="pajs_documentnumber")
	private String pajsDocumentnumber;
	@Setter
	@Getter
	@Column(name="pajs_documenttype")
	private String pajsDocumenttype;
	@Setter
	@Getter
	@Column(name="pajs_email")
	private String pajsEmail;
	@Setter
	@Getter
	@Column(name="pajs_firstname")
	private String pajsFirstname;
	@Setter
	@Getter
	@Column(name="pajs_foreigncard")
	private Boolean pajsForeigncard;
	@Setter
	@Getter
	@Column(name="pajs_fullresponse")
	private Boolean pajsFullresponse;
	@Setter
	@Getter
	@Column(name="pajs_icevalue")
	private BigDecimal pajsIcevalue;
	@Setter
	@Getter
	@Column(name="pajs_id_ar")
	private String pajsIdAr;
	@Setter
	@Getter
	@Column(name="pajs_ip")
	private String pajsIp;
	@Setter
	@Getter
	@Column(name="pajs_ivavalue")
	private BigDecimal pajsIvavalue;
	@Setter
	@Getter
	@Column(name="pajs_lastfourdigits")
	private String pajsLastfourdigits;
	@Setter
	@Getter
	@Column(name="pajs_lastname")
	private String pajsLastname;
	@Setter
	@Getter
	@Column(name="pajs_maskedcardnumber")
	private String pajsMaskedcardnumber;
	@Setter
	@Getter
	@Column(name="pajs_merchantid")
	private String pajsMerchantid;
	@Setter
	@Getter
	@Column(name="pajs_merchantname")
	private String pajsMerchantname;
	@Setter
	@Getter
	@Column(name="pajs_name")
	private String pajsName;
	@Setter
	@Getter
	@Column(name="pajs_pajs_issuingbank")
	private String pajsPajsIssuingbank;
	@Setter
	@Getter
	@Column(name="pajs_paymentbrand")
	private String pajsPaymentbrand;
	@Setter
	@Getter
	@Column(name="pajs_phone")
	private String pajsPhone;
	@Setter
	@Getter
	@Column(name="pajs_phonenumber")
	private String pajsPhonenumber;
	@Setter
	@Getter
	@Column(name="pajs_price")
	private String pajsPrice;
	@Setter
	@Getter
	@Column(name="pajs_processorbankname")
	private String pajsProcessorbankname;
	@Setter
	@Getter
	@Column(name="pajs_processorname")
	private String pajsProcessorname;
	@Setter
	@Getter
	@Column(name="pajs_processortype")
	private String pajsProcessortype;
	@Setter
	@Getter
	@Column(name="pajs_quantity")
	private Integer pajsQuantity;
	@Setter
	@Getter
	@Column(name="pajs_recap")
	private String pajsRecap;
	@Setter
	@Getter
	@Column(name="pajs_referencia")
	private String pajsReferencia;
	@Setter
	@Getter
	@Column(name="pajs_region")
	private String pajsRegion;
	@Setter
	@Getter
	@Column(name="pajs_requestamount")
	private BigDecimal pajsRequestamount;
	@Setter
	@Getter
	@Column(name="pajs_responsecode")
	private String pajsResponsecode;
	@Setter
	@Getter
	@Column(name="pajs_responsetext")
	private String pajsResponsetext;
	@Setter
	@Getter
	@Column(name="pajs_sitedomain")
	private String pajsSitedomain;
	@Setter
	@Getter
	@Column(name="pajs_sku")
	private String pajsSku;
	@Setter
	@Getter
	@Column(name="pajs_sku2")
	private String pajsSku2;
	@Setter
	@Getter
	@Column(name="pajs_status_date")
	private Timestamp pajsStatusDate;
	@Setter
	@Getter
	@Column(name="pajs_subtotaliva")
	private BigDecimal pajsSubtotaliva;
	@Setter
	@Getter
	@Column(name="pajs_subtotaliva0")
	private BigDecimal pajsSubtotaliva0;
	@Setter
	@Getter
	@Column(name="pajs_syncmode")
	private String pajsSyncmode;
	@Setter
	@Getter
	@Column(name="pajs_ticketnumber")
	private String pajsTicketnumber;
	@Setter
	@Getter
	@Column(name="pajs_token")
	private String pajsToken;
	@Setter
	@Getter
	@Column(name="pajs_transactionid")
	private String pajsTransactionid;
	@Setter
	@Getter
	@Column(name="pajs_transactionreference")
	private String pajsTransactionreference;
	@Setter
	@Getter
	@Column(name="pajs_transactionstatus")
	private String pajsTransactionstatus;
	@Setter
	@Getter
	@Column(name="pajs_transactiontype")
	private String pajsTransactiontype;
	@Setter
	@Getter
	@Column(name="pajs_type")
	private String pajsType;
	//bi-directional many-to-one association to PagoKushki
	@Setter
	@Getter
	@ManyToOne
	@JoinColumn(name="paym_id")
	private PagoKushki payment;

	public PagoKushkiJson() {
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Integer id) {
		// TODO Auto-generated method stub
		
	}

}