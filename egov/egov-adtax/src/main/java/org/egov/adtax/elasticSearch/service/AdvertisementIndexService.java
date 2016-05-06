/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.adtax.elasticSearch.service;

import org.egov.adtax.elasticSearch.entity.AdvertisementSearch;
import org.egov.adtax.entity.AdvertisementPermitDetail;
import org.egov.adtax.service.AdvertisementDemandService;
import org.egov.adtax.utils.constants.AdvertisementTaxConstants;
import org.egov.config.search.Index;
import org.egov.config.search.IndexType;
import org.egov.infra.admin.master.entity.City;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.search.elastic.annotation.Indexing;
import org.egov.infra.utils.EgovThreadLocals;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AdvertisementIndexService {
	
	@Autowired
	private CityService cityService;

	@Autowired
	private BoundaryService boundaryService;
	
	@Autowired
	private AdvertisementDemandService advertisementDemandService;
	
	@Indexing(name = Index.ADVERTISEMENT, type = IndexType.ADVERTISEMENTSEARCH)
	public AdvertisementSearch createAdvertisementIndex(final AdvertisementPermitDetail advertisementPermitDetailIndex){ 
		AdvertisementSearch advertisementSearch = null;
		if(advertisementPermitDetailIndex.getStatus()!=null &&
	   			 (advertisementPermitDetailIndex.getStatus().getCode().equalsIgnoreCase(AdvertisementTaxConstants.APPLICATION_STATUS_APPROVED)
	   			 ||	advertisementPermitDetailIndex.getStatus().getCode().equalsIgnoreCase(AdvertisementTaxConstants.APPLICATION_STATUS_ADTAXAMOUNTPAID)
	   			 || advertisementPermitDetailIndex.getStatus().getCode().equalsIgnoreCase(AdvertisementTaxConstants.APPLICATION_STATUS_ADTAXPERMITGENERATED)
	   			 || advertisementPermitDetailIndex.getStatus().getCode().equalsIgnoreCase(AdvertisementTaxConstants.APPLICATION_STATUS_CANCELLED))
  		){
			advertisementSearch  =createOrUpdateAdvIndex(advertisementPermitDetailIndex);
		}
		return advertisementSearch;
	}
	
	public AdvertisementSearch createOrUpdateAdvIndex(final AdvertisementPermitDetail advertisementPermitDetail){
		final City cityWebsite = cityService.getCityByURL(EgovThreadLocals.getDomainName());
		final AdvertisementSearch advertisementSearch = new AdvertisementSearch(advertisementPermitDetail.getAdvertisement().getAdvertisementNumber(),
				 cityWebsite.getName(),cityWebsite.getGrade(), advertisementPermitDetail.getCreatedDate(), cityWebsite.getDistrictName(), cityWebsite.getRegionName(),
					cityWebsite.getGrade());
		advertisementSearch.setAddress(advertisementPermitDetail.getAdvertisement().getAddress()!=null?advertisementPermitDetail.getAdvertisement().getAddress():"");
		advertisementSearch.setAdvertisementClass(advertisementPermitDetail.getAdvertisement().getRateClass().getDescription());
		advertisementSearch.setAdvertisementCreatedBy(advertisementPermitDetail.getAdvertisement().getCreatedBy().getName());
		advertisementSearch.setAdvertisement_duration(advertisementPermitDetail.getAdvertisementDuration().name());
		advertisementSearch.setAdvertisementNumber(advertisementPermitDetail.getAdvertisement().getAdvertisementNumber());
		advertisementSearch.setAdvertisement_status(advertisementPermitDetail.getAdvertisement().getStatus().name());
		advertisementSearch.setType(advertisementPermitDetail.getAdvertisement().getType().name());
		advertisementSearch.setAdvertiser(advertisementPermitDetail.getAdvertiser()!=null?advertisementPermitDetail.getAdvertiser():"");
		advertisementSearch.setAdvertiserParticular(advertisementPermitDetail.getAdvertisementParticular()!=null?advertisementPermitDetail.getAdvertisementParticular():"");
		advertisementSearch.setAgencyName(advertisementPermitDetail.getAgency()!=null?advertisementPermitDetail.getAgency().getName():"");
		advertisementSearch.setApplicationDate(advertisementPermitDetail.getApplicationDate());
		advertisementSearch.setApplicationNumber(advertisementPermitDetail.getApplicationNumber());
		advertisementSearch.setBlock(advertisementPermitDetail.getAdvertisement().getBlock()!=null?advertisementPermitDetail.getAdvertisement().getBlock().getName():"");
		advertisementSearch.setBreadth(advertisementPermitDetail.getBreadth()!=null?advertisementPermitDetail.getBreadth():0.0);
		advertisementSearch.setCategory(advertisementPermitDetail.getAdvertisement().getCategory().getName());  
		advertisementSearch.setCreatedDate(advertisementPermitDetail.getAdvertisement().getCreatedDate());
		advertisementSearch.setElectionWard(advertisementPermitDetail.getAdvertisement().getElectionWard()!=null?advertisementPermitDetail.getAdvertisement().getElectionWard().getName():"");
		advertisementSearch.setElectricityServiceNumber(advertisementPermitDetail.getAdvertisement().getElectricityServiceNumber()!=null?advertisementPermitDetail.getAdvertisement().getElectricityServiceNumber():"");
		advertisementSearch.setEncroachmentFee(advertisementPermitDetail.getEncroachmentFee()!=null?advertisementPermitDetail.getEncroachmentFee():BigDecimal.ZERO);
		advertisementSearch.setIslegacy(advertisementPermitDetail.getAdvertisement().getLegacy());
		advertisementSearch.setLength(advertisementPermitDetail.getLength());
		advertisementSearch.setLocality(advertisementPermitDetail.getAdvertisement().getLocality()!=null?advertisementPermitDetail.getAdvertisement().getLocality().getName():"");
		advertisementSearch.setMeasurement(advertisementPermitDetail.getMeasurement());
		advertisementSearch.setMobileNumber(advertisementPermitDetail.getAgency()!=null?advertisementPermitDetail.getAgency().getMobileNumber():"");
		advertisementSearch.setOwnerDetail(advertisementPermitDetail.getOwnerDetail()!=null?advertisementPermitDetail.getOwnerDetail():"");
		advertisementSearch.setPermissionEndDate(advertisementPermitDetail.getPermissionenddate());
		advertisementSearch.setPermissionNumber(advertisementPermitDetail.getPermissionNumber());
		advertisementSearch.setPermissionStartDate(advertisementPermitDetail.getPermissionstartdate());
		advertisementSearch.setPermitStatus(advertisementPermitDetail.getStatus().getDescription());
		advertisementSearch.setAssessmentNumber(advertisementPermitDetail.getAdvertisement().getPropertyNumber()!=null?advertisementPermitDetail.getAdvertisement().getPropertyNumber():"");
		advertisementSearch.setPropertyType(advertisementPermitDetail.getAdvertisement().getPropertyType().name());
		advertisementSearch.setRevenueInspector(advertisementPermitDetail.getAdvertisement().getRevenueInspector().getName());
		advertisementSearch.setStreet(advertisementPermitDetail.getAdvertisement().getStreet()!=null?advertisementPermitDetail.getAdvertisement().getStreet().getName():"");
		advertisementSearch.setSubCategory(advertisementPermitDetail.getAdvertisement().getSubCategory().getCode());
		advertisementSearch.setTaxAmount(advertisementPermitDetail.getTaxAmount());
		advertisementSearch.setTotalHeight(advertisementPermitDetail.getTotalHeight()!=null?advertisementPermitDetail.getTotalHeight():0.0);
		advertisementSearch.setUom(advertisementPermitDetail.getUnitOfMeasure().getCode());
		advertisementSearch.setWard(advertisementPermitDetail.getAdvertisement().getWard()!=null?advertisementPermitDetail.getAdvertisement().getWard().getName():"");
		advertisementSearch.setWidth(advertisementPermitDetail.getWidth()!=null?advertisementPermitDetail.getWidth():0.0);
		advertisementSearch.setConsumerName(advertisementPermitDetail.getAgency()!=null?advertisementPermitDetail.getAgency().getName():
       	advertisementPermitDetail.getOwnerDetail());
		
		// Demand and Collection Details
		advertisementSearch.setTax_demand(advertisementPermitDetail.getAdvertisement().getDemandId().getBaseDemand());
		advertisementSearch.setTax_collected(advertisementPermitDetail.getAdvertisement().getDemandId().getAmtCollected());
		
		Map<String, Map<String, BigDecimal>> demandCollectionMap = advertisementDemandService.getReasonWiseDemandAndCollection(advertisementPermitDetail);
        
		
		advertisementSearch.setEncroachmentfee_demand(
				demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE)!=null ?
						(demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE).get("demandAmount")!=null?
								demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE).get("demandAmount"):
									BigDecimal.ZERO):BigDecimal.ZERO);
		
		advertisementSearch.setEncroachmentfee_collected(
				demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE)!=null?
						(demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE).get("collectedAmount")!=null?
							demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE).get("collectedAmount"):
								BigDecimal.ZERO):BigDecimal.ZERO);
		
		advertisementSearch.setArrears_demand(
				demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_ARREAR_ADVERTISEMENTTAX)!=null ?
						(demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_ARREAR_ADVERTISEMENTTAX).get("demandAmount")!=null?
								demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_ARREAR_ADVERTISEMENTTAX).get("demandAmount"):
									BigDecimal.ZERO):BigDecimal.ZERO);
		
		advertisementSearch.setArrears_collected(
				demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_ARREAR_ADVERTISEMENTTAX)!=null ?
						(demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_ARREAR_ADVERTISEMENTTAX).get("collectedAmount")!=null?
								demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_ARREAR_ADVERTISEMENTTAX).get("collectedAmount"):
									BigDecimal.ZERO):BigDecimal.ZERO);
		
		advertisementSearch.setPenalty_demand(
				demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_PENALTY)!=null ?
						(demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_PENALTY).get("demandAmount")!=null?
								demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_PENALTY).get("demandAmount"):
									BigDecimal.ZERO):BigDecimal.ZERO);
		
		advertisementSearch.setPenalty_collected(
				demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_PENALTY)!=null ?
						(demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_PENALTY).get("collectedAmount")!=null?
								demandCollectionMap.get(AdvertisementTaxConstants.DEMANDREASON_PENALTY).get("collectedAmount"):
									BigDecimal.ZERO):BigDecimal.ZERO);
		
		advertisementSearch.setTotalamount(
				demandCollectionMap.get("Total")!=null ?
						(demandCollectionMap.get("Total").get("totalAmount")!=null?
								demandCollectionMap.get("Total").get("totalAmount"):
									BigDecimal.ZERO):BigDecimal.ZERO);
		
		advertisementSearch.setTotalamountcollected(
				demandCollectionMap.get("Total")!=null ?
						(demandCollectionMap.get("Total").get("totalAmountCollected")!=null?
								demandCollectionMap.get("Total").get("totalAmountCollected"):
									BigDecimal.ZERO):BigDecimal.ZERO);
		
		advertisementSearch.setTotalbalance(advertisementSearch.getTotalamount().subtract(advertisementSearch.getTotalamountcollected()));
		
		if (advertisementPermitDetail.getAdvertisement().getLatitude()!= 0.0 && advertisementPermitDetail.getAdvertisement().getLongitude()!=0.0) {
			advertisementSearch.setAdvertisementLocation(new GeoPoint(advertisementPermitDetail.getAdvertisement().getLatitude(), advertisementPermitDetail.getAdvertisement().getLongitude()));
		}
		
		//Deactivation
        if(advertisementPermitDetail.getAdvertisement().getStatus().name().equalsIgnoreCase("INACTIVE")){
        	advertisementSearch.setDeactivationDate(advertisementPermitDetail.getDeactivation_date());
    		advertisementSearch.setDeactivationRemarks(advertisementPermitDetail.getDeactivation_remarks());
        }
		
		return advertisementSearch;
	}

}
