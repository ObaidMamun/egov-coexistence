/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
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
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
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
 *
 */
package org.egov.egf.web.controller.contractor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.service.AccountdetailtypeService;
import org.egov.commons.service.ChartOfAccountsService;
import org.egov.egf.autonumber.ExpenseBillNumberGenerator;
import org.egov.egf.budget.model.BudgetControlType;
import org.egov.egf.budget.service.BudgetControlTypeService;
import org.egov.egf.contractorbill.service.ContractorBillService;
import org.egov.egf.masters.services.ContractorService;
import org.egov.egf.masters.services.WorkOrderService;
import org.egov.egf.utils.FinancialUtils;
import org.egov.egf.web.controller.expensebill.BaseBillController;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.model.bills.BillType;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.bills.EgBillPayeedetails;
import org.egov.model.bills.EgBilldetails;
import org.egov.model.bills.EgBillregister;
import org.egov.model.masters.WorkOrder;
import org.egov.utils.FinancialConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author venki
 */

@Controller
@RequestMapping(value = "/contractorbill")
public class CreateContractorBillController extends BaseBillController {
	
	private static final Logger LOGGER = Logger.getLogger(CreateContractorBillController.class);// added by abhishek

    private static final String NET_PAYABLE_CODES = "netPayableCodes";

    private static final String CONTRACTORS = "contractors";

    private static final String CONTRACTOR_ID = "contractorId";

    private static final String APPROVER_DETAILS = "approverDetails";

    private static final String APPROVER_NAME = "approverName";

    private static final String APPROVAL_COMENT = "approvalComent";

    private static final String CONTRACTOR = "Contractor";

    private static final String FILE = "file";

    private static final String WORK_ORDER = "WorkOrder";

    private static final String DESIGNATION = "designation";

    private static final String NET_PAYABLE_ID = "netPayableId";

    private static final String CONTRACTORBILL_FORM = "contractorbill-form";

    private static final String STATE_TYPE = "stateType";

    private static final String APPROVAL_POSITION = "approvalPosition";

    private static final String APPROVAL_DESIGNATION = "approvalDesignation";

    private static final int BUFFER_SIZE = 4096;

    private static final String BILL_TYPES = "billTypes";

    @Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;

    @Autowired
    private ContractorBillService contractorBillService;

    @Autowired
    private BudgetControlTypeService budgetControlTypeService;

    @Autowired
    private FileStoreService fileStoreService;

    @Autowired
    private FinancialUtils financialUtils;

    @Autowired
    private ContractorService contractorService;

    @Autowired
    private ChartOfAccountsService chartOfAccountsService;

    @Autowired
    private AccountdetailtypeService accountdetailtypeService;

    @Autowired
    private WorkOrderService workOrderService;

    @Autowired
    private MicroserviceUtils microserviceUtils;//added abhishek on 05042021
    
    @Autowired
	private AutonumberServiceBeanResolver beanResolver;
    
    public CreateContractorBillController(final AppConfigValueService appConfigValuesService) {
        super(appConfigValuesService);
    }

    @Override
    protected void setDropDownValues(final Model model) {
        super.setDropDownValues(model);
List<String> billtype=new ArrayList<>();
    	
    	for(BillType bill:BillType.values()) {
    		billtype.add(bill.getValue());
    		//System.out.println("::::::::: "+bill.getValue());
    	}
        //model.addAttribute(BILL_TYPES, billtype);
        model.addAttribute(CONTRACTORS, contractorService.getAllActiveContractors());
        model.addAttribute(NET_PAYABLE_CODES, chartOfAccountsService.getContractorNetPayableAccountCodes());
    }

    @RequestMapping(value = "/newform", method = RequestMethod.POST)
    public String showNewForm(@ModelAttribute("egBillregister") final EgBillregister egBillregister, final Model model,
            HttpServletRequest request) {
    	//added by Abhishek
    	LOGGER.info("New contractorbill creation request created");
        Cookie[] cookies = request.getCookies();
       List<String>  validActions = Arrays.asList("Forward","SaveAsDraft","CreateAndApprove");
    	
    	if(null!=cookies && cookies.length>0)
    	{
    	   for(Cookie ck:cookies) {
    		   System.out.println("Name:"+ck.getName()+" value"+ck.getValue());                                                                              
    		   
    	   }
    	}
    	/*List<String> billtype=new ArrayList<>();
    	
    	for(BillType bill:BillType.values()) {
    		billtype.add(bill.getValue());
    		//System.out.println("::::::::: "+bill.getValue());
    	}*/
    	//end
        setDropDownValues(model);
        //model.addAttribute("billNumberGenerationAuto", contractorBillService.isBillNumberGenerationAuto());
        model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
        prepareWorkflow(model, egBillregister, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
        model.addAttribute("validActionList", validActions);
       // model.addAttribute(BILL_TYPES, billtype);
        if(isBillDateDefaultValue){
            egBillregister.setBilldate(new Date());            
        }
        return CONTRACTORBILL_FORM;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@ModelAttribute("egBillregister") final EgBillregister egBillregister, final Model model,
            final BindingResult resultBinder, final HttpServletRequest request, @RequestParam final String workFlowAction)
            throws IOException {

        egBillregister.setCreatedBy(ApplicationThreadLocals.getUserId());
        ExpenseBillNumberGenerator v = beanResolver.getAutoNumberServiceFor(ExpenseBillNumberGenerator.class);

		final String billNumber = v.getNextNumber(egBillregister);
		egBillregister.setBillnumber(billNumber);
		
        if (StringUtils.isBlank(egBillregister.getExpendituretype()))
            egBillregister.setExpendituretype(FinancialConstants.STANDARD_EXPENDITURETYPE_WORKS);
        String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes(FILE);
        List<DocumentUpload> list = new ArrayList<>();
        UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles(FILE);
        String[] fileName = ((MultiPartRequestWrapper) request).getFileNames(FILE);
        if (uploadedFiles != null)
            for (int i = 0; i < uploadedFiles.length; i++) {

                Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
                byte[] fileBytes = Files.readAllBytes(path);
                ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);
                DocumentUpload upload = new DocumentUpload();
                upload.setInputStream(bios);
                upload.setFileName(fileName[i]);
                upload.setContentType(contentType[i]);
                list.add(upload);
            }

        populateBillDetails(egBillregister);
        populateSubLedgerDetails(egBillregister, resultBinder);
        validateBillNumber(egBillregister, resultBinder);
        removeEmptyRows(egBillregister);
        //validateLedgerAndSubledger(egBillregister, resultBinder);
        if(!workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))//added abhishek on 05042021
    	{ 
        	validateLedgerAndSubledger(egBillregister, resultBinder);
    	}

        if (resultBinder.hasErrors()) {
            setDropDownValues(model);
            model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
            prepareWorkflow(model, egBillregister, new WorkflowContainer());
            model.addAttribute(NET_PAYABLE_ID, request.getParameter(NET_PAYABLE_ID));
            model.addAttribute(APPROVAL_DESIGNATION, request.getParameter(APPROVAL_DESIGNATION));
            model.addAttribute(APPROVAL_POSITION, request.getParameter(APPROVAL_POSITION));
            model.addAttribute(DESIGNATION, request.getParameter(DESIGNATION));
            egBillregister.getBillPayeedetails().clear();
            prepareBillDetailsForView(egBillregister);
            prepareValidActionListByCutOffDate(model);
            model.addAttribute(CONTRACTOR_ID,
                    workOrderService.getByOrderNumber(egBillregister.getWorkordernumber()).getContractor().getId());
            return CONTRACTORBILL_FORM;
        } else {
            Long approvalPosition = 0l;
            String approvalComment = "";
            String approvalDesignation = "";
            if (request.getParameter(APPROVAL_COMENT) != null)
                approvalComment = request.getParameter(APPROVAL_COMENT);
			/*
			 * if (request.getParameter(APPROVAL_POSITION) != null &&
			 * !request.getParameter(APPROVAL_POSITION).isEmpty()) approvalPosition =
			 * Long.valueOf(request.getParameter(APPROVAL_POSITION));
			 */
            //added abhishek on 05042021
            if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
            {
            	if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
            	{            		
            		approvalPosition =populatePosition();            		
            	}
            	else
                approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
            }
            else {
            	if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
            	{            		
            		approvalPosition =populatePosition();            		
            	}
            	
            }//end
            if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
                approvalDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));

            EgBillregister savedEgBillregister;
            egBillregister.setDocumentDetail(list);
            try {

                savedEgBillregister = contractorBillService.create(egBillregister, approvalPosition, approvalComment, null,
                        workFlowAction, approvalDesignation);
            } catch (final ValidationException e) {
                setDropDownValues(model);
                model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
                prepareWorkflow(model, egBillregister, new WorkflowContainer());
                model.addAttribute(NET_PAYABLE_ID, request.getParameter(NET_PAYABLE_ID));
                model.addAttribute(APPROVAL_DESIGNATION, request.getParameter(APPROVAL_DESIGNATION));
                model.addAttribute(APPROVAL_POSITION, request.getParameter(APPROVAL_POSITION));
                model.addAttribute(DESIGNATION, request.getParameter(DESIGNATION));
                egBillregister.getBillPayeedetails().clear();
                prepareBillDetailsForView(egBillregister);
                prepareValidActionListByCutOffDate(model);
                model.addAttribute(CONTRACTOR_ID,
                        workOrderService.getByOrderNumber(egBillregister.getWorkordernumber()).getContractor().getId());
                resultBinder.reject("", e.getErrors().get(0).getMessage());
                return CONTRACTORBILL_FORM;
            }
            //final String approverName = String.valueOf(request.getParameter(APPROVER_NAME));
            //added abhishek
            String approverName =null;
            if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
        	{        		
        		approverName =populateEmpName();        		
        	}
        	else
        		approverName = String.valueOf(request.getParameter("approverName"));
           //end

            final String approverDetails = financialUtils.getApproverDetails(workFlowAction,
                    savedEgBillregister.getState(), savedEgBillregister.getId(), approvalPosition, approverName);

            return "redirect:/contractorbill/success?approverDetails=" + approverDetails + "&billNumber="
                    + savedEgBillregister.getBillnumber();

        }
    }

    void removeEmptyRows(EgBillregister egBillregister) {
        Set<EgBilldetails> billDetails = new HashSet<>();
        for (EgBilldetails details : egBillregister.getEgBilldetailes()) {
            if (!(details.getDebitamount() == null && details.getCreditamount() == null
                    && details.getChartOfAccounts() == null)) {
                billDetails.add(details);
            }
        }
        egBillregister.setEgBilldetailes(new HashSet<>(billDetails));
    }

    private void populateSubLedgerDetails(final EgBillregister egBillregister, final BindingResult resultBinder) {
        EgBillPayeedetails payeeDetail = null;
        Boolean check = false;
        Boolean woExist = false;
        Boolean contractorExist = false;
        Integer woAccountDetailTypeId, contractorAccountDetailTypeId = null;
        WorkOrder wo = null;
        Accountdetailtype woAccountdetailtype = accountdetailtypeService.findByName(WORK_ORDER);
        woAccountDetailTypeId = woAccountdetailtype.getId();
        String woAccountDetailTypeName = woAccountdetailtype.getName();
        Accountdetailtype contractorAccountdetailtype = accountdetailtypeService.findByName(CONTRACTOR);
        contractorAccountDetailTypeId = contractorAccountdetailtype.getId();
        String contractorAccountDetailTypeName = contractorAccountdetailtype.getName();
        wo = workOrderService.getByOrderNumber(egBillregister.getWorkordernumber());
        for (final EgBilldetails details : egBillregister.getEgBilldetailes()) {
            details.setEgBillPaydetailes(new HashSet<>());
            check = false;
            woExist = false;
            contractorExist = false;
            if (details.getChartOfAccounts() != null && details.getChartOfAccounts().getChartOfAccountDetails() != null
                    && !details.getChartOfAccounts().getChartOfAccountDetails().isEmpty()) {
                for (CChartOfAccountDetail cad : details.getChartOfAccounts().getChartOfAccountDetails()) {
                    if (cad.getDetailTypeId() != null) {
                        if (cad.getDetailTypeId().getName().equalsIgnoreCase(WORK_ORDER)) {
                            woExist = true;
                        }
                        if (cad.getDetailTypeId().getName().equalsIgnoreCase(CONTRACTOR)) {
                            contractorExist = true;
                        }
                        if (!cad.getDetailTypeId().getName().equalsIgnoreCase(WORK_ORDER)
                                && !cad.getDetailTypeId().getName().equalsIgnoreCase(CONTRACTOR)) {
                            check = true;
                        }
                    }
                }

                if (check && !contractorExist && !woExist) {
                    resultBinder.reject("msg.contractor.bill.wrong.sub.ledger.mapped",
                            new String[] { details.getChartOfAccounts().getGlcode() }, null);
                }

                if (details.getDebitamount() != null && details.getDebitamount().compareTo(BigDecimal.ZERO) == 1) {
                    if (woExist || (woExist && contractorExist)) {
                        payeeDetail = prepareBillPayeeDetails(details, details.getDebitamount(), BigDecimal.ZERO,
                                woAccountDetailTypeId,
                                wo.getId().intValue(),woAccountDetailTypeName,wo.getName());
                        egBillregister.getEgBillregistermis().setPayto(wo.getName());
                        details.getEgBillPaydetailes().add(payeeDetail);
                    } else if (contractorExist) {
                        payeeDetail = prepareBillPayeeDetails(details, details.getDebitamount(), BigDecimal.ZERO,
                                contractorAccountDetailTypeId, wo.getContractor().getId().intValue(),contractorAccountDetailTypeName,wo.getName());
                        egBillregister.getEgBillregistermis().setPayto(wo.getContractor().getName());
                        details.getEgBillPaydetailes().add(payeeDetail);
                    }

                }

                if (details.getCreditamount() != null && details.getCreditamount().compareTo(BigDecimal.ZERO) == 1) {
                    if (contractorExist || (woExist && contractorExist)) {
                        payeeDetail = prepareBillPayeeDetails(details, BigDecimal.ZERO, details.getCreditamount(),
                                contractorAccountDetailTypeId, wo.getContractor().getId().intValue(),contractorAccountDetailTypeName,wo.getName());
                        egBillregister.getEgBillregistermis().setPayto(wo.getContractor().getName());
                        details.getEgBillPaydetailes().add(payeeDetail);

                    } else if (woExist) {
                        payeeDetail = prepareBillPayeeDetails(details, BigDecimal.ZERO, details.getCreditamount(),
                                woAccountDetailTypeId, wo.getId().intValue(),woAccountDetailTypeName,wo.getName());
                        egBillregister.getEgBillregistermis().setPayto(wo.getName());
                        details.getEgBillPaydetailes().add(payeeDetail);
                    }
                }                
            } else {
                egBillregister.getEgBillregistermis().setPayto(wo.getContractor().getName());
            }
        }
    }

    private EgBillPayeedetails prepareBillPayeeDetails(EgBilldetails details, BigDecimal debitamount, BigDecimal creditamount,
            Integer detailTypeId, int detailKeyId, String detailTypeName, String detailKeyName) {
        EgBillPayeedetails payeeDetail = new EgBillPayeedetails();
        payeeDetail.setEgBilldetailsId(details);
        payeeDetail.setDebitAmount(debitamount);
        payeeDetail.setCreditAmount(creditamount);
        payeeDetail.setAccountDetailTypeId(detailTypeId);
        payeeDetail.setAccountDetailKeyId(detailKeyId);
        payeeDetail.setDetailTypeName(detailTypeName);
        payeeDetail.setDetailKeyName(detailKeyName);
        payeeDetail.setLastUpdatedTime(new Date());
        return payeeDetail;

    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String showSuccessPage(@RequestParam("billNumber") final String billNumber, final Model model,
            final HttpServletRequest request) {
        final String[] keyNameArray = request.getParameter(APPROVER_DETAILS).split(",");
        Long id = 0L;
        String approverName = "";
        String nextDesign = "";
        if (keyNameArray.length != 0 && keyNameArray.length > 0)
            if (keyNameArray.length == 1)
                id = Long.parseLong(keyNameArray[0].trim());
            else if (keyNameArray.length == 3) {
                id = Long.parseLong(keyNameArray[0].trim());
                approverName = keyNameArray[1];
            } else {
                id = Long.parseLong(keyNameArray[0].trim());
                approverName = keyNameArray[1];
            }
        if (id != null)
            model.addAttribute(APPROVER_NAME, approverName);

        final EgBillregister contractorBill = contractorBillService.getByBillnumber(billNumber);
        String message="";
        if(contractorBill.getState().getValue()!=null && contractorBill.getState().getValue().equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT)) {
        	message = messageSource.getMessage("msg.contractor.bill.saveasdraft.success",//added abhishek on 05042021
                    new String[]{contractorBill.getBillnumber()}, null);
        }else {
        	 message = getMessageByStatus(contractorBill, approverName, nextDesign);
        }
            
         //message = getMessageByStatus(contractorBill, approverName, nextDesign);

        model.addAttribute("message", message);

        return "contractorbill-success";
    }

    private String getMessageByStatus(final EgBillregister contractorBill, final String approverName, final String nextDesign) {
        String message = "";
       // System.out.println("contractor status code "+contractorBill.getStatus().getCode());
        if (FinancialConstants.CONTRACTORBILL_CREATED_STATUS.equals(contractorBill.getStatus().getCode())) {
            if (org.apache.commons.lang.StringUtils
                    .isNotBlank(contractorBill.getEgBillregistermis().getBudgetaryAppnumber())
                    && !BudgetControlType.BudgetCheckOption.NONE.toString()
                            .equalsIgnoreCase(budgetControlTypeService.getConfigValue()))
                message = messageSource.getMessage("msg.contractor.bill.create.success.with.budgetappropriation",
                        new String[] { contractorBill.getBillnumber(), approverName, nextDesign,
                                contractorBill.getEgBillregistermis().getBudgetaryAppnumber() },
                        null);
            else if(contractorBill.getState().getValue()!=null && contractorBill.getState().getValue().equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
                message = messageSource.getMessage("msg.contractor.bill.saveasdraft.success",//added abhishek on 05042021
                            new String[]{contractorBill.getBillnumber()}, null);
            else
                message = messageSource.getMessage("msg.contractor.bill.create.success",
                        new String[] { contractorBill.getBillnumber(), approverName, nextDesign }, null);

        } else if (FinancialConstants.CONTRACTORBILL_APPROVED_STATUS.equals(contractorBill.getStatus().getCode()))
            message = messageSource.getMessage("msg.contractor.bill.approved.success",
                    new String[] { contractorBill.getBillnumber() }, null);
        else if (FinancialConstants.WORKFLOW_STATE_REJECTED.equals(contractorBill.getState().getValue()))
            message = messageSource.getMessage("msg.contractor.bill.reject",
                    new String[] { contractorBill.getBillnumber(), approverName, nextDesign }, null);
        else if (FinancialConstants.WORKFLOW_STATE_CANCELLED.equals(contractorBill.getState().getValue()))
            message = messageSource.getMessage("msg.contractor.bill.cancel",
                    new String[] { contractorBill.getBillnumber() }, null);

        return message;
    }

    @RequestMapping(value = "/downloadBillDoc", method = RequestMethod.GET)
    public void getBillDoc(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        final ServletContext context = request.getServletContext();
        final String fileStoreId = request.getParameter("fileStoreId");
        String fileName = "";
        final File downloadFile = fileStoreService.fetch(fileStoreId, FinancialConstants.FILESTORE_MODULECODE);
        final FileInputStream inputStream = new FileInputStream(downloadFile);
        EgBillregister egBillregister = contractorBillService.getById(Long.parseLong(request.getParameter("egBillRegisterId")));
        egBillregister = getBillDocuments(egBillregister);

        for (final DocumentUpload doc : egBillregister.getDocumentDetail())
            if (doc.getFileStore().getFileStoreId().equalsIgnoreCase(fileStoreId))
                fileName = doc.getFileStore().getFileName();

        // get MIME type of the file
        String mimeType = context.getMimeType(downloadFile.getAbsolutePath());
        if (mimeType == null)
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";

        // set content attributes for the response
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());

        // set headers for the response
        final String headerKey = "Content-Disposition";
        final String headerValue = String.format("attachment; filename=\"%s\"", fileName);
        response.setHeader(headerKey, headerValue);

        // get output stream of the response
        final OutputStream outStream = response.getOutputStream();

        final byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;

        // write bytes read from the input stream into the output stream
        while ((bytesRead = inputStream.read(buffer)) != -1)
            outStream.write(buffer, 0, bytesRead);

        inputStream.close();
        outStream.close();
    }

    private EgBillregister getBillDocuments(final EgBillregister egBillregister) {
        List<DocumentUpload> documentDetailsList = contractorBillService.findByObjectIdAndObjectType(egBillregister.getId(),
                FinancialConstants.FILESTORE_MODULEOBJECT);
        egBillregister.setDocumentDetail(documentDetailsList);
        return egBillregister;
    }
   //added abhishek on 05042021 
    private Long populatePosition() {
    	Long empId = ApplicationThreadLocals.getUserId();
    	Long pos=null;
    	List<EmployeeInfo> employs = microserviceUtils.getEmployee(empId, null,null, null);
    	if(null !=employs && employs.size()>0 )
    	{
    		pos=employs.get(0).getAssignments().get(0).getPosition();
    		
    	}
    	
		return pos;
	}
    
    private String populateEmpName() {
    	Long empId = ApplicationThreadLocals.getUserId();
    	String empName=null;
    	Long pos=null;
    	List<EmployeeInfo> employs = microserviceUtils.getEmployee(empId, null,null, null);
    	if(null !=employs && employs.size()>0 )
    	{
    		empName=employs.get(0).getUser().getName();
    	}
		return empName;
	}
    //end
}