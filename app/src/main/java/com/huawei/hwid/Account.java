package com.huawei.hwid;

import android.os.Parcel;
import android.os.Parcelable;

public class Account implements Parcelable {
    String at;
    String uid;
    String nick;
    String phone;
    String email;
    String countryCode;
    String picUrl;
    int gender;
    String familyName;
    String givenName;

    public Account() {
    }

    protected Account(Parcel in) {
        at = in.readString();
        uid = in.readString();
        nick = in.readString();
        phone = in.readString();
        email = in.readString();
        countryCode = in.readString();
        picUrl = in.readString();
        gender = in.readInt();
        familyName = in.readString();
        givenName = in.readString();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(at);
        parcel.writeString(uid);
        parcel.writeString(nick);
        parcel.writeString(phone);
        parcel.writeString(email);
        parcel.writeString(countryCode);
        parcel.writeString(picUrl);
        parcel.writeInt(gender);
        parcel.writeString(familyName);
        parcel.writeString(givenName);
    }

    @Override
    public String toString() {
        return "Account{" +
                "at='" + at + '\'' +
                ", uid='" + uid + '\'' +
                ", nick='" + nick + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", gender=" + gender +
                ", familyName='" + familyName + '\'' +
                ", givenName='" + givenName + '\'' +
                '}';
    }
}
