/**
 * Util object, put your util methods here
 */
if (!window.utils) {
	window.utils = {};
	var u = window.utils;

	/**
	 * Set/Get OBJECT to localStorage. if set string, please use localStorage
	 * API directly.
	 *
	 * 1 param for getter, 2 params for setter. if want to clear localStorage,
	 * set 2nd param to null or ''.
	 *
	 * @param {String} key
	 * @param {Object} data
	 */
	u.data = function(key, data) {
		if (!arguments.length || !window.localStorage) {
			return null;
		}
		var ls = window.localStorage;
		if (arguments.length == 1) {
			try {
				data = $.parseJSON(ls.getItem(key));
			} catch (e) {
				data = null;
			}
			return data;
		} else if (arguments.length == 2) {
			if (!data) {
				ls.removeItem(key);
			} else {
				try {
					ls.setItem(key, JSON.stringify(data));
				} catch (e) {

				}
			}

		}
	};
	
	/**
	 * Set/Get OBJECT to sessionStorage. if set string, please use sessionStorage
	 * API directly.
	 *
	 * 1 param for getter, 2 params for setter. if want to clear sessionStorage,
	 * set 2nd param to null or ''.
	 *
	 * @param {String} key
	 * @param {Object} data
	 */
	u.sessionData = function(key, data) {
		if (!arguments.length || !window.sessionStorage) {
			return null;
		}
		var ss = window.sessionStorage;
		if (arguments.length == 1) {
			try {
				data = $.parseJSON(ss.getItem(key));
			} catch (e) {
				data = null;
			}
			return data;
		} else if (arguments.length == 2) {
			if (!data && data != false ) {
				ss.removeItem(key);
			} else {
				try {
					ss.setItem(key, JSON.stringify(data));
				} catch (e) {

				}
			}

		}
	};
	
	/**
	 * Send request with ajax to get the data from server
	 */
	u.ajax = function(opts){
		var options = opts || {}, params = {}, self = this;
		
		var loadTimeout = setTimeout(function(){
			if(options.showLoadingFlag != false){
//				showLoadingMask();
			}
		}, 500);
		
		var url = "";
		if(envConfig.dummy || options.dummy){
			url = options.dummyPath;
			options.method = "GET";
		}else{
			if(options.url != undefined && options.url != ""){
				url = options.url;
			}else{
//				url = apis[options.name].url;
			}
			var currentDomain = envConfig.prefix;
			if(currentDomain != undefined && currentDomain != ""){
				url = currentDomain + url;
			}
			if(options.data) {
				params = $.isFunction(options.data) ? options.data.apply(this,arguments) : options.data;
				if(options.method == "POST") {
					if(params) {
						params = JSON.stringify(params);
					}
				}
			}
		}
		$.ajax({
			type: options.method || "GET",
			url: url,
			dataType: "json",
			contentType:"application/json",
			data: params,
			success: function(result){
				self._analyse.apply(self,[result, function(data){
					if(loadTimeout){
						clearTimeout(loadTimeout);
					}
					var handler = options.callback.call(self, data);
					self._messageHandler.apply(self, [options.name, handler, data]);
				}]);
			},
			error: function(result){
				if(loadTimeout){
					clearTimeout(loadTimeout);
				}
				var r = {};
				r.statusCode = result.status;
				r.responseBody = result.responseText;
				var handler = options.callback.call(self, r);
				handler.error.apply(self, [r]);
			}
		});
	};
	
	u._analyse = function (data, execute) {
		var rs = {
			valid: false,
			errorCode: false,
			errorMsg : "",
			responseBody: data
		}, self = this;
		if(!data) {
			rs.errorCode = "jsonErr";
			rs.responseBody = {};
			execure(rs);
			return;
		}
		
		if(typeof data === "string") {
			try {
				data = $.parseJSON(data);
				rs.responseBody = data;
			} catch (err) {
				// do sth
			}
		}
		
		if (typeof rs.responseBody === "object") {
			if(rs.responseBody == null) {
				rs.errorCode = "jsonErr";
				execute(rs);
				return;
			}
			if(rs.errorCode !== false) {
				execute(rs);
				return;
			}
			if(data.info.code == "200") {
				rs.valid = true;
			}else {
				rs.valid = false;
				rs.errorCode = data.info.code;
				rs.errorMsg = data.info.msg;
			}
			execute(rs);
		}else{
			rs.errorCode = "jsonErr";
			rs.data = {};
			execute(rs);
			return;
		}
	};
	
	u._messageHandler = function(name, handler, rs) {
		var self = this;
		if(rs.valid) {
			handler.success.apply(self, [rs]);
			return;
		}
		if(handler.error && $.isFunction(handler.error)){
			var rsData = rs.responseBody;
			if (rsData != undefined && rsData != "") {
				handler.error.apply(self, [ rs ]);
			}
		}
	};
	
	u.encrypt = function(word) {
		var key = CryptoJS.enc.Utf8.parse('0102030405060708');    
	    var iv  = CryptoJS.enc.Utf8.parse('0102030405060708');    
	    var srcs = CryptoJS.enc.Utf8.parse(word);   
	    var encrypted = CryptoJS.AES.encrypt(srcs, key, { iv: iv,mode:CryptoJS.mode.CBC});   
	    return encrypted.toString();
	}
}
