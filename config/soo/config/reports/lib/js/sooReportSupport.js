//importPackage(Packages.java.util);
importPackage(Packages.de.ingmbh.sphinx.online.common);
importPackage(Packages.de.ingmbh.sphinx.online.service);
importPackage(Packages.de.ingmbh.sphinx.online.common.vo);
importPackage(Packages.de.ingmbh.sphinx.online.validation);
importPackage(Packages.org.apache.commons.lang3.time);

sooReportSupport = {
	findNodesForProject : function(projectLogicalName) {
		var allNodes = dataManager.findNodeIdsByRegex(null, '.*');
		var iterator = allNodes.iterator();
		var result = [];
		var index = 0;

		while (iterator.hasNext()) {
			var node = dataManager.getDataPointBeanByNodeId(null, iterator
					.next());
			var valueDefinition = node.getValueDefinition();
			var adapter = valueDefinition.getMappedAdapter2();
			if (adapter == null) {
				// DP ohne Adapter (reiner Strukturknoten)
				continue;
			}
			if (adapter.getProjectLogicalName() != projectLogicalName) {
				// DP nicht aus einem Adapter dieses Projektes
				continue;
			}
			// Suchkriterium erfüllt
			result[index++] = node;
		}
		return result;
	},
	
	findNodeGroupsForProject : function(projectLogicalName){
		var sessionId = null;
		try {
			// Session holen
			sessionId = adminService.connect(SooConstants.ClientType.INTERNAL, null, null);
			var nodeGroups = adminService.getNodeGroupSpecs(sessionId, projectLogicalName, false);
		} finally {
			if (sessionId != null) {
				adminService.disconnect(sessionId);
			}
		}
		
		var iterator = nodeGroups.iterator();
		var result = [];
		var index = 0;
		
		while (iterator.hasNext()) {
			var nodeGroup = iterator.next();
			result[index++] = nodeGroup;
		}
		return result;
	},
	
	findNodesInNodeGroup : function(projectLogicalName, nodeGroup){
		var includedNodeIds = nodeGroup.getIncludedNodeIds();
		var iterator = includedNodeIds.iterator();
		var aIncludedNodeIds = [];
		var index = 0;
		while(iterator.hasNext()){
			var includedNode = iterator.next();
			aIncludedNodeIds[index++] = includedNode;
		}
		var nodes = this.findNodesForProject(projectLogicalName);
		var result = [];
		index = 0;
		//prüfen, ob valueHistoryStats in dem Projekt vorkommt
		for(var i = 0; i < nodes.length; i++){
			for(var j = 0; j < aIncludedNodeIds.length; j++){
				if(nodes[i].getId() == aIncludedNodeIds[j].getNodeId()){
					result[index++] = nodes[i];
					continue;
				}
			}
		}
		return result;
	},


	findAdaptersForProject : function(projectLogicalName) {
		var sessionId = null;
		try {
			// Session holen
			sessionId = adminService.connect(SooConstants.ClientType.INTERNAL, null, null);
			var adapters = adminService.getAdapterSpecs(sessionId,
					projectLogicalName);
		} finally {
			if (sessionId != null) {
				adminService.disconnect(sessionId);
			}
		}
		return adapters;
	},
	
	findTriggersForProject : function(projectLogicalName) {
		var sessionId = null;
		try {
			// Session holen
			sessionId = adminService.connect(SooConstants.ClientType.INTERNAL, null, null);
			// Project holen
			var project = adminService.getProjectByLogicalName(sessionId, projectLogicalName);
			var triggers = adminService.getTriggerSpecs(sessionId, project.getDbId());
		} finally {
			if (sessionId != null) {
				adminService.disconnect(sessionId);
			}
		}
		return triggers;
	},
	
	getTriggerChangeOfValue : function(projectLogicalName){
		var triggers = this.findTriggersForProject(projectLogicalName);
		var result = [];
		var index = 0;
		for(var i = 0; i < triggers.length; i++){
			if(triggers[i].getTriggerType() == "TRIGGER_CHANGE_OF_VALUE"){
				result[index++] = triggers[i];
			}
		}
		return result;
	},
	
	getTriggerScheduled : function(projectLogicalName){
		var triggers = this.findTriggersForProject(projectLogicalName);
		var result = [];
		var index = 0;
		for(var i = 0; i < triggers.length; i++){
			if(triggers[i].getTriggerType() == "TRIGGER_SCHEDULED"){
				result[index++] = triggers[i];
			}
		}
		return result;
	},
	
	findImagesForProject : function(projectLogicalName){
		var sessionId = null;
		try {
			// Session holen
			sessionId = adminService.connect(SooConstants.ClientType.INTERNAL, null, null);
			// Project holen
			var project = adminService.getProjectByLogicalName(sessionId, projectLogicalName);
			var images = adminService.getImages(sessionId, project.getDbId());
		} finally {
			if (sessionId != null) {
				adminService.disconnect(sessionId);
			}
		}
		return images;
	},
	
	getImagePreviewContent : function(id){
		var sessionId = null;
		try {
			// Session holen
			sessionId = adminService.connect(SooConstants.ClientType.INTERNAL, null, null);
			var previewImage = adminService.getImagePreviewContent(sessionId, id);
		} finally {
			if (sessionId != null) {
				adminService.disconnect(sessionId);
			}
		}
		return previewImage;
	},
	
	getImageValidationMessages : function(projectLogicalName, image){
		var index = 0;
		var result = [];
		
		validationEntity = validationService.getValidationEntity(image.getProject(), "Image", image.getFileName()); 
		if (validationEntity != null) {
			var validationMessages = validationService.getValidationMessagesForValidationEntity(validationEntity);
			var iterator = validationMessages.iterator();
			while(iterator.hasNext()){
				result[index++] = iterator.next();
			}
		}
		return result;
	},
	
	getSymbolPreviewContent : function(id){
		var sessionId = null;
		try {
			// Session holen
			sessionId = adminService.connect(SooConstants.ClientType.INTERNAL, null, null);
			var previewSymbol = adminService.getSymbolPreviewContent(sessionId, id);
		} finally {
			if (sessionId != null) {
				adminService.disconnect(sessionId);
			}
		}
		return previewSymbol;
	},
	
	getPanelPreviewContent : function(id){
		var sessionId = null;
		try {
			// Session holen
			sessionId = adminService.connect(SooConstants.ClientType.INTERNAL, null, null);
			var previewPanel = adminService.getPanelPreviewContent(sessionId, id);
		} finally {
			if (sessionId != null) {
				adminService.disconnect(sessionId);
			}
		}
		return previewPanel;
	},	
	
	findTreesForProject : function(projectLogicalName) {
		var sessionId = null;
		try {
			// Session holen
			sessionId = adminService.connect(SooConstants.ClientType.INTERNAL, null, null);
			// Project holen
			var project = adminService.getProjectByLogicalName(sessionId, projectLogicalName);
			var trees = adminService.getTrees(sessionId, project.getDbId());
		} finally {
			if (sessionId != null) {
				adminService.disconnect(sessionId);
			}
		}
		return trees;
	},
	
	findChartsForProject : function(projectLogicalName) {
		var sessionId = null;
		try {
			// Session holen
			sessionId = adminService.connect(SooConstants.ClientType.INTERNAL, null, null);
			// Project holen
			var project = adminService.getProjectByLogicalName(sessionId, projectLogicalName);
			var charts = adminService.getCharts(sessionId, project.getDbId());
		} finally {
			if (sessionId != null) {
				adminService.disconnect(sessionId);
			}
		}
		return charts;
	},
	
	findPanelsForProject : function(projectLogicalName){
		var sessionId = null;
		try {
			// Session holen
			sessionId = adminService.connect(SooConstants.ClientType.INTERNAL, null, null);
			// Project holen
			var project = adminService.getProjectByLogicalName(sessionId, projectLogicalName);
			var panels = adminService.getPanels(sessionId, project.getDbId());
		} finally {
			if (sessionId != null) {
				adminService.disconnect(sessionId);
			}
		}
		return panels;
	},
	
	findSymbolsForProject : function(projectLogicalName){
		var sessionId = null;
		try {
			// Session holen
			sessionId = adminService.connect(SooConstants.ClientType.INTERNAL, null, null);
			// Project holen
			var project = adminService.getProjectByLogicalName(sessionId, projectLogicalName);
			var symbols = adminService.getSymbols(sessionId, project.getDbId());
		} finally {
			if (sessionId != null) {
				adminService.disconnect(sessionId);
			}
		}
		return symbols;
	},
	
	findHistoryStats : function(projectLogicalName) {
		// returniert ein array von ass. array mit:
		// valueHistoryStat: valueHistoryStat
		// valueDefinition: valueDefinition
		// wobei letzteres auch fehlen kann, wenn der DP unbekannt sein sollte
		var sessionId = null;
		try {
			// Session holen
			sessionId = adminService.connect(SooConstants.ClientType.INTERNAL, null, null);
			var resultMap = {};
			// resultMap von soo Nodes ausgehen bauen, wobei auf die mit History
			// beschränken
			var nodes = this.findNodesForProject(projectLogicalName);
			for (var nodeIndex = 0; nodeIndex < nodes.length; nodeIndex++) {
				var sooNode = nodes[nodeIndex];
				var valueDefinition = sooNode.getValueDefinition();
				if (valueDefinition.helperIsHistoryAvailable() == false) {
					// keine History vorgesehen, wieder raus
					continue;
				}
				resultMap[sooNode.getId()] = {
					valueDefinition : valueDefinition
				};
			}
			// zusätzlich noch über ValueHistoryStat
			var valueHistoryStatList = adminService.getValueHistoryStats(sessionId, null, false);
			var valueHistoryStatListIterator = valueHistoryStatList.iterator();
			for ( ;valueHistoryStatListIterator.hasNext(); ) {
				var valueHistoryStat = valueHistoryStatListIterator.next();
				// zugehörige Node (sofern vorhanden)
				var entry = resultMap[valueHistoryStat.getId()];
				if (entry != null) {
					entry['valueHistoryStat'] = valueHistoryStat;
				} else {
					// VHS gefunden, aber keine nodeId aus diesem Projekt
				}
			}
			// aus der Map wieder ein Array bauen, für BIRT open/fetch
			var result = [];
			for ( var nodeId in resultMap) {
				if (resultMap.hasOwnProperty(nodeId)) {
					result.push(resultMap[nodeId]);
				}
			}
			return result;
		} finally {
			if (sessionId != null) {
				adminService.disconnect(sessionId);
			}
		}
	},

	findUnlimitedHistoryNodes : function(projectLogicalName){
		var allNodes = dataManager.findNodeIdsByRegex(null, '.*');
		var iterator = null;
		var result = [];
		var index = 0;

		iterator = allNodes.iterator();

		while (iterator.hasNext()) {
			var node = dataManager.getDataPointBeanByNodeId(null, iterator
					.next());
			var valueDefinition = node.getValueDefinition();
			var adapter = valueDefinition.getMappedAdapter2();
			if (adapter == null) {
				// DP ohne Adapter (reiner Strukturknoten)
				continue;
			}
			if (adapter.getProjectLogicalName() != projectLogicalName) {
				// DP nicht aus einem Adapter dieses Projektes
				continue;
			}
			if(valueDefinition.helperIsHistoryAvailable() == false){
				continue;
			}
			if(valueDefinition.getHoldBackTime() != null){
				continue;
			}
			// Suchkriterium erfüllt
			result[index++] = node;
		}
		return result;
	},
	
	findTableSpecs : function(projectLogicalName){
		var tableSpecs = tableService.find(new TableSpec(projectLogicalName), null, false, true);
		var iterator = tableSpecs.iterator();
		var result = [];
		var index = 0;
		while(iterator.hasNext()){
			result[index++] = iterator.next();
		}
		return result;
	},
	
	findGeoMapSpecs : function(projectLogicalName){
		var geoMapSpecs = geoMapService.find(new GeoMapSpec(projectLogicalName), null, false, true);
		var iterator = geoMapSpecs.iterator();
		var result = [];
		var index = 0;
		while(iterator.hasNext()){
			result[index++] = iterator.next();
		}
		return result;
	},
	
	findDashboardSpecs : function(projectLogicalName){
		var dashboardSpecs = dashboardService.find(new DashboardSpec(projectLogicalName), null, false, true);
		var iterator = dashboardSpecs.iterator();
		var result = [];
		var index = 0;
		while(iterator.hasNext()){
			result[index++] = iterator.next();
		}
		return result;
	},
	
	findAppConficSpecs : function(projectLogicalName){
		//wenn man nur die config vom masterprojekt möchte folgende zeile
		//var appConfigSpecs =  appConfigService.find(new AppConfigSpec(projectLogicalName, SooConstants.LOGICAL_NAME_APPCONFIG, null), null, true, true);
		var appConfigSpecs =  appConfigService.find(new AppConfigSpec(projectLogicalName), null, true, true);
		var iterator = appConfigSpecs.iterator();
		var result = [];
		var index = 0;
		while(iterator.hasNext()){
			result[index++] = iterator.next();
		}
		return result;
	},
	
	wrapText : function(textToWrap, width, wrapAt){
		var wrappedText = "";
		
		var lines = textToWrap.split(wrapAt);
		for(var i = 0; i < lines.length -1; i++){
			lines[i] = lines[i] + wrapAt;
		}
		var line = "";
		var index = 0;
		while(index < lines.length || line.length > 0){
			var lineToProof = line + lines[index];
			//wenn noch Punkte enthalten sind und Teilwort kleiner als Breite oder 
			if(index < lines.length && (lineToProof.length <= width || line.length == 0)){
				line += lines[index++];
			}else{
				wrappedText += line.substring(0, line.length) + "\n";
				line = "";
			}
			while(line.length > width){
				wrappedText += line.substring(0, width) + "\n";
				line = line.substring(width);
			}
			
		}
		return wrappedText;
	},
	
	formatPeriod : function(stringToFormat){
		stringToFormat = stringToFormat.replace("Y", "y");
		var timeInterval = new TimeInterval(stringToFormat);
		return DurationFormatUtils.formatDurationWords(Number(timeInterval.toMillis()), false, true);
	}
};