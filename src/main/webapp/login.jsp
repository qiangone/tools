<!DOCTYPE html>
<html lang="en">
<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title>Our University Campus Nomination Tool</title>
<!-- <link rel="SHORTCUT ICON" href="lib/img/title.png" /> -->
<!-- Bootstrap Core CSS -->
<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/login.css" rel="stylesheet">
</head>

<body onload="myLoad()">
	<div class="login-container">
		<div id="form-container" class="container-fluid">
			<div class="row">
				<div class="col-xs-12">
					<div class="logo_icon"></div>
				</div>
			</div>
			<div class="login_icon_container">
				<i class="icon_login"></i>
<!-- 				<span class="logo-text">Our University Campus Nomination Tool</span> -->
			</div>
			<div class="row">
				<div class="col-xs-12">
					<input id="username" class="validate-input" type="text" placeholder="USERNAME" autocomplete="off" required autofocus>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-12">
					<input id="password" class="validate-input" type="password" placeholder="PASSWORD" required>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-12">
					<div id="errorMsg">
						<div id="errorText"
							class="${param.error == true ? 'toshow':'hidden'}">${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}</div>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-12">
					<input class="btn btn-login" type="button"
						id="btn-signIn" value="SIGN IN" />
				</div>
			</div>
			<form id="login-form" name="loginForm" method="POST"
				autocomplete="off">
				<input type="hidden" name="j_username"> <input type="hidden"
					name="j_password">
			</form>
		</div>

	</div>
	<script src="js/jquery.min.js"></script>
	<script src="js/aes.min.js"></script>
	<script src="js/config.js"></script>
	<script src="js/utils.js"></script>
	<script src="js/login.js"></script>
</body>

</html>
