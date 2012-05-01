<html>
    <head>
        <title>Gaelyk & SmartClient</title>
        
		<SCRIPT>var isomorphicDir="../isomorphic/";</SCRIPT>
	    <SCRIPT SRC=../isomorphic/system/modules/ISC_Core.js></SCRIPT>
	    <SCRIPT SRC=../isomorphic/system/modules/ISC_Foundation.js></SCRIPT>
	    <SCRIPT SRC=../isomorphic/system/modules/ISC_Containers.js></SCRIPT>
	    <SCRIPT SRC=../isomorphic/system/modules/ISC_Grids.js></SCRIPT>
	    <SCRIPT SRC=../isomorphic/system/modules/ISC_Forms.js></SCRIPT>
	    <SCRIPT SRC=../isomorphic/system/modules/ISC_DataBinding.js></SCRIPT>
	<SCRIPT SRC=../isomorphic/skins/Simplicity/load_skin.js></SCRIPT>
    </head>
<body>

<h1>Financisto Demo!</h1>

<!--------------------------
  Example code starts here
---------------------------->

<SCRIPT>

isc.RestDataSource.create({
    ID:"RUB",
    dataFormat:"json",
    fields:[
        {name:"id", title:"Id", primaryKey:true, canEdit:false, required:true},
        {name:"status", title:"Status", required:true, canEdit:true, valueMap:{"v":"Verified","w":"Waiting", "c":"Confirmed"}},
        {name:"category", title:"Category", canEdit:true, valueMap:{"f":"Food","c":"Comp", "h":"Home"}},
        {name:"amount", title:"Amount", canEdit:true, required:true},
        {name:"comment", title:"Comment", canEdit:true},
        {name:"project", title:"Project", canEdit:true},
        {name:"receiver", title:"Receiver"}
    ],
    
    fetchDataURL:	"/demo/rest-api/ds/fetch.json",
    addDataURL :	"/demo/rest-api/ds/add.json",
    updateDataURL:	"/demo/rest-api/ds/update.json",
    removeDataURL:	"/demo/rest-api/ds/remove.json"
    
        
});
   
isc.ListGrid.create({
    ID: "financistoGrid",
    width:500, height:224, alternateRecordStyles:true,
    emptyCellValue: "--",
    dataSource: RUB,
    // display a subset of fields from the datasource
    fields:[
        {name:"id"},
        {name:"status"},
        {name:"category"},
        {name:"amount"},
        {name:"comment"},
        {name:"project"},
        {name:"receiver"}
    ],
    sortFieldNum: 0, // sort by id
    dataPageSize: 50,
    autoFetchData:true
})


isc.IButton.create({
    left:0, top:240, width:150,
    title:"Add",
    click: function () {
    	financistoGrid.addData(
            {
                countryCode: "A1",
                countryName: "New Value",
                capital:"New Value",
                continent:"New Value"
            }
        );
        this.disable();
    }
})

isc.IButton.create({
    left:175, top:240, width:150,
    title:"Update",
    
    click: function () {
    	financistoGrid.updateData(
            {
                countryCode: "US",
                countryName:"Edited Value",
                capital:"Edited Value",
                continent:"Edited Value"
            }
        );
        
    	financistoGrid.selection.selectSingle({countryCode:"US"})
        this.disable();
    }
});

isc.IButton.create({
    left:350, top:240, width:150,
    title:"Remove",
    
    click: function () {
    	financistoGrid.removeData(
            {
                countryCode: "UK"
            }
        );
        this.disable();
    }
});

</SCRIPT>

</body>
</html>

