var request = require('request');
var express = require('express');
var router = express.Router();

var db = require('../lib/db');
var users = require('../model/users');

var errMsg = function(msg) {
  return {err: true, msg: msg || ''};
}

var successMsg = function(msg) {
  return {success: true, msg: msg || ''};
}

/* GET SignUp page. */
router.get('/signup', function(req, res) {
  res.render('signup');
});

/* POST SignUp page. */
router.post('/signup', function(req, res) {
  var TEMPLATE = 'signup';
  var msg = "Error in signing up process. Please try later!";
  if(req.body.password !== req.body.confirmPassword) {
    res.render(TEMPLATE, errMsg("Password and confirm password didn't match!"));
  } else {
    users.checkUserExists(req.body.email, function(err, exists) {
      if(err)
        res.render(TEMPLATE, errMsg(msg));
      else if(exists) {
        res.render(TEMPLATE, errMsg("Account with the email '" + req.body.email +"' exists already!"));
      } else {
        users.createUser(req.body, function(err, user) {
          if(err) {
            res.render(TEMPLATE, errMsg(msg));
          } else {
            req.session.regenerate(function(){
              req.session.user = user;
              res.render(TEMPLATE, successMsg('You signed up successfully. Redirecting to home page ...'));
            });
          }
        });
      }
    });
  }
});

/* GET Signin page. */
router.get('/signin', function(req, res) {
  res.render('signin');
});

/* GET Signin page. */
router.post('/signin', function(req, res) {
  var TEMPLATE = 'signin';
  users.verifyCredentials(req.body.email, req.body.password, function(user){
    if(user) {
      var week = 7 * 24 * 60 * 60 * 1000;
      if (req.body.remember) 
        res.cookie('remember', 1, { expires: new Date(Date.now() + 900000) });
      req.session.regenerate(function(){
        req.session.user = user;
        var redirectUrl = req.query.redirectUrl ? req.query.redirectUrl : '/';
        res.redirect(redirectUrl);
      });
    } else {
      res.render(TEMPLATE, errMsg("Invalid user and password combination!"));    
    }
  });
});

router.get('/signout', function(req, res){
  // destroy the user's session to log them out
  // will be re-created next request
  req.session.destroy(function(){
    //res.clearCookie('remember');
    res.redirect('/');
  });
});

/* GET home page. */
router.get('/', function(req, res) {
  res.render('index', {user:req.session.user});
});

/* Redirect to home page. */
router.get('/home', function(req, res) {
  res.redirect('/');
});

/* GET samples page. */
router.get('/samples', function(req, res) {
  res.render('sample', {user:req.session.user, view:'samples'});
});

/* GET demo page. */
router.get('/demo', function(req, res) {
  if(req.session.user)
    res.render('demo', {user:req.session.user, view:'demo'});
  else
    res.redirect('/signin?redirectUrl='+req.originalUrl);
});

/* GET search result json. */
router.post('/search', function(req, res) {
  request.post('http://localhost:3001/api/search', function (error, response, body) {
    if (!error && response.statusCode == 200) {
      var data = JSON.parse(body);

      var result = [];
      var type = req.body.type || 'all';
      var patientId = req.body.patientId || 'all';
      for(var i=0;i<data.length;i++) {
        if(type === 'all' && (patientId === data[i].pid || patientId === 'all')) {
          data[i].msgType = (data[i].msgType === 'ADT_A01') ? 'Admission' : 'Discharge';
          result.push(data[i]);
        } else if(type === 'admission' && data[i].msgType === 'ADT_A01' && (patientId === data[i].pid || patientId === 'all')) {
          data[i].msgType = 'Admission';
          result.push(data[i]);
        } else if(type === 'discharge' && data[i].msgType === 'ADT_A03' && (patientId === data[i].pid || patientId === 'all')) {
          data[i].msgType = 'Discharge';
          result.push(data[i]);
        }
      }
      res.send(result);
    }
  });
});

module.exports = router;
