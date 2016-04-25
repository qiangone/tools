(function () {
	'use strict';
	var selectItem = false;
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
	
	Vue.filter('dateTime', function(timestamp){
		return DateFormat.format.date(new Date(timestamp), "yyyy-MM-dd HH:mm:ss")
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
			     { name: 'history', active: false}
			],
			emails: [
			     { text: '@capgemini.com' },
			     { text: '@sogeti.com' },
			     { text: '@prosodie.com' }
			],
			selectedEmailAddress: '',
			currentIndex: 0,
			errorMsg: "",
			query: '',
			currentSbu : '',
			currentCountry: {},
			sbuId: 0,
			swapSeatsCount: 0,
			eventList:[],
			allEvent: [],
			countryCharts: [],
			programList : [],
			otherSbuSeatsList: [],
			countryTotalSeats: 0,
			countryRemainingSeats: 0,
			countrySeatsList: [],
			nomineeList: [],
			deleteNominee: {},
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
			isZero: false,
			noSelectProgram: false,
			noRecord: false,
			wholeSbuInfo: {},
			user: {}
		},
		ready : function() {
			initModalPosition();
			
			// Get Request
			this.getCurrentSbu();
			this.selectedEmailAddress = this.emails[0].text;
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
					return result;
				}
			},
			isDetailShow: function() {
				return this.selectedProgram ? true : false;
			}
		},
		methods : {
			gotoPage: function(index) {
				// Init pagination
				this.page = {};
				this.currentPage = 1;
				this.pagination = false;
				// Init program page
				this.selectedProgram = null;
				this.selectedProgramIndex = -1;
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
			        		vm.currentSbu = sbuInfo.subName;
			        	}else{ // Second level
			        		vm.noLimitation = false;
			        		var parentInfo = sbuInfo.parentSbu;
			        		vm.currentSbu = parentInfo.subName;
			        		vm.currentCountry = {
			        				id: sbuInfo.id,
			        				countryName: sbuInfo.subName
			        		};
			        	}
			        	vm.sbuId = sbuInfo.id;
			        	
//			        	vm.getProgramList();
			        	vm.getSummary();
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
			getAllEvent: function() {
				var options = {
						method: "POST",
						url: envConfig.apis.getAllEvent.url,
						callback: this.getAllEventCallback
				};
				utils.ajax(options);
			},
			getAllEventCallback: function() {
				return {
					"success": function(resp) {
						var data = resp.responseBody.result;
						var allEvent = data.content;
						vm.allEvent = allEvent;
						vm.selectedSwapEvent = vm.allEvent[0];
						
						vm.getProgramsByEvent(vm.selectedSwapEvent.eventName);
					},
					"error": function() {
						console.log(resp);
					}
				}
			},
			getProgramsByEvent: function(ename) {
				var options = {
						url: envConfig.apis.getProgramListByEvent.url,
						data:{ eventName: ename },
						callback: this.getProgramListByEventCallback
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
			getProgramList: function(max) {
				var options = {
						method: "POST",
						url: envConfig.apis.getProgramList.url,
						dummyPath: "dummy/getProgramList.json",
						data: this.getProgramListData(max),
						callback: this.getProgramListCallback
				};
				utils.ajax(options);
			},
			getProgramListData: function(max) {
				var obj = {};
				var sbuId = this.sbuId;
				var pageSize = max ? 500 : this.pageSize;
				obj = {
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
						var otherSbuSeatsList = [];
						var countrySeatsList = [];
						if(dataList.otherCourseList) {
							otherSbuSeatsList = dataList.otherCourseList;
						}
						vm.otherSbuSeatsList = otherSbuSeatsList;
						if(dataList.subSbuCourList) {
							countrySeatsList = dataList.subSbuCourList;
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
				this.getAllEvent();
//				this.getProgramList(targetSbuId, true);
			},
			minusCount: function(obj) {
				if(obj.seats != 0) {
					if(obj.seats === obj.participantList.length) {
						this.showError('Please remove nominee firstly', true);
						return;
					}
					obj.seats--;
					this.assignSeats(obj);
				}
			},
			plusCount: function(obj) {
				if(this.countryRemainingSeats != 0) {
					obj.seats++;
					this.assignSeats(obj);
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
			addNominee: function(countryInfo) {
				vm.currentCountry = countryInfo;
				/**
				 * If need to display already exist user in the search modal, uncomment the below two line code
				 */
//				var searchMonimeeList = countryInfo.participantList.slice(0, countryInfo.participantList.length);
//				vm.searchNomineeList = searchMonimeeList;
				vm.employeeSearch = '';
				vm.searchNomineeList = [];
	        	$("#addNomineeModal").modal("show");
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
				this.getProgramsByEvent(this.selectedSwapEvent.eventName);
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
        				courseId1: this.selectedProgram.courseId,
        				fromSbuId: this.sbuId,
        				toSbuId: this.selectedSwapSbu.id,
        				action: action,
						seats: this.swapSeatsCount,
						courseId2: this.selectedSwapProgram.courseId
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
			        		vm.showError('SBU for program avaliable seats is not enough.', 1008);
			        	}
			        }
			    };
        	},
        	removeNominee: function(nominee) {
        		this.deleteNominee = nominee;
        		$("#confirmModal").modal("show");
        	},
        	doAction: function() {
        		this.deleteCountryNominee();
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
        	}
		}
	});
	
	function doLogout() {
		window.sessionStorage.clear();
		window.localStorage.clear();
		location.href = "j_spring_security_logout";
	}
	
	$('#swapModal').on('hide.bs.modal', function (e) {
	});
	
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