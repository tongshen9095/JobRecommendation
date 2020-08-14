(function() {
	// step 3
	var user_id = "1111";
	var user_fullname = "John";
	var lng = -122.08;
	var lat = 37.38;
	
	function init() {
		// register event listeners
		document.querySelector('#login-form-btn').addEventListener('click',
			onSessionInvalid);
		document.querySelector('#register-form-btn').addEventListener('click',
			showRegisterForm);
		document.querySelector('#register-btn').addEventListener('click',
				register);
		document.querySelector('#login-btn').addEventListener('click', login);
		validateSession();
	}
	
	function validateSession() {
		onSessionInvalid();
		// request parameters
		var url = './login';
		var req = JSON.stringify({});
		// display loading message
		showLoadingMessage('Validating session...');
		// use login GET method to valid the session
		ajax('GET', url, req,
		// sucCb
		function(res) {
			var result = JSON.parse(res);
			if (result.status === 'ok') {
				onSessionValid(result);
			}
		}, 
		// errCb
		function() {
			console.log('login error')
		});
	}
	
	function showLoadingMessage(msg) {
		var itemList = document.querySelector('#item-list');
		itemList.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> '
				+ msg + '</p>';
	}
 
	function onSessionValid(result) {
		user_id = result.user_id;
		user_fullname = result.name;

		var loginForm = document.querySelector('#login-form');
		var registerForm = document.querySelector('#register-form');
		var itemNav = document.querySelector('#item-nav');
		var itemList = document.querySelector('#item-list');
		var avatar = document.querySelector('#avatar');
		var welcomeMsg = document.querySelector('#welcome-msg');
		var logoutBtn = document.querySelector('#logout-link');

		welcomeMsg.innerHTML = 'Welcome, ' + user_fullname;

		showElement(itemNav);
		showElement(itemList);
		showElement(avatar);
		showElement(welcomeMsg);
		showElement(logoutBtn, 'inline-block');
		hideElement(loginForm);
		hideElement(registerForm);
		
		initGeoLocation();
	}
	
	function initGeoLocation() {
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(onPositionUpdated,
					onLoadPositionFailed, {
						maximumAge : 60000
					});
			showLoadingMessage('Retrieving your location...');
		} else {
			onLoadPositionFailed();
		}
	}
	
	function onPositionUpdated(position) {
		lat = position.coords.latitude;
		lng = position.coords.longitude;
		console.log('lat -> ', lat);
		console.log('lng -> ', lng);
		loadNearbyItems();
	}

	function onLoadPositionFailed() {
		console.warn('navigator.geolocation is not available; use default location');
		console.log('lat -> ', lat);
		console.log('lng -> ', lng);
		loadNearbyItems();
	}
	
	function loadNearbyItems() {
		console.log('loadNearbyItems');
		activeBtn('nearby-btn');
		// request parameters
		var url = './search';
		var params = 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lng;
		var data = null;
		// display loading message
		showLoadingMessage('Loading nearby items...');
		// search job by lat and lat
		ajax('GET', url + '?' + params, data,
		// sucCb
		function(res) {
			var items = JSON.parse(res);
			if (!items || items.length === 0) {
				showWarningMessage('No nearby item.');
			} else {
				console.log(items);
			}
		},
		// errCb
		function() {
			showErrorMessage('Cannot load nearby items.');
		});
	}
	
	// helper function that make a navigation buttion active
	function activeBtn(btnId) {
		var btns = document.querySelectorAll('.main-nav-btn');
		// deactivate all navigation buttons
		for (var i = 0; i < btns.length; i++) {
			btns[i].className = btns[i].className.replace(/\bactive\b/, '');
		}
		// active the one that has id = btnId
		var btn = document.querySelector('#' + btnId);
		btn.className += ' active';
	}

	function showLoadingMessage(msg) {
		var itemList = document.querySelector('#item-list');
		itemList.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> '
				+ msg + '</p>';
	}

	function showWarningMessage(msg) {
		var itemList = document.querySelector('#item-list');
		itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-triangle"></i> '
				+ msg + '</p>';
	}

	function showErrorMessage(msg) {
		var itemList = document.querySelector('#item-list');
		itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-circle"></i> '
				+ msg + '</p>';
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
			if (result.status === 'ok') {
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

	// login
	function login() {
		var username = document.querySelector('#username').value;
		var password = document.querySelector('#password').value;
		password = md5(username + md5(password));
		// request parameters
		var url = './login';
		var req = JSON.stringify({
			user_id : username,
			password : password,
		});
		ajax('POST', url, req,
		// sucCb
		function(res) {
			var result = JSON.parse(res);
			if (result.status === 'ok') {
				console.log('login successfully!');
			}
		},
		// errCb
		function() {
			showLoginError();
		});
	}

	function showLoginError() {
		document.querySelector('#login-error').innerHTML = 'Invalid username or password';
	}
	
	function ajax(method, url, data, successCallback, errorCallback) {
		// step1: create XMLHttpRequest Object
		var xhr = new XMLHttpRequest();
		// step2: set request parameters
		xhr.open(method, url, true);
		// step4: register functions to handle response
		// the function is called when the request is complete
		xhr.onload = function() {
			if (xhr.status === 200) {
				successCallback(xhr.responseText);
			} else {
				errorCallback();
			}
		};
		// when the request can't be made
		xhr.onerror = function() {
			console.error("The request couldn't be completed.");
			errorCallback();
		};
		// step3: send request
		if (data === null) {
			xhr.send();
		} else {
			xhr.setRequestHeader("Content-Type",
					"application/json;charset=utf-8");
			xhr.send(data);
		}
	}

	init();
	
})();
