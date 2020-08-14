(function() {
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
		document.querySelector('#nearby-btn').addEventListener('click',
				loadNearbyItems);
		document.querySelector('#fav-btn').addEventListener('click',
				loadFavoriteItems);
		document.querySelector('#recommend-btn').addEventListener('click',
				loadRecommendedItems);
		validateSession();
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
	
	/* register
	   [POST] ./register
	  {
       "user_id": "1111",
       "password": "1111",
       "first_name": "John",
       "last_name": "Smith"
        }
    */
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
	
	/* login
	  [POST] ./login
       {
  		"user_id": "1111",
  		"password": "1111"
		} 
	*/
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
	
	/* validate session
	   [GET] ./login
	*/
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
	
	// add location info
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
	
	/* load nearby items
	   [GET] /search?lat=37.38&lon=-122.08&user_id=1111
	*/
	function loadNearbyItems() {
		console.log('loadNearbyItems');
		activeBtn('nearby-btn');
		// request parameters
		var url = './search';
		var params = 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lng;
		var data = null;
		// display loading message
		showLoadingMessage('Loading nearby items...');
		ajax('GET', url + '?' + params, data,
		// sucCb
		function(res) {
			var items = JSON.parse(res);
			if (!items || items.length === 0) {
				showWarningMessage('No nearby item.');
			} else {
				listItems(items);
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
	
	// display items
	function $create(tag, options) {
		var element = document.createElement(tag);
		for ( var key in options) {
			if (options.hasOwnProperty(key)) {
				element[key] = options[key];
			}
		}
		return element;
	}
	
	function listItems(items) {
		var itemList = document.querySelector('#item-list');
		itemList.innerHTML = ''; // clear current results

		for (var i = 0; i < items.length; i++) {
			addItem(itemList, items[i]);
		}
	}

	function addItem(itemList, item) {
		var item_id = item.item_id;
		// create the <li> tag and specify the id and class attributes
		var li = $create('li', {
			id : 'item-' + item_id,
			className : 'item'
		});
		// set the data attribute ex. <li data-item_id="G5vYZ4kxGQVCR"
		// data-favorite="true">
		li.dataset.item_id = item_id;
		li.dataset.favorite = item.favorite;
		// item image
		if (item.image_url) {
			li.appendChild($create('img', {
				src : item.image_url
			}));
		} else {
			li.appendChild($create('img', {
				src : 'https://via.placeholder.com/100'
			}));
		}
		// section
		var section = $create('div');
		// title
		var title = $create('a', {
			className : 'item-name',
			href : item.url,
			target : '_blank'
		});
		title.innerHTML = item.name;
		section.appendChild(title);
		// keyword
		var keyword = $create('p', {
			className : 'item-keyword'
		});
		keyword.innerHTML = 'Keyword: ' + item.keywords.join(', ');
		section.appendChild(keyword);
		li.appendChild(section);
		// address
		var address = $create('p', {
			className : 'item-address'
		});
		// ',' => '<br/>', '\"' => ''
		address.innerHTML = item.address.replace(/,/g, '<br/>').replace(/\"/g,
				'');
		li.appendChild(address);
		// favorite link
		var favLink = $create('p', {
			className : 'fav-link'
		});
		favLink.onclick = function() {
			changeFavoriteItem(item);
		};
		favLink.appendChild($create('i', {
			id : 'fav-icon-' + item_id,
			className : item.favorite ? 'fa fa-heart' : 'fa fa-heart-o'
		}));
		li.appendChild(favLink);
		itemList.appendChild(li);
	}

	/* favorite / unfavorite an itme
	   [POST / DELETE] .history
	*/
	function changeFavoriteItem(item) {
		// check whether this item has been visited or not
		var li = document.querySelector('#item-' + item.item_id);
		var favIcon = document.querySelector('#fav-icon-' + item.item_id);
		var favorite = !(li.dataset.favorite === 'true');
		// request parameters
		var url = './history';
		var req = JSON.stringify({
			user_id : user_id,
			favorite : item
		});
		var method = favorite ? 'POST' : 'DELETE';
		ajax(method, url, req,
		// sucCb
		function(res) {
			var result = JSON.parse(res);
			if (result.status === 'OK' || result.result === 'SUCCESS') {
				li.dataset.favorite = favorite;
				favIcon.className = favorite ? 'fa fa-heart' : 'fa fa-heart-o';
			}
		}, 
		//errCb
		function(){
			console.log('change favorite failed!')
		}
		);
	}

	// load favorite items
	// [GET] /history?user_id=1111
	function loadFavoriteItems() {
		activeBtn('fav-btn');
		// request parameters
		var url = './history';
		var params = 'user_id=' + user_id;
		var req = JSON.stringify({});
		// display loading message
		showLoadingMessage('Loading favorite items...');
		// get favorite items
		ajax('GET', url + '?' + params, req, function(res) {
			var items = JSON.parse(res);
			if (!items || items.length === 0) {
				showWarningMessage('No favorite item.');
			} else {
				listItems(items);
			}
		}, function() {
			showErrorMessage('Cannot load favorite items.');
		});
	}
	
	// load recommended items
	// [GET] /recommendation?user_id=1111
	function loadRecommendedItems() {
		activeBtn('recommend-btn');
		// request parameters
		var url = './recommendation' + '?' + 'user_id=' + user_id + '&lat='
				+ lat + '&lon=' + lng;
		var data = null;
		// display loading message
		showLoadingMessage('Loading recommended items...');
		ajax('GET', url, data,
		// sucCb
		function(res) {
			var items = JSON.parse(res);
			if (!items || items.length === 0) {
				showWarningMessage('No recommended item. Make sure you have favorites.');
			} else {
				listItems(items);
			}
		},
		// err
		function() {
			showErrorMessage('Cannot load recommended items.');
		});
	}


	init();
	
})();
