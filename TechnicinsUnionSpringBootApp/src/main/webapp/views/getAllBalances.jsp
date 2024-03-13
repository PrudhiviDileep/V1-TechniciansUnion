<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>All Balances Information</title>
</head>
<body>
	<div class="container">
		<button onclick="topFunction()" id="myBtn" title="Go to top">Top</button>
		<jsp:include page="header.jsp" />
		<jsp:include page="navigation.jsp" />
		<div class='mainbody'>
			<input type="hidden" name="memberId" value='' id="MEMBER_ID">
			<div class="form-wrap">
				<div id="commonSearchBar">
					<fieldset>
						<legend>
							<h4>Search Member</h4>
						</legend>
						<table>
							<tr>
								<td align="right" style="width: 45% !important"><select
									class='select_style' id='commonSeachDeptSelectId'
									title="Select Department"
									onchange="commonSearchGetCardNumbersByDeptId()"
									name='cmnSearchBarDeptId' style="width: 100% !important">${DEPARTMENTS}</select>
								</td>
								<td align="right" style="width: 45% !important"><input
									type="text" class="searchCardNo" name='cmnSearchBarcardNo'
									id='commonSearchCardNo' placeholder="Enter Card No.."
									title="Type in a name" disabled='true'
									style="width: 100% !important"></td>
								<td align="left" style="width: 10%"><button
										class="w3-button w3-white w3-border w3-border-blue"
										style="margin: 5px; background: #4fcdff !important; border-radius: 5px; color: #FFF !important; display: block; padding: 10px 18px; margin: 20px auto; color: #FFF; background-color: #555; background: -webkit-linear-gradient(#888, #555); background: linear-gradient(#888, #555); border: 0 none; border-radius: 3px; font-weight: bold; cursor: pointer;"
										id="commonSearchBtn" onclick="getAllBalanceDetails()">Search</button></td>
							</tr>
						</table>
					</fieldset>
				</div>
				<jsp:include page="topPanel.jsp" />
				<div id="membershipDeatilsHistory">
					<fieldset>
						<legend>
							<h4>Membership Amount Payment History</h4>
						</legend>
						<div id="MembershipAmountPaymentHistory_SearchDetails"></div>
					</fieldset>
				</div>
			
			<div calss="" style="width: 100%; height: auto; margin: 0 auto;">
				<fieldset>
					<legend>
						<h4>Loan Sanctioned Details</h4>
					</legend>
					<div id="LoanSanctioned_SearchDetails"></div>
				</fieldset>
				<fieldset>
					<legend>
						<h4>Loan Recovery Details</h4>
					</legend>
					<div id="LoanRecovery_SearchDetails"></div>
				</fieldset>
				<fieldset>
					<legend>
						<h4>Subscripton Details</h4>
					</legend>
					<div id="Subscription_SearchDetails"></div>
				</fieldset>

			</div>
		</div>
	</div></div>
	
	<jsp:include page="footer.jsp" />
	<div id='logoutDailog'></div>
</body>
</html>