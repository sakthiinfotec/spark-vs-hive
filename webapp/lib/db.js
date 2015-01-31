var sqlite3 = require('sqlite3').verbose();
var dbName =  'analytics.db';
console.log("Connecting to DB " + dbName + "...")
var db = new sqlite3.Database('data/' + dbName);
console.log("DB connected")
 
db.serialize(function() {
  var sqlQuery = "CREATE TABLE IF NOT EXISTS users (fname VARCHAR(100), lname VARCHAR(100), email VARCHAR(50) PRIMARY KEY, salt VARCHAR(100), hash VARCHAR(100))";
  db.run(sqlQuery, function(err) {
    if(err) {
      msg = "Error in creating users table.";
      console.error(msg + ". " + err);
      cb(msg);
    }
  });
});

module.exports = db