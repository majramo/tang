package dtos

class QueryCondition{
	String field
	String value
	String sutXpath
    def regExp
	boolean isString
	String attributeName
	
	QueryCondition (String field, String value, boolean isString){
		this.field = field
		this.value  = value
		this.isString  = isString
	}

	QueryCondition (String field, String sutXpath, boolean isString, String attributeName){
		this.field = field
		this.sutXpath  = sutXpath
		this.isString  = isString
		this.attributeName  = attributeName.toLowerCase()
	}
	QueryCondition (String field, String sutXpath, boolean isString, String attributeName, regExp){
		this.field = field
		this.sutXpath  = sutXpath
		this.isString  = isString
		this.attributeName  = attributeName.toLowerCase()
		this.regExp  = regExp
	}
}
