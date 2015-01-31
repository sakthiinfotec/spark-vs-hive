var hash = require('./password').hash;
var users = require('../model/users');

// Authenticate using our plain-object database of doom!
exports.authenticate = function (email, pass, cb) {
  if (!module.parent) console.log('authenticating %s:%s', email, pass);
  users.findUserByEmail(email, function(err, user){
    // query the db for the given username
    if (!user) return cb(new Error('Cannot find user'));
    // apply the same algorithm to the POSTed password, applying
    // the hash against the pass / salt, if there is a match we
    // found the user
    hash(pass, user.salt, function(err, hash){
      if (err) return cb(err);
      if (hash == user.hash) return cb(null, user);
      cb(new Error('Invalid email and password'));
    });
  });
}

exports.createUserSession = function (req, user, cb) {
  req.session.regenerate(function(){
    req.session.user = user;
    cb();
  });
}

exports.sessionExists = function (req, res, next) {
  if (req.session.user) {
    next();
  } else {
    req.session.error = 'Access denied!';
    res.redirect('/signin');
  }
}