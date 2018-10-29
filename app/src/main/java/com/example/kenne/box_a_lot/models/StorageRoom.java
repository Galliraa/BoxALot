package com.example.kenne.box_a_lot.models;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageRoom {

    public static List<StorageRoom> ITEMS = new ArrayList<StorageRoom>();


    public static final Map<String, StorageRoom> ITEM_MAP = new HashMap<String, StorageRoom>();

    Map<String, Object> StorageMap = new HashMap<>();

    public Map<String, Object> getStorageMap() {
        return StorageMap;
    }

    public void setStorageMap(DocumentSnapshot  document) {
        //this.setCoordinates((List<Double>) document.get("location"));
        this.setStorageRoomId(document.getId());
        this.setGeneralInfo((List<Boolean>) document.get("generalInfo"));
        this.setAvailable((Boolean) document.get("available"));
        this.setPrice((String) document.get("price"));
        this.setPicRef(((ArrayList<String>)document.get("picRef")));
        this.setChatIds((List<Integer>) document.get("chatIds"));
        this.setAddress((String) document.get("address"));
        this.setUserId((FirebaseUser) document.get("userId"));
    }

    /*    int storageRoomID;
            List<Double> coordinates;

            int userID;
            String address;
            String price;
            Boolean available;
            int picRef;
            List<Boolean> generalInfo;
            List<Integer> chatIds;
        */
    public String getStorageRoomId() {
        return (String) StorageMap.get("storageRoomId");
    }

    public void setStorageRoomId(String storageRoomId) {
        this.StorageMap.put("storageRoomId", storageRoomId);
    }

    public List<Double> getCoordinates() {
        return (List<Double>) StorageMap.get("location");
    }

    public void setCoordinates(List<Double> coordinates) {
        this.StorageMap.put("location", coordinates);
    }

    public FirebaseUser getUserId() {
        return (FirebaseUser) StorageMap.get("userId");
    }

    public void setUserId(FirebaseUser userId) {
        this.StorageMap.put("userId", userId);
    }

    public String getAddress() {
        return (String) StorageMap.get("address");
    }

    public void setAddress(String address) {
        this.StorageMap.put("address", address);
    }

    public String getPrice() {
        return (String) StorageMap.get("price");
    }

    public void setPrice(String price) {
        this.StorageMap.put("price", price);
    }

    public Boolean getAvailable() {
        return (Boolean) StorageMap.get("available");
    }

    public void setAvailable(Boolean available) {
        this.StorageMap.put("available", available);
    }

    public List<String> getPicRef() {
        return (List) StorageMap.get("picRef");
    }

    public void setPicRef(List picRef) {
        this.StorageMap.put("picRef", picRef);
    }

    public List<Boolean> getGeneralInfo() {
        return (List<Boolean>) StorageMap.get("generalInfo");
    }

    public void setGeneralInfo(List<Boolean> generalInfo) {
        this.StorageMap.put("generalInfo", generalInfo);
    }

    public List<Integer> getChatIds() {
        return (List<Integer>) StorageMap.get("chatIds");
    }

    public void setChatIds(List<Integer> chatIds) {
        this.StorageMap.put("chatIds", chatIds);
    }
}
