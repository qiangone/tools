function myLoad() {
	if ($("#errorText").hasClass("toshow")) {
		$('#username').val("");
		$('#password').val("");
//		$('#isRememberMe').prop('checked', false);
	}
}

(function($) {
//	var testUserArr = [ "test" ];
	$(document).ready(function() {
		var isSupport = $('#username').prop('autofocus');
		if (!isSupport) {
			$('#username').focus();
		}

		$("#login-form").on("submit", function() {
			var username = $('#username').val().trim();
			
			var password = $('#password').val().trim();
//			utils.sessionData("username", username);
//			var userFounded = $.inArray(username.toLowerCase(), testUserArr);
//			if (userFounded == -1) {
//				username = utils.encrypt(username);
//				password = utils.encrypt(password);
//			}
			document.loginForm.j_username.value = username;
			document.loginForm.j_password.value = password;
			document.loginForm.action = envConfig.prefix + "/j_spring_security_check";
		});
		$('#btn-signIn').on('click', function() {
			$("#login-form").submit();
		});
		$("#username").bind('keyup', function(event) {
			if ($(this).val()) {
				if (event.which == "13") {
					$("#password").focus();
				}
			}
		});
		$("#password").bind('keyup', function(event) {
			if (event.which == "13") {
				$('#btn-signIn').trigger("click");
			}
		});
	});
})(jQuery);