package com.org.telugucineandtvoutdoorunittechniciansunion.DAO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import com.org.telugucineandtvoutdoorunittechniciansunion.exceptions.NotValidCardNumberException;
import com.org.telugucineandtvoutdoorunittechniciansunion.init.ApplicationUtilities;
import com.org.telugucineandtvoutdoorunittechniciansunion.init.DataAccess;
import com.org.telugucineandtvoutdoorunittechniciansunion.pojo.AdminfeePayments;
import com.org.telugucineandtvoutdoorunittechniciansunion.pojo.CardNubers;
import com.org.telugucineandtvoutdoorunittechniciansunion.pojo.Departments;
import com.org.telugucineandtvoutdoorunittechniciansunion.pojo.LoanRecoveryDetails;
import com.org.telugucineandtvoutdoorunittechniciansunion.pojo.Loandetails;
import com.org.telugucineandtvoutdoorunittechniciansunion.pojo.MembershipPayments;
import com.org.telugucineandtvoutdoorunittechniciansunion.pojo.PaymentConfigurations;
import com.org.telugucineandtvoutdoorunittechniciansunion.pojo.RecommendationDetails;
import com.org.telugucineandtvoutdoorunittechniciansunion.pojo.Registration;
import com.org.telugucineandtvoutdoorunittechniciansunion.pojo.SubscriptionPayments;
import com.org.telugucineandtvoutdoorunittechniciansunion.pojo.Units;
import com.org.telugucineandtvoutdoorunittechniciansunion.reports.BankDetailsReport;
import com.org.telugucineandtvoutdoorunittechniciansunion.reports.CardBalanceReport;
import com.org.telugucineandtvoutdoorunittechniciansunion.reports.CardBalanceYearlySymmary;
import com.org.telugucineandtvoutdoorunittechniciansunion.reports.ContactDetailsReport;
import com.org.telugucineandtvoutdoorunittechniciansunion.reports.LoanBalanceReport;
import com.org.telugucineandtvoutdoorunittechniciansunion.reports.SubscriptionReport;
import com.org.telugucineandtvoutdoorunittechniciansunion.utils.Utils;

@Repository
public class MiscellaneousDAO {
	org.slf4j.Logger logger = LoggerFactory.getLogger(MiscellaneousDAO.class);
	@Autowired
	public DataAccess dataAccess;
	@Autowired
	ServletContext miscContext;
	@Autowired
	MembershipDAO membershipDAO;
	Utils utils = new Utils();

public static final String TD_START="<td align='center'>";
public static final String TD_END="</td>";
SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	@SuppressWarnings("unchecked")
	@Transactional
	public JSONArray getDepartments() {
		JSONArray resultArry = new JSONArray();
		try {
			String getDepartMentsquery = "from Departments ";
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			@SuppressWarnings("unchecked")
			List<Departments> list = this.dataAccess.queryWithParams(getDepartMentsquery, parametersMap);

			if (list != null && !list.isEmpty() && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Departments dept = list.get(i);
					JSONObject jsnObj = new JSONObject();
					jsnObj.put("DEPT_ID", dept.getDeptId());
					jsnObj.put("DEPT_NAME", dept.getDeptName());
					resultArry.add(jsnObj);
				}

			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getDepartments");
		}
		return resultArry;
	}

	@SuppressWarnings({ "unchecked" })
	@Transactional
	public JSONObject getMemberDetailsForRecomondation(String deptId, String cardNo) {
		JSONObject resutlJsnObj = new JSONObject();
		JSONObject recomondationDet = new JSONObject();

		try {
			if (deptId != null && !"".equalsIgnoreCase(deptId) && cardNo != null && !"".equalsIgnoreCase(cardNo)) {
				int cardNumber = Integer.parseInt(cardNo);
				String getDepartMentsquery = "from Registration where registrationPK.cardNo=:cardNo and registrationPK.deptId=:deptId ";
				Map<String, Object> parametersMap = new HashMap<String, Object>();
				parametersMap.put("deptId", deptId);
				parametersMap.put("cardNo", Integer.valueOf(cardNumber));
				List<Registration> list = this.dataAccess.queryWithParams(getDepartMentsquery, parametersMap);
				if (list != null && !list.isEmpty() && list.size() > 0) {
					Registration member = list.get(0);
					resutlJsnObj = new JSONObject();
					resutlJsnObj.put("NAME", member.getFirstName());
					resutlJsnObj.put("UNIT_ID", member.getUnitId());
					recomondationDet.put("FINAL_RESULT_CODE", "400");
					recomondationDet.put("DATA_DETAILS", resutlJsnObj);
				} else {
					recomondationDet.put("FINAL_RESULT_CODE", "200");
					recomondationDet.put("ERROR_MSG",
							"No records found with DEPARTMENT=" + deptId + " and CARD NO =" + cardNo);
				}

			} else {

				recomondationDet.put("FINAL_RESULT_CODE", "200");
				recomondationDet.put("ERROR_MSG",
						"No records found with DEPARTMENT=" + deptId + " and CARD NO =" + cardNo);
			}

		} catch (NotValidCardNumberException nvcne) {

			ApplicationUtilities.error(this.getClass(),nvcne.getMessage(),nvcne);
			recomondationDet.put("FINAL_RESULT_CODE", "300");
			recomondationDet.put("ERROR_MSG", nvcne.getMessage());
		} catch (Exception e) {
			ApplicationUtilities.error(this.getClass(),e.getMessage(),e);
			recomondationDet.put("FINAL_RESULT_CODE", "300");
			recomondationDet.put("ERROR_MSG", e.getMessage());
		}
		return recomondationDet;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public RecommendationDetails getRecommondationDetailsByMemberId(String memberId) {
		RecommendationDetails recommendationDetails = null;
		try {
			String query = " from RecommendationDetails where id.memberId=:memberId";
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			parametersMap.put("memberId", memberId);
			List<RecommendationDetails> list = this.dataAccess.queryWithParams(query, parametersMap);
			if (list != null && list.size() > 0) {
				recommendationDetails = list.get(0);
			}
		} catch (Exception e) {
			ApplicationUtilities.error(getClass(), e, "getRecommondationDetailsByMemberIds");
		}

		return recommendationDetails;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public String getCardNubersByDeptId(String deptId) {
		JSONArray cardNoArr = new JSONArray();
		try {
			String query = " from CardNubers where cardNubersPK.deptId=:deptId and cardStatus=:cardStatus";

			Map<String, Object> parametersMap = new HashMap<String, Object>();
			parametersMap.put("deptId", deptId);
			parametersMap.put("cardStatus", "ACTIVE");

			List<CardNubers> list = this.dataAccess.queryWithParams(query, parametersMap);

			if (list != null && list.size() > 0) {
				for (int j = 0; j < list.size(); j++) {
					CardNubers cardNumbers = list.get(j);
					cardNoArr.add(Integer.valueOf(cardNumbers.getCardNubersPK().getCardNo()));
				}

			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getCardNubersByDeptId");
		}

		return cardNoArr.toJSONString();
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public String getCardNumbersByDeptIdForAutocomplete(String deptId, String term) {
		JSONArray cardNoArr = new JSONArray();
		try {
			String query = " from CardNubers where cardNubersPK.deptId=:deptId and cardStatus=:cardStatus  ";

			Map<String, Object> parametersMap = new HashMap<String, Object>();
			parametersMap.put("deptId", deptId);
			parametersMap.put("cardStatus", "ACTIVE");

			try {
				int cardNumber = Integer.parseInt(term);
				if (cardNumber > 0) {
					query = String.valueOf(query)
							+ " and cardNubersPK.cardNo >=:term  order by cardNubersPK.cardNo  asc";
					parametersMap.put("term", Integer.valueOf(cardNumber));
				}

			} catch (NumberFormatException ne) {

				query = String.valueOf(query) + "   order by cardNubersPK.cardNo  asc";
			}

			List<CardNubers> list = this.dataAccess.queryWithParams(query, parametersMap);

			if (list != null && list.size() > 0) {
				for (int j = 0; j < list.size(); j++) {
					CardNubers cardNumbers = list.get(j);
					cardNoArr.add(Integer.valueOf(cardNumbers.getCardNubersPK().getCardNo()));
				}

			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getCardNumbersByDeptIdForAutocomplete");
		}

		return cardNoArr.toJSONString();
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public JSONArray getUnits() {
		JSONArray resultArry = new JSONArray();

		try {
			String getUnitsQuery = "from Units ";
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			List<Units> list = this.dataAccess.queryWithParams(getUnitsQuery, parametersMap);

			if (list != null && !list.isEmpty() && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Units units = list.get(i);
					JSONObject jsnObj = new JSONObject();
					jsnObj.put("UNIT_ID", units.getUnitId());
					jsnObj.put("UNIT_NAME", units.getUnitName());
					resultArry.add(jsnObj);
				}

			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getUnits");
		}
		return resultArry;
	}
	
	
	
	
	
	
public String prepareTable(String headers, String body) {
	
	StringBuilder strbld = new StringBuilder();

	strbld.append("<table border='1' cellspacing='0' cellpadding='5' style='border-color: #EEE'>");	
	strbld.append(headers).append(body);
	
	strbld.append("</table>");
	
	
	return strbld.toString();
}
	


public String prepareTHeader(ResultSet rs, List<String> headers) throws SQLException {
	
	StringBuilder strbld = new StringBuilder();
	
	
	strbld.append("<thead><tr>");
	
	getColumnNames( rs, headers);
	
	StringBuilder sbHeaders = new StringBuilder();
	
	headers.stream().forEach((k) -> {
		strbld.append("<th align='center'>").append(k).append("</th>");
	});
	
	strbld.append(sbHeaders);
	
	strbld.append("</tr></thead>");
	
	return strbld.toString();
}



public ResultSet getReportData(String reportId, String deptId ) {
	ResultSet rs = null;

	try (Connection conn = dataAccess.getConnection();
			CallableStatement cs = conn.prepareCall("{CALL GENERIC_REPORTS(?,?)}", ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

	) {
		cs.setString(1, reportId);
		cs.setString(2, deptId);
		cs.execute();
		rs = cs.getResultSet();	

	} catch (SQLException ex) {
		ApplicationUtilities.error(this.getClass(), ex.getMessage(), ex);
	}
return rs;
}


public List<BankDetailsReport> getBankDetailsReportData(ResultSet rs){
	
	List<BankDetailsReport> results = new ArrayList<>();
	try {
		
		while (rs.next()) {			
			
			BankDetailsReport data = new BankDetailsReport();			
			data.setAccountHolderName(getNonNulData(rs.getObject("BANK_ACC_HOLDER_NAME")));
			data.setAccountNumber(getNonNulData(rs.getObject("BANK_ACC_NO")));
			data.setBankBranch(getNonNulData(rs.getObject("BANK_BRANCH")));
			data.setBankName(getNonNulData(rs.getObject("BANK_NAME")));
			data.setCardNo(Integer.valueOf(getNonNulData(rs.getObject("CARD_NO"))));
			data.setDeptId(getNonNulData(rs.getObject("DEPT_ID")));
			data.setIfscCode(getNonNulData(rs.getObject("BANK_IFSC_CODE")));
			data.setMemberId(getNonNulData(rs.getObject("MEMBER_ID")));
			data.setMobilePhone(getNonNulData(rs.getObject("PHONE_NO")));
			data.setFirstName(getNonNulData(rs.getObject("FIRST_NAME")));
			results.add(data);
			}
		
	} catch (SQLException e) {
		ApplicationUtilities.error(MiscellaneousDAO.class, e.getMessage(), e);
	}
	
	
return results;
}

public List<ContactDetailsReport> getContactDetailsReportData(ResultSet rs){
	
	List<ContactDetailsReport> results = new ArrayList<>();
	try {
		
		while (rs.next()) {			
			
			ContactDetailsReport data = new ContactDetailsReport();				
			
			data.setCardNo(getNonNulData(rs.getObject("CARD_NO")));
			data.setDeptId(getNonNulData(rs.getObject("DEPT_ID")));
			data.setMemberId(getNonNulData(rs.getObject("MEMBER_ID")));
			data.setMobilePhone(getNonNulData(rs.getObject("PHONE_NO")));
			data.setAdhar(getNonNulData(rs.getObject("AADHAR_CARD_NO")));
			data.setPerminentAddress(getNonNulData(rs.getObject("PERMINENT_ADDRESS")));
			data.setPresentAddress(getNonNulData(rs.getObject("PRESENT_ADDRESS")));	
			data.setFirstName(getNonNulData(rs.getObject("FIRST_NAME")));

			results.add(data);
			}
		
	} catch (SQLException e) {
		ApplicationUtilities.error(MiscellaneousDAO.class, e.getMessage(), e);
	}
	
	
return results;
}

public List<CardBalanceReport> getCardBalanceReportData(ResultSet rs){
	
	List<CardBalanceReport> results = new ArrayList<>();
	try {
		
		while (rs.next()) {			
			
			CardBalanceReport data = new CardBalanceReport();	
			data.setCardAmount(Double.valueOf(getNonNulData(rs.getObject("CARD_AMOUNT"))));
			data.setCardBalance(Double.valueOf(getNonNulData(rs.getObject("CARD_BALANCE"))));
			data.setPaidAmount(Double.valueOf(getNonNulData(rs.getObject("PAID_AMOUNT"))));
			
			data.setCardNo(Integer.valueOf(getNonNulData(rs.getObject("CARD_NO"))));
			
			data.setDeptId(getNonNulData(rs.getObject("DEPT_ID")));
			data.setFirstName(getNonNulData(rs.getObject("FIRST_NAME")));
			data.setMemberId(getNonNulData(rs.getObject("MEMBER_ID")));
			data.setMobilePhone(getNonNulData(rs.getObject("PHONE_NO")));			
			results.add(data);
			}
		
	} catch (SQLException e) {
		ApplicationUtilities.error(MiscellaneousDAO.class, e.getMessage(), e);
	}
	
	
return results;
}


public List<CardBalanceYearlySymmary> getCardBalanceSummaryData(ResultSet rs){
	
	List<CardBalanceYearlySymmary> results = new ArrayList<>();
	
	if(Objects.isNull(rs)) {
		return results;
	}
	
	try {
		
		while (rs.next()) {			
			
			CardBalanceYearlySymmary data = new CardBalanceYearlySymmary();	
			data.setCardAmount(Double.valueOf(getNonNulData(rs.getObject("CARD_AMOUNT"))));
			data.setCardBalance(Double.valueOf(getNonNulData(rs.getObject("CARD_BALANCE"))));
			data.setPaidAmount(Double.valueOf(getNonNulData(rs.getObject("PAID_AMOUNT"))));

			
			data.setCardNo(Integer.valueOf(getNonNulData(rs.getObject("CARD_NO"))));
			
			data.setDeptId(getNonNulData(rs.getObject("DEPT_ID")));
			data.setFirstName(getNonNulData(rs.getObject("FIRST_NAME")));
			data.setMemberId(getNonNulData(rs.getObject("MEMBER_ID")));
			data.setMobilePhone(getNonNulData(rs.getObject("PHONE_NO")));	
			
			//f.paid_date,F.RECEIPT_NO,F.REGISTERED_DATE FROM (

			data.setPaidDate(getNonNulData(rs.getObject("PAID_DATE")));	
			data.setReceiptNo(getNonNulData(rs.getObject("RECEIPT_NO")));	
			data.setRegisteredDate(dateFormat.format(rs.getObject("REGISTERED_DATE")));	
			results.add(data);
			}
		
	} catch (SQLException e) {
		ApplicationUtilities.error(MiscellaneousDAO.class, e.getMessage(), e);
	}
	
	
return results;
}


public List<LoanBalanceReport> getLoanBalanceReportData(ResultSet rs){
	
	List<LoanBalanceReport> results = new ArrayList<>();
	try {
		
		while (rs.next()) {			
			
			LoanBalanceReport data = new LoanBalanceReport();
			
			
			
			if(!"".equals(getNonNulData(rs.getObject("LOAN_AMOUNT")).trim())) {
				data.setLoanAmount(Double.valueOf(getNonNulData(rs.getObject("LOAN_AMOUNT"))));
			}else {
				data.setLoanAmount(0.0);
			}
			

			
			if(!"".equals(getNonNulData(rs.getObject("LOAN_PAID")).trim())) {
				data.setLoanRePaid(Double.valueOf(getNonNulData(rs.getObject("LOAN_PAID"))));
			}else {
				data.setLoanRePaid(0.0);
			}
			
			
			data.setLoanBalance(data.getLoanAmount()-data.getLoanRePaid());

			data.setCardNo(Integer.valueOf(getNonNulData(rs.getObject("CARD_NO"))));
			data.setDeptId(getNonNulData(rs.getObject("DEPT_ID")));
			data.setFirstName(getNonNulData(rs.getObject("FIRST_NAME")));
			data.setMemberId(getNonNulData(rs.getObject("MEMBER_ID")));
			data.setMobilePhone(getNonNulData(rs.getObject("PHONE_NO")));			
			results.add(data);
			}
		
	} catch (SQLException e) {
		ApplicationUtilities.error(MiscellaneousDAO.class, e.getMessage(), e);
	}
	
	
return results;
}

public List<SubscriptionReport> getSubscriptionReportData(ResultSet rs){
	
	List<SubscriptionReport> results = new ArrayList<>();
	try {
		
		while (rs.next()) {			
			
			SubscriptionReport data = new SubscriptionReport();				
			data.setDatOfJoining(dateFormat.format(rs.getObject("APPLIED_DATAE")));
			data.setCardNo(Integer.valueOf(getNonNulData(rs.getObject("CARD_NO"))));
			data.setDeptId(getNonNulData(rs.getObject("DEPT_ID")));
			data.setFirstName(getNonNulData(rs.getObject("FIRST_NAME")));
			data.setMemberId(getNonNulData(rs.getObject("MEMBER_ID")));
			data.setMobilePhone(getNonNulData(rs.getObject("PHONE_NO")));	
			data.setSubscribedYear(Integer.valueOf(getNonNulData(rs.getObject("SUBSCRIPTION_YEAR"))));
			results.add(data);
			}
		
	} catch (SQLException e) {
		ApplicationUtilities.error(MiscellaneousDAO.class, e.getMessage(), e);
	}
	
	
return results;
}


public String getNonNulData(Object data) {
	
	return data != null ? data.toString().trim() : "";
}

public void getColumnNames(ResultSet rs, List<String> headrs) throws SQLException{
	
	ResultSetMetaData rsmd = rs.getMetaData();
	int numColumns = rsmd.getColumnCount();		
	for (int i = 1; i <= numColumns; i++) {		
		headrs.add(rsmd.getColumnName(i).toUpperCase());		
	}

}
	
	
	
	@SuppressWarnings({ "unused", "unchecked" })
	@Transactional
	public String getDetialsBySelectAtion(HttpServletRequest request) {

		JSONArray resutlJsnArr = new JSONArray();

		String action = request.getParameter("action");
		String deptId = request.getParameter("deptId");
		String orderBy = request.getParameter("orderBy");
		String sortBy = request.getParameter("sortBy");
		
		
		
		logger.info("action >> {}",action);
		logger.info("deptId >> {}",deptId);
		logger.info("orderBy >> {}",orderBy);
		logger.info("sortBy >>{} ",sortBy);
		
		String headerStr = "";
		StringBuilder tBody = new StringBuilder();
//		ResultSet rs = getReportData(action, deptId);
		
		ResultSet rs = null;

		try (Connection conn = dataAccess.getConnection();
				CallableStatement cs = conn.prepareCall("{CALL GENERIC_REPORTS(?,?)}", ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);

		) {
			cs.setString(1, action);
			cs.setString(2, deptId);
			cs.execute();
			rs = cs.getResultSet();	
			
		
		List<String> headers = new ArrayList<>();

	
				
			
			if (action.equalsIgnoreCase("CARD_BALANCE_DETAILS")) {
				
				StringBuilder strbld = new StringBuilder();	
				strbld.append("<thead><tr>");
				StringBuilder sbHeaders = new StringBuilder();	
				
				
				headers.add("Card No");
				headers.add("Department");
				headers.add("First Name");
				headers.add("Card Amount");
				headers.add("Paid Amount");
				headers.add("Card Balance");
				headers.add("Phone");
				
				headers.stream().forEach((k) -> {
					strbld.append("<th align='center'>").append(k).append("</th>");
				});
				
				strbld.append(sbHeaders);				
				strbld.append("</tr></thead>");
				
				headerStr = strbld.toString();							
				
				List<CardBalanceReport> rawData = getCardBalanceReportData(rs);

				List<CardBalanceReport> sortedData = sortCardBalanceReport(orderBy,sortBy,rawData);
				
				sortedData.stream().forEach((rec) -> {
					tBody.append("<tr>");				
					tBody.append("<td>").append(rec.getCardNo()).append(TD_END);
					tBody.append("<td>").append(rec.getDeptId()).append(TD_END);
					tBody.append("<td>").append(rec.getFirstName()).append(TD_END);
					tBody.append("<td>").append(rec.getCardAmount()).append(TD_END);
					tBody.append("<td>").append(rec.getPaidAmount()).append(TD_END);
					tBody.append("<td>").append(rec.getCardBalance()).append(TD_END);
					tBody.append("<td>").append(rec.getMobilePhone()).append(TD_END);
					tBody.append("</tr>");
				});

			} else if (action.equalsIgnoreCase("LOAN_BALANCE_DETAILS")) {
				
				
				StringBuilder strbld = new StringBuilder();	
				strbld.append("<thead><tr>");
				StringBuilder sbHeaders = new StringBuilder();	
				
				
				headers.add("Card No");
				headers.add("Department");
				headers.add("First Name");
				headers.add("Loan Amount");
				headers.add("Loan Repaid");
				headers.add("Loan Balance");
				headers.add("Phone");
				
				headers.stream().forEach((k) -> {
					strbld.append("<th align='center'>").append(k).append("</th>");
				});
				
				strbld.append(sbHeaders);				
				strbld.append("</tr></thead>");				
				headerStr = strbld.toString();
				
				
				List<LoanBalanceReport> rawData = getLoanBalanceReportData(rs);
				List<LoanBalanceReport> sortedData = sortLoanBalanceReport(orderBy,sortBy,rawData);

				sortedData.stream().forEach((rec) -> {
					tBody.append("<tr>");					
					tBody.append("<td>").append(rec.getCardNo()).append(TD_END);
					tBody.append("<td>").append(rec.getDeptId()).append(TD_END);
					tBody.append("<td>").append(rec.getFirstName()).append(TD_END);	
					tBody.append("<td>").append(rec.getLoanAmount().toString()).append(TD_END);
					tBody.append("<td>").append(rec.getLoanRePaid().toString()).append(TD_END);
					tBody.append("<td>").append(rec.getLoanBalance().toString()).append(TD_END);
					tBody.append("<td>").append(rec.getMobilePhone()).append(TD_END);
					tBody.append("</tr>");
				});


			}else if (action.equalsIgnoreCase("CONTACT_DETAILS")) {
				
				
				StringBuilder strbld = new StringBuilder();	
				strbld.append("<thead><tr>");
				StringBuilder sbHeaders = new StringBuilder();	
				
				
				headers.add("Card No");
				headers.add("Department");
				headers.add("First Name");
				headers.add("Perminent Address");
				headers.add("Present Address");
				headers.add("Adhar");
				headers.add("Phone");
				
				headers.stream().forEach((k) -> {
					strbld.append("<th align='center'>").append(k).append("</th>");
				});
				
				strbld.append(sbHeaders);				
				strbld.append("</tr></thead>");				
				headerStr = strbld.toString();
				
				
				
				List<ContactDetailsReport> data = getContactDetailsReportData(rs);
				
				List<ContactDetailsReport> sortedData = sortContactDetailsReport(orderBy,sortBy,data);

				sortedData.stream().forEach((rec) -> {
					tBody.append("<tr>");
					
					tBody.append("<td>").append(rec.getCardNo()).append(TD_END);
					tBody.append("<td>").append(rec.getDeptId()).append(TD_END);
					tBody.append("<td>").append(rec.getFirstName()).append(TD_END);
					tBody.append("<td>").append(rec.getPerminentAddress()).append(TD_END);
					tBody.append("<td>").append(rec.getPresentAddress()).append(TD_END);
					tBody.append("<td>").append(rec.getAdhar()).append(TD_END);
					tBody.append("<td>").append(rec.getMobilePhone()).append(TD_END);
					tBody.append("</tr>");
				});

			
			}else if (action.equalsIgnoreCase("BANK_DETAILS")) {
				
				
				StringBuilder strbld = new StringBuilder();	
				strbld.append("<thead><tr>");
				StringBuilder sbHeaders = new StringBuilder();	
				
				
				headers.add("Card No");
				headers.add("Department");
				headers.add("First Name");
				headers.add("Bank Name");
				headers.add("Branch");
				headers.add("IFSC");
				headers.add("Phone");
				
				headers.stream().forEach((k) -> {
					strbld.append("<th align='center'>").append(k).append("</th>");
				});
				
				strbld.append(sbHeaders);				
				strbld.append("</tr></thead>");				
				headerStr = strbld.toString();
				
				
				
				List<BankDetailsReport> data = getBankDetailsReportData(rs);
				
				List<BankDetailsReport> sortedData = sortBankDetailsReport(orderBy, sortBy, data);
				sortedData.stream().forEach((rec) -> {
					tBody.append("<tr>");
					
					tBody.append("<td>").append(rec.getCardNo()).append(TD_END);
					tBody.append("<td>").append(rec.getDeptId()).append(TD_END);
					tBody.append("<td>").append(rec.getFirstName()).append(TD_END);
					tBody.append("<td>").append(rec.getBankName()).append(TD_END);
					tBody.append("<td>").append(rec.getBankBranch()).append(TD_END);
					tBody.append("<td>").append(rec.getIfscCode()).append(TD_END);
					tBody.append("<td>").append(rec.getMobilePhone()).append(TD_END);
					tBody.append("</tr>");
				});



			} else if (action.equalsIgnoreCase("SUBSCRIPTION_BALANCE")) {
				
				
				StringBuilder strbld = new StringBuilder();	
				strbld.append("<thead><tr>");
				StringBuilder sbHeaders = new StringBuilder();	
				
				
				headers.add("Card No");
				headers.add("Department");
				headers.add("First Name");
				headers.add("Date Of Joining");
				headers.add("Balance Subscribtion Years");

				headers.add("Phone");
				
				headers.stream().forEach((k) -> {
					strbld.append("<th align='center'>").append(k).append("</th>");
				});
				
				strbld.append(sbHeaders);				
				strbld.append("</tr></thead>");				
				headerStr = strbld.toString();
				
				
				List<SubscriptionReport> data = getSubscriptionReportData(rs);

				
				
				logger.info("List Size  {}",data.size());
				Map<String, List<SubscriptionReport>> groupByData = data.stream()
						  .collect(Collectors.groupingBy(SubscriptionReport::getMemberId));
				logger.info("Map Size  "+groupByData.size());
				
				List<SubscriptionReport> finalResult = new ArrayList<>(); 
				
				
				groupByData.entrySet().stream()
		
			      .forEach(
			    		  e ->{
			    				
			    			  
			    			  List<SubscriptionReport> sortedList = e.getValue().stream()
	    				        .sorted(Comparator.comparing(SubscriptionReport::getSubscribedYear).reversed())
	    				        .collect(Collectors.toList());
			    			  			    			  

			    			  if(sortedList!=null && sortedList.size()>0) {
			    			  SubscriptionReport processedData = sortedList.get(0);
		
			    			  int lastSubscriptionYear = processedData.getSubscribedYear();
			    			 
			    			  Date date = new Date();
			    			  GregorianCalendar calendar = new GregorianCalendar();
			    			  calendar.setTime(date);
			    			  int currentYear = calendar.get(Calendar.YEAR);
//			    			  lastSubscriptionYear++;
			    			  
			    			  
			    			  if(lastSubscriptionYear != currentYear && lastSubscriptionYear < currentYear) {
		    					  lastSubscriptionYear++;
		    					  if(lastSubscriptionYear == currentYear) {
		    						  processedData.setUnsubScribedYears(""+lastSubscriptionYear);
		    						  
		    					  }else {
		    						  processedData.setUnsubScribedYears(lastSubscriptionYear+" To "+currentYear);
		    					  }
		    					 
		    					  finalResult.add(processedData);
		    				  }    
			    			  
			    			  }
			    			 
			    		  }		
			    		  
			    		  
			    		  );	
				List<SubscriptionReport> sortedData = sortSubscritpionReport(orderBy, sortBy, finalResult);
				
				sortedData.stream().forEach((rec) -> {
					tBody.append("<tr>");
					
					tBody.append("<td>").append(rec.getCardNo()).append(TD_END);
					tBody.append("<td>").append(rec.getDeptId()).append(TD_END);
					tBody.append("<td>").append(rec.getFirstName()).append(TD_END);					
					tBody.append("<td>").append(rec.getDatOfJoining()).append(TD_END);
					tBody.append("<td>").append(rec.getUnsubScribedYears()).append(TD_END);
					tBody.append("<td>").append(rec.getMobilePhone()).append(TD_END);
					tBody.append("</tr>");
					
				});


			}

		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getDetialsBySelectAtion");
		}
		return prepareTable(headerStr, tBody.toString()).toString();
	}

	private ResultSet getSummaryReportData(String action, String deptId, String fromDate, String toDate) {
		ResultSet rSet = null;
		try (Connection conn = dataAccess.getConnection();
				CallableStatement cs = conn.prepareCall(SUMMARY_REPORTS_PROC,
						ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		) {
			cs.setString(1, action);
			cs.setString(2, deptId);
			cs.setString(3, fromDate);
			cs.setString(4, toDate);
			cs.execute();
			rSet = cs.getResultSet();
		} catch (Exception e) {
			e.printStackTrace();
			ApplicationUtilities.error(getClass(), e, "getSummary");
		}
		
		return rSet;
	}
	
	public static String reverseDate(String dateStr) {
		
		if(Objects.nonNull(dateStr) && dateStr.split("\\-").length ==3) {
			String[] dateAttrbutes = dateStr.split("\\-");			
			StringBuilder reversedDate = new StringBuilder();			
			reversedDate.append(dateAttrbutes[2]).append("-").append(dateAttrbutes[1]).append("-").append(dateAttrbutes[0]);
			return reversedDate.toString();
			
		}else {
			
			return dateStr;
		}

		
	}
	private static String SUMMARY_REPORTS_PROC ="{CALL GENERIC_SUMMARY_REPORTS(?,?,?,?,?,?)}";
	
	@SuppressWarnings({ "unused", "unchecked" })
	@Transactional
	public String getSummary(HttpServletRequest request) {
		StringBuilder finalResult = new StringBuilder();
		Connection conn = dataAccess.getConnection();
		CallableStatement cs = null;
		try {
			 cs = conn.prepareCall(SUMMARY_REPORTS_PROC,
						ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		JSONArray resutlJsnArr = new JSONArray();
			
		String action = request.getParameter("action");
		String deptId = request.getParameter("deptId");
		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		String orderBy = request.getParameter("orderBy");
		String sortBy = request.getParameter("sortBy");
		
		
		logger.info("action >> {}",action);
		logger.info("deptId >> {}",deptId);
		logger.info("fromDate >>{} ",fromDate);
		logger.info("toDate >> {}",toDate);
		logger.info("orderBy >>{} ",orderBy);
		logger.info("sortBy >> {}",sortBy);
		
		String revToDate = reverseDate(toDate);
		
		String revFromDate = reverseDate(fromDate);
		
		
		
		ResultSet rs = null;// getSummaryReportData(action, deptId, fromDate, toDate );
		cs.setString(1, action);
		cs.setString(2, deptId);
		cs.setString(3, fromDate);
		cs.setString(4, toDate);
		cs.setString(5, orderBy);
		cs.setString(6, sortBy);
		cs.execute();
		rs = cs.getResultSet();

		
		List<String> headers = new ArrayList<>();					
			
			if (action.equalsIgnoreCase("CARD_BALANCE_SUMMARY")) {	
				StringBuilder cardSummaryTable = new StringBuilder();

				
				List<CardBalanceYearlySymmary> rawData = getCardBalanceSummaryData(rs);
	
				if(Objects.isNull(rawData) || rawData.isEmpty()) {
					StringBuilder noData = new StringBuilder();
					noData.append("<table border='1' cellspacing='0' cellpadding='3' style='border-color: #EEE'  border='1' width='100'> <tr ><td colspan='6' align='center'> <H4>No data found.</H4></td></tr></table>");
				return finalResult.append(noData.toString()).toString();
				}
				
				StringBuilder detailsTable = new StringBuilder();
				detailsTable.append("<table  border='1' cellspacing='0' cellpadding='3' style='border-color: #EEE' border='1'><thead><tr>");

				StringBuilder sbHeaders = new StringBuilder();				
				
				headers.add("Card No");
				headers.add("Department");
				headers.add("First Name");
				headers.add("Card Amount");
				headers.add("Paid Amount");
				headers.add("Card Balance");
				headers.add("Phone");
				headers.add("Paid Date");
				headers.add("Reciept No");
				headers.add("Registered Date");
				
				headers.stream().forEach((k) -> {
					detailsTable.append("<th align='center'>").append(k).append("</th>");
				});
				
				detailsTable.append(sbHeaders);				
				detailsTable.append("</tr></thead>");
				rawData.stream().forEach((rec) -> {
					detailsTable.append("<tr>");				
					detailsTable.append(TD_START).append(rec.getCardNo()).append(TD_END);
					detailsTable.append(TD_START).append(rec.getDeptId()).append(TD_END);
					detailsTable.append(TD_START).append(rec.getFirstName()).append(TD_END);
					detailsTable.append(TD_START).append(rec.getCardAmount()).append(TD_END);
					detailsTable.append(TD_START).append(rec.getPaidAmount()).append(TD_END);
					detailsTable.append(TD_START).append(rec.getCardBalance()).append(TD_END);					
					detailsTable.append(TD_START).append(rec.getMobilePhone()).append(TD_END);
					detailsTable.append(TD_START).append(rec.getPaidDate()).append(TD_END);
					detailsTable.append(TD_START).append(rec.getReceiptNo()).append(TD_END);
					detailsTable.append(TD_START).append(rec.getRegisteredDate()).append(TD_END);
					detailsTable.append("</tr>");
				});
				
				
				detailsTable.append("</table>");
				cardSummaryTable.append("<H4> Card amount collected between " + revFromDate + " and  " + revToDate +"</H4>");
				cardSummaryTable.append("<table  border='1' cellspacing='0' cellpadding='3' style='border-color: #EEE' border='1'><thead><tr>");
				cardSummaryTable.append("<th align='center'>").append("TOTAL AMOUNT").append("</th>");
				cardSummaryTable.append("<th align='center'>").append("COLLECTED AMOUNT").append("</th>");
				cardSummaryTable.append("<th align='center'>").append("BALANCE").append("</th></thead>");				
				Double totalCardAmount = rawData.stream().collect(Collectors.summingDouble((CardBalanceYearlySymmary::getCardAmount)));				
				Double collectedAmount = rawData.stream().collect(Collectors.summingDouble((CardBalanceYearlySymmary::getPaidAmount)));				
				Double balanceAmount = totalCardAmount- collectedAmount;
				
				cardSummaryTable.append("<tr>");				
				cardSummaryTable.append(TD_START).append(totalCardAmount.intValue()).append(TD_END);
				cardSummaryTable.append(TD_START).append(collectedAmount.intValue()).append(TD_END);
				cardSummaryTable.append(TD_START).append(balanceAmount.intValue()).append("</td></tr></table>");
				
				
				
				finalResult.append(cardSummaryTable.toString());				
				finalResult.append(detailsTable.toString());

			}else if(action.equalsIgnoreCase("LOANS_SUMMARY")) {
				
				
				action = "LOAN_SANCTIONED_SUMMARY";
				String  loanSanctionedSummary = "";
				String  loanRecoveredSummary = "";
				ResultSet loanSanctionedRs = null;//  getSummaryReportData(action, deptId, fromDate, toDate);
			
				cs = conn.prepareCall(SUMMARY_REPORTS_PROC,
						ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				cs.setString(1, action);
				cs.setString(2, deptId);
				cs.setString(3, fromDate);
				cs.setString(4, toDate);
				cs.setString(5, orderBy);
				cs.setString(6, sortBy);
				cs.execute();
				loanSanctionedRs = cs.getResultSet();

				int totalLoanSanctined =0;
				if (Objects.isNull(loanSanctionedRs) || !loanSanctionedRs.first()) {
					
					loanSanctionedSummary = "<table border='1' cellspacing='0' cellpadding='3' style='border-color: #EEE'  border='1' width='100'> <tr ><td colspan='6' align='center'> <H4>No loan sanctioned data found.</H4></td></tr></table>";
					finalResult.append(loanSanctionedSummary);
				}else {
					
				StringBuilder table = new StringBuilder();				
				table.append("<table border='1' cellspacing='0' cellpadding='3' style='border-color: #EEE'  border='1' width='100'><thead><tr>");
				StringBuilder sbHeaders = new StringBuilder();				
				headers = new ArrayList<>();	 
				headers.add("Card No");
				headers.add("Department");
				headers.add("First Name");
				headers.add("Phone");				
				headers.add("Loan Amount");
				headers.add("Loan Sanctioned Date");				
				headers.stream().forEach((k) -> {
					sbHeaders.append("<th align='center'>").append(k).append("</th>");
				});
				
				table.append(sbHeaders);				
				table.append("</tr></thead>");

				while (loanSanctionedRs.next()) {	
					
					int loanAmount = loanSanctionedRs.getObject("PAID_AMOUNT")!=null? Integer.valueOf(loanSanctionedRs.getObject("PAID_AMOUNT").toString()):0;
					table.append("<tr>");
					table.append(TD_START).append(getNonNulData(loanSanctionedRs.getObject("CARD_NO"))).append(TD_END);
					table.append(TD_START).append(getNonNulData(loanSanctionedRs.getObject("DEPT_ID"))).append(TD_END);					
					table.append(TD_START).append(getNonNulData(loanSanctionedRs.getObject("FIRST_NAME"))).append(TD_END);
					table.append(TD_START).append(getNonNulData(loanSanctionedRs.getObject("PHONE_NO"))).append(TD_END);
					table.append(TD_START).append(loanAmount).append(TD_END);
					table.append(TD_START).append(dateFormat.format(loanSanctionedRs.getObject("PAID_DATE"))).append(TD_END);					
					table.append("</tr>");

					totalLoanSanctined= totalLoanSanctined+loanAmount;
				}	
				table.append("</table>");
				
				loanSanctionedSummary="<H4> Total loan sanctioned between "+revFromDate+" and  "+revToDate+" : "+totalLoanSanctined+"</H4>";
				finalResult.append(loanSanctionedSummary);
				finalResult.append(table);
				}
							
				
				action = "LOAN_RECOVERY_SUMMARY";
				ResultSet loanRecoveryRs = null;// getSummaryReportData(action, deptId, fromDate, toDate);

				cs = conn.prepareCall(SUMMARY_REPORTS_PROC, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				
				cs.setString(1, action);
				cs.setString(2, deptId);
				cs.setString(3, fromDate);
				cs.setString(4, toDate);
				cs.setString(5, orderBy);
				cs.setString(6, sortBy);
				cs.execute();
				loanRecoveryRs = cs.getResultSet();

				

				
				int totalLoanAmountRecovered =0;
				if (Objects.isNull(loanRecoveryRs) || !loanRecoveryRs.first()) {
//					table = new StringBuilder();
					loanRecoveredSummary = "<table border='1' cellspacing='0' cellpadding='3' style='border-color: #EEE'  border='1' width='100'> <tr ><td colspan='6' align='center'> <H4>No loan recovery data found.</H4></td></tr></table>";
					finalResult.append(loanRecoveredSummary);
					

					}else {
					StringBuilder table = new StringBuilder();	
					table.append("<table  border='1' cellspacing='0' cellpadding='3' style='border-color: #EEE' width='100'><thead><tr>");
					StringBuilder sbHeaders1 = new StringBuilder();				
					headers = new ArrayList<>();	
					headers.add("Card No");
					headers.add("Department");
					headers.add("First Name");
					headers.add("Phone");				
					headers.add("Paid Amount");
					headers.add("Paid Date");				
					headers.stream().forEach((k) -> {
						sbHeaders1.append("<th align='center'>").append(k).append("</th>");
					});
					
					table.append(sbHeaders1);				
					table.append("</tr></thead>");
				while (loanRecoveryRs.next()) {	
					
					int paidAmount = loanRecoveryRs.getObject("PAID_AMOUNT")!=null? Integer.valueOf(loanRecoveryRs.getObject("PAID_AMOUNT").toString()):0;
					table.append("<tr>");
					table.append(TD_START).append(getNonNulData(loanRecoveryRs.getObject("CARD_NO"))).append(TD_END);
					table.append(TD_START).append(getNonNulData(loanRecoveryRs.getObject("DEPT_ID"))).append(TD_END);					
					table.append(TD_START).append(getNonNulData(loanRecoveryRs.getObject("FIRST_NAME"))).append(TD_END);
					table.append(TD_START).append(getNonNulData(loanRecoveryRs.getObject("PHONE_NO"))).append(TD_END);
					table.append(TD_START).append(paidAmount).append(TD_END);
					table.append(TD_START).append(dateFormat.format(loanRecoveryRs.getObject("PAID_DATE"))).append(TD_END);					
					table.append("</tr>");					
					totalLoanAmountRecovered= totalLoanAmountRecovered+paidAmount;
				}	
				table.append("</table>");				
				loanRecoveredSummary ="<H4> Total loan amount recovered between "+revFromDate+" and  "+revToDate+" : "+totalLoanAmountRecovered+"</H4>";
				finalResult.append(loanRecoveredSummary);
				finalResult.append(table);
				}
				
				
			} else 	 if (action.equalsIgnoreCase("SUBSCRIPTION_SUMMARY")) {	

				StringBuilder table = new StringBuilder();
				String subscriptionSummary = "";
				table.append("<table border='1' cellspacing='0' cellpadding='3' style='border-color: #EEE'  border='1' width='100'><thead><tr>");
				ResultSet subscriptionsRs = null;// getSummaryReportData(action, deptId, fromDate, toDate);
//				cs = conn.prepareCall(SUMMARY_REPORTS_PROC, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
				cs.setString(1, action);
				cs.setString(2, deptId);
				cs.setString(3, fromDate);
				cs.setString(4, toDate);
				cs.setString(5, orderBy);
				cs.setString(6, sortBy);
				cs.execute();
				subscriptionsRs = cs.getResultSet();

				StringBuilder sbHeaders = new StringBuilder();
				headers = new ArrayList<>();
				headers.add("Card No");
				headers.add("Department");
				headers.add("First Name");
				headers.add("Phone No");
				headers.add("Subscription Year");
				headers.add("Paid Amount");
				headers.add("Paid Date");
				headers.add("Reciept No");
				headers.add("Ctegory");
				headers.add("Remarks");
				headers.stream().forEach((k) -> {
					sbHeaders.append("<th align='center'>").append(k).append("</th>");
				});

//				DEPT_ID,CARD_NO,FIRST_NAME,PHONE_NO,PAID_DATE,RECEIPT_NO,SUBSCRIPTION_AMOUNT,SUBSCRIPTION_YEAR,PAID_AMOUNT,CATEGORY,REMARKS
				table.append(sbHeaders);
				table.append("</tr></thead>");
				int totalPpaidAmount = 0;
				if (Objects.isNull(subscriptionsRs) || !subscriptionsRs.first()) {
					table = new StringBuilder();
					table.append("<table border='1' cellspacing='0' cellpadding='3' style='border-color: #EEE'  border='1' width='100'> <tr ><td colspan='6' align='center'> <H4>No data found.</H4></td></tr></table>");
				} else {
					while (subscriptionsRs.next()) {

						int paidAmount = subscriptionsRs.getObject("PAID_AMOUNT") != null
								? Integer.valueOf(subscriptionsRs.getObject("PAID_AMOUNT").toString())
								: 0;
						table.append("<tr>");
						table.append(TD_START).append(getNonNulData(subscriptionsRs.getObject("CARD_NO")))
								.append(TD_END);
						table.append(TD_START).append(getNonNulData(subscriptionsRs.getObject("DEPT_ID")))
								.append(TD_END);
						table.append(TD_START).append(getNonNulData(subscriptionsRs.getObject("FIRST_NAME")))
								.append(TD_END);
						table.append(TD_START).append(getNonNulData(subscriptionsRs.getObject("PHONE_NO")))
								.append(TD_END);
						table.append(TD_START).append(getNonNulData(subscriptionsRs.getObject("SUBSCRIPTION_YEAR")))
								.append(TD_END);
						table.append(TD_START).append(paidAmount).append(TD_END);
						table.append(TD_START).append(dateFormat.format(subscriptionsRs.getObject("PAID_DATE")))
								.append(TD_END);
						table.append(TD_START).append(getNonNulData(subscriptionsRs.getObject("RECEIPT_NO")))
								.append(TD_END);
						table.append(TD_START).append(getNonNulData(subscriptionsRs.getObject("CATEGORY")))
								.append(TD_END);
						table.append(TD_START).append(getNonNulData(subscriptionsRs.getObject("REMARKS")))
								.append(TD_END);

						table.append("</tr>");

						totalPpaidAmount = totalPpaidAmount + paidAmount;
					}
					table.append("</table>");
					subscriptionSummary = "<H4> Total subscription amount collected between " + revFromDate + " and  " + revToDate + " : "
							+ totalPpaidAmount + "</H4>";

				}
				finalResult.append(subscriptionSummary);
				finalResult.append(table.toString());
				}
		} catch (Exception e) {
			e.printStackTrace();
			ApplicationUtilities.error(getClass(), e, "getSummary");
		}finally {
			if(Objects.nonNull(conn)) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(Objects.nonNull(cs)) {
				try {
					cs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		

		return finalResult.toString();
	}
	
	public List<CardBalanceReport> sortCardBalanceReport(String orderBy, String sortBy, List<CardBalanceReport> data){
		
		

		List<CardBalanceReport> sortedData = new ArrayList<>();
		
		
		if ("CARD_NO".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream()
						.sorted(Comparator.comparing(CardBalanceReport::getCardNo, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {

				sortedData = data.stream().sorted(Comparator.comparing(CardBalanceReport::getCardNo))
						.collect(Collectors.toList());
			}

		} else if ("FIRST_NAME".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream().sorted(
						Comparator.comparing(CardBalanceReport::getFirstName, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {
				sortedData = data.stream().sorted(Comparator.comparing(CardBalanceReport::getFirstName))
						.collect(Collectors.toList());
			}

		} else if ("CARD_AMOUNT".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream().sorted(
						Comparator.comparing(CardBalanceReport::getCardAmount, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {
				sortedData = data.stream().sorted(Comparator.comparing(CardBalanceReport::getCardAmount))
						.collect(Collectors.toList());
			}

		} else if ("PAID_AMOUNT".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream().sorted(
						Comparator.comparing(CardBalanceReport::getPaidAmount, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {
				sortedData = data.stream().sorted(Comparator.comparing(CardBalanceReport::getPaidAmount))
						.collect(Collectors.toList());
			}

		} else if ("CARD_BALANCE".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream().sorted(
						Comparator.comparing(CardBalanceReport::getCardBalance, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {
				sortedData = data.stream().sorted(Comparator.comparing(CardBalanceReport::getCardBalance))
						.collect(Collectors.toList());
			}

		} else if ("PHONE_NO".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream().sorted(
						Comparator.comparing(CardBalanceReport::getMobilePhone, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {

				sortedData = data.stream().sorted(Comparator.comparing(CardBalanceReport::getMobilePhone))
						.collect(Collectors.toList());
			}

		}
		
		return sortedData;
		
	}
	
	
	public List<LoanBalanceReport> sortLoanBalanceReport(String orderBy, String sortBy, List<LoanBalanceReport> data){
		
		

		List<LoanBalanceReport> sortedData = new ArrayList<>();
		
		
		if ("CARD_NO".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream()
						.sorted(Comparator.comparing(LoanBalanceReport::getCardNo, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {

				sortedData = data.stream().sorted(Comparator.comparing(LoanBalanceReport::getCardNo))
						.collect(Collectors.toList());
			}

		} else if ("FIRST_NAME".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream().sorted(
						Comparator.comparing(LoanBalanceReport::getFirstName, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {
				sortedData = data.stream().sorted(Comparator.comparing(LoanBalanceReport::getFirstName))
						.collect(Collectors.toList());
			}

		} else if ("LOAN_AMOUNT".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream().sorted(
						Comparator.comparing(LoanBalanceReport::getLoanAmount, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {
				sortedData = data.stream().sorted(Comparator.comparing(LoanBalanceReport::getLoanAmount))
						.collect(Collectors.toList());
			}

		} else if ("LOAN_PAID".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream().sorted(
						Comparator.comparing(LoanBalanceReport::getLoanRePaid, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {
				sortedData = data.stream().sorted(Comparator.comparing(LoanBalanceReport::getLoanRePaid))
						.collect(Collectors.toList());
			}

		} else if ("LOAN_BALANCE".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream().sorted(
						Comparator.comparing(LoanBalanceReport::getLoanBalance, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {
				sortedData = data.stream().sorted(Comparator.comparing(LoanBalanceReport::getLoanBalance))
						.collect(Collectors.toList());
			}

		} else if ("PHONE_NO".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream().sorted(
						Comparator.comparing(LoanBalanceReport::getMobilePhone, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {

				sortedData = data.stream().sorted(Comparator.comparing(LoanBalanceReport::getMobilePhone))
						.collect(Collectors.toList());
			}

		}
		
		return sortedData;
		
	}
	
	public List<ContactDetailsReport> sortContactDetailsReport(String orderBy, String sortBy, List<ContactDetailsReport > data){
		
		

		List<ContactDetailsReport> sortedData = new ArrayList<>();
		
		
		if ("CARD_NO".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream()
						.sorted(Comparator.comparing(ContactDetailsReport::getCardNo, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {

				sortedData = data.stream().sorted(Comparator.comparing(ContactDetailsReport::getCardNo))
						.collect(Collectors.toList());
			}

		} else if ("FIRST_NAME".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream().sorted(
						Comparator.comparing(ContactDetailsReport::getFirstName, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {
				sortedData = data.stream().sorted(Comparator.comparing(ContactDetailsReport::getFirstName))
						.collect(Collectors.toList());
			}

		}
		return sortedData;
	}
	
	
	public List<BankDetailsReport> sortBankDetailsReport(String orderBy, String sortBy, List<BankDetailsReport > data){
		
		

		List<BankDetailsReport> sortedData = new ArrayList<>();
		
		
		if ("CARD_NO".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream()
						.sorted(Comparator.comparing(BankDetailsReport::getCardNo, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {

				sortedData = data.stream().sorted(Comparator.comparing(BankDetailsReport::getCardNo))
						.collect(Collectors.toList());
			}

		} else if ("FIRST_NAME".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream().sorted(
						Comparator.comparing(BankDetailsReport::getFirstName, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {
				sortedData = data.stream().sorted(Comparator.comparing(BankDetailsReport::getFirstName))
						.collect(Collectors.toList());
			}

		}
		return sortedData;
	}
	
	
	
	public List<SubscriptionReport> sortSubscritpionReport(String orderBy, String sortBy, List<SubscriptionReport > data){
		
		

		List<SubscriptionReport> sortedData = new ArrayList<>();
		
		
		if ("CARD_NO".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream()
						.sorted(Comparator.comparing(SubscriptionReport::getCardNo, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {

				sortedData = data.stream().sorted(Comparator.comparing(SubscriptionReport::getCardNo))
						.collect(Collectors.toList());
			}

		} else if ("FIRST_NAME".equals(orderBy)) {

			if ("DESC".equals(sortBy)) {

				sortedData = data.stream().sorted(
						Comparator.comparing(SubscriptionReport::getFirstName, Comparator.reverseOrder()))
						.collect(Collectors.toList());

			} else {
				sortedData = data.stream().sorted(Comparator.comparing(SubscriptionReport::getFirstName))
						.collect(Collectors.toList());
			}

		} 
		return sortedData;
	}
	
	
	
	@SuppressWarnings({ "unused", "unchecked" })
	@Transactional
	public String getDetialsBySelectAtionOld(HttpServletRequest request) {
		JSONArray resutlJsnArr = new JSONArray();
		int colCount = 0;
		String theadStr = "<table border='1' cellspacing='0' cellpadding='5' style='border-color: #EEE'><thead><tr><th align='center'>SNo</th><th align='center'>Name</th><th align='center'>Department Name</th><th align='center'>Card No</th>";

		String tbody = "<tbody>";

		try {
			String action = request.getParameter("action");
			String deptId = request.getParameter("deptId");

			String query = " from Registration ";
			deptId = (deptId != null && !"".equals(deptId)) ? deptId : "ALL_DEPARTMENTS";
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			action = (action != null && !"".equals(action)) ? action : "CONTACT_DETAILS";

			if (!deptId.equalsIgnoreCase("ALL_DEPARTMENTS")) {
				query = String.valueOf(query) + " where registrationPK.deptId=:deptId ";
				parametersMap.put("deptId", deptId);
			}

			if (action.equalsIgnoreCase("CARD_BALANCE_DETAILS")) {
				colCount = 7;
				theadStr = String.valueOf(theadStr)
						+ "<th align='center'>Card Amount</th><th align='center'>Paid Amount</th><th align='center'>Card Balance</th>";

			} else if (action.equalsIgnoreCase("LOAN_BALANCE_DETAILS")) {

				colCount = 8;
				theadStr = String.valueOf(theadStr)
						+ "<th align='center'>Loan Amount</th><th align='center'>Total Paid</th><th align='center'>Loan Balance</th><th align='center'>Phone Number</th>";

			} else if (action.equalsIgnoreCase("BANK_DETAILS")) {
				colCount = 8;
				theadStr = String.valueOf(theadStr)
						+ "<th align='center'>Acc Holder Name</th><th align='center'>Acc Number</th><th align='center'>Branch</th><th align='center'>IFSC</th>";
			} else {

				colCount = 6;
				theadStr = String.valueOf(theadStr)
						+ "<th align='center'>Perminent Address</th><th align='center'>Phone No</th>";
			}

			theadStr = String.valueOf(theadStr) + "</tr></thead>";

			if (!action.equalsIgnoreCase("LOAN_BALANCE_DETAILS")) {
				query = String.valueOf(query) + " order by  deptName asc ";
				List<Registration> list = this.dataAccess.queryWithParams(query, parametersMap);

				if (list != null && list.size() > 0) {
					int k = 1;
					for (int j = 0; j < list.size(); j++) {

						JSONObject detailsObj = new JSONObject();
						Registration member = list.get(j);
						tbody = String.valueOf(tbody) + "<tr><td align='center' >" + k + TD_END;
						if (action.equalsIgnoreCase("CARD_BALANCE_DETAILS")) {

							tbody = String.valueOf(tbody) + "<td>"
									+ this.utils.convertNullToEmptyString(member.getFirstName()) + TD_END;
							tbody = String.valueOf(tbody) + "<td>"
									+ this.utils.convertNullToEmptyString(member.getDeptName()) + TD_END;
							tbody = String.valueOf(tbody) + "<td>" + this.utils.convertNullToEmptyString(
									Integer.valueOf(member.getRegistrationPK().getCardNo())) + TD_END;
							tbody = String.valueOf(tbody) + "<td>"
									+ this.utils.convertNullToEmptyString(member.getCardAmount()) + TD_END;
							tbody = String.valueOf(tbody) + "<td>"
									+ this.utils.convertNullToEmptyString(Integer.valueOf(member.getPaidAmount()))
									+ TD_END;
							tbody = String.valueOf(tbody) + "<td>"
									+ this.utils.convertNullToEmptyString(member.getCardBalance()) + TD_END;
						} else if (action.equalsIgnoreCase("LOAN_BALANCE_DETAILS")) {

							String loanDetailsSearch = " SELECT MEMBER_ID,SUM(LOAN_AMOUNT) as LOAN_AMOUNT FROM LOANDETAILS     GROUP BY MEMBER_ID ";

							List<Object[]> loanDetailsSearchList = this.dataAccess.sqlqueryWithParams(query,
									parametersMap);

							if (loanDetailsSearchList != null && loanDetailsSearchList.size() > 0) {
								for (int i = 0; i < loanDetailsSearchList.size(); i++) {
									Object[] objectArr = loanDetailsSearchList.get(i);

									String sqlLoanQuery = "from Registration  where  RegistrationPK.memberId=:MEMBER_ID ";
									Map<String, Object> loanBalMap = new HashMap<String, Object>();
									loanBalMap.put("MEMBER_ID", objectArr[0]);

									List<Registration> membrLonDetlist = this.dataAccess
											.queryWithParams(sqlLoanQuery, loanBalMap);
									if (membrLonDetlist != null && membrLonDetlist.size() > 0) {
										member = membrLonDetlist.get(0);

										tbody = String.valueOf(tbody) + "<td>"
												+ this.utils.convertNullToEmptyString(member.getFirstName())
												+ TD_END;
										tbody = String.valueOf(tbody) + "<td>"
												+ this.utils.convertNullToEmptyString(member.getDeptName())
												+ TD_END;
										tbody = String.valueOf(tbody) + "<td>"
												+ this.utils.convertNullToEmptyString(
														Integer.valueOf(member.getRegistrationPK().getCardNo()))
												+ TD_END;
										tbody = String.valueOf(tbody) + "<td>" + this.utils
												.convertNullToEmptyString(Integer.valueOf(member.getCurrentLoanIssuedAmount()))
												+ TD_END;
										tbody = String.valueOf(tbody) + "<td>" + this.utils
												.convertNullToEmptyString(Integer.valueOf(Integer.valueOf(member.getCurrentLoanIssuedAmount())
														
														-Integer.valueOf(member.getCardBalance())))
												+ TD_END;
										tbody = String.valueOf(tbody) + "<td>"
												+ this.utils.convertNullToEmptyString(Integer.valueOf(member.getCardBalance()))
												+ TD_END;
										tbody = String.valueOf(tbody) + "<td>"
												+ this.utils.convertNullToEmptyString(member.getPhoneNo())
												+ TD_END;

									}

								

								}

							}
						} else if (action.equalsIgnoreCase("BANK_DETAILS")) {

							tbody = String.valueOf(tbody) + "<td>"
									+ this.utils.convertNullToEmptyString(member.getFirstName()) + TD_END;
							tbody = String.valueOf(tbody) + "<td>"
									+ this.utils.convertNullToEmptyString(member.getDeptName()) + TD_END;
							tbody = String.valueOf(tbody) + "<td>" + this.utils.convertNullToEmptyString(
									Integer.valueOf(member.getRegistrationPK().getCardNo())) + TD_END;
							tbody = String.valueOf(tbody) + "<td>"
									+ this.utils.convertNullToEmptyString(member.getBankAccHolderName()) + TD_END;
							tbody = String.valueOf(tbody) + "<td>"
									+ this.utils.convertNullToEmptyString(member.getBankAccNo()) + TD_END;
							tbody = String.valueOf(tbody) + "<td>"
									+ this.utils.convertNullToEmptyString(member.getBankBranch()) + TD_END;
							tbody = String.valueOf(tbody) + "<td>"
									+ this.utils.convertNullToEmptyString(member.getBankIfscCode()) + TD_END;

						} else {

							tbody = String.valueOf(tbody) + "<td>"
									+ this.utils.convertNullToEmptyString(member.getFirstName()) + TD_END;
							tbody = String.valueOf(tbody) + "<td>"
									+ this.utils.convertNullToEmptyString(member.getDeptName()) + TD_END;
							tbody = String.valueOf(tbody) + "<td>" + this.utils.convertNullToEmptyString(
									Integer.valueOf(member.getRegistrationPK().getCardNo())) + TD_END;
							tbody = String.valueOf(tbody) + "<td>"
									+ this.utils.convertNullToEmptyString(member.getPerminentAddress()) + TD_END;
							tbody = String.valueOf(tbody) + "<td>"
									+ this.utils.convertNullToEmptyString(member.getPhoneNo()) + TD_END;
						}
						tbody = String.valueOf(tbody) + "</tr>";
						k++;
					}
				}
			} else if (action.equalsIgnoreCase("LOAN_BALANCE_DETAILS")) {

				String loanDetailsSearch = " SELECT MEMBER_ID,SUM(LOAN_AMOUNT) as LOAN_AMOUNT FROM LOANDETAILS     GROUP BY MEMBER_ID ";
				Map<String, Object> loanDetailsSearchMap = new HashMap<String, Object>();

				List<Object[]> loanDetailsSearchList = this.dataAccess.sqlqueryWithParams(loanDetailsSearch,
						loanDetailsSearchMap);

				if (loanDetailsSearchList != null && loanDetailsSearchList.size() > 0) {
					int countt = 0;
					for (int i = 0; i < loanDetailsSearchList.size(); i++) {
						Object[] objectArr = loanDetailsSearchList.get(i);

							tbody = String.valueOf(tbody) + "<tr><td align='center' >" + countt++ + TD_END;

							String sqlLoanQuery = "from Registration  where  registrationPK.memberId=:MEMBER_ID ";
							Map<String, Object> loanBalMap = new HashMap<String, Object>();
							loanBalMap.put("MEMBER_ID", objectArr[0]);

							List<Registration> membrLonDetlist = this.dataAccess.queryWithParams(sqlLoanQuery,
									loanBalMap);
							if (membrLonDetlist != null && membrLonDetlist.size() > 0) {
								Registration member = membrLonDetlist.get(0);

								tbody = String.valueOf(tbody) + "<td>"
										+ this.utils.convertNullToEmptyString(member.getFirstName()) + TD_END;
								tbody = String.valueOf(tbody) + "<td>"
										+ this.utils.convertNullToEmptyString(member.getDeptName()) + TD_END;
								tbody = String.valueOf(tbody) + "<td>" + this.utils.convertNullToEmptyString(
										Integer.valueOf(member.getRegistrationPK().getCardNo())) + TD_END;
								tbody = String.valueOf(tbody) + "<td>"
										+ this.utils.convertNullToEmptyString(Integer.valueOf(member.getCurrentLoanIssuedAmount()))
										+ TD_END;
								tbody = String.valueOf(tbody) + "<td>"
										+ this.utils.convertNullToEmptyString(Integer.valueOf(member.getCurrentLoanIssuedAmount())
												
												-Integer.valueOf(member.getCardBalance()))
										+ TD_END;
								tbody = String.valueOf(tbody) + "<td>"
										+ this.utils.convertNullToEmptyString(Integer.valueOf(member.getCardBalance())) + TD_END;
								tbody = String.valueOf(tbody) + "<td>"
										+ this.utils.convertNullToEmptyString(member.getPhoneNo()) + "</td></tr>";

							}

						

					}

				}

			} else {

				tbody = String.valueOf(tbody) + "<tr><td colspan='" + colCount
						+ "' align='center'> No data found !</td></tr>";
			}

			tbody = String.valueOf(tbody) + "</tbody>";

		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getDetialsBySelectAtion");
		}

		return String.valueOf(theadStr) + tbody + "</table>";
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public JSONArray getPaymentConfigDetialsForSelect(String deptId, String category) {
		PaymentConfigurations paymentConfigurations = null;
		JSONArray objArray = new JSONArray();

		try {
			String query = " from PaymentConfigurations where paymentConfigurationsPK.category=:category and status=:status  ";
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			parametersMap.put("status", "ACTIVE");
			parametersMap.put("category", category);
			if (deptId != null && !"".equalsIgnoreCase(deptId)) {

				query = String.valueOf(query) + " and paymentConfigurationsPK.deptId=:deptId  ";
				parametersMap.put("deptId", deptId);
			}

			List<PaymentConfigurations> list = this.dataAccess.queryWithParams(query, parametersMap);

			if (list != null && list.size() > 0) {
				for (int j = 0; j < list.size(); j++) {
					JSONObject obj = new JSONObject();
					paymentConfigurations = list.get(j);
					obj.put("DEPT_ID", paymentConfigurations.getPaymentConfigurationsPK().getDeptId());
					obj.put("PAYMENT_CONF_ID", paymentConfigurations.getPaymentConfigurationsPK().getPaymentConfId());
					obj.put("MEMBERSHIP_AMOUNT", Integer.valueOf(paymentConfigurations.getMembershipAmount()));
					objArray.add(obj);
				}

			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getPaymentConfigDetialsForSelect");
		}

		return objArray;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public JSONObject getPaymentConfigDetials(String deptId, String category) {
		PaymentConfigurations paymentConfigurations = null;
		JSONObject obj = null;
		try {
			String query = " from PaymentConfigurations where paymentConfigurationsPK.category=:category  ";

			Map<String, Object> parametersMap = new HashMap<String, Object>();

			parametersMap.put("category", category);
			List<PaymentConfigurations> list = this.dataAccess.queryWithParams(query, parametersMap);
			if (list != null && list.size() > 0) {
				obj = new JSONObject();
				for (int j = 0; j < list.size(); j++) {
					paymentConfigurations = list.get(j);
					obj.put(paymentConfigurations.getPaymentConfigurationsPK().getDeptId(), paymentConfigurations);
				}

			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getPaymentConfigDetials");
		}

		return obj;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public JSONArray getUnsubsribedYears(String deptId, String cardNo) {
		JSONArray resultArray = new JSONArray();

		try {
			Registration registeredMember = getMemberDetailsByDeptIdAndCardNo(deptId, cardNo);
			int currentYear = Integer.parseInt((new SimpleDateFormat("yyyy")).format(new Date()));
			int joinedYear = Integer
					.parseInt((new SimpleDateFormat("yyyy")).format(registeredMember.getRegisteredDate()));
			List list = null;
			String query = "from SubscriptionPayments where memberId =:memberId  and paymentStatus=:paymentStatus and subscriptionPaymentsPK.subscriptionYear=:subscriptionYear  ";
			for (int i = joinedYear; i <= currentYear; i++) {
				Map<String, Object> parametersMap = new HashMap<String, Object>();
				parametersMap.put("memberId", registeredMember.getRegistrationPK().getMemberId());
				parametersMap.put("paymentStatus", "Yes");
				parametersMap.put("subscriptionYear", Integer.valueOf(i));
				list = this.dataAccess.queryWithParams(query, parametersMap);
				if (list == null || list.size() <= 0) {

					resultArray.add(Integer.valueOf(i));
				}
			}
		} catch (Exception e) {
			ApplicationUtilities.error(getClass(), e, "getUnsubsribedYears");
		}

		return resultArray;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public String isDuplicateReceiptNumberInSubscriptionPayments(String receiptNo, String memberId) {
		String result = "";

		try {
			String query = "from SubscriptionPayments where subscriptionPaymentsPK.receiptNo=:receiptNo and category='SUBSCRIPTION' and memberId!='"
					+ memberId + "'";
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			parametersMap.put("receiptNo", receiptNo);

			@SuppressWarnings("unchecked")
			List<SubscriptionPayments> list = this.dataAccess.queryWithParams(query, parametersMap);

			if (list != null && list.size() > 0) {
				SubscriptionPayments obj = list.get(0);
				parametersMap.put("memeber id found  >>> ", obj.getMemberId());

				result = obj.getMemberId();
			}

		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "isDuplicateReceiptNumberInSubscriptionPayments");
		}
		return result;
	}

	@Transactional
	public boolean isDuplicateReceiptNumberInSubscriptionPayments(String receiptNo) {
		boolean result = false;

		try {
			String query = "from SubscriptionPayments where subscriptionPaymentsPK.receiptNo=:receiptNo and category='SUBSCRIPTION' ";
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			parametersMap.put("receiptNo", receiptNo);

			List list = this.dataAccess.queryWithParams(query, parametersMap);

			if (list != null && list.size() > 0) {
				result = true;

			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "isDuplicateReceiptNumberInSubscriptionPayments");
		}
		return result;
	}

	@Transactional
	public boolean isDuplicateReceiptNumberInRegistration(String receiptNo) {
		boolean result = false;

		try {
			String query = "from Registration where receiptNo=:receiptNo  ";
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			parametersMap.put("receiptNo", receiptNo);
			List list = this.dataAccess.queryWithParams(query, parametersMap);
			if (list != null && list.size() > 0) {
				result = true;

			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "isDuplicateReceiptNumberInRegistration");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public SubscriptionPayments getSubscriptionByTransId(String transactionId) {
		SubscriptionPayments result = null;

		try {
			String query = "from SubscriptionPayments where transactionId=:transactionId  ";
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			parametersMap.put("transactionId", transactionId);

			List<SubscriptionPayments> list = this.dataAccess.queryWithParams(query, parametersMap);

			if (list != null && list.size() > 0) {
				result = list.get(0);

			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getSubscriptionByTransId");
		}
		return result;
	}


	@SuppressWarnings("unchecked")
	@Transactional
	public JSONObject getTopPanel(int cardNo, String deptId, String pageId) {
		JSONObject topPanelResultObj = new JSONObject();
		JSONObject queryDataObj = new JSONObject();
		logger.info("================================TOP PANEL========================================");
		logger.info("========================================================================");
		try {
			Registration registeredMember = getMemberDetailsByDeptIdAndCardNo(deptId, "" + cardNo);

			if (registeredMember != null) {
				@SuppressWarnings("unused")
				String fileName = String.valueOf(registeredMember.getFileName()) + "." + registeredMember.getFileType();
				queryDataObj.put("CARD_NO", Integer.valueOf(registeredMember.getRegistrationPK().getCardNo()));
				queryDataObj.put("DEPT_ID", registeredMember.getRegistrationPK().getDeptId());
				queryDataObj.put("MEMBER_ID", registeredMember.getRegistrationPK().getMemberId());
				queryDataObj.put("DEPT_NAME", registeredMember.getDeptName());
				queryDataObj.put("FIRST_NAME", registeredMember.getFirstName());
				queryDataObj.put("PAYMENT_RECEIPT_NO", registeredMember.getReceiptNo());
				queryDataObj.put("PERMINENT_ADDRESS", registeredMember.getPerminentAddress());

				queryDataObj.put("REGISTERED_DATE",
						(new SimpleDateFormat("dd/MM/yyyy")).format(registeredMember.getRegisteredDate()));
				queryDataObj.put("DATE_OF_BIRTH",
						(new SimpleDateFormat("dd/MM/yyyy")).format(registeredMember.getDateOfBirth()));

				
				PaymentConfigurations config =  getPaymentConfig(registeredMember.getPaymentConfId());
				
				List<MembershipPayments> payments = membershipDAO.getMembershipPaymentsByMemberId(registeredMember.getRegistrationPK().getMemberId());
				
				int paidCardAmount = 0;
				if(payments!=null && payments.size()>0) {				
					

					List<Integer> intlist =payments.stream().map(e->e.getPaidAmount()).collect(Collectors.toList());
					paidCardAmount = intlist.stream().collect(Collectors.summingInt(Integer::intValue));
				}
				
				int cardBalance = config.getMembershipAmount()-paidCardAmount;
				queryDataObj.put("CARD_BALANCE",cardBalance);
				queryDataObj.put("CURRENT_LOAN_BALANCE", registeredMember.getCurrentLoanBalance());
//				queryDataObj.put("CARD_BALANCE", );
//				queryDataObj.put("CURRENT_LOAN_BALANCE",);
				queryDataObj.put("PHONE_NO", registeredMember.getPhoneNo());
				queryDataObj.put("ALT_PHONE_NO", registeredMember.getAltPhoneNo());

				queryDataObj.put("FATHER_NAME", registeredMember.getFatherName());

				queryDataObj.put("FILE_CONTENT", this.utils.convertImageToBase64(registeredMember.getFileContent(),
						registeredMember.getFileType()));
				queryDataObj.put("FILE_TYPE", registeredMember.getFileType());
				topPanelResultObj.put("TOP_PANEL_RESULT_CODE", "400");
				topPanelResultObj.put("TOP_PANEL_DETAILS", queryDataObj);
			} else {

				topPanelResultObj.put("TOP_PANEL_RESULT_CODE", "200");
				topPanelResultObj.put("TOP_PANEL_ERROR_MSG",
						"No records found with DEPARTMENT=" + deptId + " and CARD NO =" + cardNo);
			}

			logger.info("===========================TOP PANEL============================================");
			logger.info("========================================================================");
		} catch (Exception e) {

			topPanelResultObj.put("TOP_PANEL_RESULT_CODE", "300");
			topPanelResultObj.put("TOP_PANEL_ERROR_MSG", e.getMessage());
			ApplicationUtilities.error(getClass(), e, "getTopPanel");
		}

		return topPanelResultObj;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public PaymentConfigurations getPaymentConfig(String configId) {
		PaymentConfigurations paymentConfigurations = null;

		try {
			String query = " from PaymentConfigurations where paymentConfigurationsPK.paymentConfId=:PAYMENT_CONF_ID and status=:status  ";
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			parametersMap.put("status", "ACTIVE");
			parametersMap.put("PAYMENT_CONF_ID", configId);


			List<PaymentConfigurations> list = this.dataAccess.queryWithParams(query, parametersMap);
			
			if(list!=null && list.size()>0) {
				paymentConfigurations = list.get(0);
			}

		
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getPaymentConfigDetialsForSelect");
		}

		return paymentConfigurations;
	}


	@SuppressWarnings({ "unchecked", "unused" })
	@Transactional
	public JSONObject getTopPanelDetails2(int cardNo, String deptId, String pageId) {
		Registration registeredMember = getMemberDetailsByDeptIdAndCardNo(deptId, "" + cardNo);

		JSONObject topPanelResultObj = new JSONObject();

		JSONObject queryDataObj = new JSONObject();
		try {
			if (registeredMember != null) {
				String memberId = registeredMember.getRegistrationPK().getMemberId();
				if (registeredMember != null && !"".equalsIgnoreCase(memberId)) {

					String query = " SELECT  C.FILE_NAME,  A.CARD_NO,  A.DEPT_ID,  A.MEMBER_ID,  A.DEPT_NAME,  A.FIRST_NAME,  A.RECEIPT_NO,  A.PERMINENT_ADDRESS,  A.REGISTERED_DATE,  A.DATE_OF_BIRTH,  A.CURRENT_LOAN_BALANCE ,  A.CARD_BALANCE,  A.PHONE_NO,  C.FILE_CONTENT,  C.FILE_TYPE , A.PAYMENT_CONF_ID,  A.TRANSACTION_ID  FROM    REGISTRATION A LEFT OUTER JOIN ATTACHMENTS C  ON A.MEMBER_ID=C.MEMBER_ID  WHERE  A.MEMBER_ID=:memberId AND C.ATTACHMENT_CATEGORY='PROFILE_PIC' ";

					query = "from Registration where registrationPK.cardNo=:cardNo and registrationPK.deptId=:deptId ";

					if (registeredMember != null) {
						String fileName = String.valueOf(registeredMember.getFileName()) + "."
								+ registeredMember.getFileType();
						queryDataObj.put("CARD_NO", Integer.valueOf(registeredMember.getRegistrationPK().getCardNo()));
						queryDataObj.put("DEPT_ID", registeredMember.getRegistrationPK().getDeptId());
						queryDataObj.put("MEMBER_ID", registeredMember.getRegistrationPK().getMemberId());
						queryDataObj.put("DEPT_NAME", registeredMember.getDeptName());
						queryDataObj.put("FIRST_NAME", registeredMember.getFirstName());
						queryDataObj.put("PAYMENT_RECEIPT_NO", registeredMember.getPaymentConfId());
						queryDataObj.put("PERMINENT_ADDRESS", registeredMember.getPerminentAddress());
						queryDataObj.put("REGISTERED_DATE",
								(new SimpleDateFormat("dd/MM/yyyy")).format(registeredMember.getRegisteredDate()));
						queryDataObj.put("DATE_OF_BIRTH",
								(new SimpleDateFormat("dd/MM/yyyy")).format(registeredMember.getDateOfBirth()));
						queryDataObj.put("CURRENT_LOAN_BALANCE", registeredMember.getCurrentLoanBalance());
						queryDataObj.put("CARD_BALANCE",
								Integer.valueOf(getCardBalance(memberId, deptId, registeredMember.getPaymentConfId(),
										registeredMember.getRegistrationPK().getTransactionId(), "REGISTRATION")));
						queryDataObj.put("LOAN_BALANCE", registeredMember.getCurrentLoanBalance());
						queryDataObj.put("PHONE_NO", registeredMember.getPhoneNo());
						this.utils.fileWriter(
								String.valueOf(this.miscContext.getContextPath()) + "\\AppFiles\\Downloads", fileName,
								registeredMember.getFileContent());
						queryDataObj.put("FILE_CONTENT", this.utils.convertImageToBase64(
								registeredMember.getFileContent(), registeredMember.getFileType()));
						queryDataObj.put("FILE_TYPE", registeredMember.getFileType());
						topPanelResultObj.put("TOP_PANEL_RESULT_CODE", "400");
						topPanelResultObj.put("TOP_PANEL_DETAILS", queryDataObj);
					} else {
						topPanelResultObj.put("TOP_PANEL_RESULT_CODE", "200");
						topPanelResultObj.put("TOP_PANEL_ERROR_MSG",
								"No records found with DEPARTMENT=" + deptId + " and CARD NO =" + cardNo);
					}

				} else {

					topPanelResultObj.put("TOP_PANEL_RESULT_CODE", "200");
					topPanelResultObj.put("TOP_PANEL_ERROR_MSG",
							"No records found with DEPARTMENT=" + deptId + " and CARD NO =" + cardNo);
				}

			} else {

				topPanelResultObj.put("TOP_PANEL_RESULT_CODE", "200");
				topPanelResultObj.put("TOP_PANEL_ERROR_MSG",
						"No records found with DEPARTMENT=" + deptId + " and CARD NO =" + cardNo);

			}

		} catch (Exception e) {

			topPanelResultObj.put("TOP_PANEL_RESULT_CODE", "300");
			topPanelResultObj.put("TOP_PANEL_ERROR_MSG", e.getMessage());
			ApplicationUtilities.error(getClass(), e, "getTopPanelDetails");
		}
		return topPanelResultObj;
	}

	@SuppressWarnings("unused")
	@Transactional
	public int getCardBalance(String memberId, String deptId, String paymentConfId, String transactionId,
			String category) {
		int cardBalance = 0;

		try {
			PaymentConfigurations paymentConfigurations = getPaymentConfigurationsDetails(paymentConfId, deptId,
					category);
			if (paymentConfigurations != null) {
				int finalMemberShipAmmount = 0;
				int adminFeeAmount = getAdminFeePaidByMemberIdAndTransId(memberId, transactionId, category);
				int subscripionAmount = getSubscriptionAmountPaidByMemberIdAndTransId(memberId, transactionId,
						category);
				int memberShipAmmount = getMebershipAmountPaidByMemberIdAndTransId(memberId);
				if (adminFeeAmount > -1 && subscripionAmount > -1 && memberShipAmmount > -1) {
					cardBalance = adminFeeAmount + subscripionAmount + memberShipAmmount;
				}
			}

		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getCardBalance");
		}
		return cardBalance;
	}

	@Transactional
	public int caluclateCardBalance(String memberId) {
		int cardBalance = 0;
		int paidAmount = 0;
		int cardAmount = 0;
		try {
			if (memberId != null && !"".equalsIgnoreCase(memberId)) {
				Registration registeredMember = getMemberDetailsByMemberId(memberId);
				if (registeredMember != null) {
					PaymentConfigurations paymentConfigurations = getPaymentConfigurationsDetailsById(
							registeredMember.getPaymentConfId());
					if (paymentConfigurations != null) {
						cardAmount = paymentConfigurations.getMembershipAmount();
						List<MembershipPayments> list = this.membershipDAO.getMembershipPaymentsByMemberId(memberId);
						if (list != null && list.size() > 0) {
							for (int i = 0; i < list.size(); i++) {
								MembershipPayments membershipPayments = list.get(i);
								paidAmount += membershipPayments.getPaidAmount();
							}
						}

						cardBalance = cardAmount - paidAmount;
					}

				}

			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "caluclateCardBalance");
		}

		return cardBalance;
	}

	@Transactional
	public int caluclateCardBalance(Registration registeredMember) {
		int cardBalance = 0;
		int paidAmount = 0;
		int cardAmount = 0;

		try {
			if (registeredMember != null) {
				PaymentConfigurations paymentConfigurations = getPaymentConfigurationsDetailsById(
						registeredMember.getPaymentConfId());
				if (paymentConfigurations != null) {
					cardAmount = paymentConfigurations.getMembershipAmount();
					List<MembershipPayments> list = this.membershipDAO
							.getMembershipPaymentsByMemberId(registeredMember.getRegistrationPK().getMemberId());
					if (list != null && list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							MembershipPayments membershipPayments = list.get(i);
							paidAmount += membershipPayments.getPaidAmount();
						}
					}

					cardBalance = cardAmount - paidAmount;

				}

			}

		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "caluclateCardBalance");
		}

		return cardBalance;
	}

	@Transactional
	public void updateCardBalance(String memberId) {
		try {
			if (memberId != null && !"".equalsIgnoreCase(memberId)) {
				Registration registeredMember = getMemberDetailsByMemberId(memberId);
				registeredMember.setCardBalance(caluclateCardBalance(registeredMember));
				this.dataAccess.update(registeredMember);
			}

		} catch (Exception e) {
			ApplicationUtilities.error(getClass(), e, "updateCardBalance");
		}
	}

//	@Transactional
//	public int caluclateLoanBalance(Registration registeredMember) {
//		int loanBalance = 0;
//		int sanctionLaonAmount = 0;
//		int paidLoanAmount = 0;
//		try {
//			if (registeredMember != null) {
//				Loandetails loandetails = getCurrentLoanDetails(registeredMember.getRegistrationPK().getMemberId(),
//						registeredMember.getCurrentLoanId());
//
//				if (loandetails != null) {
//					sanctionLaonAmount = getTotalLoanAmount(registeredMember.getRegistrationPK().getMemberId());
//					paidLoanAmount = getPaidLoanAmount(registeredMember.getRegistrationPK().getMemberId());
//					loanBalance = sanctionLaonAmount - paidLoanAmount;
//				}
//
//			}
//		} catch (Exception e) {
//
//			ApplicationUtilities.error(getClass(), e, "caluclateLoanBalance");
//		}
//
//		return loanBalance;
//	}

//	@Transactional
//	public int caluclateLoanBalance(String memberId) {
//		int loanBalance = 0;
//		int sanctionLaonAmount = 0;
//		int paidLoanAmount = 0;
//
//		try {
//			sanctionLaonAmount = getTotalLoanAmount(memberId);
//			if (sanctionLaonAmount > 0) {
//				paidLoanAmount = getPaidLoanAmount(memberId);
//				loanBalance = sanctionLaonAmount - paidLoanAmount;
//			} else {
//
//				return loanBalance;
//			}
//
//		} catch (Exception e) {
//			ApplicationUtilities.error(getClass(), e, "caluclateLoanBalance");
//		}
//
//		return loanBalance;
//	}



	@SuppressWarnings("unchecked")
	@Transactional
	public Loandetails getCurrentLoanDetails(String memberId, String loanId) {
		Loandetails loanDetails = null;

		try {
			if (loanId != null && memberId != null && !"".equalsIgnoreCase(loanId) && !"".equalsIgnoreCase(memberId)) {
				String query = "from Loandetails where loandetailsPK.memberId=:memberId and   loandetailsPK.loanId=:loanId";
				Map<String, Object> parametersMap = new HashMap<String, Object>();
				parametersMap.put("memberId", memberId);
				parametersMap.put("loanId", loanId);

				List<Loandetails> list = this.dataAccess.queryWithParams(query, parametersMap);
				if (list != null && list.size() > 0) {
					loanDetails = list.get(0);
				}
			}

		} catch (Exception e) {
			ApplicationUtilities.error(getClass(), e, "getCurrentLoanDetails");
		}
		return loanDetails;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public int getPaidLoanAmount(String memberId, String loanId) {
		int paidLoanAmount = 0;

		try {
			if (loanId != null && memberId != null && !"".equalsIgnoreCase(loanId) && !"".equalsIgnoreCase(memberId)) {

				String query = "from LoanRecoveryDetails where loanRecoveryDetailsPK.memberId=:memberId and   loanRecoveryDetailsPK.loanId=:loanId";
				Map<String, Object> parametersMap = new HashMap<String, Object>();
				parametersMap.put("memberId", memberId);

				parametersMap.put("loanId", loanId);
				List<LoanRecoveryDetails> list = this.dataAccess.queryWithParams(query, parametersMap);
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						LoanRecoveryDetails obj = list.get(i);
						paidLoanAmount += obj.getPaidAmount();
					}

				}
			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getPaidLoanAmount");
		}
		return paidLoanAmount;
	}

//	@SuppressWarnings("unchecked")
//	@Transactional
//	public int getPaidLoanAmount(String memberId) {
//		int paidLoanAmount = 0;
//
//		try {
//			if (memberId != null && !"".equalsIgnoreCase(memberId)) {
//
//				String query = "from LoanRecoveryDetails where loanRecoveryDetailsPK.memberId=:memberId ";
//				Map<String, Object> parametersMap = new HashMap<String, Object>();
//				parametersMap.put("memberId", memberId);
//
//				List<LoanRecoveryDetails> list = this.dataAccess.queryWithParams(query, parametersMap);
//				if (list != null && list.size() > 0) {
//					for (int i = 0; i < list.size(); i++) {
//						LoanRecoveryDetails obj = list.get(i);
//						paidLoanAmount += obj.getPaidAmount();
//					}
//
//				}
//			}
//		} catch (Exception e) {
//
//			ApplicationUtilities.error(getClass(), e, "getPaidLoanAmount");
//		}
//		return paidLoanAmount;
//	}

	@SuppressWarnings("unchecked")
	@Transactional
	public int getAdminFeePaidByMemberIdAndTransId(String memberId, String transactionId, String category) {
		int paidPdminFee = -1;

		try {
			if (transactionId != null && memberId != null && category != null && !"".equalsIgnoreCase(transactionId)
					&& !"".equalsIgnoreCase(memberId) && !"".equalsIgnoreCase(category)) {

				String query = "from AdminfeePayments where adminfeePaymentsPK.memberId=:memberId and   adminfeePaymentsPK.transactionId=:transactionId and category=:category";
				Map<String, Object> parametersMap = new HashMap<String, Object>();
				parametersMap.put("memberId", memberId);
				parametersMap.put("transactionId", transactionId);
				parametersMap.put("category", category);
				List<AdminfeePayments> list = this.dataAccess.sqlqueryWithParams(query, parametersMap);

				if (list != null && list.size() > 0) {
					paidPdminFee = 0;
					for (int i = 0; i < list.size(); i++) {
						AdminfeePayments obj = list.get(i);
						paidPdminFee += obj.getAdminFeeAmount();
					}

				}

			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getAdminFeePaidByMemberIdAndTransId");
		}
		return paidPdminFee;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public int getSubscriptionAmountPaidByMemberIdAndTransId(String memberId, String transactionId, String category) {
		int paidSubscriptionAmount = -1;

		try {
			if (transactionId != null && memberId != null && category != null && !"".equalsIgnoreCase(transactionId)
					&& !"".equalsIgnoreCase(memberId) && !"".equalsIgnoreCase(category)) {

				String query = "from SubscriptionPayments where subscriptionPaymentsPK.memberId=:memberId and   subscriptionPaymentsPK.transactionId=:transactionId and category=:category";
				Map<String, Object> parametersMap = new HashMap<String, Object>();
				parametersMap.put("memberId", memberId);
				parametersMap.put("transactionId", transactionId);
				parametersMap.put("category", category);
				List<SubscriptionPayments> list = this.dataAccess.sqlqueryWithParams(query, parametersMap);
				if (list != null && list.size() > 0) {
					paidSubscriptionAmount = 0;
					for (int i = 0; i < list.size(); i++) {
						SubscriptionPayments obj = list.get(i);
						paidSubscriptionAmount += obj.getSubscriptionAmount();
					}

				}

			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getSubscriptionAmountPaidByMemberIdAndTransId");
		}
		return paidSubscriptionAmount;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public int getMebershipAmountPaidByMemberIdAndTransId(String memberId) {
		int paidMebershiAmount = -1;

		try {
			if (memberId != null && !"".equalsIgnoreCase(memberId)) {
				String query = "from MembershipPayments where membershipPaymentsPK.memberId=:memberId ";
				Map<String, Object> parametersMap = new HashMap<String, Object>();
				parametersMap.put("memberId", memberId);
				List<MembershipPayments> list = this.dataAccess.sqlqueryWithParams(query, parametersMap);
				if (list != null && list.size() > 0) {
					paidMebershiAmount = 0;
					for (int i = 0; i < list.size(); i++) {
						MembershipPayments obj = list.get(i);
						paidMebershiAmount += obj.getPaidAmount();
					}

				}

			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getMebershipAmountPaidByMemberIdAndTransId");
		}
		return paidMebershiAmount;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Registration getMemberDetailsByMemberId(String memberId) {
		Registration registration = null;

		try {
			String getRegistrationQuery = "from Registration where registrationPK.memberId =:memberId ";
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			parametersMap.put("memberId", memberId);

			List<Registration> list = this.dataAccess.queryWithParams(getRegistrationQuery, parametersMap);
			if (list != null && !list.isEmpty() && list.size() > 0) {
				registration = list.get(0);
			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getMemberDetailsByMemberId");
		}
		return registration;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public PaymentConfigurations getPaymentConfigurationsDetails(String paymentConfId, String deptId, String category) {
		PaymentConfigurations paymentConfigurations = null;

		try {
			if (paymentConfId != null && deptId != null && category != null && !"".equalsIgnoreCase(paymentConfId)
					&& !"".equalsIgnoreCase(deptId) && !"".equalsIgnoreCase(category)) {
				String query = "from PaymentConfigurations where paymentConfigurationsPK.paymentConfId=:paymentConfId and paymentConfigurationsPK.deptId=:deptId and paymentConfigurationsPK.category=:category ";
				Map<String, Object> parametersMap = new HashMap<String, Object>();
				parametersMap.put("paymentConfId", paymentConfId);
				parametersMap.put("deptId", deptId);
				parametersMap.put("category", category);
				List<PaymentConfigurations> list = this.dataAccess.sqlqueryWithParams(query, parametersMap);

				if (list != null && list.size() > 0) {
					paymentConfigurations = list.get(0);
				}
			}

		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getPaymentConfigurationsDetails");
		}
		return paymentConfigurations;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public PaymentConfigurations getPaymentConfigurationsDetailsById(String paymentConfId) {
		PaymentConfigurations paymentConfigurations = null;

		try {
			if (paymentConfId != null && !"".equalsIgnoreCase(paymentConfId)) {
				String query = "from PaymentConfigurations where paymentConfigurationsPK.paymentConfId=:paymentConfId ";
				Map<String, Object> parametersMap = new HashMap<String, Object>();
				parametersMap.put("paymentConfId", paymentConfId);
				List<PaymentConfigurations> list = this.dataAccess.queryWithParams(query, parametersMap);
				if (list != null && list.size() > 0) {
					paymentConfigurations = list.get(0);
				}
			}

		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getPaymentConfigurationsDetailsById");
		}
		return paymentConfigurations;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Registration getMemberDetailsByDeptIdAndCardNo(String deptId, String cardNo) {
		Registration registration = null;

		try {
			String getRegistrationQuery = "from Registration where registrationPK.cardNo =:cardNo and  registrationPK.deptId=:deptId ";
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			parametersMap.put("deptId", deptId);
			parametersMap.put("cardNo", Integer.valueOf(Integer.parseInt(cardNo)));
			List<Registration> list = this.dataAccess.queryWithParams(getRegistrationQuery, parametersMap);

			if (list != null && !list.isEmpty() && list.size() > 0) {
				registration = list.get(0);

			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getMemberDetailsByDeptIdAndCardNo");
		}
		return registration;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public int getTotalLoanAmount(String memberId) {
		int totalLoanAmount = 0;

		try {
			String getTotalLoanAmount = "from Loandetails where loandetailsPK.memberId =:memberId and loanStatus=:LOAN_STATUS";
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			parametersMap.put("memberId", memberId);
			parametersMap.put("LOAN_STATUS", "LOAN_UNDER_RECOVERY");
			List<Loandetails> list = this.dataAccess.queryWithParams(getTotalLoanAmount, parametersMap);

			if (list != null && list.size() > 0) {
				for (int j = 0; j < list.size(); j++) {
					Loandetails loandetails = list.get(j);
					totalLoanAmount += loandetails.getLoanAmount();
				}

			}
		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getTotalLoanAmount");
		}
		return totalLoanAmount;
	}

	
	@SuppressWarnings("unchecked")
	@Transactional
	public int getTotalLoanAmountNew(String memberId) {
		int totalLoanAmount = 0;

		try {
			String getTotalLoanAmount = "from Loandetails where loandetailsPK.memberId =:memberId";
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			parametersMap.put("memberId", memberId);
	
			List<Loandetails> list = this.dataAccess.queryWithParams(getTotalLoanAmount, parametersMap);			
			
			List<Integer> intlist =list.stream().map(e->e.getLoanAmount()).collect(Collectors.toList());
			totalLoanAmount = intlist.stream().collect(Collectors.summingInt(Integer::intValue));

		} catch (Exception e) {

			ApplicationUtilities.error(getClass(), e, "getTotalLoanAmount");
		}
		return totalLoanAmount;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public int getLoanDetails(String memberId) {
		int totalLoanAmount = 0;

		try {
			String getTotalLoanAmount = "from Loandetails where loandetailsPK.memberId =:memberId and loanStatus=:LOAN_STATUS";
			Map<String, Object> parametersMap = new HashMap<String, Object>();
			parametersMap.put("memberId", memberId);
			parametersMap.put("LOAN_STATUS", "LOAN_UNDER_RECOVERY");
			List<Loandetails> list = this.dataAccess.queryWithParams(getTotalLoanAmount, parametersMap);

			if (list != null && list.size() > 0) {
				for (int j = 0; j < list.size(); j++) {
					Loandetails loandetails = list.get(j);
					totalLoanAmount += loandetails.getLoanAmount();
				}

			}
		} catch (Exception e) {

			ApplicationUtilities.error(this.getClass(),e.getMessage(),e);
		}
		return totalLoanAmount;
	}

}
