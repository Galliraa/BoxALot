package com.example.kenne.box_a_lot.models;


import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    private Map<String, Object> userMap = new HashMap<>();

    public Map<String, Object> getUserMap() {
        return userMap;
    }

    public void setUserMap(DocumentSnapshot document) {
        this.setAddress((String)document.get("address"));
        this.setName((String)document.get("name"));
        this.setPhoneNumber((String)document.get("phoneNumber"));
        this.setPhotoURL((String)document.get("photoURL"));
        this.setStorageroomIds((List<String>)document.get("storageroomIds"));
    }

    public String getAddress() {
        return (String)this.userMap.get("address");
    }
    public void setAddress(String address) {
        this.userMap.put("address", address);
    }
    public String getName() {
        return (String)this.userMap.get("name");
    }
    public void setName(String name) {
        this.userMap.put("name", name);
    }
    public String getPhoneNumber() {
        return (String)this.userMap.get("phoneNumber");
    }
    public void setPhoneNumber(String PhoneNumber) {
        this.userMap.put("phoneNumber", PhoneNumber);
    }
    public String getPhotoURL() {
        return (String)this.userMap.get("photoURL");
    }
    public void setPhotoURL(String PhotoURL) {
        this.userMap.put("photoURL", PhotoURL);
    }
    public List<String> getStorageroomIds() {
        return (List<String>) this.userMap.get("storageroomIds");
    }
    public void setStorageroomIds(List<String> StorageroomIds) {
        this.userMap.put("storageroomIds", StorageroomIds);
    }

}
