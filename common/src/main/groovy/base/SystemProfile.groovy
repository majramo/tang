package base

public class SystemProfile {
    def systemProfileRows = [:]
    def name

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
        println(name)
        systemProfileRows.each {k,v->
            println("$k < " + v.toString())
        }
    }

}