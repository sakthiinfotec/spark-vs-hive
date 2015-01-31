var express = require('express');
var router = express.Router();

/* GET search result json. */
router.post('/search', function(req, res) {
  var data = [
    {admissionDt:'23-11-2014',msgType:'ADT_A03',pid:'P1002',pname:'Roland Mendel',dob:'22-04-1982',sex:'M',address:'1288 Anderson Garden, GREENSBORO, DC',married:'Y',ssn:'158-75-2950'},
    {admissionDt:'17-11-2014',msgType:'ADT_A01',pid:'P1002',pname:'Roland Mendel',dob:'22-04-1982',sex:'M',address:'1288 Anderson Garden, GREENSBORO, DC',married:'Y',ssn:'158-75-2950'},
    {admissionDt:'13-11-2014',msgType:'ADT_A01',pid:'P1000',pname:'Maria Anders',dob:'14-09-1985',sex:'F',address:'1200 N ELM STREET, GREENSBORO, NC',married:'Y',ssn:'078-85-7120'},
    {admissionDt:'14-11-2014',msgType:'ADT_A03',pid:'P1001',pname:'Helen Bennett',dob:'08-03-1978',sex:'M',address:'145 Maddison Square, NY',married:'Y',ssn:'078-435-7120'},
    {admissionDt:'14-11-2014',msgType:'ADT_A01',pid:'P1001',pname:'Helen Bennett',dob:'08-03-1978',sex:'M',address:'145 Maddison Square, NY',married:'Y',ssn:'078-435-7120'},
    {admissionDt:'12-10-2014',msgType:'ADT_A03',pid:'P1000',pname:'Maria Anders',dob:'14-09-1985',sex:'F',address:'1200 N ELM STREET, GREENSBORO, NC',married:'Y',ssn:'078-85-7120'},
    {admissionDt:'05-10-2014',msgType:'ADT_A01',pid:'P1000',pname:'Maria Anders',dob:'14-09-1985',sex:'F',address:'1200 N ELM STREET, GREENSBORO, NC',married:'Y',ssn:'078-85-7120'}
  ];
  res.send(data);
});

module.exports = router;
