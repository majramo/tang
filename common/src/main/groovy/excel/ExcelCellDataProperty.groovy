package excel

import static dtos.base.Constants.CompareType.EQUAL
import static dtos.base.Constants.CompareType.DIFF
import static dtos.base.Constants.CompareType.NOT_EMPTY

/**
 * Created by majid on 2016-05-20.
 */
class ExcelCellDataProperty {
    public ExcelCellDataProperty(column) {
        this.column = column
    }
    def column = ""
    def valueToComprae = ""
    def compareType = ""

    boolean compare(cellName) {
        if (compareType == "" || (compareType == EQUAL && this.valueToComprae == cellName)) {
            return true
        } else {
            if (compareType == DIFF && !this.valueToComprae.equals(cellName)) {
                return true
            }else {
                if (compareType == NOT_EMPTY && !cellName.isEmpty()) {
                    return true
                }
            }
        }
        return false
    }

    public String toString(){
        return "    Column <$column> valueToComprae <$valueToComprae> compareType <$compareType>"
    }
}
