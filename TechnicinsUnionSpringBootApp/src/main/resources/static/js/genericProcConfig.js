/**
 * 
 */
 var gridConfig=[];
 
  //CREATE INITIATE GRID
  
  function initializeGrid(gridDivId,gridId){
  				gridConfig.height='100';
  				gridConfig=generateGridConfigObj(gridId);
  				
  				if('GENERIC_PROC_CONFIG_GRID'==gridId){

  				gridConfig.rowClick=addRowSelectEvent();
  				gridConfig.onItemUpdating= function(args) {
  				console.log(args);
  				}
  				
  				}
  				gridConfig.paging=true;
  				gridConfig.pageSize=10;
  				
			 	gridConfig.controller= {
						                loadData: function(filter) {  	return loadGeneircGridData(gridDivId,gridConfig.geturl,filter,gridId,gridConfig.fetch_proc_id);},
						                insertItem: function(item) { 	item.PROC_ID=gridConfig.insert_proc_id; genericGridSaveUpdateDelete(gridDivId,gridId,gridConfig.inserturl,item);},
						                updateItem: function(item) { 	item.PROC_ID=gridConfig.update_proc_id; genericGridSaveUpdateDelete(gridDivId,gridId,gridConfig.updateurl,item);},
						                deleteItem: function(item) { 	item.PROC_ID=gridConfig.delete_proc_id; genericGridSaveUpdateDelete(gridDivId,gridId,gridConfig.deleteurl,item);}
			            			},
		    
		     gridConfig.fields.push({ type: "control"});
		    
 			 $("#"+gridDivId).jsGrid(gridConfig);
  }
  
  
  //GENERIC GRID CRUD METHODS
  
  function loadGeneircGridData(selectedItems,gridURL,filter,grid_id,fetch_proc_id){
  
  					var dataObj="";
  					filter.GRID_ID=grid_id;
  					filter.PROC_ID=fetch_proc_id;
  					filter.SELECTED_ITEMS=selectedItems;
  					console.log ('loadGeneircGridData selectedItems '+selectedItems);
                      $.ajax({
                        type: "POST",
                        url: gridURL,
                        async: false,
                        data: filter,
                        success: function(gridRecords) {
                            dataObj = gridRecords;
                        }
                    });
  				return JSON.parse(dataObj);
  }
  
 function genericGridSaveUpdateDelete(gridDivId,gridId,gridURL,item){
 
                      $.ajax({
                        type: "POST",
                        url: gridURL,
                        async: false,
                        data: item,
                        success: function(gridRecords) {
                            alert(gridRecords);
                           // $("#"+gridDivId).jsGrid("loadData");
                            //$("#"+gridDivId).jsGrid("reset");
                           //  $("#"+gridId).jsGrid("refresh");
                           initializeGrid(gridDivId,gridId);
                           
                        }
                    });
  				
  }
  
function generateGridConfigObj(gridId){

var gridConfigObj={};
$.ajax({
    type: "POST",
    async: false,
    url: "getGenericGridConfigData",
    data: {"GRID_ID":gridId},
    success: function(resultData) {
    //console.log(resultData);
     gridConfigObj=JSON.parse(resultData);
    }
});

return gridConfigObj;

}
//selection checkbox

  

function getGenericSelctChebBox(gridId){

var checkBoxObj={};

                	
		                	
		                			checkBoxObj.headerTemplate= function() { return $("<input>").attr("type", "checkbox").attr("id","selectAllCheckbox"); },
                    			
                    				checkBoxObj.itemTemplate=  function(_, item) {
                        return $("<input>")
                            .attr("type", "checkbox")
                            .attr("class",
                                "singleCheckbox")
                            .prop(
                                "checked",
                                $
                                .inArray(
                                    item.firstName,
                                    selectedItems) > -1)
                            .on(
                                "change",
                                function() {
                                    $(this)
                                        .is(
                                            ":checked") ? 
                                            selectItem(
                                            $(this)
                                            .parent()
                                            .next()
                                            .text()) 
                                            
                                            :
                                        unselectItem(
                                        $(this)
                                            .parent()
                                            .next()
                                            .text());
                                  //  loadDisbursmentDetailsGrid();

                                });}
                    				
                    				
                    				
				                    checkBoxObj.align= "center",
				                    checkBoxObj.width= 25,
				                    checkBoxObj. sorting= false

                
                
                return checkBoxObj;
}

function loadDependentGrid(selectedItems){
//initializeGrid("GRID_CONGFIGURATIONS_DETIALS",'GRID_COLUMN_CONFIG_DETAILS');
var dependetnGridConfig=[];
var gridId='GENERIC_PROC_PARAM_CONFIG_GRID';
var gridDivId='GENERIC_PROC_PARAM_CONFIG_GRID';
	dependetnGridConfig.height='100';
  				dependetnGridConfig=generateGridConfigObj(gridId);
  				dependetnGridConfig.paging=dependetnGridConfig.PAGING;
  				dependetnGridConfig.pageSize=dependetnGridConfig.PAGESIZE;
			 	dependetnGridConfig.controller= {
						                loadData: function(filter) {  	return loadGeneircGridData(selectedItems,dependetnGridConfig.geturl,filter,gridId,dependetnGridConfig.fetch_proc_id);},
						                insertItem: function(item) { 	item.PROC_ID=dependetnGridConfig.insert_proc_id; genericGridSaveUpdateDelete(gridDivId,gridId,dependetnGridConfig.inserturl,item);},
						                updateItem: function(item) { 	item.PROC_ID=dependetnGridConfig.update_proc_id; genericGridSaveUpdateDelete(gridDivId,gridId,dependetnGridConfig.updateurl,item);},
						                deleteItem: function(item) { 	item.PROC_ID=dependetnGridConfig.delete_proc_id; genericGridSaveUpdateDelete(gridDivId,gridId,dependetnGridConfig.deleteurl,item);}
			            			},
		    
		     dependetnGridConfig.fields.push({ type: "control"});
 			 $("#"+gridDivId).jsGrid(dependetnGridConfig);
}



function addRowSelectEvent(){
var selecteGrid='';
		var rowClick= function(args) {
		console.log((args.item.GRID_ID));
		selecteGrid=args.item.GRID_ID;
        for(var i = 0; i<this.data.length; i++)
            this.rowByIndex(i).removeClass("highlight");
         var $row = this.rowByItem(args.item);
  			$row.toggleClass("highlight");


	loadDependentGrid(selecteGrid);
 };
 return rowClick;
}



