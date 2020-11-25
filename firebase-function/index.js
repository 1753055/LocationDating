const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

// doc du lieu realtime => distance => today Statistics ben firestore
const db = admin.firestore();
var d = new Date();
var today = d.getDate();
var thisMonth = d.getMonth().toString();
var thisYear = d.getFullYear().toString();

var currentUserID;
exports.sumDistance = functions.database.ref('/LocationHistory/{userID}').onWrite(
  async(change) =>{
    currentUserID = change.after.key;
    var sum = 0;
    var sumMonth = 0;
    const data = change.after.forEach(element => {
      // console.log("month"+element.child('date').child('month').val()+"this"+today);
      // console.log("date"+element.child('date').child('date').val()+"this"+thisMonth);
      // console.log(element.child('distance').val());
      distance = element.child('distance').val()
      if(element.child('date').child('date').val() === today){
        sum+=distance;
      }
      
      if(element.child('date').child('month').val().toString() === thisMonth){
        sumMonth+=distance;
        console.log(sumMonth);
      }else{
        console.log(element.child('date').child('month').val() +"thismonth"+thisMonth)
      }
    });

    var obj = JSON.parse('{"Statistics": {'+'"'+thisYear+'"'+':{'+'"'+thisMonth+'": {'+'"'+today+'":'+sum+'}}}}');
    // var obj2 = JSON.parse('{"TotalDistance": {'+'"'+thisYear+'"'+':{'+'"'+thisMonth+'":'+sum+'}}}');
    db.doc('User/'+currentUserID).set(obj, {merge: true});
    // db.doc('User/'+currentUserID).set(obj2, {merge: true});
    // console.log('hellllllll'+sum);
    // cap nhat totaldistance firestore
    // const dataMonth = change.after.forEach(element => {
      
    // });
    var objMonth = JSON.parse('{"TotalDistance": {'+'"'+thisYear+'"'+':{'+'"'+thisMonth+'":'+sumMonth+'}}}');
    db.doc('User/'+currentUserID).set(objMonth, {merge: true});
});

// exports.sumMonth = functions.firestore.document('User/{userID}').onWrite((change, context)=>{
//   console.log("month");
//   var objMonth = JSON.parse('{"TotalDistance": {'+'"'+thisYear+'"'+':{'+'"'+thisMonth+'":'+sumMonth+'}}}');
//   db.doc('User/'+currentUserID).set(objMonth, {merge: true});
// })