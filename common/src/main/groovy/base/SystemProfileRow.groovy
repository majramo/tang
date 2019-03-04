package base

public class SystemProfileRow {
    def System
    def Table
    def Column
    def Type
    def Sensitive
    def Masking
    def Action
    def MaskOverride
    def MaskOverrideAddon
    def MaskExtra
    def TargetSizeMinimumDiff
    def TargetSizeMaximumDiff
    def RunSql
    def SearchCriteria
    def SearchExtraCondition
    def Verify
    private row
    public tableColumn

    public SystemProfileRow(row){
        this.row = row
        int i = 0
        System = row[i++]
        Table = row[i++]
        Column = row[i++]
        type = row[i++]
        Sensitive = row[i++]
        Masking = row[i++]
        Action = row[i++]
        MaskOverride = row[i++]
        MaskOverrideAddon = row[i++]
        MaskExtra = row[i++]
        TargetSizeMinimumDiff = row[i++]
        TargetSizeMaximumDiff = row[i++]
        RunSql = row[i++]
        SearchCriteria = row[i++]
        SearchExtraCondition = row[i++]
        Verify = row[i++]

        tableColumn = "${table}.${column}"

    }
    public String toString(){
        return [System, Table, Column, Type, Sensitive, Masking, Action, MaskOverride, MaskOverrideAddon, MaskExtra, TargetSizeMinimumDiff, TargetSizeMaximumDiff, RunSql, SearchCriteria, SearchExtraCondition, Verify].join("\t")
    }
    public  getValues(){
        return [System, Table, Column, Type, Sensitive, Masking, Action, MaskOverride, MaskOverrideAddon, MaskExtra, TargetSizeMinimumDiff, TargetSizeMaximumDiff, RunSql, SearchCriteria, SearchExtraCondition, Verify]
    }
    public  getDbValues(){
        return [System:System, Table:Table, Column:Column, Type:Type]
    }
}