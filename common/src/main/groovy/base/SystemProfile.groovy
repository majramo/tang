package base

import dtos.SettingsHelper

public class SystemProfile {
    def systemProfileRows = [:]
    def name
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings

    public SystemProfile(name){
        this.name = name
    }

    public add(row){
        SystemProfileRow systemProfileRow = new SystemProfileRow(row)
        systemProfileRows[systemProfileRow.tableColumn] = systemProfileRow
    }

    public getSystemProfileKeys(){
        systemProfileRows.keySet()
    }

    public getSystemProfileRowsContainingKeys(keys){
        systemProfileRows.findAll {
            keys.contains(it.key)
        }
    }


    public print(){
        if(settings.debug == true){
            println(name)
        }
        systemProfileRows.each {k,v->
            if(settings.debug == true){
                println("$k < " + v.toString())
            }
        }
    }
    public  getDbValues(){
        return systemProfileRows.collect {it-> it.value.getDbValues()}
    }

}