package com.deepanshu.whatsappdemo.model;

public class Token {
    private String token;

    public Token(String token) {
        this.token = token;
    }
    public Token() {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    //todo this is the request for the postman

/* postman request  : post request:https://fcm.googleapis.com/fcm/send
header:
Authorization : key=AAAA7O5Cr1Y:APA91bGjg2umWGcdpKuCMlx_mAeTVSQYXyVm0tCC2CDi-uSwDFJV_3bhEBn-2CByBjX6cKvEoLQWNRrd0Wsz5hNSotgvBcGMVhdw7Mr-Zn1fKhgCQ0BglTFajomtAyEFJ92RV381Gx4_
 Content-Type: application/json
 body:
{
 "to" : "f8zqq6S_SxmB-autizbpdo:APA91bGgs58hZCWH6Njnobnwpdc88xHMqv35Wr4NHYPscBdC8YoJFI64pzCt4OtbsopRe8GbpkDLh0_uena4wRakyQr1hWio8Hz8cgOM7_Hg8U6glOsHAUwU3f-HmT2kd4nZ4d3TDXQy",
 "collapse_key" : "type_a",
 "data" : {
   "body" : "Body of Your Notification",
    "title": "Title of Your Notification",
    "image":"https://www.gstatic.com/webp/gallery/1.sm.jpg?dcb_=0.7505541297390113",
	"priority":"high",
	"channelid":"HighPriority",
	"classtype":"Landing"
 }
}
*/
}
