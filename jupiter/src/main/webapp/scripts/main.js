(function() {
	// step 3
	var user_id = "1111";
	var user_fullname = "John";
	var lng = -122.08;
	var lat = 37.38;
	
	function init() {
		document.querySelector('#login-form-btn').addEventListener('click',
			onSessionInvalid);
		document.querySelector('#register-form-btn').addEventListener('click',
			showRegisterForm);
		document.querySelector('#register-btn').addEventListener('click',
				register);
		validateSession();
	}
	
	function validateSession() {
		onSessionInvalid();
	}
	
	// only show login info
	function onSessionInvalid() {
		var loginForm = document.querySelector('#login-form');
		var registerForm = document.querySelector('#register-form');
		var itemNav = document.querySelector('#item-nav');
		var itemList = document.querySelector('#item-list');
		var avatar = document.querySelector('#avatar');
		var welcomeMsg = document.querySelector('#welcome-msg');
		var logoutBtn = document.querySelector('#logout-link');

		hideElement(itemNav);
		hideElement(itemList);
		hideElement(avatar);
		hideElement(logoutBtn);
		hideElement(welcomeMsg);
		hideElement(registerForm);

		clearLoginError();
		showElement(loginForm);
	}
	
	// only show register form
	function showRegisterForm() {
		var loginForm = document.querySelector('#login-form');
		var registerForm = document.querySelector('#register-form');
		var itemNav = document.querySelector('#item-nav');
		var itemList = document.querySelector('#item-list');
		var avatar = document.querySelector('#avatar');
		var welcomeMsg = document.querySelector('#welcome-msg');
		var logoutBtn = document.querySelector('#logout-link');

		hideElement(itemNav);
		hideElement(itemList);
		hideElement(avatar);
		hideElement(logoutBtn);
		hideElement(welcomeMsg);
		hideElement(loginForm);

		clearRegisterResult();
		showElement(registerForm);
	}

	function clearRegisterResult() {
		document.querySelector('#register-result').innerHTML = '';
	}

	function hideElement(element) {
		element.style.display = 'none';
	}
	
	function clearLoginError() {
		document.querySelector('#login-error').innerHTML = '';
	}
	
	function showElement(element, style) {
		var displayStyle = style ? style : 'block';
		element.style.display = displayStyle;
	}
	
	// register
	function register() {
		var username = document.querySelector('#register-username').value;
		var password = document.querySelector('#register-password').value;
		var firstName = document.querySelector('#register-first-name').value;
		var lastName = document.querySelector('#register-last-name').value;
		if (username === "" || password == "" || firstName === ""
				|| lastName === "") {
			showRegisterResult('Please fill in all fields');
			return;
		}	
		// user name can only contains a-z or 0-9
		if (username.match(/^[a-z0-9_]+$/) === null) {
			showRegisterResult('Invalid username');
			return;
		}
		// encrypt
		password = md5(username + md5(password));
		// request parameters
		var url = './register';
		var req = JSON.stringify({
			user_id : username,
			password : password,
			first_name : firstName,
			last_name : lastName,
		});
		ajax('POST', url, req,
		// sucCb
		function(res) {
			var result = JSON.parse(res);
			if (result.status === 'OK') {
				showRegisterResult('Succesfully registered');
			} else {
				showRegisterResult('User already existed');
			}
		},
		// errCb
		function() {
			showRegisterResult('Failed to register');
		});
	}

	function showRegisterResult(registerMessage) {
		document.querySelector('#register-result').innerHTML = registerMessage;
	}

	function clearRegisterResult() {
		document.querySelector('#register-result').innerHTML = '';
	}

	function ajax(method, url, data, successCallback, errorCallback) {
		// step1: create XMLHttpRequest Object
		var xhr = new XMLHttpRequest();
		// step2: set request parameters
		xhr.open(method, url, true);
		// step3: send request
		if (data === null) {
			xhr.send();
		} else {
			xhr.setRequestHeader("Content-Type",
					"application/json;charset=utf-8");
			xhr.send(data);
		}
		// step4: handle response
		xhr.onload = function() {
			if (xhr.status === 200) {
				successCallback(xhr.responseText);
			} else {
				errorCallback();
			}
		};
		xhr.onerror = function() {
			console.error("The request couldn't be completed.");
			errorCallback();
		};
	}

	init();
	
})();
