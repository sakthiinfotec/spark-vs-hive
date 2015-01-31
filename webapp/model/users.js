var db = require('../lib/db');
var hash = require('../lib/password').hash;

var msg = "";

exports.createUser = function (user, cb) {
  console.log("user password:" + user.password)
  hash(user.password, function(err, salt, hash){
    if (err) throw err;
    // store the salt & hash in the "db"
    var sqlQuery = "INSERT INTO users (fname, lname, email, salt, hash) " +
           "VALUES('" + user.fname + "','" + user.lname + "','" + user.email + "','" + salt + "','" + hash + "')";
    db.run(sqlQuery, function(err) {
      if(err) {
        msg = "Error in creating user details for user with email:" + user.email;
        console.error(msg + ". " + err);
        cb(msg);
      } else
        cb(null, {email: user.email, fname: user.fname, lname: user.lname});
    });
  });
}

var findUserByEmail = exports.findUserByEmail = function (email, cb) {
  var sqlQuery = "SELECT * from users WHERE email='" + email + "'";
  db.all(sqlQuery, function(err, users) {
    if(err) {
      msg = "Error while retrieving user by email:" + email;
      console.error(msg + ". " + err);
      cb(err);
    } else {
      (users.length == 0) ? cb(null, null) : cb(null, users[0]);
    }
  });  
}

exports.checkUserExists = function (email, cb) {
  findUserByEmail(email, function(err, user) {
    if(null != err) {
      console.error(err);
      cb("Error in checking user with email:" + email);
    } else {
      (user) ? cb(null, true) : cb(null, false);
    }
  });
}

exports.verifyCredentials = function (email, password, cb) {
  findUserByEmail(email, function(err, user) {
    if(null != err) {
      console.error(err);
      cb("Error in verifying user:" + email);
    } else {
      if (user) {
        // when you create a user, generate a salt
        // and hash the password ('foobar' is the pass here)
        hash(password, user.salt, function(err, hash){
          if (err) {
            console.error(err);
            cb("Error in verifying user:" + email);
          } else if(user.hash === hash) {
            cb({email: user.email, fname: user.fname, lname: user.lname});
          } else {
            cb();
          }
        });
      } else {
        cb();
      }
    }
  });
}