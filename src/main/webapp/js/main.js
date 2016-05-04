(function () {
	'use strict';
	var selectItem = false;
	var refreshAdminPrograms = false;
	Vue.transition('slideHorizontal', {
		css: false,
		enter : function(el, done){
			var originalPos = $(el).css('width').split('px')[0] * -1;
			$(el).css('right', originalPos).animate({
				right : 0
			}, 300, done);
		},
		enterCancelled : function(el){
			$(el).stop(); // when animating but been click, stop it
		},
		leave : function(el, done){
			var targetPos = $(el).css('width').split('px')[0] * -1;
			$(el).css('right', 0).animate({
				right : targetPos
			}, 300, done);
		},
		leaveCancelled : function(el){
			$(el).stop();
		}
	});
	
	Vue.component('profile-component', {
		template: "#profile-component-template",
		props: ['user'],
		methods: {
			logout: function() {
				doLogout();
			}
		}
	});
	
	Vue.component('pagination-component', {
		template: "#pagination-component-template",
		props:['page'],
		methods: {
			gotoPrev: function(currentPage) {
				if(currentPage == 1) {
					return;
				}
				// Get prev list
				this.$parent.currentPage--;
				if(this.$parent.currentIndex === 0) {
					// Get dashboard
				}else if(this.$parent.currentIndex === 1) {
					this.$parent.getProgramList();
				}else if(this.$parent.currentIndex === 3) {
					this.$parent.getOperationList();
				}else if(this.$parent.currentIndex === 4) {
					refreshAdminPrograms = true;
					this.$parent.getAdminProgramList();
				}
			},
			gotoNext: function(currentPage) {
				if(this.page.totalPageNum == currentPage) {
					return;
				}
				// get next list
				this.$parent.currentPage++;
				if(this.$parent.currentIndex === 0) {
					// Get dashboard
				}else if(this.$parent.currentIndex === 1) {
					this.$parent.getProgramList();
				}else if(this.$parent.currentIndex === 3) {
					this.$parent.getOperationList();
				}else if(this.$parent.currentIndex === 4) {
					refreshAdminPrograms = true;
					this.$parent.getAdminProgramList();
				}
			}
		}
	});
	
	Vue.directive('remaining-progress',function(info){
		setTimeout(function() {
			$(this.el).css("width", (info.p/info.t).toFixed(2) * 100 + "%");
			if(info.p/info.t === 1) {
				$(this.el).css("border-radius", "5px");
			}
		}.bind(this), 100);
	});
	
	Vue.directive('chart-ratio', function(info) {
		setTimeout(function() {
			if(info) {
				$(this.el).css('width', Math.floor(info.c/info.t*100) + '%');
			}
		}.bind(this),300);
	});
	
	Vue.directive('total-width',function(num){
		setTimeout(function() {
			$(this.el).css("width", num * 21 + "px");
		}.bind(this), 100);
	});
	
	Vue.filter('formatDate', function(date){
		var startDate = date[0];
		var endDate = date[1];
		var startDateStr = new Date(startDate);
		var endDateStr = new Date(endDate);
		var startYear = startDateStr.getFullYear();
		var endYear = endDateStr.getFullYear();
		var startFormatDate = "";
		var endFormatDate = "";
		if(startYear == endYear) {
			startFormatDate = DateFormat.format.date(startDateStr, "MMM dd");
		}else{
			startFormatDate = DateFormat.format.date(startDateStr, "MMM dd, yyyy");
		}
		endFormatDate = DateFormat.format.date(endDateStr, "MMM dd, yyyy");
		return startFormatDate + " - " + endFormatDate;
	});
	
	Vue.filter('dateTime', {
		read: function(timestamp, type) {
			var type = type;
			switch (type) {
			case 1:
				if(timestamp){
					return DateFormat.format.date(new Date(timestamp), "yyyy-MM-dd");
				}
				break;
			default:
				if(timestamp){
					return DateFormat.format.date(new Date(timestamp), "yyyy-MM-dd HH:mm:ss");
				}
				break;
			}
			return '';
		},
		write: function(val) {
			return val;
		}
	});
	
	Vue.filter('getFirstName', function(name) {
		if(name) {
			var nameArr = name.split(',');
			var nameLength = nameArr.length;
			var firstName = nameArr[nameLength - 1];
			return firstName.trim();
		}
	});
	
	var vm = new Vue({
		el : 'body',
		data : {
			noLimitation: true,
			modules: [
			     { name: 'dashboard', active: true},
			     { name: 'program', active: false},
			     { name: 'assignment', active: false},
			     { name: 'history', active: false},
			     { name: 'admin', active: false}
			],
			emails: [
			     { text: '@capgemini.com' },
			     { text: '@sogeti.com' },
			     { text: '@prosodie.com' }
			],
			selectedType: {},
			selectedEmailAddress: '',
			currentIndex: 0,
			errorMsg: "",
			query: '',
			currentSbu : {},
			currentCountry: {},
			sbuId: 0,
			swapSeatsCount: 0,
			eventList:[],
			allEvent: [],
			adminProgramList: [],
			countryCharts: [],
			programList : [],
			otherSbuSeatsList: [],
			countryTotalSeats: 0,
			countryRemainingSeats: 0,
			countrySeatsList: [],
			nomineeList: [],
			deleteNominee: {},
			deleteSbu: {},
			isDeleteNominee: true,
			searchNomineeList: [],
			searchNominee: {},
			selectedProgram: null,
			selectedProgramIndex: -1,
			operationList: [],
			pagination: false,
			page: {},
			currentPage: 1,
			pageSize: 100,
			employeeSearch: '',
			targetSwapPrograms: [],
			selectedSwapSbu: {},
			selectedSwapEvent: {},
			selectedSwapProgram: {},
			selectedEvent: {},
			isZero: false,
			noSelectProgram: false,
			noRecord: false,
			wholeSbuInfo: {},
			user: {},
			noSwapEvent: false,
			typeList: [
				{ text: 'All' },
				{ index: 3, text: 'Upcoming' },
				{ index: 2, text: 'Ongoing' },
				{ index: 1, text: 'Closed' }
			],
			sbuList: [],
			isEdit: false,
			uploadFileName: '',
			incorrectFile: false,
			uploadType: '',
			programDetail: {},
			noSwapSeats: true,
			newSbu: {}
		},
		ready : function() {
			initModalPosition();
			
			// Get Request
			this.getCurrentSbu();
			this.selectedEmailAddress = this.emails[0].text;
			this.selectedType = this.typeList[1];
		},
		computed: {
			countryRemainingSeats: {
				get: function() {
					var result = this.countryTotalSeats;
					var countrySeatsListNumber = this.countrySeatsList.length;
					if(countrySeatsListNumber===0) {
						if(this.currentCountry.participantList) { // For country level user
							countrySeatsListNumber = this.currentCountry.participantList.length;
						}
					}
					for(var i=0;i<countrySeatsListNumber;i++){
						if(this.countrySeatsList.length>0) {
							result -= this.countrySeatsList[i].seats;
						}else{
							result--; //For country level user
						}
					}
					if(this.currentSbu.forSwapNumber !== undefined) {
						result -= this.currentSbu.forSwapNumber;
					}
					return result;
				}
			},
			isDetailShow: function() {
				return this.selectedProgram ? true : false;
			}
		},
		methods : {
			selectType: function(i) {
				this.query = '';
				this.selectedType = this.typeList[i];
				this.getProgramList();
			},
			limitNumber: function() {
				if(this.swapSeatsCount > this.currentSbu.forSwapNumber) {
					this.swapSeatsCount = this.currentSbu.forSwapNumber;
				}
			},
			gotoPage: function(index) {
				// Init pagination
				this.page = {};
				this.currentPage = 1;
				this.pagination = false;
				// Init program page
				this.selectedProgram = null;
				this.selectedProgramIndex = -1;
				this.selectedType = this.typeList[1];
				// Init data
				this.allEvent = [];
				this.eventList = [];
				this.noRecord = false;
				// Module index --- 0: program; 1: assignment; 2: history
				var modules = this.modules;
				for (var i in modules) {
					this.modules[i].active = false;
					$("#" + this.modules[i].name).addClass("hidden");
				}
				this.modules[index].active = true;
				$("#" + this.modules[index].name).removeClass("hidden");
				this.currentIndex = Number(index);
				
				if(this.currentIndex === 0) { // Dashboard
					this.getSummary();
				}
				if(this.currentIndex === 1) { // Program
					this.getProgramList();
				}
				if(this.currentIndex === 3) { // History
					this.getOperationList();
				}
				if(this.currentIndex === 4) { // Admin
					refreshAdminPrograms = false;
					this.getAdminProgramList();
//					this.getSbuList();
				}
			},
			getCurrentSbu: function () {
				var options = {
						url: envConfig.apis.getCurrentSbu.url,
						dummyPath: "dummy/getCurrentSBU.json",
						callback: this.getCurrentSbuCallback
				};
				utils.ajax(options);
			},
			getCurrentSbuCallback: function() {
				return {
			        "success": function (resp) {
			        	var data = resp.responseBody.result;
			        	var sbuInfo = data.content;
			        	if(sbuInfo && sbuInfo.length ===1) {
			        		sbuInfo = sbuInfo[0];
			        		var loginInfo = sbuInfo.lbps;
				        	
				        	vm.user = loginInfo;
				        	vm.wholeSbuInfo = sbuInfo;
				        	if(typeof sbuInfo === "string") {
				        		// No permission
				        		return;
				        	}
				        	var parentId = sbuInfo.parentId;
				        	if(parentId === 0) { // First level
				        		vm.noLimitation = true;
				        		vm.currentSbu = sbuInfo;
				        		vm.$set('currentSbu.parentSbuId', parentId);
				        	}else{ // Second level
				        		vm.noLimitation = false;
				        		var parentInfo = sbuInfo.parentSbu;
				        		vm.currentSbu = parentInfo;
				        		vm.currentCountry = {
				        				id: sbuInfo.id,
				        				countryName: sbuInfo.subName,
				        				parentSbuId: sbuInfo.parentId
				        		};
				        	}
				        	vm.sbuId = sbuInfo.id;
				        	
//				        	vm.getProgramList();
				        	vm.getSummary();
				        	$("body").show();
				        	$("body").scrollTop(0);
			        	}else if(sbuInfo && sbuInfo.length > 1) {
			        		// Multiple SBU owner
			        	}
			        },
			        "error": function (resp) {
			        	if(!resp.valid) {
			        		// error handler
			        		top.location.href = "login.jsp";
			        	}
			        }
			    };
			},
			/**
			 * two params
			 * param 'admin' means if admin call the event list
			 * param 'type' means get event list by type (all, upcoming, ongoing, closed)
			 */
			getAllEvent: function(isAdmin, type) {
				var options = {
						url: envConfig.apis.getAllEvent.url,
						data: {type: type?type:''}, // 1: Closed; 2: Ongoing; 3: Upcoming
						callback: isAdmin ? this.getAllEventByAdminCallback : this.getAllEventCallback
				};
				utils.ajax(options);
			},
			getAllEventCallback: function() {
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
						var allEvent = data.content;
						vm.allEvent = allEvent;
						console.log(allEvent.length);
						if(allEvent && allEvent.length>0) {
							vm.noSwapEvent = false;
							vm.selectedSwapEvent = vm.allEvent[0];
//							if(vm.selectedEvent) {
								vm.getProgramsByEvent(vm.selectedSwapEvent.event_name);
//							}
						}else{
							vm.noSwapEvent = true;
							vm.swapSeatsCount = 0
							vm.selectedSwapProgram = {};
							$("#swapModal").modal("show");
						}
					},
					"error": function() {
						console.log(resp);
					}
				}
			},
			getAllEventByAdminCallback: function() {
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
						var allEvent = data.content;
						vm.allEvent = allEvent;
						console.log(allEvent.length);
						if(allEvent && allEvent.length>0) {
							vm.noRecord = false;
							vm.getProgramsByEvent(vm.selectedSwapEvent.event_name, true);
						}else {
							vm.noRecord = true;
						}
					},
					"error": function() {
						console.log(resp);
					}
				}
			},
			getProgramsByEvent: function(ename, isAdmin) {
				var options = {
						url: envConfig.apis.getProgramListByEvent.url,
						data:{ eventName: ename ? ename : '' },
						callback: isAdmin ? this.getProgramListByEventByAdminCallback : this.getProgramListByEventCallback
				};
				utils.ajax(options);
			},
			getProgramListByEventCallback: function() {
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
						var programList = data.content;
						// GET SWAP FLOW
		        		vm.targetSwapPrograms = []; // reset the targetSwapPrograms list
						var programs = [];
						var removeDefault = function() {
							if(programList && programList.length > 0) {
								var programListLength = programList.length;
								for(var i=0;i<programListLength;i++) {
//								for (var i in programList) {
									if(programList[i].name.toLowerCase() === vm.selectedProgram.courseName.toLowerCase()) {
										var defaultIndex = programList.indexOf(programList[i]);
										var totalLength = programList.length;
										var preArr = programList.slice(0,defaultIndex);
										var postArr = programList.slice(defaultIndex+1,totalLength);
										programs = preArr.concat(postArr);
										
										break;
										/**
										 * Purpose of above code is equals with below code, want to get the array without default program.
										 * But $remove is splice function which will change the original array. 
										 * The changed array(this.programList) will impact the view of home page.
										 * So create another array to avoid this kind of situation(might be another way)
										 */
//										programList.$remove(programList[i]);
									}else {
										programs = programList;
									}
								}
								return programs;
							}
						}
						vm.targetSwapPrograms =  removeDefault();
						vm.swapSeatsCount = 0
						vm.selectedSwapProgram = {};
						$("#swapModal").modal("show");
					},
					"error": function(resp) {
						console.log(resp);
					}
				};
			},
			getProgramListByEventByAdminCallback: function() {
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
						var programList = data.content;
						
					},
					"error": function(resp) {
						console.log(resp);
					}
				}
			},
			getProgramList: function() {
				var options = {
						method: "POST",
						url: envConfig.apis.getProgramList.url,
						dummyPath: "dummy/getProgramList.json",
						data: this.getProgramListData(),
						callback: this.getProgramListCallback
				};
				utils.ajax(options);
			},
			getProgramListData: function() {
				var obj = {};
				var sbuId = this.sbuId;
				var pageSize = this.pageSize;
				var type = this.selectedType.index;
				var typeNumber = type ? type : '';
				obj = {
						type: typeNumber,
						sbuId: sbuId,
						currentPage: this.currentPage,
						pageSize: pageSize,
						courseName: this.query 
				};
				return obj;
			},
			getProgramListCallback: function() {
		    	return {
			        "success": function (resp) {
			        	var data = resp.responseBody.result.content;
			        	if(!data) {
			        		// Display empty page and init programlist
			        		vm.noRecord = true;
			        		vm.pagination = false;
			        		vm.eventList = [];
//			        		vm.programList = [];
			        		// init program
			        		vm.selectedProgram = null;
							vm.selectedProgramIndex = -1;
			        		return;
			        	}
			        	vm.noRecord = false;
			        	var pageObj = data.page;
			        	vm.$set('page', pageObj);
			        	
			        	var eventListArr = data.result;
			        	var eventList = eventListArr[0];
			        	
		        		if(pageObj.totalPageNum>1) {
			        		vm.pagination = true;
			        	}else{
			        		vm.pagination = false;
			        	}
//				        	vm.$set("programList", programList);
			        	vm.$set("eventList", eventList);
//			        	$("body").show();
//			        	$("body").scrollTop(0);
			        },
			        "error": function (resp) {
			        	console.log(resp);
			        }
			    };
			},
			showDetail: function(event, index, esc) {
				if(esc) {
					this.selectedProgramIndex = index;
					this.selectedProgram = $.extend({}, this.eventList[event.eventName].results[index]);
					this.getAllInfo();
				}else{
					this.selectedProgram = null;
					this.selectedProgramIndex = -1;
					setTimeout(function() {
						this.showDetail(event, index, true);
					}.bind(this), 300);
				}
			},
			/**
			 * Get all information in the right part. It contains:
			 * 1. Other SBUs info;
			 * 2. Current SBU's country remaining seats;
			 * 3. Current SBU's country nominees' info
			 */
			getAllInfo: function() {
				var options = {
					url: envConfig.apis.getAllInfo.url,
					data: this.getAllInfoData,
					callback: this.getAllInfoCallback
				};
				utils.ajax(options);
			},
			getAllInfoData: function() {
				var obj = {};
				obj = {
					sbuId: this.sbuId,
					courseId: this.selectedProgram.courseId
				};
				return obj;
			},
			getAllInfoCallback: function() {
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
						var dataList = data.content;
						var forSwapSeatNum = dataList.forSwap;
						vm.$set('currentSbu.forSwapNumber', forSwapSeatNum);
						var otherSbuSeatsList = [];
						var countrySeatsList = [];
						vm.noSwapSeats = true;
						if(dataList.otherSbuSwapList && dataList.otherSbuSwapList.length>0) {
							vm.noSwapSeats = false;
							otherSbuSeatsList = dataList.otherSbuSwapList;
						}
						vm.otherSbuSeatsList = otherSbuSeatsList;
						if(dataList.subSbuList) {
							countrySeatsList = dataList.subSbuList;
						}
						vm.countrySeatsList = countrySeatsList;
						
						var sbuCourse = dataList.subCourse;
						vm.countryTotalSeats =  sbuCourse.seats;
						
						// Below for country level sbu
						if(dataList.participantList) {
//							vm.currentCountry.participantList = dataList.participantList; // This not work, but below works. why?
							vm.$set('currentCountry.participantList', dataList.participantList);
						}
						vm.currentCountry.courseId = sbuCourse.courseId;
						vm.currentCountry.seats = sbuCourse.seats;
						$("#allInfo").scrollTop(0);
					},
					"error": function(resp){
						console.log(resp)
					}
				};
			},
			swapSeats: function(index) {
				this.selectedSwapSbu = this.otherSbuSeatsList[index];
//				var targetSbuId = this.selectedSwapSbu.id;
				this.getAllEvent(false, "3");
//				this.getProgramList(targetSbuId, true);
			},
			minusCount: function(obj) {
				if(obj.seats !== undefined && obj.seats != 0) {
					if(obj.seats === obj.participantList.length) {
						this.showError('Please remove nominee firstly', true);
						return;
					}
					obj.seats--;
					this.assignSeats(obj);
				}else if(obj.forSwapNumber !== undefined && obj.forSwapNumber != 0) {
					obj.forSwapNumber--;
					// Update swap number
					this.updateSwapSeats();
				}
			},
			plusCount: function(obj) {
				if(this.countryRemainingSeats != 0) {
					if(obj.seats !== undefined){
						obj.seats++;
						this.assignSeats(obj);
					}else{
						obj.forSwapNumber++;
						// update swap number
						this.updateSwapSeats();
					}
				}
			},
			assignSeats: function(obj) {
				var options = {
						method: "POST",
						url: envConfig.apis.assignSeatsForCountry.url,
						data: this.assignSeatsData(obj),
						callback: this.assignSeatsCallback
				};
				utils.ajax(options);
			},
			assignSeatsData: function(obj) {
				var seats = obj.seats;
				var countrySbuId = obj.id;
				var obj = {};
				obj = {
						sbuId: countrySbuId,
						courseId: this.selectedProgram.courseId,
						seats: seats
				}
				return obj;
			},
			assignSeatsCallback: function() {
				return {
					"success": function(resp) {
						var returnInfo = resp.responseBody.info;
						var statusCode = Number(returnInfo.code);
						if(statusCode === 200) {
							console.log(returnInfo.msg);
						}
					},
					"error": function(resp){
						console.log(resp)
					}
				};
			},
			updateSwapSeats: function() {
				var options = {
						method: "POST",
						url: envConfig.apis.updateSwapSeats.url,
						data: this.updateSwapSeatsData,
						callback: this.updateSwapSeatsCallback
				};
				utils.ajax(options);
			},
			updateSwapSeatsData: function() {
				var obj = {};
				var sbuId = this.currentSbu.id;
				var courseId = this.selectedProgram.courseId;
				var swapSeatsNumber = this.currentSbu.forSwapNumber;
				obj = {
						sbuId: sbuId,
						courseId: courseId,
						swapSeats: swapSeatsNumber
				};
				return obj;
			},
			updateSwapSeatsCallback: function() {
				return {
					"success": function(resp) {
						var returnInfo = resp.responseBody.info;
						var statusCode = Number(returnInfo.code);
						if(statusCode === 200) {
							console.log(returnInfo.msg);
						}
					},
					"error": function(resp){
						console.log(resp)
					}
				};
			},
			addNominee: function(countryInfo) {
				this.currentCountry = countryInfo;
				/**
				 * If need to display already exist user in the search modal, uncomment the below two line code
				 */
//				var searchMonimeeList = countryInfo.participantList.slice(0, countryInfo.participantList.length);
//				vm.searchNomineeList = searchMonimeeList;
				this.employeeSearch = '';
				this.searchNomineeList = [];
	        	$("#addNomineeModal").modal("show");
			},
			addSbu: function() {
				this.newSbu = {};
				$("#addSbuModal").modal("show");
			},
			getOperationList: function() {
				var options = {
						method: "POST",
						url: envConfig.apis.getOperationList.url,
						dummyPath: "dummy/getOperationList.json",
						data: this.getOperationListData,
						callback: this.getOperationListCallback
				};
				utils.ajax(options);
			},
			getOperationListData: function() {
				var parentId = this.wholeSbuInfo.parentId;
				var sbuId = "";
	        	if(parentId === 0) {
	        		sbuId = this.wholeSbuInfo.id;
	        	}else{
	        		sbuId = this.wholeSbuInfo.parentSbu.id;
	        	}
				var obj = {
						sbuId: sbuId,
						currentPage: this.currentPage,
						pageSize: this.pageSize
				}
				return obj;
			},
			getOperationListCallback: function() {
				return {
			        "success": function (resp) {
			        	var data = resp.responseBody.result.content;
			        	if(!data) {
			        		// Display empty page and init programlist
			        		vm.noRecord = true;
			        		vm.pagination = false;
			        		vm.operationList = [];
			        		return;
			        	}
			        	vm.noRecord = false; // reset
			        	var pageObj = data.page;
			        	vm.$set('page', pageObj);
			        	if(pageObj.totalPageNum>1) {
			        		vm.pagination = true;
			        	}else{
			        		vm.pagination = false;
			        	}
			        	var operationList = data.result;
			        	vm.operationList =  operationList;
			        },
			        "error": function (resp) {
			        	console.log(resp);
			        }
			    };
			},
			searchProgram: function() {
				this.getProgramList();
			},
			searchParticipant: function() {
				var options = {
						url: envConfig.apis.searchNominee.url,
						data: this.searchParticipantData,
						callback: this.searchParticipantCallback
				};
				var input = this.employeeSearch.trim();
				if(input) {
					if(vm.searchNomineeList.length === 0 && vm.currentCountry.participantList.length === 0) { // Confirm if already has nominee or already search nominee
						utils.ajax(options);
	        		}
					else {
						var duplicateSearch = 0;
						var duplicateExists = 0;
						for (var i in vm.searchNomineeList) {
							if((input+'@capgemini.com').toLowerCase() === vm.searchNomineeList[i].email.toLowerCase()) { // If searchNominee has the duplicated record, error
								duplicateSearch++;
							}
						}
						for (var i in vm.currentCountry.participantList) {
							if((input+'@capgemini.com').toLowerCase() === vm.currentCountry.participantList[i].email.toLowerCase()) { // If existNominee has the duplicated recoard, error
								duplicateExists++;
							}
						}
						if(duplicateSearch>0) {
							vm.showError('The nominee already found in below');
						}else if(duplicateExists>0){
							vm.showError('The nominee already exist');
						}else{
							utils.ajax(options);
						}
					}
				}
			},
			searchParticipantData: function(){
				var obj = {};
				obj = {
						name: this.employeeSearch.trim()+'@capgemini.com'
				};
				return obj;
			},
			searchParticipantCallback: function() {
				return {
			        "success": function (resp) {
			        	var data = resp.responseBody.result;
			        	var participantInfo = data.content;
			        	if(participantInfo && participantInfo.length>0) {
			        		// search success
			        		vm.searchNominee = participantInfo[0];
		        			vm.searchNomineeList.push(participantInfo[0]);
			        	}else{
			        		vm.showError('The email address is not correct');
			        	}
			        },
			        "error": function (resp) {
			        	console.log(resp);
			        }
			    };
			},
			selectSwapSbu : function(index) {
				this.selectedSwapSbu = this.otherSbuSeatsList[index];
//				this.getProgramList(targetSbuId, true);
			},
			selectSwapEvent: function(index) {
				this.selectedSwapEvent = this.allEvent[index];
				this.getProgramsByEvent(this.selectedSwapEvent.event_name);
			},
			selectSwapProgram : function(id, name) {
				selectItem = true;
				Vue.nextTick(function(){
					vm.$set('selectedSwapProgram.courseId', id);
					vm.$set('selectedSwapProgram.courseName', name);
				});
				$('#getPrograms').removeClass('open');
//				this.selectedSwapProgram.courseId = id;
//				this.selectedSwapProgram.courseName = name;
			},
			filterFocus: function() {
				$("#targetProgramList").attr("style","");
				if(!$('#getPrograms').hasClass('open')) {
					$('#getPrograms').addClass('open');
				}
//				$("#targetProgramDropdown").trigger('click');
				$("#targetProgramList").scrollTop(0);
			},
			filterBlur: function() {
				selectItem = false;
				var hasChild = $('#targetProgramList').is(':has(li)');
				if(hasChild) {
					return;
				}
				this.clickDropdown();
			},
			clickDropdown: function() {
				if(selectItem) {
					return;
				}
				var text = this.selectedSwapProgram.courseName;
				$("#targetProgramList").hide();
				var hasChild = $('#targetProgramList').is(':has(li)');
				if(hasChild) {
					if(!selectItem) {
						this.selectedSwapProgram.courseName = "";
					}
				}else{
					this.selectedSwapProgram.courseName = "";
				}
				selectItem = false;
			},
			showError: function(errMsg, center) {
        		this.errorMsg = errMsg;
        		if(center) {
        			if(center == 1008){
        				if(!$("#errorMsgToastModal").is(":animated")) {
        					$("#errorMsgToastModal").fadeIn('fast').fadeOut(6000);
						}
        			}else{
        				if(!$("#errorMsgToast").is(":animated")) {
        					$("#errorMsgToast").fadeIn('fast').fadeOut(3000);
						}
        			}
        		}else{
        			if(!$("#errorToast").is(":animated")) {
        				$("#errorToast").fadeIn('fast').fadeOut(3000);
					}
        			if(!$("#errorSbuToast").is(":animated")) {
        				$("#errorSbuToast").fadeIn('fast').fadeOut(3000);
					}
        		}
        	},
        	relateParticipant: function() {
        		if(this.searchNomineeList.length===0) {
        			vm.showError('Please add a nominee');
        			return;
        		}
        		if(this.currentCountry.participantList.length === this.currentCountry.seats) {
        			vm.showError('Your have no seat to add a nominee');
        			return;
        		}
        		var options = {
        				method: "POST",
						url: envConfig.apis.addNomineeForCountry.url,
						data: this.relateParticipantData,
						callback: this.relateParticipantCallback
				};
        		utils.ajax(options);
        	},
        	relateParticipantData: function() {
        		var countryId = this.currentCountry.id;
        		var courseId = this.currentCountry.courseId;
        		var obj = {
        				sbuId: countryId,
        				courseId: courseId,
        				participantList: this.searchNomineeList
//        				loginName: this.searchNominee.cnName,
//        				displayName: this.searchNominee.displayName,
//        				email: this.searchNominee.email
        		};
        		return obj;
        	},
        	relateParticipantCallback: function() {
        		return {
			        "success": function (resp) {
			        	var returnInfo = resp.responseBody.info;
						var statusCode = Number(returnInfo.code);
						if(statusCode === 200) {
							console.log(returnInfo.msg);
							$("#addNomineeModal").modal("hide");
							vm.getAllInfo();
						}
			        },
			        "error": function (resp) {
			        	console.log(resp);
			        }
			    };
        	},
        	confirmSwap: function(action) {
        		if(!this.selectedSwapProgram.courseName) {
        			Vue.nextTick(function(){
        				vm.noSelectProgram = true;
        				setTimeout(function(){
            				vm.noSelectProgram = false;
            			}, 500);
		        	});
        			return;
        		}
        		if(Number(this.swapSeatsCount) === 0) {
        			Vue.nextTick(function(){
        				vm.isZero = true;
        				setTimeout(function(){
            				vm.isZero = false;
            			}, 500);
		        	});
        			return;
        		}
        		
        		this.isZero = false;
        		this.noSelectProgram = false;
        		var options = {
        			method: "POST",
        			url: envConfig.apis.swapSeatsToSbu.url,
        			data: this.confirmSwapData(action),
        			callback: this.confirmSwapCallback
        		};
        		utils.ajax(options);
        	},
        	confirmSwapData: function(action) {
        		var obj = {};
        		obj = {
        				mySbuId: this.currentSbu.id,
        				giveoutCourseId: this.selectedSwapProgram.courseId,
        				swapSbuId: this.selectedSwapSbu.sbu_id,
    					swapCourseId: this.selectedProgram.courseId, 
    					swapSeats: this.swapSeatsCount
        		};
        		return obj;
        	},
        	confirmSwapCallback: function() {
        		return {
			        "success": function (resp) {
			        	var returnInfo = resp.responseBody.info;
						var statusCode = Number(returnInfo.code);
						if(statusCode === 200) {
							console.log(returnInfo.msg);
							$("#swapModal").modal("hide");
							vm.getAllInfo();
						}
			        },
			        "error": function (resp) {
			        	if(!resp.valid) {
			        		// error handler
			        		vm.showError('You have no seats to swap with other SBUs', 1008);
			        	}
			        }
			    };
        	},
        	removeNominee: function(nominee) {
        		this.deleteNominee = nominee;
        		this.isDeleteNominee = true;
        		$("#confirmModal").modal("show");
        	},
        	doAction: function() {
        		if(this.isDeleteNominee) {
        			this.deleteCountryNominee();
        		}else{
        			this.deleteTopSbu();
        		}
        	},
        	deleteCountryNominee: function() {
        		var options = {
        				method: "POST",
        				url: envConfig.apis.removeNomineeForCountry.url,
        				data: this.deleteCountryNomineeData,
        				callback: this.deleteCountryNomineeCallback
        		};
        		utils.ajax(options);
        	},
        	deleteCountryNomineeData: function() {
        		var deleteInfo = this.deleteNominee;
        		var countryId = deleteInfo.sbuId;
        		var courseId = deleteInfo.courseId;
        		var email = deleteInfo.email;
        		var obj = {
        				sbuId: countryId,
        				courseId: courseId,
        				email: email
        		};
        		return obj;
        	},
        	deleteCountryNomineeCallback: function() {
        		return {
			        "success": function (resp) {
			        	var returnInfo = resp.responseBody.info;
						var statusCode = Number(returnInfo.code);
						if(statusCode === 200) {
							console.log(returnInfo.msg);
							$("#confirmModal").modal("hide");
							vm.getAllInfo();
						}
			        },
			        "error": function (resp) {
			        	console.log(resp);
			        }
			    };
        	},
        	deleteTopSbu: function() {
				var options = {
						method: "POST",
						url: envConfig.apis.deleteSbu.url,
						dummyPath: "dummy/deleteSbu.json",
						data: { id: this.deleteSbu.id },
						callback: this.deleteTopSbuCallback
				};
				utils.ajax(options);
			},
			deleteTopSbuCallback: function() {
				return {
					"success": function(resp) {
						var returnInfo = resp.responseBody.info;
						var statusCode = Number(returnInfo.code);
						if(statusCode === 200) {
							console.log(returnInfo.msg);
							$("#confirmModal").modal("hide");
							vm.getSbuList(true);
						}
					},
					"error": function(resp) {
						console.log(resp);
					}
				}
			},
        	selectEmailSuffix: function(index) {
        		this.selectedEmailAddress = this.emails[index].text;
        	},
        	hideRight: function() {
    			if(this.selectedProgram) {
        			this.selectedProgram = null;
    				this.selectedProgramIndex = -1;
        		}
        	},
        	gotop: function() {
//        		$("body").scrollTop(0);
        		$("body").animate({ scrollTop: 0 }, 300);
        	},
        	getSummary: function() {
        		var options = {
        				url: "",
        				dummy: true,
        				dummyPath: "dummy/getSummary.json",
        				data: this.getSummaryData,
        				callback: this.getSummaryCallback
        		};
        		utils.ajax(options);
        	},
        	getSummaryData: function() {},
        	getSummaryCallback: function() {
        		return {
			        "success": function (resp) {
			        	var data = resp.responseBody.result;
			        	
			        	var sbuParticipantSummary = data.sbuParticipant;
			        	var totalParticipants = sbuParticipantSummary.total;
			        	var usedParticipants = sbuParticipantSummary.used;
			        	var availableParticipants = sbuParticipantSummary.available;
			        	var lostParticipants = sbuParticipantSummary.lost;
			        	var participantChart = echarts.init(document.getElementById('participantsChart'));
			        	
		        		var participantOption = {
		        				title: {
		        					text: totalParticipants,
		        					subtext: 'PARTICIPANTS',
		        					x: 'center',
		        					y: 'center',
		        					itemGap: 15,
		        					textStyle: { color: '#49648c', fontSize: 20, fontWeight: 'bolder' },
		        					subtextStyle: { color: '#4a638c', fontSize: 13, fontWeight: 'bolder' }
		        				},
		        			    series: [
		        			        {
		        			            type:'pie',
		        			            radius: ['77%', '95%'],
		        			            hoverAnimation: false,
		        			            label: {
		        			                normal: { show: true, formatter: function(params) {
		        			                	return Math.round(params.percent) + '% \n'+params.name + ' ';
		        			                }, textStyle: { color: "#4d708c", fontSize: 14 } }
		        			            },
		        			            labelLine:{
		        			              normal: { length: 14, length2: 45, lineStyle: { color: "#c4dae8" } }
		        			            },
		        			            data:[
		        			                {value:usedParticipants, name:'USED', itemStyle: {normal: {color:'#3e5172'}}},
		        			                {value:availableParticipants, name:'AVAILABLE', itemStyle: {normal: {color:'#adb5c8'}}},
		        			                {value:lostParticipants, name:'LOST', itemStyle: {normal: {color:'#5299f1'}}}
		        			            ]
		        			        }
		        			    ]
		        			};
		        		participantChart.setOption(participantOption);
		        		
		        		var sbuPmdsSummary = data.sbuPmds;
			        	var totalPmds = sbuPmdsSummary.total;
			        	var usedPmds = sbuPmdsSummary.used;
			        	var availablePmds = sbuPmdsSummary.available;
			        	var lostPmds = sbuPmdsSummary.lost;
		        		var pmdsChart = echarts.init(document.getElementById('pmdsChart'));
		        		var pmdsOption = {
		        				title: {
		        					text: totalPmds,
		        					subtext: 'PMDs',
		        					x: 'center',
		        					y: 'center',
		        					itemGap: 15,
		        					textStyle: { color: '#49648c', fontSize: 20, fontWeight: 'bolder' },
		        					subtextStyle: { color: '#4a638c', fontSize: 13, fontWeight: 'bolder' }
		        				},
		        			    series: [
		        			        {
		        			            type:'pie',
		        			            radius: ['77%', '95%'],
		        			            hoverAnimation: false,
		        			            label: {
		        			                normal: { show: true, formatter: function(params) {
		        			                	return Math.round(params.percent) + '% \n'+params.name + ' ';
		        			                }, textStyle: { color: "#4d708c", fontSize: 14 } }
		        			            },
		        			            labelLine:{
		        			              normal: { length: 14, length2: 45, lineStyle: { color: "#c4dae8" } }  
		        			            },
		        			            data:[
		        			                {value:usedPmds, name:'USED', itemStyle: {normal: {color:'#46a0c2'}}},
		        			                {value:availablePmds, name:'AVAILABLE', itemStyle: {normal: {color:'#c5e3a3'}}},
		        			                {value:lostPmds, name:'LOST', itemStyle: {normal: {color:'#61c0c4'}}}
		        			            ]
		        			        }
		        			    ]
		        			};
		        		pmdsChart.setOption(pmdsOption);
		        		
		        		var countryCharts = data.countryListSummary;
		        		vm.countryCharts = countryCharts;
			        },
			        "error": function (resp) {
			        	console.log(resp);
			        }
			    };
        	},
        	getAdminProgramList: function() {
        		var options = {
        				method: "POST",
        				data: { 
        					currentPage: this.currentPage,
        					pageSize: 20
        				},
						url: envConfig.apis.getAdminProgramList.url,
						callback: this.getAdminProgramListCallback
				};
				utils.ajax(options);
        	},
        	getAdminProgramListCallback: function() {
				return {
					"success": function(resp) {
						var data = resp.responseBody.result.content;
			        	if(!data) {
			        		// No record
			        		vm.pagination = false;
			        		vm.adminProgramList = [];
			        		return;
			        	}else{
			        		var pageObj = data.page;
				        	vm.$set('page', pageObj);
				        	if(pageObj.totalPageNum>1) {
				        		vm.pagination = true;
				        	}else{
				        		vm.pagination = false;
				        	}
				        	var adminProgramList = data.result;
				        	vm.adminProgramList = adminProgramList;
				        	if(!refreshAdminPrograms) {
								vm.getSbuList(true);
							}
			        	}
					},
					"error": function(resp) {
						console.log(resp);
					}
				}
			},
        	getSbuList: function (isAdmin) {
				var options = {
						method: "POST",
						url: envConfig.apis.getSbuList.url,
						data: { parentSbuId: isAdmin ? 0 : this.currentCountry.parentSbuId },
						dummyPath: "dummy/getSbuList.json",
						callback: this.getSbuListCallback
				};
				utils.ajax(options);
			},
			getSbuListCallback: function() {
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
						var sbuList = data.content;
						vm.sbuList = sbuList;
						
						vm.getAllEvent(true);
					},
					"error": function(resp) {
						console.log(resp);
					}
				}
			},
			renameMode: function(index) {
				this.isEdit = true;
			},
			exitRenameMode: function() {
				this.isEdit = false;
			},
			removeSbu: function(index){
				this.isDeleteNominee = false;
				this.deleteSbu = this.sbuList[index];
				$("#confirmModal").modal("show");				
			},
			showUploadModal: function() {
				var uploadFileNode = document.getElementsByClassName('file');
				if(uploadFileNode.length === 1) {
					var cloneNode = uploadFileNode[0].cloneNode(true);
					cloneNode.onchange = this.selectFile;
					uploadFileNode[0].parentNode.replaceChild(cloneNode, uploadFileNode[0]);
				}
				
				this.uploadFileName = "";
				$("#uploadModal").modal("show");
			},
			selectFile: function(e) {
				this.uploadFileName = e.target.value;
			},
			uploadFile: function() {
				var isExcel = this.uploadFileName && 
									(/.xls$/i.test(this.uploadFileName) || 
									/.xlsx$/i.test(this.uploadFileName));
				if(isExcel) {
					this.incorrectFile = false;
					// Upload excel file
					$.ajaxFileUpload({
						url : envConfig.prefix + envConfig.apis.uploadPrograms.url,
						data: {append: this.uploadType},
						secureuri : false,
						fileElementId : 'file',
						dataType : 'json',
						success : function(data, status){
							try{
								var returnInfo = data.info;
								var statusCode = Number(returnInfo.code);
								if(statusCode === 200) {
									var type = this.uploadType;
									if(Number(type) === 1) { // override
										this.adminProgramList = [];
										// get program list again
										refreshAdminPrograms = true;
										this.getAdminProgramList();
									}else { // append
									}
									$('#uploadModal').modal('hide');
								}else {
									console.log('Error Format Excel');
								}
							}catch(e){
								console.log(e);
							}
						}.bind(this),
						error : function(response, status, e){
							console.log('ERROR');
							console.log(response);
							$('#file').trigger('change');
						},
						complete : function(){
							$('#file').trigger('change');
						}
					});
				}else {
					this.incorrectFile = true;
					setTimeout(function(){
        				vm.incorrectFile = false;
        			}, 500);
				}
			},
			editProgram: function(index) {
				// Set value for program detail modal
				var programDetail = this.adminProgramList[index];
				var cloneObj = clone(programDetail); // clone a new obj
				this.programDetail = cloneObj;
//				Vue.nextTick(function() {
//					$("#startTime").datepicker({
//						format: "yyyy-mm-dd",
//						todayHighlight: true,
////						minViewMode: 0,
//						autoclose: true
//					});
//					
//					$("#endTime").datepicker({
//						format: "yyyy-mm-dd",
//						todayHighlight: true,
////						minViewMode: 0,
//						autoclose: true
//					});
//					
//					$("#startTime").on("changeDate", function(){
//						$("#endTime").datepicker('setStartDate', $('#startTime').val());
//					});
//					
//					$("#endTime").on("changeDate", function(){
//						$("#startTime").datepicker('setEndDate', $('#endTime').val());
//					});
//				});
				$("#programDetailModal").modal("show");
			},
			submitEditProgram: function() {
				// updateProgram
				var options = {
						method: "POST",
						url: envConfig.apis.updateProgram.url,
						data: this.submitEditProgramData,
						callback: this.submitEditProgramCallback
				};
				utils.ajax(options);
			},
			submitEditProgramData: function(){
				var obj = {};
				var submitProgramDetail = this.programDetail;
				obj = {
						id: submitProgramDetail.id,
						eventName: submitProgramDetail.eventName,
						name: submitProgramDetail.name,
						url: submitProgramDetail.url,
						startTime: submitProgramDetail.startTime,
						endTime: submitProgramDetail.endTime,
						duration: submitProgramDetail.duration
				};
				return obj;
			},
			submitEditProgramCallback: function() {
				return {
					"success": function(resp) {
						var returnInfo = resp.responseBody.info;
						var statusCode = Number(returnInfo.code);
						if(statusCode === 200) {
							console.log(returnInfo.msg);
							vm.getAdminProgramList();
							$("#programDetailModal").modal("hide");
						}
					},
					"error": function(resp) {
						console.log(resp);
					}
				}
			},
			submitAddSbu: function() {
				if(!$.trim(this.newSbu.name)) {
					vm.$set('newSbu.noValue', true);
					setTimeout(function(){
						vm.$set('newSbu.noValue', false);
        			}, 500);
					return;
				}
				// check duplicated with current sbu list
				var input = this.newSbu.name.trim();
				var duplicateExists = 0;
				for (var i in vm.sbuList) {
					if(input.toLowerCase() === vm.sbuList[i].subName.toLowerCase()) {
						duplicateExists++;
					}
				}
				if(duplicateExists>0){
					vm.showError('The sbu already exist');
				}else{
					vm.$set('newSbu.noValue', false);
					this.addNewSbu(true);
				}
			},
			addNewSbu: function(isAdmin) {
				var options = {
						method: 'POST',
						url: envConfig.apis.addSbu.url,
						data: {
							parentId: isAdmin ? 0 : this.currentCountry.parentSbuId,
							subName: this.newSbu.name
						},
						callback: this.addNewSbuCallback
				};
				utils.ajax(options);
			},
			addNewSbuCallback: function() {
				return {
					"success": function(resp) {
						var returnInfo = resp.responseBody.info;
						var statusCode = Number(returnInfo.code);
						if(statusCode === 200) {
							console.log(returnInfo.msg);
							$("#addSbuModal").modal("hide");
							vm.getSbuList(true);
						}
					},
					"error": function(resp) {
						console.log(resp);
					}
				}
			}
		}
	});
	
	function doLogout() {
		window.sessionStorage.clear();
		window.localStorage.clear();
		location.href = "j_spring_security_logout";
	}
	
	function initModalPosition() {
		$(".modal").on("show.bs.modal", reposition);
		// Reposition when the window is resized
		$(window).on("resize", function(){
			$(".modal:visible").each(reposition);
			var headHeight = $('#top').height()
			var moduleOffsetTop = $('#moduleBar').offset().top;
			if(moduleOffsetTop === headHeight) {
				$('.container-fluid').css({
					"margin-top": headHeight + 20 + "px"
				});
			}else {
				$('.container-fluid').css({
					"margin-top": headHeight + "px"
				});
			}
		});
		function reposition() {
			var modal = $(this), dialog = modal.find(".modal-dialog");
			modal.css("display", "block");
			dialog.css("margin-top", Math.max(0, ($(window).height() - dialog.height()) / 2));
		}
	}
	
	var datePicker = {
			format: 'yyyy-mm-dd',
			orientation: 'bottom right',
			autoclose: true,
			keyboardNavigation: false,
			todayHighlight: true
	};
	
	var dpStart = $('#startTime').datepicker(datePicker)
	.on('changeDate', function(e){
		vm.programDetail.startTime = $(e.target).val();
		dpEnd.datepicker('setStartDate', vm.programDetail.startTime);
		dpEnd.trigger('changeDate');
	});
	var dpEnd = $('#endTime').datepicker(datePicker)
	.on('changeDate', function(e) {
		if(vm.programDetail.startTime>vm.programDetail.endTime) {
			vm.programDetail.endTime = vm.programDetail.startTime
		}
	});
	
	$('#programDetailModal').on('shown.bs.modal', function () {
		var formatedStartTime = DateFormat.format.date(new Date(vm.programDetail.startTime), "yyyy-MM-dd");
		var formatedEndTime = DateFormat.format.date(new Date(vm.programDetail.endTime), "yyyy-MM-dd");
		dpStart.datepicker('setDate', formatedStartTime);
		dpEnd.datepicker('setDate', formatedEndTime);
	});
	
	function clone(fromObj){  
		if (typeof (fromObj) != 'object'){
			return fromObj;
		}
		if (fromObj == null){
			return fromObj;
		}
		var toObj = new Object();
		for ( var i in fromObj)
			toObj[i] = clone(fromObj[i]);
		return toObj;
	}
	
	$(window).scroll(function() {
		var isProgramPage = !$('#program').hasClass("hidden");
		if(isProgramPage) {
			if (window.scrollY > 1200) {
				$(".gotop-btn").addClass('is-visible');
			}else{
				$(".gotop-btn").removeClass('is-visible');
			}
		}
	});
})()