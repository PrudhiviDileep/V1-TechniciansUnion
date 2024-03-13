<!DOCTYPE html>
<html>
<head>
<%String context=request.getContextPath(); %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Cards Summary</title>
    <link rel="stylesheet" href="<%=context %>/css/jquery-ui.css">
<link rel="stylesheet" href="<%=context %>/css/main.css">
<link rel="stylesheet" type="text/css" href="<%=context %>/js/jsgrid/css/jsgrid.css" /> 
<link rel="stylesheet" type="text/css" href="<%=context %>/js/jsgrid/css/theme.css" />
  <link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
 <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
  <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script> 



<style>
.saveButn {
	margin: 5px;
	background: #4fcdff !important;
	border-radius: 5px;
	color: #FFF !important;
	padding: 5px 9px;
	margin: 0px auto;
	color: #FFF;
	background-color: #555;
	background: -webkit-linear-gradient(#888, #555);
	background: linear-gradient(#888, #555);
	border: 0 none;
	border-radius: 3px;
	cursor: pointer !important;
	/*font-weight: bold;*/
}

.external-pager {
	margin: 10px 0;
}

.external-pager .jsgrid-pager-current-page {
	background: #c4e2ff;
	color: #fff;
}

.config-panel {
	padding: 10px;
	margin: 10px 0;
	background: #fcfcfc;
	border: 1px solid #e9e9e9;
	display: inline-block;
}

.config-panel label {
	margin-right: 10px;
}
</style>
<script> var context="<%=context %>"; 
$(document).ajaxStart(function() {
    // show loader on start
    $("#loader").css("display","block");
}).ajaxSuccess(function() {
    // hide loader on success
    $("#loader").css("display","none");
});
$(document).ready(function() {

	var today = new Date();
	var dateInReqFormat = today.getDate()+'-'+(today.getMonth()+1)+'-'+today.getFullYear();
	  $("#FromDateId").datepicker({dateFormat:"dd-mm-yy"}).val(dateInReqFormat)
	  $("#ToDateId").datepicker({dateFormat:"dd-mm-yy"}).val(dateInReqFormat)

});
</script>

</head>
<body>
<button onclick="topFunction()" id="myBtn" title="Go to top">Top</button>
<div id="loader"></div>
<jsp:include page="header.jsp" />
<script type="text/javascript">

 function getCardYearlySummary(){
	
	var deptId = $("#DEPARTMENT option:selected" ).text();
 
	var fromDateCnvrted = $.datepicker.formatDate('yy-mm-dd', $('#FromDateId').datepicker('getDate'));
	

	var toDateCnvrted =  $.datepicker.formatDate('yy-mm-dd', $('#ToDateId').datepicker('getDate'));
	
	
	
	if(fromDateCnvrted === null || fromDateCnvrted===''){
		alert('Please select From date.')
		return false;
	}
	if(toDateCnvrted === null || toDateCnvrted===''){
		alert('Please select To date.')
		return false;
	}
	
	
	if (Date.parse(fromDateCnvrted) > Date.parse(toDateCnvrted)) {
		alert("Invalid Date Range! From Date cannot be after To Date!")
		return false;
		}
	
	
	var orderBy = $("#ORDER_BY option:selected" ).val();
	var sort = $("#SORT option:selected" ).val();


	$.ajax({
		type : "POST",
		url : 'getSummary',
		data : {
			deptId : deptId,
			action : 'CARD_BALANCE_SUMMARY',			
			fromDate:	fromDateCnvrted,
			toDate:toDateCnvrted,
			orderBy:orderBy,
			sortBy:sort

		},
		traditional : true,
		cache : false,
		// async: false,
		success : function(response) {
			$('#CARD_BALANCE_SUMMARY').empty();
			$('#CARD_BALANCE_SUMMARY').append(response);

		},
		error : function(response) {
		}
	});
	
	
	
} 


</script>
<jsp:include page="navigation.jsp" />
<fieldset>
<legend><H4>Filters</H4></legend>

<table style="width: 75%;margin:0 auto;"><tr>
<td>Department</td><td>
<select onchange="" id="DEPARTMENT">
<option value='ALL'>ALL</option>
<option value='CAMERA'>CAMERA</option>
<option value='ELECTRICIAN'>ELECTRICIAN</option>
<option value='SOUND'>SOUND</option>
<option value='UNIT_DRIVER'>UNIT_DRIVER</option>
</select>
</td>
<td>From Date</td><td><input type="text" name="FromDate" id="FromDateId" ></td>
<td>To Dater</td><td><input type="text" name="ToDate" id="ToDateId" ></td>
<td>Order By</td><td>
<select onchange="" id="ORDER_BY">
<option value='CARD_NO'>Card No</option>
<option value='DEPT_ID'>Department</option>
<option value='FIRST_NAME'>Name</option>
<option value='paid_date'>Paid Date</option>
<option value='REGISTERED_DATE'>Registered Date</option></select>
</td>
<td>Sort</td><td><select onchange="" id="SORT"><option id='ASC'>ASC</option><option id='DSC'>DESC</option></select></td>
<td><input type="submit" name="Submit" value="Submit" onclick="getCardYearlySummary()"></td>
</tr>
</table>

</fieldset>
<fieldset>
<legend><H4>Results</H4></legend>
<div id="CARD_BALANCE_SUMMARY" style="width: 97%;margin:0 auto;"></div>
</fieldset>
<jsp:include page="footer.jsp" />

<div id='logoutDailog'>
</div>
</body>
</html>