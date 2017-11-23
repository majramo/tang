package base

public class EnvironmentProperties {
    def fields = [:]


    public EnvironmentProperties(fieldsToAddToEnv){
        fieldsToAddToEnv.each {String k,String v->
            fields[k.trim()] = v.trim()
        }
    }

    def getPropertyValue(String field){
        return fields[field]
    }

    def String toString(){
        def str = ""
        fields.each {k, v->
            str += "$k:  <$v> \n"
        }
        return str
    }
}
