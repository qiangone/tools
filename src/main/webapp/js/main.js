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
//				console.log("info.c = " + info.c + " ,info.t = " + info.t);
				if(info.c ==0 || info.t == 0){
					$(this.el).css('width', 0);
				}else{
					$(this.el).css('width', Math.floor(info.c/info.t*100) + '%');
				}
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
			     { name: 'dashboard', active: true, hide: false},
			     { name: 'program', active: false, hide: false},
			     { name: 'assignment', active: false, hide: false},
			     { name: 'history', active: false, hide: false},
			     { name: 'admin', active: false, hide: false}
			],
			emails: [
			     { text: '@capgemini.com' },
			     { text: '@sogeti.com' },
			     { text: '@prosodie.com' }
			],
			freeze: false,
			selectedType: {},
			selectedAdminType: {},
			selectedEmailAddress: null,
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
			markProgramList: [],
			markAttendeesWidthSbuList: [],
			countryCharts: [],
			programList : [],
			otherSbuSeatsList: [],
			sbuCourseInfo: null,
			countryRemainingSeats: 0,
			countrySeatsList: [],
			nomineeList: [],
			deleteNominee: {},
			deleteSbu: {},
			isDeleteNominee: true,
			isDeleteCourse: false,
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
			user: {},
			noSwapEvent: false,
			typeList: [
				{ text: 'All' },
				{ index: 3, text: 'Upcoming' },
				{ index: 2, text: 'Ongoing' },
				{ index: 1, text: 'Closed' }
			],
			sbuList: [],
			sbuArrList: [],
			isEdit: false,
			uploadFileName: '',
			incorrectFormat: false,
			uploadType: '',
			programDetail: {},
			editSbuDetail: {},
			noSwapSeats: true,
			newSbu: {},
			lbpInfo: {},
			currentSwitchSbu: null,
			currentAdminEvent: null,
			isMultiRole: false,
			selectedAdminProgram: null,
			selectedAdminProgramIndex: -1,
			forSwapProgramList: [],
			freeSeatProgramList: [],
			freeSeatProgram: null,
			countryListForFree: [],
			programType: 1, // 0 means program list; 1 means for swap program; 2 means free seat program
			selectedProgramFromSwap: null,
			swapSuccess: false
		},
		ready : function() {
			initModalPosition();
			initRightSlidePosition();
			
			// Get Request
			this.getCurrentSbu();
			this.selectedEmailAddress = this.emails[0];
			this.selectedType = this.typeList[1];
			this.selectedAdminType = this.typeList[0];
		},
		computed: {
			countryRemainingSeats: { // Calculate the number of remaining seats
				get: function() {
					if(this.sbuCourseInfo){
						if(this.noLimitation) {
							var result = this.sbuCourseInfo.seats;
							var countrySeatsListNumber = this.countrySeatsList.length;
							for(var i=0;i<countrySeatsListNumber;i++){
								if(this.countrySeatsList.length>0) {
									result -= this.countrySeatsList[i].seats;
								}
							}
							if(this.currentSbu.forSwapNumber !== undefined) {
								result -= this.currentSbu.forSwapNumber;
							}
							return result;
						}else{
							return this.sbuCourseInfo.seats-this.sbuCourseInfo.assignSeats;
						}
					}
					return 0;
				}
			},
			isDetailShow: function() { // Control the slide for the right part
				if(this.selectedProgram) {
					calcTop();
				}
				return this.selectedProgram ? true : false;
			},
			isAttendeeShow: function() {
				if(this.selectedAdminProgram) {
					calcTop();
				}
				return this.selectedAdminProgram ? true: false;
			},
			isFreeSeatNomineeShow: function() {
				if(this.freeSeatProgram) {
					calcTop();
				}
				return this.freeSeatProgram ? true: false;
			},
			loopParticipant: function() {
				var arr = this.markAttendeesWidthSbuList;
				for (var i in arr) {
					var attendeesLength = arr[i].participantList.length;
					if (attendeesLength>0) {
						return true;					}
				}
				return false;
			},
//			canFreeze: function() { // If ongoing, don't freeze delete function
//			},
		},
		methods : {
			selectType: function(i) { // Select program type on the program list page
				this.query = '';
				this.selectedType = this.typeList[i];
				this.getProgramList();
			},
			selectAdminType: function(i) {
				this.selectedAdminType = this.typeList[i];
				this.getProgramsByEvent(this.currentAdminEvent);
			},
			limitNumber: function() { // Limit the swap number of swap page
				if(this.programType === 2) { // for swap seats
					if(this.swapSeatsCount > this.selectedProgramFromSwap.swap_seats) {
						this.swapSeatsCount = this.selectedProgramFromSwap.swap_seats;
					}
				}else{
					if(this.swapSeatsCount > this.currentSbu.forSwapNumber) {
						this.swapSeatsCount = this.currentSbu.forSwapNumber;
					}
				}
			},
			initialData: function() {
				// Init pagination
				this.page = {};
				this.currentPage = 1;
				this.pagination = false;
				// Init program page
				this.selectedProgram = null;
				this.selectedProgramIndex = -1;
				this.selectedAdminProgram = null;
				this.markAttendeesWidthSbuList = [];
				this.selectedAdminProgramIndex = -1;
				this.selectedType = this.typeList[1];
				this.freeSeatProgram = null;
				// Init data
				this.allEvent = [];
				this.eventList = [];
				this.noRecord = false;
				this.freeze = false;
			},
			gotoPage: function(index, position) { // Click the top menu, switch to corresponding page
				this.initialData();
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
					if(position === 2){
						this.programType = 2;
						this.getSwapProgramList();
					}else if(position === 3){
						this.programType = 3;
						this.getFreeSeatProgramList();
					}else {
						this.programType = 1;
						this.getProgramList();
					}
				}
				if(this.currentIndex === 2) { // Assignment
					this.getSbuList();
				}
				if(this.currentIndex === 3) { // History
					this.getOperationList();
				}
				if(this.currentIndex === 4) { // Admin
					// Update menu sbulist for admin
					this.currentSwitchSbu = null;
					this.$set('modules[1].hide', true);
					
					refreshAdminPrograms = false;
					this.currentAdminEvent = null;
					this.getAdminProgramList();
				}
			},
			getPerSbuInfo: function(perSbuInfo) {
				this.sbuCourseInfo = null;
				if(perSbuInfo) {
		        	if(typeof perSbuInfo === "string") {
		        		// No permission
		        		return;
		        	}
		        	var parentId = perSbuInfo.parentId;
		        	if(parentId === 0) { // First level
		        		vm.noLimitation = true;
		        		vm.currentSbu = perSbuInfo;
		        		vm.$set('currentSbu.parentSbuId', parentId);
		        	}else{ // Second level
		        		vm.noLimitation = false;
		        		var parentInfo = perSbuInfo.parentSbu;
		        		vm.currentSbu = parentInfo;
		        		vm.currentCountry = {
		        				id: perSbuInfo.id,
		        				countryName: perSbuInfo.subName,
		        				parentSbuId: perSbuInfo.parentId
		        		};
		        	}
		        	vm.sbuId = perSbuInfo.id;
		        	
		        	// If not admin
		        	vm.$set('modules[1].hide', false); // Show program page if redirect from admin page
		        	if(vm.user.role === 1) { // if admin login, always show admin page
		        		vm.$set('modules[4].hide', false);
		        	}else{
		        		vm.$set('modules[4].hide', true); // hide admin page
		        	}
		        	if(vm.noLimitation) {
		        		vm.$set('modules[2].hide', false);
		        	}else{ // If second level sbu, hide assignment page
		        		vm.$set('modules[2].hide', true);
		        	}
		        	vm.gotoPage(0);
				}else{ // is admin
					vm.$set('modules[1].hide', true); // Hide program page
					vm.$set('modules[4].hide', false); // Show admin page
					vm.gotoPage(4); // Go to admin page
				}
        		
	        	$("body").scrollTop(0);
			},
			getCurrentSbu: function () {
				var options = {
						url: envConfig.apis.getCurrentSbu.url,
						dummyPath: "dummy/getCurrentSBU.json",
						callback: this.getCurrentSbuCallback
				};
				utils.ajax(options);
			},
			getCurrentSbuCallback: function() { // Get current sbu information when login
				return {
			        "success": function (resp) {
			        	var data = resp.responseBody.result;
			        	var sbuData = data.content;
			        	if(typeof sbuData === "string") {
			        		// No permission, how to display
			        		return;
			        	}
			        	var loginInfo = sbuData.lbps;
			        	vm.user = loginInfo;
			        	var sbuInfo = sbuData.sbuList;
			        	var perSbuInfo = sbuInfo[0];
			        	
			        	if(sbuInfo && sbuInfo.length ===1) {
				        	vm.isMultiRole = false;
				        	vm.currentSwitchSbu = perSbuInfo;
			        	}else if(sbuInfo && sbuInfo.length > 1) {
			        		// Multiple SBU owner
			        		vm.isMultiRole = true;
			        		vm.sbuArrList = sbuInfo;
			        		vm.sbuList = sbuInfo;
			        		if(sbuData.role.toLowerCase() === "admin") {
				        		vm.currentIndex = 4;
				        		vm.currentSwitchSbu = null;
				        	}else {
				        		vm.currentSwitchSbu = perSbuInfo;
				        	}
			        	}
			        	
			        	vm.getPerSbuInfo(vm.currentSwitchSbu);
			        	
			        	$("body").show();
			        	$("body").scrollTop(0);
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
			 * param 'type' means get event list by type (all, upcoming, ongoing, closed)
			 */
			getAllEvent: function(type) {
				var options = {
						url: envConfig.apis.getAllEvent.url,
						data: this.getAllEventData(type),
						callback: this.currentSwitchSbu ? this.getAllEventCallback : this.getAllEventByAdminCallback
				};
				utils.ajax(options);
			},
			getAllEventData: function(type) { // 1: Closed; 2: Ongoing; 3: Upcoming
				var type = type;
				var obj = {};
				var currentDisplay = this.currentSwitchSbu;
				var fromAdmin = false;
				if(!currentDisplay){
					fromAdmin = true;
				}
				if(fromAdmin) {
					obj = {
							type: type ? type : ''
					}
				}else{
					obj = {
							type: type ? type : '',
							sbuId: this.currentSwitchSbu.id
					}
				}
				return obj;
			},
			getAllEventCallback: function() { // Get all event for swap function
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
								vm.getProgramsByEvent(vm.selectedSwapEvent);
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
			getAllEventByAdminCallback: function() { // Get all event for admin page
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
						var allEvent = data.content;
						vm.allEvent = allEvent;
						if(allEvent && allEvent.length>0) {
							vm.noRecord = false;
						}else {
							vm.noRecord = true;
						}
					},
					"error": function(resp) {
						console.log(resp);
					}
				}
			},
			getProgramsByEvent: function(cEvent) {
				if(!this.currentSwitchSbu) {
					this.currentAdminEvent = cEvent;
				}
				var options = {
						url: envConfig.apis.getProgramListByEvent.url,
						data: this.getProgramsData(cEvent),
						callback: this.currentSwitchSbu ? this.getProgramListByEventCallback : this.getProgramListByEventByAdminCallback 
				};
				utils.ajax(options);
			},
			getProgramsData: function(cEvent) {
				var obj = {};
				var currentDisplay = this.currentSwitchSbu;
				var fromAdmin = false;
				if(!currentDisplay){
					fromAdmin = true;
				}
				var type = '';
				if(fromAdmin) {
					type = this.selectedAdminType.index ? this.selectedAdminType.index : '';
					if(!this.currentAdminEvent && !this.selectedAdminType.index) {
						obj = {type: type}
					}else{
						obj = {
							eventName : cEvent ? cEvent.event_name : '',
							type : type 
						};
					}
				}else{
					type = '3'; // type is 3 means get upcomming programs for swap function
					obj = {
						eventName : cEvent ? cEvent.event_name : '',
						sbuId : fromAdmin ? '' : this.currentSbu.id,
						type : type 
					};
				}
				return obj;
			},
			getProgramListByEventCallback: function() {   //Get program list by event name for swap function, remove current selected program
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
						var programList = data.content;
						// GET SWAP FLOW
		        		vm.targetSwapPrograms = []; // reset the targetSwapPrograms list
		        		var currentCourse = null;
		        		if(vm.programType === 2) {
		        			currentCourse = vm.selectedProgramFromSwap;
		        		}else{
		        			currentCourse = vm.selectedProgram;
		        		}
						var programs = [];
						var removeDefault = function() {
							if(programList && programList.length > 0) {
								var programListLength = programList.length;
								for(var i=0;i<programListLength;i++) {
//								for (var i in programList) {
									if(programList[i].name.toLowerCase() === currentCourse.courseName.toLowerCase()) {
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
			getProgramListByEventByAdminCallback: function() { // Get the program list by event name by admin
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
						var programList = data.content;
						vm.markProgramList = programList;
						var programNum = programList.length;
						if(programNum>0) {
							vm.noRecord = false;
						}else{
							vm.noRecord = true;
						}
//						vm.pagination = false;
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
				var sbuId = "";
				if(this.noLimitation) {
					sbuId = this.currentSbu.id;
				}else{
					sbuId = this.currentCountry.id;
				}
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
			getProgramListCallback: function() { // Get program list
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
			        },
			        "error": function (resp) {
			        	console.log(resp);
			        }
			    };
			},
			getSwapProgramList: function() {
				var options = {
					url: envConfig.apis.getSwapList.url,
					data: {sbuId: this.currentSbu.id},
					callback: this.getSwapProgramListCallback
				};
				utils.ajax(options);
			},
			getSwapProgramListCallback: function() {
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
						var forSwapProgramList = data.content;
						if(forSwapProgramList.length>0) {
							vm.noRecord = false;
							vm.forSwapProgramList = forSwapProgramList;
						}else{
							vm.noRecord = true;
							vm.forSwapProgramList = [];
						}
					},
					"error": function(resp) {
						console.log(resp);
					}
				}
			},
			getFreeSeatProgramList: function() {
				var options = {
						url: envConfig.apis.getFreeSeatProgramList.url,
						dummyPath: "dummy/getFreeSeatProgramList.json",
						callback: this.getFreeSeatProgramListCallback
				};
				utils.ajax(options);
			},
			getFreeSeatProgramListCallback: function() {
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
						var freeSeatProgramList = data.content;
						if(freeSeatProgramList.length>0) {
							vm.noRecord = false;
							vm.freeSeatProgramList = freeSeatProgramList;
						}else{
							vm.noRecord = true;
							vm.freeSeatProgramList = [];
						}
					},
					"error": function(resp) {
						console.log(resp);
					}
				}
			},
			showDetail: function(event, index, esc) { // Show program detail information for right part
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
				var sbuId = "";
				if(this.noLimitation) {
					sbuId = this.currentSbu.id;
				}else{
					sbuId = this.currentCountry.id;
				}
				obj = {
					sbuId: sbuId,
					courseId: this.selectedProgram.courseId
				};
				return obj;
			},
			getAllInfoCallback: function() { // Get all the information for right slide part for program
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
						vm.sbuCourseInfo = sbuCourse;
						
						// Below for country level sbu
						if(dataList.participantList) {
//							vm.currentCountry.participantList = dataList.participantList; // This not work, but below works. why?
							vm.$set('currentCountry.participantList', dataList.participantList);
						}
						vm.currentCountry.courseId = sbuCourse.courseId;
						vm.currentCountry.seats = sbuCourse.seats;
						$("#allInfo").scrollTop(0);
						
						var currentIndex = vm.selectedType.index;
						
						if(currentIndex !== 3) {
							vm.freeze = true;
						}else{
							vm.freeze = false;
						}
						vm.eventList[vm.selectedProgram.event_name].results[vm.selectedProgramIndex].seats = vm.sbuCourseInfo.seats;
						vm.eventList[vm.selectedProgram.event_name].results[vm.selectedProgramIndex].remainedSeats = vm.countryRemainingSeats;
					},
					"error": function(resp){
						console.log(resp)
					}
				};
			},
			swapSeats: function(index) {
				this.selectedSwapSbu = this.otherSbuSeatsList[index];
//				var targetSbuId = this.selectedSwapSbu.id;
				this.getAllEvent("3");
//				this.getProgramList(targetSbuId, true);
			},
			minusCount: function(obj) { // Decrease assign seats for country
				if(obj.seats !== undefined && obj.seats != 0) { // Country Info
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
			plusCount: function(obj) { // Increase assign seats for country
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
			assignSeatsCallback: function() { // Assign seats for country
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
			updateSwapSeatsCallback: function() { // Assign swap seats for program of current sbu
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
			addNominee: function(countryInfo) { // Add nominee for country
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
			openAddNominee: function(freeProgram) {
				vm.freeSeatProgram = freeProgram;
				if(vm.noLimitation) {
					vm.getSbuList();
				}else{
					vm.listParticipant();
				}
			},
			addSbu: function() { // Add sbu for admin
				this.newSbu = {};
				$("#addSbuModal").modal("show");
			},
			getOperationList: function() {
				if(!this.currentSwitchSbu) {
					// get all the history
					var options = {
							method: "POST",
							url: envConfig.apis.getOperationListByAdmin.url,
							dummyPath: "dummy/getOperationList.json",
							data: { currentPage: this.currentPage, pageSize: this.pageSize },
							callback: this.getOperationListCallback
					};
				}else {
					var options = {
							method: "POST",
							url: envConfig.apis.getOperationList.url,
							dummyPath: "dummy/getOperationList.json",
							data: this.getOperationListData,
							callback: this.getOperationListCallback
					};
				}
				utils.ajax(options);
			},
			getOperationListData: function() {
				var sbuId = "";
	        	if(this.noLimitation) { // If not admin and if is first level sbu
	        		sbuId = this.currentSwitchSbu.id;
	        	}else{
	        		sbuId = this.currentSwitchSbu.parentSbu.id;
	        	}
				var obj = {
						sbuId: sbuId,
						currentPage: this.currentPage,
						pageSize: this.pageSize
				}
				return obj;
			},
			getOperationListCallback: function() { // Get the operation list for admin and sbu
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
			searchParticipant: function() { // Use email to search nominee
				var options = {
						url: envConfig.apis.searchNominee.url,
						data: this.searchParticipantData,
						callback: this.searchParticipantCallback
				};
				var input = this.employeeSearch.trim();
				if(input) {
					var text = vm.selectedEmailAddress.text;
					var wholeEmail = input + text;
					var nominees = [];
					nominees = vm.currentCountry.participantList;
					if(vm.searchNomineeList.length === 0 && nominees.length === 0) { // Confirm if already has nominee or already search nominee
						if(text !== '@capgemini.com'){ 
							var externalEmail = vm.getExternalEmail(wholeEmail);
							vm.searchNomineeList.push(externalEmail);
						}else{
							utils.ajax(options);
						}
	        		}else{
	        			this.checkAddNomineeError(wholeEmail, options, nominees);
	        		}
				}
			},
			checkAddNomineeError: function(email, options, nominees) {
				var emailAddress = email;
				var suffix = this.selectedEmailAddress.text;
				var duplicateSearch = 0;
				var duplicateExists = 0;
				
				for (var i in vm.searchNomineeList) {
					if(emailAddress.toLowerCase() === vm.searchNomineeList[i].email.toLowerCase()) { // If searchNominee has the duplicated record, error
						duplicateSearch++;
					}
				}
				for (var i in nominees) {
					if(emailAddress.toLowerCase() === nominees[i].email.toLowerCase()) { // If existNominee has the duplicated recoard, error
						duplicateExists++;
					}
				}
				if(duplicateSearch>0) {
					vm.showError('The nominee already found in below');
				}else if(duplicateExists>0){
					vm.showError('The nominee already exist');
				}else{
					if(suffix === '@capgemini.com') { // cg email, call ajas to search from ldap
						utils.ajax(options);
					}else{ // other email, just push
						var externalEmail = vm.getExternalEmail(email);
						vm.searchNomineeList.push(externalEmail);
					}
				}
			},
			getExternalEmail: function(email) {
				var obj = {};
				obj = {
						email: email,
						displayName: email
				};
				return obj;
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
			btnSwap: function(forSwapProgram) {
				this.selectedProgramFromSwap = forSwapProgram;
				this.getAllEvent("3");
			},
			selectSwapSbu : function(index) {
				this.selectedSwapSbu = this.otherSbuSeatsList[index];
			},
			selectSwapEvent: function(index) {
				this.selectedSwapEvent = this.allEvent[index];
				this.getProgramsByEvent(this.selectedSwapEvent);
			},
			selectSwapProgram : function(id, name, canSwapSeats) {
				selectItem = true;
				Vue.nextTick(function(){
					vm.$set('selectedSwapProgram.courseId', id);
					vm.$set('selectedSwapProgram.courseName', name);
					vm.$set('selectedSwapProgram.canSwapNum', canSwapSeats);
				});
				$('#getPrograms').removeClass('open');
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
        	relateParticipant: function() { // Add nominee for country
        		if(this.searchNomineeList.length===0) {
        			vm.showError('Please add a nominee');
        			return;
        		}
        		var nominees = [];
        		nominees = vm.currentCountry.participantList;
				var nomineeNum = nominees.length;
				for(var i in nominees){
					if(nominees[i].source === 1) {
						nomineeNum--; // Remove the nominees who added from free seat 
					}
				}
        		if(nomineeNum === this.currentCountry.seats) {
        			vm.showError('Your have no seat to add a nominee');
        			return;
        		}
        		var options = {};
        		if(this.programType === 3) {
        			options = {
        					method: "POST",
    						url: envConfig.apis.takeFreeSeat.url,
    						data: this.relateParticipantData,
    						callback: this.takeFreeSeatCallback
    				};
        		}else{
        			options = {
            				method: "POST",
    						url: envConfig.apis.addNomineeForCountry.url,
    						data: this.relateParticipantData,
    						callback: this.relateParticipantCallback
    				};
        		}
        		
        		utils.ajax(options);
        	},
        	relateParticipantData: function() {
        		var countryId = this.currentCountry.id;
        		var courseId;
        		if(this.programType === 3) {
        			courseId = this.freeSeatProgram.courseId;
        		}else{
        			courseId = this.currentCountry.courseId;
        		}
        		var obj = {
        				sbuId: countryId,
        				courseId: courseId,
        				participantList: this.searchNomineeList
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
			        	vm.showError('Your have no seat to add a nominee');
	        			return;
			        }
			    };
        	},
        	takeFreeSeatCallback: function() {
        		return {
			        "success": function (resp) {
			        	var returnInfo = resp.responseBody.info;
			        	var statusCode = Number(returnInfo.code);
						if(statusCode === 200) {
							console.log(returnInfo.msg);
							var updateNumber = vm.searchNomineeList.length;
							vm.$set('freeSeatProgram.seats', vm.freeSeatProgram.seats-updateNumber);
							$("#addNomineeModal").modal("hide");
							if(vm.noLimitation) {
								vm.getSbuList();
							}else{
								vm.listParticipant();
							}
						}
			        },
			        "error": function (resp) {
			        	console.log(resp);
			        }
			    };
        	},
        	confirmSwap: function(action) { // Swap seat
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
        		var swapSbuId, swapCourseId;
        		if(this.programType === 2) {
        			swapSbuId = this.selectedProgramFromSwap.sbuId;
        			swapCourseId = this.selectedProgramFromSwap.courseId;
        		}else{
        			swapSbuId = this.selectedSwapSbu.sbu_id;
        			swapCourseId = this.selectedProgram.courseId;
        		}
        		obj = {
        				mySbuId: this.currentSbu.id,
        				giveoutCourseId: this.selectedSwapProgram.courseId,
        				swapSbuId: swapSbuId,
    					swapCourseId: swapCourseId, 
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
							if(vm.programType === 2) {
								vm.gotoPage(1, 2);
							}else{
								vm.getAllInfo();
							}
							vm.swapSuccess = true;
							setTimeout(function(){
		        				vm.swapSuccess = false;
		        			}, 1500);
						}
			        },
			        "error": function (resp) {
			        	vm.swapSuccess = false;
			        	if(!resp.valid) {
			        		// error handler
			        		if(Number(resp.errorCode)===1008) {
			        			vm.showError('You have no seats to swap with other SBUs', 1008);
			        		}else{
			        			vm.showError('Swap failed, please try again later', 1008);
			        		}
			        	}
			        }
			    };
        	},
        	removeNominee: function(nominee) { // Remove nominee for country
        		this.deleteNominee = nominee;
        		this.isDeleteNominee = true;
        		this.isDeleteCourse = false;
        		$("#confirmModal").modal("show");
        	},
        	doAction: function() {
        		if(this.isDeleteNominee) {
        			this.deleteCountryNominee();
        		}else if (this.isDeleteCourse){
        			this.deleteCourse();
        		}else {
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
							if(vm.programType === 3) {
								vm.$set('freeSeatProgram.seats', vm.freeSeatProgram.seats+1);
								if(vm.noLimitation) {
									vm.getSbuList();
								}else{
									vm.listParticipant();
								}
							}else{
								vm.getAllInfo();
							}
						}
			        },
			        "error": function (resp) {
			        	console.log(resp);
			        }
			    };
        	},
        	deleteTopSbu: function() { // Delete sbu
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
							vm.getSbuList();
						}
					},
					"error": function(resp) {
						console.log(resp);
					}
				}
			},
        	selectEmailSuffix: function(index) { // Select email suffix
        		this.selectedEmailAddress = this.emails[index];
        	},
        	hideRight: function() {
    			if(this.selectedProgram) {
        			this.selectedProgram = null;
    				this.selectedProgramIndex = -1;
        		}
    			if(this.selectedAdminProgram) {
    				this.selectedAdminProgram = null;
    				this.selectedAdminProgramIndex = -1;
    				this.markAttendeesWidthSbuList = [];
    			}
    			if(this.freeSeatProgram) {
        			this.freeSeatProgram = null;
        		}
        	},
        	gotop: function() { // Go top function
        		$("body").animate({ scrollTop: 0 }, 300);
        	},
        	getSummary: function() { // Get dashboard data
        		var options = {
//        				dummy: true,
        				url: envConfig.apis.dashboard.url,
        				dummyPath: "dummy/getSummary.json",
        				data: { sbuId:  this.currentSwitchSbu? this.currentSwitchSbu.id : 0 },
        				callback: this.getSummaryCallback
        		};
        		utils.ajax(options);
        	},
        	getSummaryCallback: function() {
        		return {
			        "success": function (resp) {
			        	var data = resp.responseBody.result;
			        	var chartData = data.content;
			        	
			        	var availableParticipants = chartData.total_avaliable_seats;
			        	var lostParticipants = chartData.total_lost_seats;
			        	var usedParticipants = chartData.total_used_seats;
			        	var totalParticipants = availableParticipants + lostParticipants + usedParticipants;
			        	
//			        	var sbuParticipantSummary = data.sbuParticipant;
//			        	var totalParticipants = sbuParticipantSummary.total;
//			        	var usedParticipants = sbuParticipantSummary.used;
//			        	var availableParticipants = sbuParticipantSummary.available;
//			        	var lostParticipants = sbuParticipantSummary.lost;
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
		        			            radius: ['62%', '80%'],
		        			            hoverAnimation: false,
//		        			            minAngle: 5,
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
		        		
//		        		var sbuPmdsSummary = data.sbuPmds;
//			        	var totalPmds = sbuPmdsSummary.total;
//			        	var usedPmds = sbuPmdsSummary.used;
//			        	var availablePmds = sbuPmdsSummary.available;
//			        	var lostPmds = sbuPmdsSummary.lost;
		        		
			        	var availablePmds= chartData.total_avaliable_pmds;
			        	var lostPmds = chartData.total_lost_pmds;
			        	var usedPmds = chartData.total_used_pmds;
			        	var totalPmds = availablePmds + lostPmds + usedPmds;
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
		        			            radius: ['62%', '80%'],
		        			            hoverAnimation: false,
//		        			            minAngle: 5,
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
		        		
		        		var countryCharts = chartData.sbuList;
		        		vm.countryCharts = countryCharts;
			        },
			        "error": function (resp) {
			        	console.log(resp);
			        }
			    };
        	},
        	getAdminProgramList: function() { // Get program list for admin
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
			        		vm.noRecord = true;
			        		vm.pagination = false;
			        		vm.adminProgramList = [];
			        		vm.markProgramList = [];
			        		return;
			        	}else{
			        		vm.noRecord = false;
			        		var pageObj = data.page;
				        	vm.$set('page', pageObj);
				        	if(pageObj.totalPageNum>1) {
				        		vm.pagination = true;
				        	}else{
				        		vm.pagination = false;
				        	}
				        	var adminProgramList = data.result;
				        	vm.adminProgramList = adminProgramList;
				        	vm.markProgramList = adminProgramList;
				        	if(!refreshAdminPrograms) {
								vm.getAllEvent();
							}
			        	}
					},
					"error": function(resp) {
						console.log(resp);
					}
				}
			},
        	getSbuList: function () {
				var options = {
						method: "POST",
						url: envConfig.apis.getSbuList.url,
//						data: { parentSbuId: this.currentSwitchSbu ? this.currentSwitchSbu.id : 0 },
						data: this.getSbuListData,
						dummyPath: "dummy/getSbuList.json",
						callback: this.getSbuListCallback
				};
				utils.ajax(options);
			},
			getSbuListData: function() {
				var obj = {};
				obj = {
						parentSbuId: this.currentSwitchSbu ? this.currentSwitchSbu.id: 0
				};
				var programType = this.programType;
				if(programType === 3) {
					obj.courseId = this.freeSeatProgram.courseId;
				}
				return obj;
			},
			getSbuListCallback: function() {
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
						var sbuList = data.content;
						vm.sbuList = sbuList;
						if(sbuList.length>0) {
							var countryList = sbuList.slice(0, sbuList.length);
							vm.countryListForFree = countryList;
						}
						if(vm.currentIndex === 2 || vm.programType === 3) { // If assignment page or free seats program list page,  needn't to get all event list
							return;
						}
						vm.getAllEvent();
					},
					"error": function(resp) {
						console.log(resp);
					}
				}
			},
			listParticipant: function() {
				var options = {
						url: envConfig.apis.listParticipant.url,
						data: {
							sbuId: this.currentSwitchSbu.id,
							courseId: this.freeSeatProgram.courseId
						},
						callback: this.listParticipantCallback
				}
				utils.ajax(options);
			},
			listParticipantCallback: function() {
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
						var participants = data.content;
						vm.$set('currentCountry.participantList', participants);
					},
					"error": function(resp) {
						console.log(resp);
					}
				}
			},
			renameMode: function(index) {
				var editSbuDetail = clone(this.sbuList[index]);
				this.editSbuDetail = editSbuDetail;
				this.editSbuDetail.arrIndex = index;
				vm.$set('sbuList[' + index + '].isEdit', true);
				setTimeout(function() {
					$('.rename-text').focus();
					
					$(".btn-rename").on("mouseover",function(){
						$(".rename-text").off("blur");
					});
					
					$(".rename-text").on("blur", function() {
						vm.exitRenameMode(index);
					});
					
					$(".btn-rename").on("mouseout",function(){
						$(".rename-text").on("blur", function() {
							vm.exitRenameMode(index);
						});
					});
				}, 300);
			},
			exitRenameMode: function(index, success) {
				if(!success){
					this.editSbuDetail.subName = this.sbuList[index].subName;
				}else{
					this.sbuList[index].subName = this.editSbuDetail.subName; 
				}
				vm.$set('sbuList[' + index + '].isEdit', false);
			},
			confirmRename: function(index) {
				var id = this.editSbuDetail.id;
				var sbuName = this.editSbuDetail.subName; 
				var options = {
						method: "POST",
						url: envConfig.apis.updateSbu.url,
						data: {id: id, subName: sbuName },
						callback: this.confirmRenameCallback
				};
				utils.ajax(options);
			},
			confirmRenameCallback: function() {
				return {
					"success": function(resp) {
						var returnInfo = resp.responseBody.info;
						var statusCode = Number(returnInfo.code);
						if(statusCode === 200) {
							console.log(returnInfo.msg);
							vm.exitRenameMode(vm.editSbuDetail.arrIndex, true);
						}
					},
					"error": function(resp) {
						console.log(resp);
					}
				};
			},
			removeSbu: function(index){ // Remove sbu
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
			uploadFile: function() { // Upload program excel template
				var isExcel = this.uploadFileName && 
									(/.xls$/i.test(this.uploadFileName) || 
									/.xlsx$/i.test(this.uploadFileName));
				if(isExcel) {
					this.incorrectFormat = false;
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
					this.incorrectFormat = true;
					setTimeout(function(){
        				vm.incorrectFormat = false;
        			}, 500);
				}
			},
			editProgram: function(index) {
				// Set value for program detail modal
				var programDetail = this.adminProgramList[index];
				var cloneObj = clone(programDetail); // clone a new obj
				this.programDetail = cloneObj;
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
				var updateSbuList = submitProgramDetail.sbuList;
				obj = {
						id: submitProgramDetail.id,
						eventName: submitProgramDetail.eventName,
						name: submitProgramDetail.name,
						url: submitProgramDetail.url,
						startTime: submitProgramDetail.startTime,
						endTime: submitProgramDetail.endTime,
						duration: submitProgramDetail.duration,
						sbuList: updateSbuList
				};
				return obj;
			},
			submitEditProgramCallback: function() { // Submit the edit program info
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
			deleteProgram: function() {
				$("#programDetailModal").modal("hide");
				// delete program
				this.isDeleteCourse = true;
				this.isDeleteNominee = false;
				$("#confirmModal").modal("show");
			},
			deleteCourse: function() {
				var programId = this.programDetail.id;
				var options = {
						url: envConfig.apis.deleteProgram.url,
						data: { courseId: programId },
						callback: this.deleteCourseCallback
				};
				utils.ajax(options);
			},
			deleteCourseCallback: function() {
				return {
					"success": function(resp) {
						var returnInfo = resp.responseBody.info;
						var statusCode = Number(returnInfo.code);
						if(statusCode === 200) {
							console.log(returnInfo.msg);
							vm.getAdminProgramList();
							$("#confirmModal").modal("hide");
						}
					},
					"error": function(resp) {
						console.log(resp);
					}
				};
			},
			submitAddSbu: function() { // Add a new sbu
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
					this.addNewSbu();
				}
			},
			addNewSbu: function() {
				var options = {
						method: 'POST',
						url: envConfig.apis.addSbu.url,
						data: {
							parentId: this.currentSwitchSbu ?  this.currentSwitchSbu.id : 0,
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
							vm.getSbuList();
						}
					},
					"error": function(resp) {
						console.log(resp);
					}
				}
			},
			operateUser: function(index) {
				var lbpInfo = this.sbuList[index].lbps;
				if(!lbpInfo) {
					lbpInfo = {};
				}
				lbpInfo.sbuId = this.sbuList[index].id;
				lbpInfo.sbuLbpsId = this.sbuList[index].sbuLbpsId;
				
				var cloneObj = clone(lbpInfo); // clone a new obj
				this.lbpInfo = cloneObj;
				
				if(!this.lbpInfo.sbuId) { // If not has sbuId, edit modes (Because sbuid is added by add mode)
					// Edit lbp
					this.isEdit = false;
				}else{
					// Add lbp
					this.isEdit = true;
				}
				this.incorrectFormat = false;
				$("#editLBPModal").modal("show");
			},
			editLBPMode: function() {
				this.isEdit = true;
			},
			displayLBPMode: function() {
				this.searchLBP();
			},
			searchLBP: function() { // Use email to search nominee
				var searchText = this.lbpInfo.email;
				if(searchText && /@capgemini.com$/i.test(searchText)){
					this.incorrectFormat = false;
					var options = {
							url: envConfig.apis.searchNominee.url,
							data: {name: searchText},
							callback: this.searchLBPCallback
					};
					utils.ajax(options);
				}else{
					this.incorrectFormat = true;
					setTimeout(function(){
        				vm.incorrectFormat = false;
        			}, 500);
				}
			},
			searchLBPCallback: function() {
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
			        	var participantInfo = data.content;
			        	if(participantInfo && participantInfo.length>0) {
			        		// search success
		        			vm.lbpInfo.name = participantInfo[0].displayName;
		        			vm.lbpInfo.email = participantInfo[0].email;
		        			vm.isEdit = false;
			        	}else{
			        		vm.incorrectFormat = true;
			        		setTimeout(function(){
		        				vm.incorrectFormat = false;
		        			}, 500);
			        	}
					},
					"error": function(resp) {
						console.log(resp);
					}
				};
			},
			confirmEditLBP: function() {
				if(vm.isEdit){
					vm.incorrectFormat = true;
	        		setTimeout(function(){
        				vm.incorrectFormat = false;
        			}, 500);
	        		return;
				}
				var name = this.lbpInfo.name;
				var email = this.lbpInfo.email;
				var sbuId = this.lbpInfo.sbuId;
				var sbuLbpsId = this.lbpInfo.sbuLbpsId
				var options = {
						method: 'POST',
						url: envConfig.apis.relateSbuLbps.url,
						data: {
							sbuId: sbuId, displayName: name, 
							sbuLbpsId: sbuLbpsId, email: email, logo: ''
						},
						callback: this.confirmEditLBPCallback
				};
				utils.ajax(options);
			},
			confirmEditLBPCallback: function() {
				return {
					"success": function(resp) {
						var returnInfo = resp.responseBody.info;
						var statusCode = Number(returnInfo.code);
						if(statusCode === 200) {
							console.log(returnInfo.msg);
							$("#editLBPModal").modal("hide");
							vm.getSbuList();
						}
					},
					"error": function(resp) {
						console.log(resp);
					}
				};
			},
			exportData: function() {
				var $btnExport = $('#export');
				if($btnExport.hasClass('export-disable')) {
					return;
				}
				var currentEvent = this.currentAdminEvent;
				if(currentEvent){
					window.open(envConfig.prefix + envConfig.apis.exportData.url + '?eventName=' + currentEvent.event_name);
				}else{
					window.open(envConfig.prefix + envConfig.apis.exportData.url);
				}
			},
			switchSbu: function(index){
				this.currentSwitchSbu = this.sbuArrList[index];
				this.getPerSbuInfo(this.currentSwitchSbu);
			},
			switchToAdmin: function() {
				this.currentSwitchSbu = null;
				this.getPerSbuInfo(this.currentSwitchSbu);
			},
			getMarkProgramList: function() {
				var mpList = this.adminProgramList.slice(0, this.adminProgramList.length);
				this.markProgramList = mpList;
				this.currentAdminEvent = null;
			},
			showAttendees: function(index, esc) {
				if(esc) {
					this.selectedAdminProgram = $.extend({}, this.markProgramList[index]);
					this.selectedAdminProgramIndex = index;
					this.getAttendees();
				}else{
					this.selectedAdminProgram = null;
					setTimeout(function() {
						this.showAttendees(index, true);
					}.bind(this), 300);
				}
			},
			getAttendees: function() {
				var options = {
						url: envConfig.apis.getAttendees.url,
						data: {
							courseId: this.selectedAdminProgram.id
						},
						callback: this.getAttendeesCallback
				};
				utils.ajax(options);
			},
			getAttendeesCallback: function() {
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
						var attendeeSbuList = data.content;
						vm.markAttendeesWidthSbuList = attendeeSbuList;
//						vm.$set('selectedAdminProgram.attendeeSbuList', attendeeSbuList);
					},
					"error": function(resp) {
						console.log(resp);
					}
				};
			},
			updateAttend: function(attendee) {
				var attendeeId = attendee.id;
				var changeChecked = attendee.attend;
				var attend = 1; // Default attend
				if(changeChecked) { // if true, mark as no attend: 0
					attend = 0;
				}
				var options = {
						method: 'POST',
						url: envConfig.apis.recordAttend.url,
						data: {
							id: attendeeId,
							attend: attend
						},
						callback: this.recordAttendCallback
				};
				utils.ajax(options);
			},
			recordAttendCallback: function() {
				return {
					"success": function(resp) {
						var returnInfo = resp.responseBody.info;
						var statusCode = Number(returnInfo.code);
						if(statusCode === 200) {
							console.log(returnInfo.msg);
						}
					},
					"error": function(resp) {
						console.log(resp);
					}
				};
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
			var headHeight = $('#top').height();
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
	
	function initRightSlidePosition() {
		calcTop();
		$(window).on("resize", function(){
			calcTop();
		});
	}
	
	function calcTop() {
		var headHeight = $('#top').height();
		var moduleOffsetTop = $('#moduleBar').offset().top;
		if(headHeight!==0) {
			$('.fixed-right').css('top', headHeight);
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
		if($.isFunction(Object.assign)) {
			return Object.assign({}, fromObj);
		}
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