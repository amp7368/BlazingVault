package com.blazing.vault.database.system;


import com.blazing.vault.database.model.entity.staff.DStaffConductor;

public class InitDatabase {

    public static void init() {
        DStaffConductor.insertDefaultConductors();
    }
}
