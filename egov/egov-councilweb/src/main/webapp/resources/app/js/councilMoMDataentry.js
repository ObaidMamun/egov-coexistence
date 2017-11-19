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

String.prototype.compose = (function (){
	   var re = /\{{(.+?)\}}/g;
	   return function (o){
	       return this.replace(re, function (_, k){
	           return typeof o[k] != 'undefined' ? o[k] : '';
	       });
	   }
}());

var emptyRow = '<tr><td colspan="9" id="emptyRow" class="text-center">No preamble item available</td></tr>';
var tbody = $('#preambleTable').children('tbody');
//var table = tbody.find('tr').length == 0 ? $('#preambleTable tbody').append(emptyRow) : $('#preambleTable');
var table = tbody.length ? tbody : $('#preambleTable');



var row = '<tr>'+
'<td><input type="text" class="form-control text-left patternvalidation" required="required" name="meeting.meetingMOMs[{{idx}}].itemNumber" {{readonly}}  value="{{itemNumberTextBoxValue}}"/></td>'+
 '<td><select name="meeting.meetingMOMs[{{idx}}].preamble.department" class="form-control" required="required" > <option value="" >Loading...</option></select></td>'+
 '<td><input type="text" class="form-control text-left patternvalidation numberval" required="required" name="meeting.meetingMOMs[{{idx}}].preamble.preambleNumber" id="meetingMOMs[{{idx}}].preambleNumber" {{readonly}}  value="{{preamblenumberTextBoxValue}}"/></td>'+
 '<td><div class="input-group"><textarea class="form-control textarea-content" required="required" name="meeting.meetingMOMs[{{idx}}].preamble.gistOfPreamble" maxlength="10000"  value="{{gistTextBoxValue}}" /><span class="input-group-addon" id="showModal" data-header="Preamble - GIST of Preamble"><span class="glyphicon glyphicon-pencil" style="cursor:pointer"></span></span></div></td>'+
 '<td><input type="text" class="form-control text-left patternvalidation validnum" required="required" name="meeting.meetingMOMs[{{idx}}].resolutionNumber" {{readonly}}  value="{{resolutionNumberTextBoxValue}}"/></td>'+
 '<td><div class="input-group"><textarea class="form-control textarea-content addorremoverequired" required="required" name="meeting.meetingMOMs[{{idx}}].resolutionDetail" maxlength="5000" value="{{gistTextBoxValue}}" /><span class="input-group-addon" id="showModal" data-header="Preamble Resolution - Resolution comments"><span class="glyphicon glyphicon-pencil" style="cursor:pointer"></span></span></div></td>'+
 '<td><input type="text" class="form-control text-left patternvalidation text-right" name="meeting.meetingMOMs[{{idx}}].preamble.sanctionAmount" {{readonly}} data-pattern="number" value="{{amountTextBoxValue}}"/></td>'+
 '<td><select name="meeting.meetingMOMs[{{idx}}].resolutionStatus" class="form-control addorremoverequired" required="required"><option value="">Loading...</option></select></td>'+
 '<td></a>&nbsp;<button type="button" class="fa fa-trash-o delete" style="font-size:20px;color:red" ></button></td>'+
 '</tr>';

jQuery('#add-preamble').click(function(){
	$('.agenda-section').show();
	
	var idx=$(tbody).find('tr').length;
	var sno=idx+1;
	
//Add row
	var row={
	       'sno': sno,
	       'idx': idx
	   };
	addRowFromObject(row);
	loadDepartmentlist("meeting.meetingMOMs["+idx+"].preamble.department");
	loadResolutionlist("meeting.meetingMOMs["+idx+"].resolutionStatus");
});

$("#preambleTable tbody").on('click','tr td .delete',function(event) {
	$(this).closest('tr').remove();
	regenerateIndexes();
	/*var idx  = $("#preambleTable > tbody").children().length;
	if(idx == 0){
		$('#preambleTable tbody').append(emptyRow);
	}*/
});

$("#preambleTable tbody").on('blur','tr .numberval',function(event) {
	validatePreambleNumber($(this));
});

$("#preambleTable tbody").on('blur','tr .validnum',function(event) {
	validateResolutionNumber($(this));
});

$('#buttonSubmit').click(function(e) {
	if($('#emptyRow').length){
		bootbox.alert("Atleast one preamble item should be added into agenda");
		e.preventDefault();
	}
	else if ($('form').valid()) {
		 var action = '/council/councilmom/savedataentry' ;
			$('#councilMeetingform').attr('method', 'post');
			$('#councilMeetingform').attr('action', action); 
	} else {
		e.preventDefault();
	}
});	 

$('#meetingNumber').blur(function(){
	validateMeetingNumber();
});


function validateMeetingNumber(){
	var meetingNumber=$('#meetingNumber').val();
	if(meetingNumber != '') {
		$.ajax({
			url: "/council/councilmom/checkUnique-MeetingNo",      
			type: "GET",
			data: {
				meetingNumber : meetingNumber,  
			},
			dataType: "json",
			success: function (response) { 
				if(!response) {
						$('#meetingNumber').val('');
						bootbox.alert("Entered Meeting Number already exists. Please Enter Unique Number.");
				}
			}, 
			error: function (response) {
				$('#meetingNumber').val('');
				bootbox.alert("connection validation failed");
			}
		});
	}	
}

function validatePreambleNumber(preambleNumber){
	var preambleNo= preambleNumber.val()
	if(preambleNo != '') {
		$.ajax({
			url: "/council/councilmom/checkUnique-preambleNo",      
			type: "GET",
			data: {
				preambleNumber : preambleNo, 
			},
			dataType: "json",
			success: function (response) { 
				if(!response) {
						$(preambleNumber).val('');
						bootbox.alert("Entered Preamble Number already exists. Please Enter Unique Number.");
				}
			}, 
			error: function (response) {
				$(preambleNumber).val('');
				bootbox.alert("connection validation failed");
			}
		});
	}	
}

function validateResolutionNumber(resolutionNumber){
	var resolutionNo = resolutionNumber.val();
	if(resolutionNo != '') {
		$.ajax({
			url: "/council/councilmom/checkUnique-resolutionNo",      
			type: "GET",
			data: {
				resolutionNumber : resolutionNo, 
			},
			dataType: "json",
			success: function (response) { 
				if(!response) {
						$(resolutionNumber).val('');
						bootbox.alert("Entered Resolution Number already exists. Please Enter Unique Number.");
				}
			}, 
			error: function (response) {
				$(resolutionNumber).val('');
				bootbox.alert("connection validation failed");
			}
		});
	}	
}

function regenerateIndexes()
{
	var idx=0;
	$(tbody).find("tr").each(function() {
		
		($(this).find("input,select,textarea")).each(function() {
			
			console.log('BEFORE INPUT ITERATION!', $(this)[0].outerHTML);
			
			   $(this).attr({
			      'name': function(_, name) {
			    	  console.log('name', name);
			    	  return name.replace(/\[.\]/g, '['+ idx +']'); 
			      }
			   });
			   console.log('AFTER INPUT ITERATION!', $(this)[0].outerHTML);
	    });
		idx++;
	});
	generateOrderNo();
}

function generateOrderNo(){
	$('span.sno').each(function(i){
		  $(this).html((i+1));    
	});
	
	$('input[data-sno]').each(function(i){
		$(this).val(i+1);;
	});
}


//ajax call 
function loadDepartmentlist(selectBoxName){

 $.ajax({
		url: "/council/councilmom/departmentlist",     
		type: "GET",
		async: false,
		dataType: "json",
		success: function (response) {
			$('select[name="'+selectBoxName+'"]').empty();
			$('select[name="'+selectBoxName+'"]').append($("<option value=''>Select </option>"));
			$.each(response.departmentLists, function(index, departmentLists) {
				$('select[name="'+selectBoxName+'"]').append($('<option>').val(departmentLists.id).text(departmentLists.name));
			});
		}, 
		error: function (response) {
		}
	});
}

function validateAgengaDetails(){
    
    var isValid=true;
    $('#preambleTable tbody tr').each(function(index){
        var itemNumber  = $(this).find('*[name$="itemNumber"]').val();
        var department = $(this).find('*[name$="department"]').val();
        var preambleNumber = $(this).find('*[name$="preambleNumber"]').val(); 
        var gistOfPreamble = $(this).find('*[name$="gistOfPreamble"]').val(); 
        var resolutionNumber = $(this).find('*[name$="resolutionNumber"]').val(); 
        var resolutionDetail = $(this).find('*[name$="resolutionDetail"]').val(); 
        var resolutionStatus = $(this).find('*[name$="resolutionStatus"]').val(); 
        if(!itemNumber || !department || !preambleNumber || !gistOfPreamble || !resolutionNumber || !resolutionDetail || !resolutionStatus) { 
            bootbox.alert("Enter all values for existing rows before adding. Values cannot be empty.");
            isValid=false;
            return false;
        } 
    });
   
    return isValid;
}

function loadResolutionlist(selectBoxNameResolution){

	 $.ajax({
			url: "/council/councilmom/resolutionlist",     
			type: "GET",
			async: false,
			dataType: "json",
			success: function (response) {
				$('select[name="'+selectBoxNameResolution+'"]').empty();
				$('select[name="'+selectBoxNameResolution+'"]').append($("<option value=''>Select </option>"));
				$.each(response.resolutionLists, function(index, resolutionLists) {
					$('select[name="'+selectBoxNameResolution+'"]').append($('<option>').val(resolutionLists.id).text(resolutionLists.code));
				});
			}, 
			error: function (response) {
			}
		});
	}

function addRowFromObject(rowJsonObj)
{
	$('#emptyRow').closest('tr').remove();
	if(validateAgengaDetails())
	table.append(row.compose(rowJsonObj));
}

$(document).ready(function() {
	    
    var table_rowindex = 0;
    var tablecolumn_index = 0;
    var tableheaderid ='';
    var tableid='';
    
    $(document).on('blur','.textarea-content', function(evt) {
    	$(this).tooltip('hide')
        .attr('data-original-title', $(this).val());
    	evt.stopImmediatePropagation();
    });
    
    $(document).on('click','#showModal',function(evt){
    	table_rowindex = $(this).closest('tr').index();
    	tablecolumn_index = $(this).closest('td').index();
    	tableheaderid = $(this).data('header');
    	tableid = $(this).closest('table').attr('id');
    	$('#textarea-header').html(tableheaderid);
    	$('#textarea-updatedcontent').val($(this).parent().parent().find('textarea').val());
    	$("#textarea-modal").modal('show');
    	evt.stopImmediatePropagation();
    });
    
    //update textarea content in table wrt index
    $(document).on('click','#textarea-btnupdate',function(evt){
    	$('#'+tableid +' tbody tr:eq(' + table_rowindex + ') td:eq('+tablecolumn_index+')').find('textarea').val($('#textarea-updatedcontent').val()).tooltip('hide')
        .attr('data-original-title', $('#textarea-updatedcontent').val());
    	evt.stopImmediatePropagation();
    });

});