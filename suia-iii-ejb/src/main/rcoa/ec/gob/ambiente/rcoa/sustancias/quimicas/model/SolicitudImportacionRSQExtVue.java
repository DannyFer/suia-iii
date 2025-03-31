package ec.gob.ambiente.rcoa.sustancias.quimicas.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the import_request database table.
 * 
 */
@Entity
@Table(name="import_request_ext", schema = "coa_chemical_sustances")
public class SolicitudImportacionRSQExtVue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	//@GeneratedValue(strategy=GenerationType.)
	@Column(name="id_import_request_ext")
	private Integer idImportRequestExt;
	
	@Column(name="req_no")
	private String reqNo;

	@Temporal(TemporalType.DATE)
	@Column(name="apv_de")
	private Date apvDe;

	@Column(name="ctft_iss_city_cd")
	private String ctftIssCityCd;

	@Column(name="ctft_iss_city_nm")
	private String ctftIssCityNm;

	@Column(name="dclr_ad")
	private String dclrAd;

	@Column(name="dclr_cel_no")
	private String dclrCelNo;

	@Column(name="dclr_cl_cd")
	private String dclrClCd;

	@Column(name="dclr_cl_nm")
	private String dclrClNm;

	@Column(name="dclr_cmp_nm")
	private String dclrCmpNm;

	@Column(name="dclr_cuty_cd")
	private String dclrCutyCd;

	@Column(name="dclr_cuty_nm")
	private String dclrCutyNm;

	@Column(name="dclr_em")
	private String dclrEm;

	@Column(name="dclr_idt_no")
	private String dclrIdtNo;

	@Column(name="dclr_idt_no_type_cd")
	private String dclrIdtNoTypeCd;

	@Column(name="dclr_idt_no_type_nm")
	private String dclrIdtNoTypeNm;

	@Column(name="dclr_nm")
	private String dclrNm;

	@Column(name="dclr_prqi_cd")
	private String dclrPrqiCd;

	@Column(name="dclr_prqi_nm")
	private String dclrPrqiNm;

	@Column(name="dclr_prvhc_cd")
	private String dclrPrvhcCd;

	@Column(name="dclr_prvhc_nm")
	private String dclrPrvhcNm;

	@Column(name="dclr_tel_no")
	private String dclrTelNo;

	@Column(name="impr_ad")
	private String imprAd;

	@Column(name="impr_cl_cd")
	private String imprClCd;

	@Column(name="impr_cl_nm")
	private String imprClNm;

	@Column(name="impr_cmp_nm")
	private String imprCmpNm;

	@Column(name="impr_cuty_cd")
	private String imprCutyCd;

	@Column(name="impr_cuty_nm")
	private String imprCutyNm;

	@Column(name="impr_em")
	private String imprEm;

	@Column(name="impr_idt_no")
	private String imprIdtNo;

	@Column(name="impr_idt_no_type_cd")
	private String imprIdtNoTypeCd;

	@Column(name="impr_idt_no_type_nm")
	private String imprIdtNoTypeNm;

	@Column(name="impr_nm")
	private String imprNm;

	@Column(name="impr_prqi_cd")
	private String imprPrqiCd;

	@Column(name="impr_prqi_nm")
	private String imprPrqiNm;

	@Column(name="impr_prvhc_cd")
	private String imprPrvhcCd;

	@Column(name="impr_prvhc_nm")
	private String imprPrvhcNm;

	@Column(name="impr_rsq_cd")
	private String imprRsqCd;

	@Column(name="impr_tel_no")
	private String imprTelNo;

	@Column(name="ld_ctry_cd")
	private String ldCtryCd;

	@Column(name="ld_ctry_nm")
	private String ldCtryNm;

	@Column(name="ld_port_cd")
	private String ldPortCd;

	@Column(name="ld_port_nm")
	private String ldPortNm;

	@Column(name="ld_trsp_way_cd")
	private String ldTrspWayCd;

	@Column(name="ld_trsp_way_nm")
	private String ldTrspWayNm;

	@Column(name="ld_unld_pl_cd")
	private String ldUnldPlCd;

	@Column(name="ld_unld_pl_nm")
	private String ldUnldPlNm;

	@Temporal(TemporalType.DATE)
	@Column(name="lic_exp_de")
	private Date licExpDe;

	@Temporal(TemporalType.DATE)
	@Column(name="lic_ini_de")
	private Date licIniDe;

	@Column(name="req_city_cd")
	private String reqCityCd;

	@Column(name="req_city_nm")
	private String reqCityNm;

	@Column(name="req_dcm_cd")
	private String reqDcmCd;

	@Column(name="req_dcm_nm")
	private String reqDcmNm;

	@Temporal(TemporalType.DATE)
	@Column(name="req_de")
	private Date reqDe;
	
	//De la tabla tn_eld_edoc_last_stat
	@Column(name="estado_formulario")
	private String estadoFormulario;
	
	//De la tabla tn_eld_edoc_last_stat
	@Column(name="estado_transmision")
	private String estadoTransmision;

	public SolicitudImportacionRSQExtVue() {
	}

	public String getReqNo() {
		return this.reqNo;
	}

	public void setReqNo(String reqNo) {
		this.reqNo = reqNo;
	}

	public Date getApvDe() {
		return this.apvDe;
	}

	public void setApvDe(Date apvDe) {
		this.apvDe = apvDe;
	}

	public String getCtftIssCityCd() {
		return this.ctftIssCityCd;
	}

	public void setCtftIssCityCd(String ctftIssCityCd) {
		this.ctftIssCityCd = ctftIssCityCd;
	}

	public String getCtftIssCityNm() {
		return this.ctftIssCityNm;
	}

	public void setCtftIssCityNm(String ctftIssCityNm) {
		this.ctftIssCityNm = ctftIssCityNm;
	}

	public String getDclrAd() {
		return this.dclrAd;
	}

	public void setDclrAd(String dclrAd) {
		this.dclrAd = dclrAd;
	}

	public String getDclrCelNo() {
		return this.dclrCelNo;
	}

	public void setDclrCelNo(String dclrCelNo) {
		this.dclrCelNo = dclrCelNo;
	}

	public String getDclrClCd() {
		return this.dclrClCd;
	}

	public void setDclrClCd(String dclrClCd) {
		this.dclrClCd = dclrClCd;
	}

	public String getDclrClNm() {
		return this.dclrClNm;
	}

	public void setDclrClNm(String dclrClNm) {
		this.dclrClNm = dclrClNm;
	}

	public String getDclrCmpNm() {
		return this.dclrCmpNm;
	}

	public void setDclrCmpNm(String dclrCmpNm) {
		this.dclrCmpNm = dclrCmpNm;
	}

	public String getDclrCutyCd() {
		return this.dclrCutyCd;
	}

	public void setDclrCutyCd(String dclrCutyCd) {
		this.dclrCutyCd = dclrCutyCd;
	}

	public String getDclrCutyNm() {
		return this.dclrCutyNm;
	}

	public void setDclrCutyNm(String dclrCutyNm) {
		this.dclrCutyNm = dclrCutyNm;
	}

	public String getDclrEm() {
		return this.dclrEm;
	}

	public void setDclrEm(String dclrEm) {
		this.dclrEm = dclrEm;
	}

	public String getDclrIdtNo() {
		return this.dclrIdtNo;
	}

	public void setDclrIdtNo(String dclrIdtNo) {
		this.dclrIdtNo = dclrIdtNo;
	}

	public String getDclrIdtNoTypeCd() {
		return this.dclrIdtNoTypeCd;
	}

	public void setDclrIdtNoTypeCd(String dclrIdtNoTypeCd) {
		this.dclrIdtNoTypeCd = dclrIdtNoTypeCd;
	}

	public String getDclrIdtNoTypeNm() {
		return this.dclrIdtNoTypeNm;
	}

	public void setDclrIdtNoTypeNm(String dclrIdtNoTypeNm) {
		this.dclrIdtNoTypeNm = dclrIdtNoTypeNm;
	}

	public String getDclrNm() {
		return this.dclrNm;
	}

	public void setDclrNm(String dclrNm) {
		this.dclrNm = dclrNm;
	}

	public String getDclrPrqiCd() {
		return this.dclrPrqiCd;
	}

	public void setDclrPrqiCd(String dclrPrqiCd) {
		this.dclrPrqiCd = dclrPrqiCd;
	}

	public String getDclrPrqiNm() {
		return this.dclrPrqiNm;
	}

	public void setDclrPrqiNm(String dclrPrqiNm) {
		this.dclrPrqiNm = dclrPrqiNm;
	}

	public String getDclrPrvhcCd() {
		return this.dclrPrvhcCd;
	}

	public void setDclrPrvhcCd(String dclrPrvhcCd) {
		this.dclrPrvhcCd = dclrPrvhcCd;
	}

	public String getDclrPrvhcNm() {
		return this.dclrPrvhcNm;
	}

	public void setDclrPrvhcNm(String dclrPrvhcNm) {
		this.dclrPrvhcNm = dclrPrvhcNm;
	}

	public String getDclrTelNo() {
		return this.dclrTelNo;
	}

	public void setDclrTelNo(String dclrTelNo) {
		this.dclrTelNo = dclrTelNo;
	}

	public String getImprAd() {
		return this.imprAd;
	}

	public void setImprAd(String imprAd) {
		this.imprAd = imprAd;
	}

	public String getImprClCd() {
		return this.imprClCd;
	}

	public void setImprClCd(String imprClCd) {
		this.imprClCd = imprClCd;
	}

	public String getImprClNm() {
		return this.imprClNm;
	}

	public void setImprClNm(String imprClNm) {
		this.imprClNm = imprClNm;
	}

	public String getImprCmpNm() {
		return this.imprCmpNm;
	}

	public void setImprCmpNm(String imprCmpNm) {
		this.imprCmpNm = imprCmpNm;
	}

	public String getImprCutyCd() {
		return this.imprCutyCd;
	}

	public void setImprCutyCd(String imprCutyCd) {
		this.imprCutyCd = imprCutyCd;
	}

	public String getImprCutyNm() {
		return this.imprCutyNm;
	}

	public void setImprCutyNm(String imprCutyNm) {
		this.imprCutyNm = imprCutyNm;
	}

	public String getImprEm() {
		return this.imprEm;
	}

	public void setImprEm(String imprEm) {
		this.imprEm = imprEm;
	}

	public String getImprIdtNo() {
		return this.imprIdtNo;
	}

	public void setImprIdtNo(String imprIdtNo) {
		this.imprIdtNo = imprIdtNo;
	}

	public String getImprIdtNoTypeCd() {
		return this.imprIdtNoTypeCd;
	}

	public void setImprIdtNoTypeCd(String imprIdtNoTypeCd) {
		this.imprIdtNoTypeCd = imprIdtNoTypeCd;
	}

	public String getImprIdtNoTypeNm() {
		return this.imprIdtNoTypeNm;
	}

	public void setImprIdtNoTypeNm(String imprIdtNoTypeNm) {
		this.imprIdtNoTypeNm = imprIdtNoTypeNm;
	}

	public String getImprNm() {
		return this.imprNm;
	}

	public void setImprNm(String imprNm) {
		this.imprNm = imprNm;
	}

	public String getImprPrqiCd() {
		return this.imprPrqiCd;
	}

	public void setImprPrqiCd(String imprPrqiCd) {
		this.imprPrqiCd = imprPrqiCd;
	}

	public String getImprPrqiNm() {
		return this.imprPrqiNm;
	}

	public void setImprPrqiNm(String imprPrqiNm) {
		this.imprPrqiNm = imprPrqiNm;
	}

	public String getImprPrvhcCd() {
		return this.imprPrvhcCd;
	}

	public void setImprPrvhcCd(String imprPrvhcCd) {
		this.imprPrvhcCd = imprPrvhcCd;
	}

	public String getImprPrvhcNm() {
		return this.imprPrvhcNm;
	}

	public void setImprPrvhcNm(String imprPrvhcNm) {
		this.imprPrvhcNm = imprPrvhcNm;
	}

	public String getImprRsqCd() {
		return this.imprRsqCd;
	}

	public void setImprRsqCd(String imprRsqCd) {
		this.imprRsqCd = imprRsqCd;
	}

	public String getImprTelNo() {
		return this.imprTelNo;
	}

	public void setImprTelNo(String imprTelNo) {
		this.imprTelNo = imprTelNo;
	}

	public String getLdCtryCd() {
		return this.ldCtryCd;
	}

	public void setLdCtryCd(String ldCtryCd) {
		this.ldCtryCd = ldCtryCd;
	}

	public String getLdCtryNm() {
		return this.ldCtryNm;
	}

	public void setLdCtryNm(String ldCtryNm) {
		this.ldCtryNm = ldCtryNm;
	}

	public String getLdPortCd() {
		return this.ldPortCd;
	}

	public void setLdPortCd(String ldPortCd) {
		this.ldPortCd = ldPortCd;
	}

	public String getLdPortNm() {
		return this.ldPortNm;
	}

	public void setLdPortNm(String ldPortNm) {
		this.ldPortNm = ldPortNm;
	}

	public String getLdTrspWayCd() {
		return this.ldTrspWayCd;
	}

	public void setLdTrspWayCd(String ldTrspWayCd) {
		this.ldTrspWayCd = ldTrspWayCd;
	}

	public String getLdTrspWayNm() {
		return this.ldTrspWayNm;
	}

	public void setLdTrspWayNm(String ldTrspWayNm) {
		this.ldTrspWayNm = ldTrspWayNm;
	}

	public String getLdUnldPlCd() {
		return this.ldUnldPlCd;
	}

	public void setLdUnldPlCd(String ldUnldPlCd) {
		this.ldUnldPlCd = ldUnldPlCd;
	}

	public String getLdUnldPlNm() {
		return this.ldUnldPlNm;
	}

	public void setLdUnldPlNm(String ldUnldPlNm) {
		this.ldUnldPlNm = ldUnldPlNm;
	}

	public Date getLicExpDe() {
		return this.licExpDe;
	}

	public void setLicExpDe(Date licExpDe) {
		this.licExpDe = licExpDe;
	}

	public Date getLicIniDe() {
		return this.licIniDe;
	}

	public void setLicIniDe(Date licIniDe) {
		this.licIniDe = licIniDe;
	}

	public String getReqCityCd() {
		return this.reqCityCd;
	}

	public void setReqCityCd(String reqCityCd) {
		this.reqCityCd = reqCityCd;
	}

	public String getReqCityNm() {
		return this.reqCityNm;
	}

	public void setReqCityNm(String reqCityNm) {
		this.reqCityNm = reqCityNm;
	}

	public String getReqDcmCd() {
		return this.reqDcmCd;
	}

	public void setReqDcmCd(String reqDcmCd) {
		this.reqDcmCd = reqDcmCd;
	}

	public String getReqDcmNm() {
		return this.reqDcmNm;
	}

	public void setReqDcmNm(String reqDcmNm) {
		this.reqDcmNm = reqDcmNm;
	}

	public Date getReqDe() {
		return this.reqDe;
	}

	public void setReqDe(Date reqDe) {
		this.reqDe = reqDe;
	}

	public Integer getIdImportRequestExt() {
		return idImportRequestExt;
	}

	public void setIdImportRequestExt(Integer idImportRequestExt) {
		this.idImportRequestExt = idImportRequestExt;
	}

	public String getEstadoFormulario() {
		return estadoFormulario;
	}

	public void setEstadoFormulario(String estadoFormulario) {
		this.estadoFormulario = estadoFormulario;
	}

	public String getEstadoTransmision() {
		return estadoTransmision;
	}

	public void setEstadoTransmision(String estadoTransmision) {
		this.estadoTransmision = estadoTransmision;
	}
	
}